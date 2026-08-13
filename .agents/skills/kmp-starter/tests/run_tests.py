#!/usr/bin/env python3
"""Test runner for the KMP Starter skill scripts.

Discovers and runs all `test_*.py` under this directory, then emits a Markdown
report (report.md) and prints a terminal-friendly summary. Exits non-zero on
failure/error so CI fails correctly.

Usage:
    python3 run_tests.py                 # run all tests, write report.md
    python3 run_tests.py --no-report     # print only, don't write report.md
    python3 run_tests.py --json          # also dump results to report.json

Stdlib only. Python 3.9+.
"""

import argparse
import json
import os
import subprocess
import sys
import tempfile
import time
import unittest
from pathlib import Path

HERE = Path(__file__).resolve().parent
SKILL_DIR = HERE.parent


class Result:
    def __init__(self, id_, kind, message=""):
        self.id = id_
        self.kind = kind  # 'pass' | 'fail' | 'error' | 'skip'
        self.message = message
        self.duration = 0.0


class MarkdownResult(unittest.TestResult):
    def __init__(self):
        super().__init__()
        self.results = []
        self._start = {}

    def startTest(self, test):
        self._start[test.id()] = time.perf_counter()
        super().startTest(test)

    def _record(self, test, kind, message):
        dur = time.perf_counter() - self._start.get(test.id(), time.perf_counter())
        self.results.append(Result(test.id(), kind, message, ))

    def addSuccess(self, test):
        dur = time.perf_counter() - self._start.get(test.id(), time.perf_counter())
        self.results.append(Result(test.id(), "pass"))
        super().addSuccess(test)

    def addFailure(self, test, err):
        self.results.append(Result(test.id(), "fail", self._fmt(err)))

    def addError(self, test, err):
        self.results.append(Result(test.id(), "error", self._fmt(err)))

    def addSkip(self, test, reason):
        self.results.append(Result(test.id(), "skip", reason))

    def addExpectedFailure(self, test, err):
        self.results.append(Result(test.id(), "pass", "(expected failure)"))

    def addUnexpectedSuccess(self, test):
        self.results.append(Result(test.id(), "fail", "unexpected success"))

    def _fmt(self, err):
        import traceback
        return "".join(traceback.format_exception(*err))


def discover():
    loader = unittest.TestLoader()
    suite = loader.discover(str(HERE), pattern="test_*.py")
    return suite


def run(suite):
    result = MarkdownResult()
    start = time.perf_counter()
    suite.run(result)
    elapsed = time.perf_counter() - start
    return result, elapsed


def group_by_class(results):
    classes = {}
    for r in results:
        # id is like "module.ClassName.test_method"
        parts = r.id.split(".")
        if len(parts) >= 2:
            cls = parts[-2]
        else:
            cls = "(top-level)"
        classes.setdefault(cls, []).append(r)
    return classes


def count(results, kind):
    return sum(1 for r in results if r.kind == kind)


# ---------------------------------------------------------------------------
# Script runs — capture real stdout/stderr of each skill script (offline).
# ---------------------------------------------------------------------------

def run_script(script_name, args, cwd=None, timeout=60):
    """Run a skill script as a subprocess, return (returncode, combined_output)."""
    cmd = [sys.executable, str(SKILL_DIR / script_name)] + args
    try:
        proc = subprocess.run(
            cmd, capture_output=True, text=True, timeout=timeout, cwd=cwd,
            env={**os.environ, "PYTHONIOENCODING": "utf-8"},
        )
        out = proc.stdout or ""
        if proc.stderr:
            out += ("\n" if out else "") + "[stderr]\n" + proc.stderr
        return proc.returncode, out.strip()
    except subprocess.TimeoutExpired:
        return -1, "[timeout]"


def _make_fixture_project():
    """Create a tiny Starter-like project tree for search-code/scan-project."""
    td = tempfile.TemporaryDirectory()
    root = Path(td.name)
    (root / "settings.gradle.kts").write_text(
        'rootProject.name = "DemoApp"\n'
        'include(":starter:core")\n'
        'include(":features:purchases:data")\n',
        encoding="utf-8",
    )
    g = root / "gradle"
    g.mkdir()
    (g / "libs.versions.toml").write_text(
        '[versions]\nkoin = "4.2.2"\nstarter = "0.5.7"\n\n'
        '[libraries]\nstarter-core = { module = "io.github.devatrii:starter-core", version.ref = "starter" }\n\n'
        '[plugins]\nkotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }\n',
        encoding="utf-8",
    )
    vm = root / "starter" / "core" / "src" / "commonMain" / "kotlin" / "com" / "x" / "vm"
    vm.mkdir(parents=True)
    (vm / "MviViewModel.kt").write_text(
        "/** A base ViewModel for MVI. */\n"
        "abstract class MviViewModel<STATE, ACTIONS, EVENTS>(\n"
        "    stateTimeoutMillis: Long = 5000L,\n"
        ") : ViewModel() {\n"
        "    /** The initial state. */\n"
        "    abstract val initialState: STATE\n"
        "    /** Send an action. */\n"
        "    abstract fun onAction(action: ACTIONS)\n"
        "}\n",
        encoding="utf-8",
    )
    f = root / "features" / "purchases" / "data" / "src" / "commonMain" / "kotlin" / "com" / "x" / "p"
    f.mkdir(parents=True)
    (f / "PurchasesViewModel.kt").write_text(
        "class PurchasesViewModel : MviViewModel<PurchasesState, PurchasesActions, PurchasesEvents>() {\n"
        "    override val initialState = PurchasesState()\n"
        "    override fun onAction(action: PurchasesActions) {}\n"
        "}\n",
        encoding="utf-8",
    )
    return td, root


def _seed_docs_cache():
    """Seed the real .skill-storage/docs.json with a fixture (fresh, so offline)."""
    cache_dir = SKILL_DIR / ".skill-storage"
    cache_dir.mkdir(parents=True, exist_ok=True)
    cache_file = cache_dir / "docs.json"
    backup = None
    if cache_file.exists():
        backup = cache_file.read_text(encoding="utf-8")
    cache_file.write_text(json.dumps({
        "items": [
            {"location": "koin/", "level": 1, "title": "Dependency Injection",
             "text": "", "path": ["Fundamentals"], "tags": []},
            {"location": "koin/#scopes", "level": 2, "title": "Scopes",
             "text": "<p>Koin scoped deps.</p>", "path": ["Fundamentals"], "tags": []},
            {"location": "utils/#fieldstate", "level": 2, "title": "FieldState",
             "text": "<p>value &amp; error</p>", "path": ["Utils"], "tags": []},
        ]
    }), encoding="utf-8")
    # touch fresh mtime
    os.utime(cache_file, None)
    return backup


def _restore_docs_cache(backup):
    cache_file = SKILL_DIR / ".skill-storage" / "docs.json"
    if backup is None:
        cache_file.unlink(missing_ok=True)
    else:
        cache_file.write_text(backup, encoding="utf-8")


def collect_script_runs():
    """Run each skill script in a safe offline scenario and collect outputs."""
    runs = []

    # search-docs (offline via seeded cache)
    backup = _seed_docs_cache()
    try:
        rc, out = run_script("search-docs.py", ["--sitemap", "koin"])
        runs.append(("search-docs.py --sitemap koin", rc, out))
        rc, out = run_script("search-docs.py", ["--get", "koin/#scopes"])
        runs.append(("search-docs.py --get koin/#scopes", rc, out))
        rc, out = run_script("search-docs.py", ["--json", "field"])
        runs.append(("search-docs.py --json field", rc, out))
    finally:
        _restore_docs_cache(backup)

    # search-code (--source against fixture)
    td, root = _make_fixture_project()
    try:
        rc, out = run_script("search-code.py", ["MviViewModel", "--source", str(root),
                                                "--types", "class", "--kdocs"])
        runs.append(("search-code.py MviViewModel --types class --kdocs", rc, out))
        rc, out = run_script("search-code.py", ["MviViewModel", "--source", str(root),
                                                "--only-inheritor"])
        runs.append(("search-code.py MviViewModel --only-inheritor", rc, out))
        rc, out = run_script("search-code.py", ["--source", str(root), "--get-version", "koin"])
        runs.append(("search-code.py --get-version koin", rc, out))
    finally:
        td.cleanup()

    # scan-project (--print + --json against fixture)
    td, root = _make_fixture_project()
    try:
        rc, out = run_script("scan-project.py", ["--root", str(root), "--print"])
        runs.append(("scan-project.py --print", rc, out))
        rc, out = run_script("scan-project.py", ["--root", str(root), "--json"])
        runs.append(("scan-project.py --json", rc, out))
    finally:
        td.cleanup()

    return runs


def render_markdown(result, elapsed, script_runs=None):
    results = result.results
    total = len(results)
    passed = count(results, "pass")
    failed = count(results, "fail")
    errors = count(results, "error")
    skipped = count(results, "skip")

    lines = []
    lines.append("# Skill Script Test Report\n")

    # Summary
    lines.append("## Summary\n")
    lines.append("| Metric | Value |")
    lines.append("| --- | --- |")
    lines.append(f"| Total tests | {total} |")
    lines.append(f"| Passed | {passed} |")
    lines.append(f"| Failed | {failed} |")
    lines.append(f"| Errors | {errors} |")
    lines.append(f"| Skipped | {skipped} |")
    lines.append(f"| Duration | {elapsed:.2f}s |")
    lines.append("")

    status = "PASS" if (failed + errors) == 0 else "FAIL"
    lines.append(f"**Result: {status}**\n")

    # Per-class breakdown
    lines.append("## Breakdown by class\n")
    lines.append("| Class | Pass | Fail | Error | Skip |")
    lines.append("| --- | --- | --- | --- | --- |")
    for cls, rs in sorted(group_by_class(results).items()):
        p = count(rs, "pass")
        f = count(rs, "fail")
        e = count(rs, "error")
        s = count(rs, "skip")
        lines.append(f"| `{cls}` | {p} | {f} | {e} | {s} |")
    lines.append("")

    # Failures / errors detail
    problems = [r for r in results if r.kind in ("fail", "error")]
    if problems:
        lines.append("## Failures & Errors\n")
        for r in problems:
            lines.append(f"### `{r.id}` ({r.kind})\n")
            lines.append("```text")
            lines.append(r.message.rstrip())
            lines.append("```\n")

    # Script outputs (collapsible)
    runs = script_runs if script_runs is not None else []
    if runs:
        lines.append("## Script Runs\n")
        for name, rc, out in runs:
            status = "ok" if rc == 0 else f"exit {rc}"
            lines.append(f"### `{name}` — {status}\n")
            lines.append("<details>")
            lines.append("<summary>Show output</summary>")
            lines.append("")
            lines.append("```text")
            lines.append(out if out else "(no output)")
            lines.append("```")
            lines.append("")
            lines.append("</details>")
            lines.append("")

    return "\n".join(lines)


def render_terminal(result, elapsed):
    results = result.results
    passed = count(results, "pass")
    failed = count(results, "fail")
    errors = count(results, "error")
    skipped = count(results, "skip")

    lines = []
    lines.append("=" * 60)
    lines.append(f"  {len(results)} tests  |  {passed} passed  |  {failed} failed  |  "
                 f"{errors} errors  |  {skipped} skipped  |  {elapsed:.2f}s")
    lines.append("=" * 60)

    problems = [r for r in results if r.kind in ("fail", "error")]
    if problems:
        lines.append("")
        for r in problems:
            lines.append(f"  [{r.kind.upper()}] {r.id}")
        lines.append("")
        lines.append("  See report.md for full tracebacks.")
    return "\n".join(lines)


def main():
    ap = argparse.ArgumentParser(description="Run skill script tests and emit a Markdown report.")
    ap.add_argument("--no-report", action="store_true", help="don't write report.md")
    ap.add_argument("--json", action="store_true", help="also dump results to report.json")
    args = ap.parse_args()

    suite = discover()
    result, elapsed = run(suite)

    print(render_terminal(result, elapsed))

    script_runs = collect_script_runs()

    if not args.no_report:
        report = render_markdown(result, elapsed, script_runs)
        (HERE / "report.md").write_text(report, encoding="utf-8")
        print(f"\nWrote {HERE / 'report.md'}")

    if args.json:
        data = {
            "total": len(result.results),
            "passed": count(result.results, "pass"),
            "failed": count(result.results, "fail"),
            "errors": count(result.results, "error"),
            "skipped": count(result.results, "skip"),
            "elapsed": elapsed,
            "results": [
                {"id": r.id, "kind": r.kind, "message": r.message[:2000]}
                for r in result.results
            ],
        }
        (HERE / "report.json").write_text(json.dumps(data, indent=2), encoding="utf-8")
        print(f"Wrote {HERE / 'report.json'}")

    # Exit code: fail CI on failures/errors.
    failed = count(result.results, "fail") + count(result.results, "error")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
