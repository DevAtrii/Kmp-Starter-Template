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
import sys
import time
import unittest
from pathlib import Path

HERE = Path(__file__).resolve().parent


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


def render_markdown(result, elapsed):
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

    if not args.no_report:
        report = render_markdown(result, elapsed)
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
