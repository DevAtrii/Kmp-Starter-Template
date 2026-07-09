## Testing
```shell
# 1. Build the npm package
./gradlew :generator:cli:assembleStarterCliNpm

# 2. Optional: inspect tarball contents
./gradlew :generator:cli:starterCliNpmPack
tar -tzf generator/cli/npm-package/devatrii-starter-0.4.7.tgz

# 3. Install globally from the built folder
npm install -g ./generator/cli/npm-package

# 4. Run it (requires JDK 17+ on PATH)
starter --help
starter init --dir . --package com.example.app --mode lib

# Or use npm link while developing
cd generator/cli/npm-package && npm link
starter --help
```

### Running
```shell

./gradlew :generator:cli:jvmRun --console=plain --args="create"

```