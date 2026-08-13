"""Tests for scan-project.py — pure functions and full scan on a fixture tree."""

import json
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import _helpers

sp = _helpers.import_script("scan-project")


def make_fixture_project(root: Path) -> Path:
    """Build a minimal project resembling a generated Starter app."""
    (root / "settings.gradle.kts").write_text(
        'rootProject.name = "MyApp"\n'
        'include(":composeApp")\n'
        'include(":starter:core")\n'
        'include(":starter:utils")\n'
        'include(":features:navigation")\n'
        'include(":features:core:domain")\n'
        'include(":features:core:data")\n'
        'include(":features:core:presentation")\n'
        'include(":features:purchases:data")\n',
        encoding="utf-8",
    )
    g = root / "gradle"
    g.mkdir()
    (g / "libs.versions.toml").write_text(
        "[versions]\n"
        'kotlin = "2.4.0"\n'
        'koin = "4.2.2"\n'
        'starter = "0.5.7"\n'
        "\n"
        "[libraries]\n"
        'starter-core = { module = "io.github.devatrii:starter-core", version.ref = "starter" }\n'
        'koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }\n',
        encoding="utf-8",
    )
    app = root / "composeApp"
    (app).mkdir()
    (app / "build.gradle.kts").write_text(
        'kotlin {\n  android { namespace = "com.example.myapp" }\n}\n',
        encoding="utf-8",
    )
    nav = app / "src" / "commonMain" / "kotlin" / "com" / "example" / "myapp" / "navigation"
    nav.mkdir(parents=True)
    (nav / "AppScreens.kt").write_text(
        "import androidx.navigation3.runtime.NavKey\n"
        "@Serializable\n"
        "sealed class AppScreens : NavKey {\n"
        "  data object Welcome : AppScreens()\n"
        "  data object Splash : AppScreens()\n"
        "}\n",
        encoding="utf-8",
    )
    di = app / "src" / "commonMain" / "kotlin" / "com" / "example" / "myapp" / "di"
    di.mkdir(parents=True)
    (di / "InitKoin.kt").write_text(
        "import org.koin.core.context.startKoin\n"
        "fun initKoin() { startKoin { modules(coreDomainModule, utilsModule, purchasesDataModule) } }\n",
        encoding="utf-8",
    )
    return root


class TestParseIncludes(unittest.TestCase):
    def test_extracts(self):
        with tempfile.TemporaryDirectory() as td:
            p = Path(td) / "settings.gradle.kts"
            p.write_text('include(":a:b")\ninclude(":c")\n', encoding="utf-8")
            self.assertEqual(sp.parse_includes(p), [":a:b", ":c"])


class TestParseTomlVersions(unittest.TestCase):
    def test_versions(self):
        with tempfile.TemporaryDirectory() as td:
            p = Path(td) / "v.toml"
            p.write_text('[versions]\nfoo = "1.0"\n# bar = "2.0"\n[libraries]\nx = "y"\n', encoding="utf-8")
            self.assertEqual(sp.parse_toml_versions(p), {"foo": "1.0"})


class TestParseLibraries(unittest.TestCase):
    def test_libraries(self):
        with tempfile.TemporaryDirectory() as td:
            p = Path(td) / "v.toml"
            p.write_text(
                '[libraries]\n'
                'starter-core = { module = "io.github.devatrii:starter-core", version.ref = "starter" }\n',
                encoding="utf-8",
            )
            self.assertEqual(sp.parse_libraries(p), {"starter-core": "io.github.devatrii:starter-core"})


class TestGroupFeatures(unittest.TestCase):
    def test_grouping(self):
        mods = [":features:core:domain", ":features:core:data", ":features:analytics:data", ":features:database"]
        feats = sp.group_features(mods)
        self.assertEqual(sorted(feats["core"]), ["data", "domain"])
        self.assertEqual(feats["analytics"], ["data"])
        self.assertEqual(feats["database"], ["(single)"])


class TestSlugify(unittest.TestCase):
    def test_slug(self):
        self.assertEqual(sp.slugify("My App!"), "my-app")
        self.assertEqual(sp.slugify("Com.Example.MyApp"), "com-example-myapp")


class TestScanProject(unittest.TestCase):
    def test_full_scan(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture_project(Path(td))
            data = sp.scan_project(root)

            self.assertEqual(data["project"], "MyApp")
            self.assertEqual(data["namespace"], "com.example.myapp")

            self.assertEqual(
                data["modules"]["starter"], [":starter:core", ":starter:utils"]
            )
            self.assertIn(":features:core:domain", data["modules"]["features"])
            self.assertIn(":composeApp", data["modules"]["other"])

            self.assertEqual(
                sorted(data["feature_slices"]["core"]), ["data", "domain", "presentation"]
            )

            screens = {n["screen"] for n in data["navigation"]}
            self.assertEqual(screens, {"Welcome", "Splash"})

            self.assertIn("coreDomainModule", data["koin_modules"])
            self.assertIn("utilsModule", data["koin_modules"])
            self.assertIn("purchasesDataModule", data["koin_modules"])

            self.assertEqual(data["versions"]["kotlin"], "2.4.0")
            self.assertEqual(data["versions"]["starter"], "0.5.7")
            self.assertEqual(
                data["libraries"]["starter-core"], "io.github.devatrii:starter-core"
            )

    def test_empty_project(self):
        with tempfile.TemporaryDirectory() as td:
            root = Path(td)
            data = sp.scan_project(root)
            self.assertEqual(data["modules"]["starter"], [])
            self.assertEqual(data["feature_slices"], {})
            self.assertEqual(data["navigation"], [])


class TestRenderMarkdown(unittest.TestCase):
    def test_render(self):
        with tempfile.TemporaryDirectory() as td:
            root = make_fixture_project(Path(td))
            data = sp.scan_project(root)
            md = sp.render_markdown(data)
            self.assertIn("# Project Structure — MyApp", md)
            self.assertIn("## Modules", md)
            self.assertIn("## Navigation", md)
            self.assertIn("Welcome", md)
            self.assertIn("## Koin modules", md)
            self.assertIn("## Key versions", md)


if __name__ == "__main__":
    unittest.main()
