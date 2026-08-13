"""Tests for update-skills.py — version compare, frontmatter, sync (offline)."""

import sys
import tarfile
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import _helpers

us = _helpers.import_script("update-skills")


class TestParseFrontmatter(unittest.TestCase):
    def test_parse(self):
        text = "---\nname: x\nversion: 2\nauthor: DevAtrii\nlicense: MIT\n---\nbody"
        fm = us.parse_frontmatter(text)
        self.assertEqual(fm["name"], "x")
        self.assertEqual(fm["version"], "2")
        self.assertEqual(fm["author"], "DevAtrii")
        self.assertEqual(fm["license"], "MIT")

    def test_no_frontmatter(self):
        self.assertEqual(us.parse_frontmatter("plain text"), {})

    def test_missing_version(self):
        fm = us.parse_frontmatter("---\nname: x\n---\n")
        self.assertNotIn("version", fm)


class TestVersionTuple(unittest.TestCase):
    def test_numeric(self):
        self.assertEqual(us.version_tuple("1.2.3"), (1, 2, 3))

    def test_single(self):
        self.assertEqual(us.version_tuple("1"), (1,))

    def test_none(self):
        self.assertEqual(us.version_tuple(None), ())


class TestCompareVersions(unittest.TestCase):
    def test_equal(self):
        self.assertEqual(us.compare_versions("1", "1"), 0)
        self.assertEqual(us.compare_versions("1.0", "1"), 0)

    def test_less(self):
        self.assertLess(us.compare_versions("1", "2"), 0)
        self.assertLess(us.compare_versions("1.0", "1.1"), 0)

    def test_greater(self):
        self.assertGreater(us.compare_versions("2", "1"), 0)


class TestShouldUpdate(unittest.TestCase):
    def test_no_local(self):
        self.assertTrue(us.should_update(None, "1", force=False))

    def test_same(self):
        self.assertFalse(us.should_update("1", "1", force=False))

    def test_remote_newer(self):
        self.assertTrue(us.should_update("1", "2", force=False))

    def test_remote_older(self):
        self.assertFalse(us.should_update("2", "1", force=False))

    def test_force(self):
        self.assertTrue(us.should_update("2", "1", force=True))

    def test_no_remote_version(self):
        self.assertFalse(us.should_update("1", None, force=False))


class TestArchiveUrl(unittest.TestCase):
    def test_main(self):
        u = us.archive_url("DevAtrii/Kmp-Starter-Template", "main")
        self.assertIn("refs/heads/main", u)

    def test_tag(self):
        u = us.archive_url("DevAtrii/Kmp-Starter-Template", "0.5.7")
        self.assertIn("refs/tags/0.5.7", u)


class TestSyncTree(unittest.TestCase):
    def test_sync_preserves_local(self):
        with tempfile.TemporaryDirectory() as td:
            src = Path(td) / "src"
            dst = Path(td) / "dst"
            (src / "SKILL.md").parent.mkdir(parents=True)
            (src / "SKILL.md").write_text("new", encoding="utf-8")
            (src / "ui").mkdir()
            (src / "ui" / "SKILL.md").write_text("new ui", encoding="utf-8")

            # pre-existing local-only file
            (dst / ".skill-storage").mkdir(parents=True)
            (dst / ".skill-storage" / "keep.txt").write_text("keep", encoding="utf-8")
            (dst / "SKILL.md").write_text("old", encoding="utf-8")

            synced = us.sync_tree(src, dst)
            self.assertIn("SKILL.md", synced)
            self.assertIn("ui/SKILL.md", synced)
            # local preserved
            self.assertEqual((dst / ".skill-storage" / "keep.txt").read_text(), "keep")
            # overwritten
            self.assertEqual((dst / "SKILL.md").read_text(), "new")

    def test_sync_skips_storage(self):
        with tempfile.TemporaryDirectory() as td:
            src = Path(td) / "src"
            dst = Path(td) / "dst"
            (src / ".skill-storage" / "x.txt").parent.mkdir(parents=True)
            (src / ".skill-storage" / "x.txt").write_text("x", encoding="utf-8")
            synced = us.sync_tree(src, dst)
            self.assertEqual(synced, [])
            self.assertFalse((dst / ".skill-storage" / "x.txt").exists())


if __name__ == "__main__":
    unittest.main()
