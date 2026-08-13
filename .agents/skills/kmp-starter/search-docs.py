#!/usr/bin/env python3
"""search-docs — search and read KMP Starter Template docs offline (cached).

Fetches https://starter.atherio.dev/search.json and caches it for 5 minutes at
{skill}/.skill-storage/docs.json so the agent can search/read docs without
hitting the live site on every query.

Usage:
    python3 search-docs.py "koin module"            # search
    python3 search-docs.py "koin module" --max 5    # limit results
    python3 search-docs.py "koin" --refresh         # force refetch, then search
    python3 search-docs.py --get "koin/"            # read a page (all sections)
    python3 search-docs.py --get "koin/#scopes"     # read one section
    python3 search-docs.py --sitemap                # print page -> section map
    python3 search-docs.py --sitemap resource       # filter sitemap to paths with "resource"
    python3 search-docs.py --json "koin"            # machine-readable output

No third-party deps (stdlib only). Python 3.8+.
"""

import argparse
import html
import json
import re
import sys
import urllib.request
from pathlib import Path

BASE_URL = "https://starter.atherio.dev"
SEARCH_URL = f"{BASE_URL}/search.json"
CACHE_TTL_SECONDS = 5 * 60  # 5 minutes

# Cache lives next to the skill: {skill}/.skill-storage/docs.json
SKILL_DIR = Path(__file__).resolve().parent
CACHE_DIR = SKILL_DIR / ".skill-storage"
CACHE_FILE = CACHE_DIR / "docs.json"

_TAG_RE = re.compile(r"<[^>]+>")


def strip_html(text: str) -> str:
    """Remove HTML tags and unescape entities for readable text."""
    if not text:
        return ""
    text = _TAG_RE.sub(" ", text)
    text = html.unescape(text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def cache_is_fresh() -> bool:
    try:
        if not CACHE_FILE.exists():
            return False
        age = __import__("time").time() - CACHE_FILE.stat().st_mtime
        return age < CACHE_TTL_SECONDS
    except OSError:
        return False


def load_cache():
    with CACHE_FILE.open("r", encoding="utf-8") as f:
        return json.load(f)


def fetch_docs():
    req = urllib.request.Request(SEARCH_URL, headers={"User-Agent": "kmp-starter-skill"})
    with urllib.request.urlopen(req, timeout=30) as resp:
        data = resp.read().decode("utf-8")
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    CACHE_FILE.write_text(data, encoding="utf-8")
    return json.loads(data)


def get_docs(force_refresh: bool):
    if not force_refresh and cache_is_fresh():
        try:
            return load_cache()
        except (OSError, json.JSONDecodeError):
            pass  # stale/corrupt cache -> refetch
    return fetch_docs()


def items(docs):
    return docs.get("items", [])


def url_for(location: str) -> str:
    return f"{BASE_URL}/{location}" if location else BASE_URL


def search(docs, query, max_results):
    terms = [t.lower() for t in re.split(r"\s+", query.strip()) if t]
    if not terms:
        return []

    scored = []
    for it in items(docs):
        title = (it.get("title") or "").lower()
        text = strip_html(it.get("text") or "").lower()
        location = (it.get("location") or "").lower()
        breadcrumb = " ".join(it.get("path") or []).lower()

        score = 0
        for term in terms:
            if term in title:
                score += 6
            if term in location:
                score += 4
            if term in breadcrumb:
                score += 3
            if term in text:
                score += 1

        if score > 0:
            scored.append((score, it))

    scored.sort(key=lambda pair: (-pair[0], pair[1].get("level", 99)))
    return [it for _, it in scored[:max_results]]


def render_item(it, show_full_text=False):
    location = it.get("location", "")
    title = it.get("title", "")
    level = it.get("level", 0)
    breadcrumb = " > ".join(it.get("path") or [])
    text = strip_html(it.get("text") or "")

    lines = []
    lines.append(f"{'#' * max(1, min(level, 3))} {title or '(untitled)'}")
    lines.append(f"URL: {url_for(location)}")
    if breadcrumb:
        lines.append(f"Path: {breadcrumb}")
    if show_full_text or not text:
        if text:
            lines.append("")
            lines.append(text)
    else:
        snippet = text[:400] + ("…" if len(text) > 400 else "")
        lines.append(f"Text: {snippet}")
    return "\n".join(lines)


def cmd_search(docs, query, max_results):
    results = search(docs, query, max_results)
    if not results:
        print(f"No results for: {query}", file=sys.stderr)
        return 1
    print(f"{len(results)} result(s) for: {query}\n")
    for it in results:
        print(render_item(it))
        print()
    return 0


def cmd_get(docs, target):
    target = target.strip().strip("/")

    # A page = its root item (location == target) + all `#section` items under it.
    page_items = [
        it for it in items(docs)
        if (it.get("location") or "").strip("/") == target
        or (it.get("location") or "").startswith(target + "/")
        or (it.get("location") or "").startswith(target + "#")
    ]
    if not page_items:
        print(f"No page/section found for: {target}", file=sys.stderr)
        return 1

    page_items.sort(key=lambda it: it.get("level", 99))
    label = "section" if len(page_items) == 1 else "sections"
    print(f"{len(page_items)} {label} for: {target}\n")
    for it in page_items:
        print(render_item(it, show_full_text=True))
        print()
    return 0


def page_root(location: str) -> str:
    """Return the page root of a location (strip any `#anchor`)."""
    loc = (location or "").strip("/")
    return loc.split("#")[0].rstrip("/")


def cmd_sitemap(docs, query=None):
    # Group items by page root, ordered by first appearance.
    pages = {}
    for it in items(docs):
        root = page_root(it.get("location", ""))
        pages.setdefault(root, []).append(it)

    terms = [t.lower() for t in re.split(r"\s+", (query or "").strip()) if t]

    def matches(s: str) -> bool:
        if not terms:
            return True
        s = s.lower()
        return any(t in s for t in terms)

    matched = 0
    for root, its in pages.items():
        root_item = next(
            (it for it in its if "#" not in (it.get("location") or "")),
            None,
        )
        title = (root_item or its[0]).get("title") or root or "(home)"
        root_url = f"{BASE_URL}/{root}/" if root else f"{BASE_URL}/"

        # Collect matching sections for this page.
        section_lines = []
        for it in its:
            loc = it.get("location", "")
            if "#" in loc:
                label = it.get("title") or loc.split("#")[1]
                sec_url = url_for(loc)
                if matches(f"{label} {loc} {sec_url}"):
                    indent = "  " * max(0, it.get("level", 0) - 1)
                    section_lines.append(f"{indent}- {label} — {sec_url}")

        page_match = matches(f"{title} {root} {root_url}")

        if not terms:
            # No filter: print everything.
            print(f"{title} — {root_url}")
            for line in section_lines:
                print(line)
            print()
            continue

        if page_match:
            print(f"{title} — {root_url}")
            matched += 1
            for line in section_lines:
                print(line)
            print()
        elif section_lines:
            print(f"{title} — {root_url}")
            matched += 1
            for line in section_lines:
                print(line)
            print()

    if terms and matched == 0:
        print(f"No sitemap matches for: {query}", file=sys.stderr)
        return 1
    return 0


def main():
    parser = argparse.ArgumentParser(description="Search/read KMP Starter docs (cached).")
    parser.add_argument("query", nargs="?", help="search terms")
    parser.add_argument("--max", type=int, default=10, help="max results (default 10)")
    parser.add_argument("--get", metavar="LOCATION", help="read a page/section by location (e.g. 'koin/' or 'koin/#scopes')")
    parser.add_argument("--sitemap", nargs="?", const="", metavar="QUERY", help="print the docs sitemap (page -> sections); optional QUERY filters to matching paths")
    parser.add_argument("--refresh", action="store_true", help="force refetch of search.json")
    parser.add_argument("--json", action="store_true", help="output raw JSON results")
    args = parser.parse_args()

    if args.sitemap is not None:
        docs = get_docs(args.refresh)
        return cmd_sitemap(docs, args.sitemap)

    if args.get:
        docs = get_docs(args.refresh)
        if args.json:
            target = args.get.strip().strip("/")
            out = [it for it in items(docs) if (it.get("location") or "").strip("/").startswith(target)]
            print(json.dumps(out, indent=2))
            return 0
        return cmd_get(docs, args.get)

    if not args.query:
        parser.print_help(sys.stderr)
        return 2

    docs = get_docs(args.refresh)
    if args.json:
        print(json.dumps(search(docs, args.query, args.max), indent=2))
        return 0
    return cmd_search(docs, args.query, args.max)


if __name__ == "__main__":
    sys.exit(main())
