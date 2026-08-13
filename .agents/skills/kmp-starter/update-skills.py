#!/usr/bin/env python3
"""update-skills — update local KMP Starter skills from GitHub.

Fetches the latest skill files from the public repo, compares the parent
SKILL.md `version` field, and updates local files when the remote version
differs. Local-only files (`.skill-storage/`, tests, scripts) are preserved.

Usage:
    python3 update-skills.py                 # check + update if newer
    python3 update-skills.py --check         # report only, don't write
    python3 update-skills.py --force         # update regardless of version
    python3 update-skills.py --version <ref> # pin branch/tag (default main)
    python3 update-skills.py --repo <slug>   # override repo (default DevAtrii/Kmp-Starter-Template)

Stdlib only. Python 3.9+.
"""

import argparse
import json
import shutil
import sys
import tarfile
import tempfile
import urllib.request
from pathlib import Path

DEFAULT_REPO = "DevAtrii/Kmp-Starter-Template"
DEFAULT_REF = "main"
SKILL_REL = ".agents/skills/kmp-starter"

SKILL_DIR = Path(__file__).resolve().parent
PARENT = SKILL_DIR / "SKILL.md"

# Local-only paths that must never be overwritten by a remote sync.
PRESERVE_DIRS = {".skill-storage"}

UA = {"User-Agent": "kmp-starter-skill"}


# ---------------------------------------------------------------------------
# Frontmatter / version
# ---------------------------------------------------------------------------

def parse_frontmatter(text: str) -> dict:
    """Parse a YAML-ish frontmatter block (--- ... ---) into a dict."""
    if not text.startswith("---"):
        return {}
    end = text.find("\n---", 3)
    if end == -1:
        return {}
    block = text[3:end]
    data = {}
    for line in block.splitlines():
        if ":" in line:
            k, v = line.split(":", 1)
            data[k.strip()] = v.strip().strip('"').strip("'")
    return data


def version_tuple(version) -> tuple:
    """Return a comparable tuple for a version string like '1' or '1.2.3'."""
    if version is None:
        return ()
    s = str(version).strip()
    parts = []
    for p in s.split("."):
        p = p.strip()
        try:
            parts.append(int(p))
        except ValueError:
            parts.append(p)
    return tuple(parts)


def compare_versions(a, b) -> int:
    """Compare two versions. Returns -1, 0, or 1."""
    ta, tb = version_tuple(a), version_tuple(b)
    # Pad with 0 for numeric comparison when one is a prefix of the other.
    for i in range(max(len(ta), len(tb))):
        av = ta[i] if i < len(ta) else 0
        bv = tb[i] if i < len(tb) else 0
        # mixed int/str: coerce to str
        if isinstance(av, int) and isinstance(bv, int):
            if av != bv:
                return -1 if av < bv else 1
        else:
            avs, bvs = str(av), str(bv)
            if avs != bvs:
                return -1 if avs < bvs else 1
    return 0


def local_version() -> str:
    if not PARENT.exists():
        return None
    fm = parse_frontmatter(PARENT.read_text(encoding="utf-8", errors="replace"))
    return fm.get("version")


def should_update(local_ver, remote_ver, force: bool) -> bool:
    if force:
        return True
    if local_ver is None:
        # No local version -> assume stale, update.
        return True
    if remote_ver is None:
        return False
    return compare_versions(local_ver, remote_ver) < 0


# ---------------------------------------------------------------------------
# Fetching
# ---------------------------------------------------------------------------

def fetch_bytes(url: str) -> bytes:
    req = urllib.request.Request(url, headers=UA)
    with urllib.request.urlopen(req, timeout=120) as resp:
        return resp.read()


def fetch_text(url: str) -> str:
    return fetch_bytes(url).decode("utf-8", errors="replace")


def raw_url(repo, ref, rel_path) -> str:
    return f"https://raw.githubusercontent.com/{repo}/{ref}/{rel_path}"


def archive_url(repo, ref) -> str:
    # GitHub archive: branches use refs/heads/<branch>, tags use refs/tags/<tag>.
    if ref in ("main", "master", "latest", ""):
        return f"https://github.com/{repo}/archive/refs/heads/{ref or 'main'}.tar.gz"
    if ref.startswith("refs/"):
        return f"https://github.com/{repo}/archive/{ref}.tar.gz"
    return f"https://github.com/{repo}/archive/refs/tags/{ref}.tar.gz"


def fetch_remote_version(repo, ref) -> str:
    url = raw_url(repo, ref, f"{SKILL_REL}/SKILL.md")
    fm = parse_frontmatter(fetch_text(url))
    return fm.get("version")


def download_remote_tree(repo, ref) -> Path:
    """Download + extract the remote skill dir into a temp dir; return its path."""
    tmp = tempfile.TemporaryDirectory()
    td = Path(tmp.name)
    url = archive_url(repo, ref)
    tgz = td / "src.tar.gz"
    tgz.write_bytes(fetch_bytes(url))
    with tarfile.open(tgz, "r:gz") as tf:
        tf.extractall(td)
    tgz.unlink()
    # Find the extracted top-level dir, then the skill subdir.
    roots = [p for p in td.iterdir() if p.is_dir()]
    if not roots:
        raise RuntimeError(f"Archive for {repo}@{ref} had no directory")
    skill_dir = roots[0] / SKILL_REL
    if not skill_dir.is_dir():
        raise RuntimeError(f"Skill dir not found in archive: {SKILL_REL}")
    return skill_dir


# ---------------------------------------------------------------------------
# Syncing
# ---------------------------------------------------------------------------

def sync_tree(src: Path, dst: Path) -> list:
    """Copy files from src into dst (overwrite). Preserve dst/.skill-storage and
    other local-only paths. Return the list of synced relative paths."""
    synced = []
    for p in sorted(src.rglob("*")):
        if not p.is_file():
            continue
        rel = p.relative_to(src)
        if rel.parts and rel.parts[0] in PRESERVE_DIRS:
            continue
        target = dst / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(p, target)
        synced.append(str(rel))
    return synced


# ---------------------------------------------------------------------------
# Commands
# ---------------------------------------------------------------------------

def cmd_check(repo, ref):
    remote_ver = fetch_remote_version(repo, ref)
    local_ver = local_version()
    print(f"local:  {local_ver or '(none)'}")
    print(f"remote: {remote_ver or '(none)'}")
    return should_update(local_ver, remote_ver, force=False)


def cmd_update(repo, ref, force, check_only):
    remote_ver = fetch_remote_version(repo, ref)
    local_ver = local_version()
    needs = should_update(local_ver, remote_ver, force)

    if not needs:
        print(f"Up to date (local {local_ver} == remote {remote_ver}). Nothing to do.")
        return 0

    print(f"Update available: local {local_ver or '(none)'} -> remote {remote_ver}")

    if check_only:
        print("--check: not writing.")
        return 0

    src = download_remote_tree(repo, ref)
    synced = sync_tree(src, SKILL_DIR)
    print(f"Updated {len(synced)} file(s):")
    for s in synced:
        print(f"  {s}")
    return 0


def main():
    ap = argparse.ArgumentParser(description="Update KMP Starter skills from GitHub.")
    ap.add_argument("--check", action="store_true", help="report only, don't write")
    ap.add_argument("--force", action="store_true", help="update regardless of version")
    ap.add_argument("--version", default=DEFAULT_REF, help="branch/tag to fetch (default main)")
    ap.add_argument("--repo", default=DEFAULT_REPO, help="GitHub repo slug")
    args = ap.parse_args()

    return cmd_update(args.repo, args.version, args.force, args.check)


if __name__ == "__main__":
    sys.exit(main())
