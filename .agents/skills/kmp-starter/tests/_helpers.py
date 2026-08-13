"""Shared helpers for importing the stdlib-only skill scripts under test."""

import importlib.util
import sys
from pathlib import Path

SKILL_DIR = Path(__file__).resolve().parent.parent


def import_script(name: str):
    """Import a hyphenated script as a module by file path."""
    path = SKILL_DIR / f"{name}.py"
    spec = importlib.util.spec_from_file_location(name.replace("-", "_"), path)
    mod = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = mod
    spec.loader.exec_module(mod)
    return mod
