#!/usr/bin/env python3
"""scan-project — map a KMP Starter Template project into structure.md + JSON.

Reads a generated project's modules, feature slices, navigation, Koin wiring,
and versions, then writes a concise structure map for the memory system at
{skill}/.skill-storage/{project}/structure.md (see the memory skill).

Project-agnostic: works in ANY project built on the Starter Template — point
--root at the generated project directory, not the template repo itself.

Usage:
    python3 scan-project.py                          # scan cwd, derive slug
    python3 scan-project.py --root /path/to/app      # scan a project dir
    python3 scan-project.py --project my-app         # explicit slug
    python3 scan-project.py --json                   # machine-readable output
    python3 scan-project.py --print                  # print to stdout, no file

No third-party deps (stdlib only). Python 3.8+.
"""

import argparse
import json
import re
import sys
from pathlib import Path

SKILL_DIR = Path(__file__).resolve().parent
STORAGE_DIR = SKILL_DIR / ".skill-storage"

IGNORE_DIRS = {"build", ".gradle", ".idea", ".git", "node_modules", ".kotlin", "buildSrc"}


def find_up(path: Path, name: str) -> Path | None:
    for p in [path, *path.parents]:
        if (p / name).exists():
            return p / name
    return None


def read_text(path: Path) -> str | None:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return None


def parse_includes(settings: Path) -> list[str]:
    """Extract include(":...") module paths from settings.gradle.kts."""
    text = read_text(settings) or ""
    out = []
    for m in re.finditer(r'include\("([^"]+)"\)', text):
        out.append(m.group(1))
    return out


def parse_toml_versions(toml: Path) -> dict[str, str]:
    """Extract [versions] key = value pairs from libs.versions.toml."""
    text = read_text(toml) or ""
    versions: dict[str, str] = {}
    in_section = False
    for line in text.splitlines():
        s = line.strip()
        if s.startswith("["):
            in_section = s.startswith("[versions]")
            continue
        if in_section and "=" in s and not s.startswith("#"):
            key, val = s.split("=", 1)
            versions[key.strip()] = val.strip().strip('"')
    return versions


def parse_libraries(toml: Path) -> dict[str, str]:
    """Extract [libraries] aliases -> module coordinates."""
    text = read_text(toml) or ""
    libs: dict[str, str] = {}
    in_section = False
    for line in text.splitlines():
        s = line.strip()
        if s.startswith("["):
            in_section = s.startswith("[libraries]")
            continue
        if in_section and "=" in s and not s.startswith("#"):
            key, val = s.split("=", 1)
            key = key.strip()
            mod = re.search(r'module\s*=\s*"([^"]+)"', val)
            if mod:
                libs[key] = mod.group(1)
    return libs


def group_features(modules: list[str]) -> dict[str, list[str]]:
    """Group :features:<name>:<layer> modules by feature name."""
    feats: dict[str, list[str]] = {}
    for m in modules:
        parts = m.split(":")
        if len(parts) >= 3 and parts[1] == "features":
            name = parts[2]
            layer = parts[3] if len(parts) > 3 else "(single)"
            feats.setdefault(name, []).append(layer)
    return feats


def source_sets(mod_root: Path) -> dict[str, bool]:
    """Detect which source sets a module has (commonMain/androidMain/iosMain)."""
    sets = {}
    for s in ("commonMain", "androidMain", "iosMain"):
        sets[s] = (mod_root / "src" / s).is_dir()
    return sets


def module_src_dir(root: Path, module: str) -> Path | None:
    rel = Path(*module.split(":"))
    for cand in (root / rel, root / module.replace(":", "/")):
        if cand.is_dir():
            return cand
    return None


def scan_nav_keys(root: Path) -> list[dict]:
    """Find Navigation3 NavKey routes (data object / data class in a sealed NavKey class)."""
    out = []
    seen: set[Path] = set()
    candidates = []
    for pattern in ("**/AppScreens.kt", "**/*Screens.kt", "**/navigation/*.kt"):
        candidates += list(root.glob(pattern))
    for f in candidates:
        if f in seen or any(ig in f.parts for ig in IGNORE_DIRS):
            continue
        seen.add(f)
        text = read_text(f) or ""
        if "NavKey" not in text:
            continue
        for m in re.finditer(r'(?:data\s+object|data\s+class|object)\s+(\w+)\s*(?:\(|:)', text):
            name = m.group(1)
            # skip class declarations, keep screen-ish entries
            if name.lower() in ("appscreens", "navkey"):
                continue
            out.append({"screen": name, "file": str(f.relative_to(root))})
    return out


def scan_koin_modules(root: Path) -> list[str]:
    """Find Koin modules referenced in DI wiring files (InitKoin / *Module / di/*)."""
    mods: set[str] = set()
    for f in root.rglob("*.kt"):
        if any(ig in f.parts for ig in IGNORE_DIRS):
            continue
        name = f.name
        # Only files that plausibly wire DI: InitKoin.kt, *Module.kt, or under a di/ dir.
        if not (name == "InitKoin.kt" or name.endswith("Module.kt") or "di" in f.parts):
            continue
        text = read_text(f) or ""
        if "startKoin" not in text and "initKoin" not in text and "module" not in text.lower():
            continue
        for m in re.finditer(r'\b([A-Za-z_][A-Za-z0-9_]*Module)\b', text):
            mods.add(m.group(1))
    return sorted(mods)


def scan_namespace(root: Path) -> str | None:
    """Detect app namespace from composeApp/build.gradle.kts."""
    for g in root.glob("*/build.gradle.kts"):
        text = read_text(g) or ""
        m = re.search(r'namespace\s*=\s*"([^"]+)"', text)
        if m:
            return m.group(1)
    return None


def scan_project(root: Path) -> dict:
    settings = root / "settings.gradle.kts"
    toml = root / "gradle" / "libs.versions.toml"

    modules = parse_includes(settings) if settings.exists() else []
    versions = parse_toml_versions(toml) if toml.exists() else {}
    libraries = parse_libraries(toml) if toml.exists() else {}
    features = group_features(modules)
    nav = scan_nav_keys(root)
    koin = scan_koin_modules(root)
    namespace = scan_namespace(root)

    root_name = None
    s_text = read_text(settings) or ""
    m = re.search(r'rootProject\.name\s*=\s*"([^"]+)"', s_text)
    if m:
        root_name = m.group(1)

    starter_modules = [m for m in modules if m.startswith(":starter:")]
    feature_modules = [m for m in modules if m.startswith(":features:")]
    other_modules = [m for m in modules if not m.startswith(":starter:") and not m.startswith(":features:")]

    return {
        "project": root_name or root.name,
        "root": str(root),
        "namespace": namespace,
        "modules": {
            "starter": starter_modules,
            "features": feature_modules,
            "other": other_modules,
        },
        "feature_slices": {k: sorted(v) for k, v in features.items()},
        "navigation": nav,
        "koin_modules": koin,
        "versions": versions,
        "libraries": libraries,
    }


def render_markdown(data: dict) -> str:
    lines = []
    lines.append(f"# Project Structure — {data['project']}\n")
    if data.get("namespace"):
        lines.append(f"- Namespace: `{data['namespace']}`")
    lines.append(f"- Root: `{data['root']}`\n")

    lines.append("## Modules\n")
    for label, mods in data["modules"].items():
        if mods:
            lines.append(f"### {label.title()}")
            for m in mods:
                lines.append(f"- `{m}`")
            lines.append("")

    feats = data["feature_slices"]
    if feats:
        lines.append("## Feature slices\n")
        for name, layers in feats.items():
            lines.append(f"- **{name}**: " + ", ".join(f"`{l}`" for l in layers))
        lines.append("")

    nav = data["navigation"]
    if nav:
        lines.append("## Navigation (NavKey routes)\n")
        for n in nav:
            lines.append(f"- `{n['screen']}` — {n['file']}")
        lines.append("")

    koin = data["koin_modules"]
    if koin:
        lines.append("## Koin modules (wired)\n")
        for k in koin:
            lines.append(f"- `{k}`")
        lines.append("")

    versions = data["versions"]
    if versions:
        lines.append("## Key versions\n")
        for key in ("kotlin", "agp", "compose-multiplatform", "koin", "navigation3", "kotlinx-datetime", "room", "starter"):
            if key in versions:
                lines.append(f"- {key}: `{versions[key]}`")
        lines.append("")

    return "\n".join(lines)


def slugify(name: str) -> str:
    return re.sub(r"[^a-z0-9]+", "-", name.lower()).strip("-")


def main():
    ap = argparse.ArgumentParser(description="Map a KMP Starter Template project to structure.md.")
    ap.add_argument("--root", default=".", help="project root (default: cwd)")
    ap.add_argument("--project", help="project slug (default: derived from rootProject.name or folder)")
    ap.add_argument("--json", action="store_true", help="print JSON instead of writing a file")
    ap.add_argument("--print", dest="to_stdout", action="store_true", help="print markdown to stdout")
    args = ap.parse_args()

    root = Path(args.root).resolve()
    if not root.is_dir():
        print(f"Not a directory: {root}", file=sys.stderr)
        return 2

    data = scan_project(root)
    slug = args.project or slugify(data["project"])

    if args.json:
        print(json.dumps(data, indent=2))
        return 0

    md = render_markdown(data)

    if args.to_stdout:
        print(md)
        return 0

    out_dir = STORAGE_DIR / slug
    out_dir.mkdir(parents=True, exist_ok=True)
    out_file = out_dir / "structure.md"
    out_file.write_text(md, encoding="utf-8")

    print(f"Wrote {out_file} ({len(md.splitlines())} lines)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
