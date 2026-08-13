"""Tests for search-docs.py — pure functions and CLI subcommands (offline)."""

import json
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import _helpers

sd = _helpers.import_script("search-docs")


# Minimal fixture mirroring the real search.json shape.
FIXTURE = {
    "config": {"lang": ["en"], "separator": r"[\s\-_,:!=\[\]()\\\"`/]+|\.(?!\d)"},
    "items": [
        {
            "location": "",
            "level": 1,
            "title": "KMP Starter Template",
            "text": "<p>Boilerplate for KMP.</p>",
            "path": ["Home"],
            "tags": [],
        },
        {
            "location": "getting-started/",
            "level": 1,
            "title": "Getting Started",
            "text": "",
            "path": ["Getting Started"],
            "tags": [],
        },
        {
            "location": "getting-started/#requirements",
            "level": 2,
            "title": "Requirements",
            "text": "<p>Kotlin 2.4.0, Java 17.</p>",
            "path": ["Getting Started"],
            "tags": [],
        },
        {
            "location": "koin/",
            "level": 1,
            "title": "Dependency Injection",
            "text": "",
            "path": ["Fundamentals"],
            "tags": [],
        },
        {
            "location": "koin/#scopes",
            "level": 2,
            "title": "Scopes",
            "text": "<p>Koin scoped dependencies.</p>",
            "path": ["Fundamentals", "Dependency Injection"],
            "tags": [],
        },
        {
            "location": "utils/#fieldstate",
            "level": 2,
            "title": "FieldState",
            "text": "<p>Form field state helper with value &amp; error.</p>",
            "path": ["Utils"],
            "tags": [],
        },
    ],
}


class TestStripHtml(unittest.TestCase):
    def test_strips_tags_and_entities(self):
        self.assertEqual(sd.strip_html("<p>a &amp; b</p>"), "a & b")

    def test_empty(self):
        self.assertEqual(sd.strip_html(""), "")
        self.assertEqual(sd.strip_html(None), "")


class TestUrlFor(unittest.TestCase):
    def test_location(self):
        self.assertEqual(sd.url_for("koin/"), "https://starter.atherio.dev/koin/")

    def test_empty(self):
        self.assertEqual(sd.url_for(""), "https://starter.atherio.dev")


class TestSearch(unittest.TestCase):
    def test_ranked_by_title(self):
        res = sd.search(FIXTURE, "koin", 10)
        # "Dependency Injection" title doesn't contain "koin"; section Scopes does via location.
        self.assertTrue(any("koin" in (r["location"] or "") for r in res))

    def test_empty_query_returns_empty(self):
        self.assertEqual(sd.search(FIXTURE, "   ", 10), [])

    def test_max_results(self):
        res = sd.search(FIXTURE, "koin", 1)
        self.assertLessEqual(len(res), 1)

    def test_no_match(self):
        self.assertEqual(sd.search(FIXTURE, "zzzznope", 10), [])


class TestRenderItem(unittest.TestCase):
    def test_snippet(self):
        it = FIXTURE["items"][2]  # getting-started/#requirements
        out = sd.render_item(it)
        self.assertIn("Requirements", out)
        self.assertIn("getting-started/#requirements", out)
        self.assertIn("Kotlin 2.4.0", out)

    def test_full_text(self):
        it = FIXTURE["items"][5]  # utils/#fieldstate
        out = sd.render_item(it, show_full_text=True)
        self.assertIn("value & error", out)


class TestPageRoot(unittest.TestCase):
    def test_strips_anchor(self):
        self.assertEqual(sd.page_root("koin/#scopes"), "koin")

    def test_no_anchor(self):
        self.assertEqual(sd.page_root("koin/"), "koin")

    def test_empty(self):
        self.assertEqual(sd.page_root(""), "")


class TestCmdGet(unittest.TestCase):
    def test_page_with_sections(self):
        import io
        buf = io.StringIO()
        old = sys.stdout
        sys.stdout = buf
        try:
            rc = sd.cmd_get(FIXTURE, "koin/")
        finally:
            sys.stdout = old
        self.assertEqual(rc, 0)
        out = buf.getvalue()
        self.assertIn("Scopes", out)

    def test_single_section(self):
        import io
        buf = io.StringIO()
        old = sys.stdout
        sys.stdout = buf
        try:
            rc = sd.cmd_get(FIXTURE, "koin/#scopes")
        finally:
            sys.stdout = old
        self.assertEqual(rc, 0)
        self.assertIn("Scopes", buf.getvalue())

    def test_missing_returns_1(self):
        import io
        buf = io.StringIO()
        old = sys.stderr
        sys.stderr = buf
        try:
            rc = sd.cmd_get(FIXTURE, "does-not-exist/")
        finally:
            sys.stderr = old
        self.assertEqual(rc, 1)


class TestCmdSitemap(unittest.TestCase):
    def _run(self, query=None):
        import io
        buf = io.StringIO()
        old = sys.stdout
        sys.stdout = buf
        try:
            rc = sd.cmd_sitemap(FIXTURE, query)
        finally:
            sys.stdout = old
        return rc, buf.getvalue()

    def test_full(self):
        rc, out = self._run()
        self.assertEqual(rc, 0)
        self.assertIn("Dependency Injection", out)
        self.assertIn("Getting Started", out)

    def test_filter_match(self):
        rc, out = self._run("field")
        self.assertEqual(rc, 0)
        self.assertIn("FieldState", out)
        self.assertNotIn("Requirements", out)

    def test_filter_no_match(self):
        rc, out = self._run("zzzznope")
        self.assertEqual(rc, 1)


class TestMain(unittest.TestCase):
    def test_no_args_prints_help(self):
        import io
        # main() with no query returns 2 and prints help to stderr
        old_argv = sys.argv
        old_stderr = sys.stderr
        sys.stderr = io.StringIO()
        try:
            sys.argv = ["search-docs.py"]
            rc = sd.main()
        finally:
            sys.argv = old_argv
            sys.stderr = old_stderr
        self.assertEqual(rc, 2)


if __name__ == "__main__":
    unittest.main()
