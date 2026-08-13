#!/usr/bin/env python3
"""search-code — download & search the KMP Starter Template source code.

Downloads the public GitHub repo (DevAtrii/Kmp-Starter-Template) at a given
version (default: latest = main) into {skill}/.skill-storage/codes/{version}/,
then lets the agent search declarations (classes, objects, functions,
properties, interfaces, typealiases) with their KDoc, signature, params,
inheritance, and usages — token-efficient output.

Usage:
    python3 search-code.py MviViewModel --types class      # find a class
    python3 search-code.py onAction --types function       # find a function
    python3 search-code.py --types function --kdocs        # list all functions + kdocs
    python3 search-code.py --types class --kdocs-depth 3   # kdocs incl. grandchildren
    python3 search-code.py Foo --types all --relations     # relationships + usages
    python3 search-code.py MviViewModel --only-inheritor    # only its subclasses
    python3 search-code.py MviViewModel --include-inheritor # class + subclasses
    python3 search-code.py --list-modules                  # modules from settings.gradle.kts
    python3 search-code.py --get-version koin              # version from libs.versions.toml
    python3 search-code.py --get-library starter-core      # library coordinate
    python3 search-code.py --get-plugin kotlin-multiplatform
    python3 search-code.py --clear-codes                   # delete all cached source
    python3 search-code.py --clear-codes 0.5.7 main        # delete specific versions

Options:
    --types T[,T...]   kinds to match: class, interface, object, function,
                       property, typealias, enum, annotation, sealed, data,
                       or "all" (default: all)
    --kdocs            include KDoc text
    --kdocs-depth N    kdoc depth: 1=self, 2=+children, ... max 5, -1=all (default 2)
    --relations        show parent chain, supertypes, and references
    --include-inheritor  include classes inheriting the matched type
    --only-inheritor     only return classes inheriting the matched type
    --modules M[,M]    restrict search to modules/dirs (default: all)
    --extensions e[,e] file extensions (default: kt,kts,toml)
    --version V        source version/tag (default: main)
    --list-versions    list available repo tags
    --clear-codes [V..]  delete cached source (optional version list; none = all)
    --source PATH      search a local source tree instead of downloading
    --max N            max results (default 25)
    --regex            treat query as a regex
    --in-body          also match kdoc/signature text (not just the name)
    --json             machine-readable output

No third-party deps (stdlib only). Python 3.9+.
"""

import argparse
import json
import os
import re
import sys
import tarfile
import tempfile
import urllib.request
from pathlib import Path

REPO_SLUG = "DevAtrii/Kmp-Starter-Template"
REPO_ARCHIVE = f"https://github.com/{REPO_SLUG}/archive"
SKILL_DIR = Path(__file__).resolve().parent
STORAGE_DIR = SKILL_DIR / ".skill-storage"
CODE_DIR = STORAGE_DIR / "codes"

IGNORE_DIRS = {"build", ".gradle", ".idea", ".git", "node_modules", ".kotlin", "generated", ".skill-storage", ".agents"}

# Declaration keywords and qualifiers.
KIND_KEYWORDS = {"class", "interface", "object", "fun", "val", "var", "typealias"}
QUALIFIERS = {
    "public", "private", "protected", "internal", "override", "open", "final",
    "abstract", "sealed", "data", "enum", "annotation", "value", "inner",
    "suspend", "inline", "noinline", "crossinline", "operator", "infix",
    "tailrec", "external", "const", "lateinit", "companion", "expect",
    "actual", "vararg", "reified",
}

TYPE_ALIASES = {
    "fun": "function", "function": "function", "functions": "function",
    "var": "property", "val": "property", "variable": "property",
    "variables": "property", "property": "property", "properties": "property",
    "class": "class", "classes": "class", "interface": "interface",
    "interfaces": "interface", "object": "object", "objects": "object",
    "typealias": "typealias", "typealiases": "typealias",
    "enum": "enum", "annotation": "annotation", "sealed": "sealed", "data": "data",
}


# ---------------------------------------------------------------------------
# Download / source root
# ---------------------------------------------------------------------------

def fetch_bytes(url: str) -> bytes:
    req = urllib.request.Request(url, headers={"User-Agent": "kmp-starter-skill"})
    with urllib.request.urlopen(req, timeout=120) as resp:
        return resp.read()


def list_remote_versions():
    try:
        import subprocess
        out = subprocess.run(
            ["git", "ls-remote", "--tags", f"https://github.com/{REPO_SLUG}.git"],
            capture_output=True, text=True, timeout=60,
        ).stdout
    except Exception:
        out = ""
    tags = []
    for line in out.splitlines():
        ref = line.rsplit("\t", 1)[-1]
        if ref.startswith("refs/tags/") and not ref.endswith("^{}"):
            tags.append(ref[len("refs/tags/"):])
    return sorted(tags)


def resolve_archive_ref(version: str) -> str:
    if version in ("main", "latest", ""):
        return "refs/heads/main"
    return f"refs/tags/{version}"


def ensure_source(version: str) -> Path:
    version = version or "main"
    target = CODE_DIR / version
    marker = target / ".ready"
    if marker.exists() and target.is_dir():
        return target

    target.parent.mkdir(parents=True, exist_ok=True)
    ref = resolve_archive_ref(version)
    url = f"{REPO_ARCHIVE}/{ref}.tar.gz"

    with tempfile.TemporaryDirectory() as td:
        tmp_dir = Path(td)
        tgz = tmp_dir / "src.tar.gz"
        tgz.write_bytes(fetch_bytes(url))
        with tarfile.open(tgz, "r:gz") as tf:
            tf.extractall(tmp_dir)
        tgz.unlink()
        # The archive extracts to a single top-level dir like "Kmp-Starter-Template-main".
        entries = [p for p in tmp_dir.iterdir() if p.is_dir()]
        if not entries:
            raise RuntimeError(f"Archive for {version} had no directory")
        extracted = entries[0]
        if target.exists():
            import shutil
            shutil.rmtree(target)
        extracted.rename(target)

    (target / ".ready").write_text(version, encoding="utf-8")
    return target


def source_root(args) -> Path:
    if args.source:
        return Path(args.source).resolve()
    return ensure_source(args.version or "main")


# ---------------------------------------------------------------------------
# Lexer
# ---------------------------------------------------------------------------

def tokenize(text: str):
    """Yield (kind, value, start, line) tokens."""
    toks = []
    i, n = 0, len(text)
    line = 1
    while i < n:
        c = text[i]
        if c == "\n":
            line += 1
            i += 1
            continue
        if c in " \t\r":
            i += 1
            continue
        if text.startswith("//", i):
            j = text.find("\n", i)
            i = n if j == -1 else j
            continue
        if text.startswith("/*", i):
            is_kdoc = text.startswith("/**", i) and not text.startswith("/**/", i)
            j = text.find("*/", i + 2)
            if j == -1:
                j = n
            if is_kdoc:
                toks.append(("kdoc", text[i + 3:j], i, line))
            line += text[i:j + 2].count("\n")
            i = j + 2
            continue
        if c == '"':
            if text.startswith('"""', i):
                j = text.find('"""', i + 3)
                if j == -1:
                    j = n
                seg = text[i:j + 3]
                line += seg.count("\n")
                toks.append(("str", seg, i, line))
                i = j + 3
            else:
                j = i + 1
                while j < n:
                    if text[j] == "\\":
                        j += 2
                        continue
                    if text[j] == '"':
                        break
                    j += 1
                toks.append(("str", text[i:j + 1], i, line))
                i = j + 1
            continue
        if c == "'":
            j = i + 1
            while j < n:
                if text[j] == "\\":
                    j += 2
                    continue
                if text[j] == "'":
                    break
                j += 1
            toks.append(("str", text[i:j + 1], i, line))
            i = j + 1
            continue
        if c.isalpha() or c == "_":
            j = i
            while j < n and (text[j].isalnum() or text[j] == "_"):
                j += 1
            toks.append(("id", text[i:j], i, line))
            i = j
            continue
        toks.append((c, c, i, line))
        i += 1
    return toks


def clean_kdoc(raw: str) -> str:
    out = []
    for ln in raw.splitlines():
        ln = ln.strip()
        ln = re.sub(r"^\*\s?", "", ln)
        ln = ln.strip()
        if ln.startswith("@"):
            continue  # skip @param/@return tags in inline kdoc output
        out.append(ln)
    text = " ".join(out)
    return re.sub(r"\s+", " ", text).strip()


# ---------------------------------------------------------------------------
# Parser
# ---------------------------------------------------------------------------

def split_top_level(s: str, sep=","):
    parts, depth, cur = [], 0, ""
    for ch in s:
        if ch in "([{<":
            depth += 1
        elif ch in ")]}>":
            depth = max(0, depth - 1)
        if ch == sep and depth == 0:
            parts.append(cur)
            cur = ""
            continue
        cur += ch
    if cur.strip():
        parts.append(cur)
    return [p.strip() for p in parts if p.strip()]


def match_paren(s: str, open_idx: int):
    depth = 0
    for i in range(open_idx, len(s)):
        if s[i] == "(":
            depth += 1
        elif s[i] == ")":
            depth -= 1
            if depth == 0:
                return i
    return -1


def skip_balanced(s: str, i: int, open_c, close_c):
    depth = 0
    while i < len(s):
        if s[i] == open_c:
            depth += 1
        elif s[i] == close_c:
            depth -= 1
            if depth == 0:
                return i + 1
        i += 1
    return len(s)


def parse_param(p: str):
    p = p.strip()
    p = re.sub(r"@\w+(?:\([^)]*\))?", "", p).strip()
    for m in ("private", "public", "protected", "internal", "val", "var",
              "noinline", "crossinline", "override", "open", "abstract",
              "vararg", "const", "lateinit", "in", "out"):
        p = re.sub(r"^\b" + m + r"\b\s+", "", p)
    mm = re.match(r"([A-Za-z_][A-Za-z0-9_]*)\s*:\s*(.*)$", p, re.S)
    if mm:
        return {"name": mm.group(1), "type": mm.group(2).strip()}
    return {"name": p, "type": ""}


def extract_params(sig: str, name: str):
    """Pull primary-constructor / function params from a signature string."""
    idx = sig.find(name)
    if idx == -1:
        return []
    i = idx + len(name)
    # skip type parameters <...> then look for (
    j = i
    while j < len(sig) and sig[j] in " \t":
        j += 1
    if j < len(sig) and sig[j] == "<":
        j = skip_balanced(sig, j, "<", ">")
        while j < len(sig) and sig[j] in " \t":
            j += 1
    if j >= len(sig) or sig[j] != "(":
        return []
    close = match_paren(sig, j)
    if close == -1:
        return []
    inner = sig[j + 1:close]
    return [parse_param(p) for p in split_top_level(inner)]


def extract_supertypes(sig: str, name: str):
    """Find supertypes after the class/interface header colon."""
    idx = sig.find(name)
    if idx == -1:
        return []
    i = idx + len(name)
    # skip <typeparams> then (primary ctor)
    j = i
    while j < len(sig) and sig[j] in " \t":
        j += 1
    if j < len(sig) and sig[j] == "<":
        j = skip_balanced(sig, j, "<", ">")
        while j < len(sig) and sig[j] in " \t":
            j += 1
    if j < len(sig) and sig[j] == "(":
        j = match_paren(sig, j) + 1
        while j < len(sig) and sig[j] in " \t":
            j += 1
    if j >= len(sig) or sig[j] != ":":
        return []
    rest = sig[j + 1:].strip()
    return [s.strip() for s in split_top_level(rest)]


def detect_kind_and_name(toks, pos):
    """Given the index of a KIND keyword token, classify and return (kind, name)."""
    kind_tok = toks[pos]
    kw = kind_tok[1]
    # collect qualifiers preceding (on the same line-ish)
    # determine actual kind from kw + neighbors
    kind = {
        "class": "class", "interface": "interface", "object": "object",
        "fun": "function", "val": "property", "var": "property",
        "typealias": "typealias",
    }[kw]

    # "fun interface" -> interface, else function
    if kw == "fun":
        nxt = toks[pos + 1][1] if pos + 1 < len(toks) else ""
        if nxt == "interface":
            return "interface", None  # name resolved by caller
    # name = next identifier token after the kind keyword (skip qualifiers like 'enum class' -> 'class' already kind)
    j = pos + 1
    while j < len(toks) and toks[j][0] == "id" and toks[j][1] in QUALIFIERS:
        j += 1
    # for "object : Type" the name is the object's own name right after 'object'
    if j < len(toks) and toks[j][0] == "id":
        name = toks[j][1]
    else:
        return kind, None
    return kind, name


def parse_file(path: Path, rel: str):
    text = path.read_text(encoding="utf-8", errors="replace")
    toks = tokenize(text)

    decls = []
    type_stack = []  # list of (depth, decl) for containers
    pending_kdoc = ""

    i = 0
    n = len(toks)
    brace_depth = 0
    while i < n:
        kind_t, val, start, line = toks[i]

        if kind_t == "kdoc":
            pending_kdoc = clean_kdoc(val)
            i += 1
            continue

        if kind_t == "id" and val in KIND_KEYWORDS:
            # Skip 'fun' as part of a lambda-type `() -> Unit` is not present; but skip
            # 'object'/'class' after '.' or ':' contexts that aren't declarations is rare.
            # Skip if preceded by '.' (e.g. SomeClass.Companion is handled by object detection below).
            prev = toks[i - 1][1] if i > 0 else ""
            if prev in (".", ":"):
                i += 1
                continue

            kind, name = detect_kind_and_name(toks, i)
            if name is None:
                i += 1
                continue

            # Build signature: from qualifier run start to terminator.
            head_start = i
            j = i - 1
            while j >= 0 and toks[j][0] == "id" and toks[j][1] in QUALIFIERS:
                head_start = j
                j -= 1
            # include annotations before qualifiers
            k = head_start - 1
            while k >= 0 and toks[k][0] == "@":
                head_start = k
                k -= 1

            sig_start = toks[head_start][2]
            sig_end = _signature_end(toks, i, start)
            sig = text[sig_start:sig_end].strip()
            sig = re.sub(r"\s+", " ", sig)

            # compute depth for this decl
            depth = brace_depth

            decl = {
                "name": name,
                "kind": kind,
                "modifiers": sorted({toks[t][1] for t in range(head_start, i) if toks[t][0] == "id" and toks[t][1] in QUALIFIERS}),
                "signature": sig,
                "kdoc": pending_kdoc,
                "params": extract_params(sig, name) if kind in ("function", "class") else [],
                "supertypes": extract_supertypes(sig, name) if kind in ("class", "interface", "object") else [],
                "depth": depth,
                "parent": None,
                "file": rel,
                "line": line,
                "children": [],
            }
            pending_kdoc = ""

            # assign parent from type_stack
            while type_stack and type_stack[-1][0] >= depth:
                type_stack.pop()
            if type_stack:
                parent = type_stack[-1][1]
                decl["parent"] = parent["name"]
                parent["children"].append(name)

            if kind in ("class", "interface", "object"):
                type_stack.append((depth, decl))

            decls.append(decl)
            i = _skip_declaration(toks, i)
            # advance brace depth over the decl's head? The signature_end consumed up to `{`/`=`/`;`.
            # We need to keep brace tracking correct. Simplest: recompute brace depth by
            # counting tokens between i and sig end is complex; instead we just continue.
            # Brace depth is tracked below via explicit updates.
            continue

        if kind_t in ("{",):
            brace_depth += 1
        elif kind_t == "}":
            brace_depth = max(0, brace_depth - 1)
            # pop containers closed at this depth
            while type_stack and type_stack[-1][0] >= brace_depth:
                type_stack.pop()
        elif kind_t == ";":
            pending_kdoc = ""

        i += 1

    return decls


def _signature_end(toks, pos, _start):
    """Return char offset where the declaration header ends.

    Stops at `{`, `=`, `;`, or `}` at depth 0 — and at a new declaration keyword
    (`fun`/`val`/`var`/`class`/`interface`/`object`/`typealias`) at depth 0.
    """
    paren = bracket = 0
    past_name = False
    stop_kw = set(KIND_KEYWORDS) | QUALIFIERS
    for k in range(pos, len(toks)):
        kind_t, val, start, line = toks[k]
        if kind_t == "id":
            past_name = True
            if paren == 0 and bracket == 0 and k > pos and val in stop_kw:
                # a new declaration keyword/qualifier after we've left the head -> stop
                return start
        elif kind_t == "kdoc":
            # a KDoc block always precedes a new declaration -> stop
            return start
        elif kind_t == "(":
            paren += 1
        elif kind_t == ")":
            paren = max(0, paren - 1)
        elif kind_t == "[":
            bracket += 1
        elif kind_t == "]":
            bracket = max(0, bracket - 1)
        elif kind_t == "{":
            if paren == 0 and bracket == 0:
                return start
        elif kind_t == "=":
            if paren == 0 and bracket == 0:
                return start
        elif kind_t == ";":
            if paren == 0 and bracket == 0:
                return start
        elif kind_t == "}":
            if paren == 0:
                return start
    return toks[-1][2] + len(toks[-1][1])


def _skip_declaration(toks, pos):
    """Advance past the declaration header so the main loop doesn't re-scan it.
    We move until the terminator token (`{`, `=`, `;`, `}`) without consuming it."""
    paren = bracket = 0
    k = pos
    while k < len(toks):
        kind_t, val = toks[k][0], toks[k][1]
        if kind_t == "(":
            paren += 1
        elif kind_t == ")":
            paren = max(0, paren - 1)
        elif kind_t == "[":
            bracket += 1
        elif kind_t == "]":
            bracket = max(0, bracket - 1)
        elif kind_t in ("{", "=", ";", "}"):
            if paren == 0 and bracket == 0:
                return k  # stop before terminator
        k += 1
    return len(toks)


# ---------------------------------------------------------------------------
# Search
# ---------------------------------------------------------------------------

def parse_includes(settings: Path):
    text = settings.read_text(encoding="utf-8", errors="replace")
    return re.findall(r'include\("([^"]+)"\)', text)


def parse_toml_sections(toml: Path):
    """Return dict {section: {key: raw_value}} for [versions]/[libraries]/[plugins]."""
    text = toml.read_text(encoding="utf-8", errors="replace")
    sections = {}
    cur = None
    for line in text.splitlines():
        s = line.strip()
        if s.startswith("[") and s.endswith("]") and not s.startswith("[["):
            cur = s[1:-1]
            sections.setdefault(cur, {})
            continue
        if cur and "=" in s and not s.startswith("#"):
            k, v = s.split("=", 1)
            sections[cur][k.strip()] = v.strip().strip('"')
    return sections


def should_include(rel: Path, exts):
    name = rel.name
    if name == "libs.versions.toml":
        return "toml" in exts
    return rel.suffix.lstrip(".") in exts


def iter_code_files(root: Path, exts):
    for p in root.rglob("*"):
        if not p.is_file():
            continue
        if any(ig in p.parts for ig in IGNORE_DIRS):
            continue
        rel = p.relative_to(root)
        if should_include(rel, exts):
            yield p, str(rel)


def build_module_filter(modules):
    norm = [m.strip().lstrip(":").replace(":", "/").strip("/") for m in modules if m.strip()]
    if not norm:
        return lambda rel: True

    def keep(rel):
        parts = list(rel.parts)
        for m in norm:
            mparts = m.split("/")
            if mparts == parts[:len(mparts)]:
                return True
            if len(mparts) == 1 and mparts[0] in parts:
                return True
        return False
    return keep


def matches_type(decl, requested):
    if not requested or "all" in requested:
        return True
    kind = decl["kind"]
    mods = set(decl["modifiers"])
    for r in requested:
        r = TYPE_ALIASES.get(r.lower(), r.lower())
        if r == kind:
            return True
        if r == "enum" and kind == "class" and "enum" in mods:
            return True
        if r == "annotation" and kind == "class" and "annotation" in mods:
            return True
        if r == "sealed" and kind in ("class", "interface") and "sealed" in mods:
            return True
        if r == "data" and kind in ("class", "object") and "data" in mods:
            return True
    return False


def matches_query(decl, terms, regex, in_body):
    if not terms:
        return True
    name = decl["name"]
    body = f"{decl['kdoc']} {decl['signature']}"
    for t in terms:
        if regex:
            try:
                if re.search(t, name, re.I):
                    continue
                if in_body and re.search(t, body, re.I):
                    continue
            except re.error:
                return False
            return False
        else:
            if t.lower() in name.lower():
                continue
            if in_body and t.lower() in body.lower():
                continue
            return False
    return True


def find_references(root, name, exts):
    refs = []
    pat = re.compile(r"\b" + re.escape(name) + r"\b")
    for p, rel in iter_code_files(root, exts):
        text = p.read_text(encoding="utf-8", errors="replace")
        c = len(pat.findall(text))
        if c:
            refs.append((rel, c))
    refs.sort(key=lambda x: -x[1])
    return refs


# ---------------------------------------------------------------------------
# Rendering
# ---------------------------------------------------------------------------

def collect_descendants(decl_map, name, max_levels):
    """Return list of descendant decls within max_levels below the decl (levels >= 1)."""
    out = []
    frontier = [(name, 1)]
    seen = {name}
    while frontier:
        cur, lvl = frontier.pop(0)
        if lvl > max_levels:
            continue
        d = decl_map.get(cur)
        if not d:
            continue
        for child in d["children"]:
            if child in seen:
                continue
            seen.add(child)
            out.append((decl_map.get(child), lvl))
            frontier.append((child, lvl + 1))
    return out


def render_decl(d, opts, decl_map):
    lines = []
    mods = [d["kind"]] + sorted(d["modifiers"])
    lines.append(f"\u25b8 {d['name']}  [{', '.join(mods)}]  {d['file']}:{d['line']}")
    if opts.kdocs and d.get("kdoc"):
        lines.append(f"  kdoc: {d['kdoc']}")
    lines.append(f"  sig: {d['signature']}")
    if d.get("params"):
        lines.append("  params:")
        for p in d["params"]:
            lines.append(f"    - {p['name']}: {p['type']}")
    if d.get("supertypes"):
        lines.append(f"  super: {', '.join(d['supertypes'])}")

    if opts.relations:
        if d.get("parent"):
            lines.append(f"  parent: {d['parent']}")
        refs = opts.ref_cache.get(d["name"])
        if refs is None:
            refs = find_references(opts.root, d["name"], opts.exts)
            opts.ref_cache[d["name"]] = refs
        # exclude the declaration's own file (it always contains the name)
        refs = [r for r in refs if r[0] != d["file"]]
        if refs:
            shown = "; ".join(f"{r} (x{c})" for r, c in refs[:5])
            lines.append(f"  refs: {shown}")

    depth = opts.kdocs_depth
    if opts.kdocs and depth != 1 and d["kind"] in ("class", "interface", "object"):
        levels = None if depth == -1 else max(0, depth - 1)
        if levels is None or levels >= 1:
            desc = collect_descendants(decl_map, d["name"], levels if levels is not None else 10**6)
            if desc:
                lines.append("  members:")
                for cd, lvl in desc:
                    if not cd:
                        continue
                    kd = f" — {cd['kdoc']}" if (opts.kdocs and cd.get("kdoc")) else ""
                    lines.append(f"    {'  '*(lvl-1)}- {cd['name']} [{cd['kind']}]{kd}")
    return lines


# ---------------------------------------------------------------------------
# Commands
# ---------------------------------------------------------------------------

def cmd_clear_codes(versions):
    import shutil
    if not CODE_DIR.exists():
        print("No cached source.", file=sys.stderr)
        return 0
    if not versions:
        versions = [p.name for p in CODE_DIR.iterdir() if p.is_dir()]
    removed = 0
    for v in versions:
        p = CODE_DIR / v
        if p.exists():
            shutil.rmtree(p)
            print(f"Removed {p}")
            removed += 1
        else:
            print(f"Not found: {v}", file=sys.stderr)
    if removed == 0:
        print("Nothing removed.")
    return 0


def find_inheritors(decls, name):
    """Return decls whose supertypes (or parent chain) reference `name`."""
    out = []
    for d in decls:
        if d["name"] == name:
            continue
        super_str = " ".join(d.get("supertypes", []))
        if name in super_str:
            out.append(d)
        # also match generic form like MviViewModel<State, Action, Event>
        elif re.search(r"\b" + re.escape(name) + r"\b", super_str):
            out.append(d)
    return out


def cmd_search(root, opts, decls, decl_map):
    terms = opts.query.split() if opts.query else []
    requested = opts.types

    results = []
    for d in decls:
        if not matches_type(d, requested):
            continue
        if not matches_query(d, terms, opts.regex, opts.in_body):
            continue
        results.append(d)
        if len(results) >= opts.max:
            break

    # Inheritor filtering / expansion.
    if opts.only_inheritor or opts.include_inheritor:
        matched_names = {d["name"] for d in results}
        inheritors = []
        for name in matched_names:
            for inh in find_inheritors(decls, name):
                if inh not in inheritors:
                    inheritors.append(inh)

        if opts.only_inheritor:
            results = inheritors
        else:  # include_inheritor
            seen = {id(r) for r in results}
            for inh in inheritors:
                if id(inh) not in seen:
                    results.append(inh)
                    seen.add(id(inh))

    if opts.json:
        print(json.dumps(results, indent=2))
        return 0

    if not results:
        print(f"No matches for: {opts.query or requested}", file=sys.stderr)
        return 1

    print(f"{len(results)} result(s)\n")
    for d in results:
        for ln in render_decl(d, opts, decl_map):
            print(ln)
        print()
    return 0


def main():
    ap = argparse.ArgumentParser(description="Download & search KMP Starter Template source.")
    ap.add_argument("query", nargs="?", help="search terms (match declaration name)")
    ap.add_argument("--types", help="comma/space list: class, interface, object, function, property, typealias, enum, annotation, sealed, data, all")
    ap.add_argument("--kdocs", action="store_true", help="include KDoc text")
    ap.add_argument("--kdocs-depth", type=int, default=2, help="kdoc depth 1..5 or -1 (default 2)")
    ap.add_argument("--relations", action="store_true", help="parent + supertypes + references")
    ap.add_argument("--include-inheritor", action="store_true", help="include classes inheriting the matched type")
    ap.add_argument("--only-inheritor", action="store_true", help="only return classes inheriting the matched type")
    ap.add_argument("--modules", help="restrict to modules/dirs (comma list)")
    ap.add_argument("--extensions", default="kt,kts,toml", help="file extensions (default kt,kts,toml)")
    ap.add_argument("--version", help="source version/tag (default main)")
    ap.add_argument("--list-versions", action="store_true", help="list remote tags")
    ap.add_argument("--clear-codes", nargs="*", metavar="VERSION", help="delete cached source; optional version list (none = all)")
    ap.add_argument("--source", help="search a local source tree instead of downloading")
    ap.add_argument("--list-modules", action="store_true", help="list modules from settings.gradle.kts")
    ap.add_argument("--get-version", metavar="KEY", help="version from libs.versions.toml")
    ap.add_argument("--get-library", metavar="KEY", help="library from libs.versions.toml")
    ap.add_argument("--get-plugin", metavar="KEY", help="plugin from libs.versions.toml")
    ap.add_argument("--list-versions-toml", action="store_true", help="list [versions]")
    ap.add_argument("--list-libraries", action="store_true", help="list [libraries]")
    ap.add_argument("--list-plugins", action="store_true", help="list [plugins]")
    ap.add_argument("--max", type=int, default=25, help="max results (default 25)")
    ap.add_argument("--regex", action="store_true", help="treat query as regex")
    ap.add_argument("--in-body", action="store_true", help="also match kdoc/signature")
    ap.add_argument("--json", action="store_true", help="machine-readable output")
    args = ap.parse_args()

    if args.list_versions:
        for v in list_remote_versions():
            print(v)
        return 0

    if args.clear_codes is not None:
        return cmd_clear_codes(args.clear_codes)

    if args.kdocs_depth < -1 or args.kdocs_depth > 5:
        print("--kdocs-depth must be 1..5 or -1", file=sys.stderr)
        return 2

    root = source_root(args)

    if args.list_modules:
        settings = root / "settings.gradle.kts"
        if not settings.exists():
            print(f"No settings.gradle.kts in {root}", file=sys.stderr)
            return 1
        for m in parse_includes(settings):
            print(m)
        return 0

    toml = root / "gradle" / "libs.versions.toml"
    if any([args.get_version, args.get_library, args.get_plugin,
            args.list_versions_toml, args.list_libraries, args.list_plugins]):
        sections = parse_toml_sections(toml) if toml.exists() else {}
        versions = sections.get("versions", {})
        libraries = sections.get("libraries", {})
        plugins = sections.get("plugins", {})
        if args.get_version:
            print(versions.get(args.get_version, f"<not found: {args.get_version}>"))
        if args.get_library:
            print(libraries.get(args.get_library, f"<not found: {args.get_library}>"))
        if args.get_plugin:
            print(plugins.get(args.get_plugin, f"<not found: {args.get_plugin}>"))
        if args.list_versions_toml:
            for k, v in versions.items():
                print(f"{k} = {v}")
        if args.list_libraries:
            for k, v in libraries.items():
                print(f"{k} = {v}")
        if args.list_plugins:
            for k, v in plugins.items():
                print(f"{k} = {v}")
        return 0

    exts = {e.strip().lower().lstrip(".") for e in args.extensions.split(",") if e.strip()}
    exts.discard("libs.versions")  # normalize any accidental full name
    if not exts:
        exts = {"kt", "kts", "toml"}

    module_keep = build_module_filter(args.modules.split(",") if args.modules else [])

    decls = []
    decl_map = {}
    for p, rel in iter_code_files(root, exts):
        if not module_keep(Path(rel)):
            continue
        if p.name == "libs.versions.toml" or p.suffix not in (".kt", ".kts"):
            continue
        for d in parse_file(p, rel):
            decls.append(d)
            decl_map.setdefault(d["name"], d)

    opts = args
    opts.ref_cache = {}
    opts.types = [t.strip() for t in re.split(r"[,\s]+", (args.types or "").strip()) if t.strip()]
    opts.root = root
    opts.exts = exts

    return cmd_search(root, opts, decls, decl_map)


if __name__ == "__main__":
    sys.exit(main())
