"""Tests for search-code.py — parser, search, inheritors, and CLI (offline).

All tests use `--source` against a fixture tree; no network or download.
"""

import json
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import _helpers

sc = _helpers.import_script("search-code")


def make_fixture(root: Path) -> Path:
    """A tiny Kotlin tree exercising the parser."""
    s = root / "starter" / "ui" / "utils" / "src" / "commonMain" / "kotlin" / "com" / "x" / "vm"
    s.mkdir(parents=True)

    (s / "MviViewModel.kt").write_text(
        "/**\n"
        " * A base ViewModel for MVI.\n"
        " * @param STATE state type\n"
        " */\n"
        "abstract class MviViewModel<STATE, ACTIONS, EVENTS>(\n"
        "    stateTimeoutMillis: Long = 5000L,\n"
        ") : ViewModel() {\n"
        "    /** The initial state. */\n"
        "    abstract val initialState: STATE\n"
        "    val state: String = \"x\"\n"
        "    /** Send an action. */\n"
        "    abstract fun onAction(action: ACTIONS)\n"
        "    fun helper() {}\n"
        "}\n"
        "sealed class MviActions\n",
        encoding="utf-8",
    )

    f = root / "features" / "purchases" / "presentation" / "src" / "commonMain" / "kotlin" / "com" / "x" / "p"
    f.mkdir(parents=True)
    (f / "PurchasesViewModel.kt").write_text(
        "class PurchasesViewModel(\n"
        "    private val repo: Repo,\n"
        ") : MviViewModel<PurchasesState, PurchasesActions, PurchasesEvents>() {\n"
        "    override val initialState = PurchasesState()\n"
        "    override fun onAction(action: PurchasesActions) {}\n"
        "}\n",
        encoding="utf-8",
    )

    g = root / "gradle"
    g.mkdir()
    (g / "libs.versions.toml").write_text(
        "[versions]\nkoin = \"4.2.2\"\nstarter = \"0.5.7\"\n\n"
        "[libraries]\nstarter-core = { module = \"io.github.devatrii:starter-core\", version.ref = \"starter\" }\n\n"
        "[plugins]\nkotlin-multiplatform = { id = \"org.jetbrains.kotlin.multiplatform\", version.ref = \"kotlin\" }\n",
        encoding="utf-8",
    )
    (root / "settings.gradle.kts").write_text(
        'include(":starter:ui:utils")\ninclude(":features:purchases:presentation")\n',
        encoding="utf-8",
    )
    return root


class Args:
    def __init__(self, **kw):
        self.query = None
        self.types = []
        self.kdocs = False
        self.kdocs_depth = 2
        self.relations = False
        self.include_inheritor = False
        self.only_inheritor = False
        self.max = 25
        self.regex = False
        self.in_body = False
        self.ref_cache = {}
        for k, v in kw.items():
            setattr(self, k, v)


class TestTokenize(unittest.TestCase):
    def test_kdoc_token(self):
        toks = sc.tokenize("/** hi */\nfun x() {}")
        kinds = [t[0] for t in toks]
        self.assertIn("kdoc", kinds)
        self.assertIn("fun", [t[1] for t in toks])


class TestCleanKdoc(unittest.TestCase):
    def test_clean(self):
        self.assertEqual(sc.clean_kdoc(" * hello\n * world\n * @param x y\n"), "hello world")


class TestParseParam(unittest.TestCase):
    def test_simple(self):
        self.assertEqual(sc.parse_param("name: String"), {"name": "name", "type": "String"})

    def test_modifier(self):
        r = sc.parse_param("private val repo: Repo")
        self.assertEqual(r["name"], "repo")
        self.assertEqual(r["type"], "Repo")

    def test_no_type(self):
        self.assertEqual(sc.parse_param("foo"), {"name": "foo", "type": ""})


class TestExtractParams(unittest.TestCase):
    def test_params(self):
        sig = "class Foo(a: Int, b: String)"
        p = sc.extract_params(sig, "Foo")
        self.assertEqual(len(p), 2)
        self.assertEqual(p[0]["name"], "a")


class TestExtractSupertypes(unittest.TestCase):
    def test_super(self):
        sig = "class Foo : MviViewModel<A, B, C>()"
        sups = sc.extract_supertypes(sig, "Foo")
        self.assertTrue(any("MviViewModel" in s for s in sups))


class TestParseFile(unittest.TestCase):
    def test_parses_decls(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            f = root / "starter" / "ui" / "utils" / "src" / "commonMain" / "kotlin" / "com" / "x" / "vm" / "MviViewModel.kt"
            decls = sc.parse_file(f, "rel.kt")
            names = {d["name"] for d in decls}
            self.assertIn("MviViewModel", names)
            self.assertIn("onAction", names)
            self.assertIn("initialState", names)
            self.assertIn("MviActions", names)

            mv = next(d for d in decls if d["name"] == "MviViewModel")
            self.assertEqual(mv["kind"], "class")
            self.assertTrue(mv["kdoc"].startswith("A base ViewModel"))
            self.assertTrue(any("ViewModel" in s for s in mv["supertypes"]))
            # children of the class
            self.assertIn("onAction", mv["children"])

    def test_parent_assignment(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            f = root / "starter" / "ui" / "utils" / "src" / "commonMain" / "kotlin" / "com" / "x" / "vm" / "MviViewModel.kt"
            decls = sc.parse_file(f, "rel.kt")
            on_action = next(d for d in decls if d["name"] == "onAction")
            self.assertEqual(on_action["parent"], "MviViewModel")


class TestParseTomlSections(unittest.TestCase):
    def test_sections(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            secs = sc.parse_toml_sections(root / "gradle" / "libs.versions.toml")
            self.assertEqual(secs["versions"]["koin"], "4.2.2")
            self.assertIn("starter-core", secs["libraries"])
            self.assertIn("kotlin-multiplatform", secs["plugins"])


class TestParseIncludes(unittest.TestCase):
    def test_includes(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            mods = sc.parse_includes(root / "settings.gradle.kts")
            self.assertIn(":starter:ui:utils", mods)


class TestResolveArchiveRef(unittest.TestCase):
    def test_main(self):
        self.assertEqual(sc.resolve_archive_ref("main"), "refs/heads/main")
        self.assertEqual(sc.resolve_archive_ref("latest"), "refs/heads/main")
        self.assertEqual(sc.resolve_archive_ref(""), "refs/heads/main")

    def test_tag(self):
        self.assertEqual(sc.resolve_archive_ref("0.5.7"), "refs/tags/0.5.7")


class TestBuildModuleFilter(unittest.TestCase):
    def test_filter(self):
        keep = sc.build_module_filter(["starter:ui:utils"])
        self.assertTrue(keep(Path("starter/ui/utils/foo.kt")))
        self.assertFalse(keep(Path("features/purchases/foo.kt")))

    def test_empty_is_all(self):
        keep = sc.build_module_filter([])
        self.assertTrue(keep(Path("anything/foo.kt")))

    def test_single_part(self):
        keep = sc.build_module_filter(["features"])
        self.assertTrue(keep(Path("features/x/y.kt")))


class TestMatchesType(unittest.TestCase):
    def _d(self, kind, mods=()):
        return {"kind": kind, "modifiers": set(mods)}

    def test_all(self):
        self.assertTrue(sc.matches_type(self._d("class"), ["all"]))

    def test_direct(self):
        self.assertTrue(sc.matches_type(self._d("class"), ["class"]))
        self.assertFalse(sc.matches_type(self._d("class"), ["function"]))

    def test_enum_alias(self):
        self.assertTrue(sc.matches_type(self._d("class", ["enum"]), ["enum"]))

    def test_sealed_alias(self):
        self.assertTrue(sc.matches_type(self._d("class", ["sealed"]), ["sealed"]))

    def test_data_alias(self):
        self.assertTrue(sc.matches_type(self._d("class", ["data"]), ["data"]))


class TestFindInheritors(unittest.TestCase):
    def test_finds_subclass(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            decls = []
            for p in root.rglob("*.kt"):
                for d in sc.parse_file(p, str(p.relative_to(root))):
                    decls.append(d)
            inh = sc.find_inheritors(decls, "MviViewModel")
            names = {d["name"] for d in inh}
            self.assertIn("PurchasesViewModel", names)


class TestFindReferences(unittest.TestCase):
    def test_refs(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            refs = sc.find_references(root, "MviViewModel", {"kt"})
            self.assertTrue(any("PurchasesViewModel" in r for r, _ in refs))


class TestClearCodes(unittest.TestCase):
    def test_nothing(self):
        # CODE_DIR is module-level; run against empty temp by monkeypatching CODE_DIR
        import tempfile
        old = sc.CODE_DIR
        with tempfile.TemporaryDirectory() as td:
            sc.CODE_DIR = Path(td)
            try:
                rc = sc.cmd_clear_codes([])
                self.assertEqual(rc, 0)
            finally:
                sc.CODE_DIR = old


class TestShouldSkipDir(unittest.TestCase):
    def test_skill_dir_skipped(self):
        self.assertTrue(sc.should_skip_dir((".agents", "skills", "kmp-starter", "x.kt")))

    def test_other_dir_not_skipped(self):
        self.assertFalse(sc.should_skip_dir(("features", "x.kt")))

    def test_partial_not_skipped(self):
        self.assertFalse(sc.should_skip_dir((".agents", "x.kt")))


class TestDirSearch(unittest.TestCase):
    def test_dir_searches_local(self):
        # --dir scans a local tree; ensure skill dir is ignored but app code found
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture(Path(td))
            # simulate a skill dir that should be skipped
            skill = root / ".agents" / "skills" / "kmp-starter"
            skill.mkdir(parents=True)
            (skill / "Noise.kt").write_text(
                "class ShouldBeSkipped\n", encoding="utf-8",
            )

            # run search via cmd on --dir=root
            class O:
                pass
            opts = Args(query="ShouldBeSkipped", types=["all"], kdocs=False,
                        relations=False, root=root, exts={"kt"}, skip_dirs=True)

            decls, decl_map = [], {}
            for p, rel in sc.iter_code_files(root, {"kt"}, skip_dirs=True):
                for d in sc.parse_file(p, rel):
                    decls.append(d)
                    decl_map.setdefault(d["name"], d)

            names = {d["name"] for d in decls}
            self.assertNotIn("ShouldBeSkipped", names)
            self.assertIn("MviViewModel", names)


class TestSourceRootDir(unittest.TestCase):
    def test_dir_resolves(self):
        class A:
            source = None
            dir = "/tmp/x"
            version = "main"
        r = sc.source_root(A())
        self.assertEqual(str(r), str(Path("/tmp/x").resolve()))

    def test_source_prefers_source(self):
        class A:
            source = "/tmp/src"
            dir = "/tmp/x"
            version = "main"
        r = sc.source_root(A())
        self.assertEqual(str(r), str(Path("/tmp/src").resolve()))


if __name__ == "__main__":
    unittest.main()
