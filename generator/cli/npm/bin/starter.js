#!/usr/bin/env node
'use strict';

const { spawnSync } = require('node:child_process');
const path = require('node:path');
const fs = require('node:fs');

const jarPath = path.join(__dirname, '..', 'lib', 'starter-cli.jar');

if (!fs.existsSync(jarPath)) {
  console.error(`CLI jar not found at ${jarPath}. Reinstall @devatrii/starter.`);
  process.exit(1);
}

const javaCmd = process.env.KMP_STARTER_JAVA || 'java';
const args = ['-jar', jarPath, ...process.argv.slice(2)];
const result = spawnSync(javaCmd, args, { stdio: 'inherit' });

if (result.error) {
  console.error('Failed to start Java. Install JDK 17+ and ensure `java` is on your PATH.');
  console.error(result.error.message);
  process.exit(1);
}

process.exit(result.status === null ? 1 : result.status);
