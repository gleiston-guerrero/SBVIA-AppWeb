#!/usr/bin/env node
/**
 * Runs N real Lighthouse passes against a URL and stores EVERY
 * run (JSON and HTML) plus an aggregated manifest.json.
 *
 * Why `lhci autorun` is not used: on Windows this process cannot
 * terminate Chrome's children (taskkill is denied by the sandbox), so
 * chrome-launcher fails to delete its temporary profile (rmSync EPERM) AFTER
 * producing the report, and lhci discards the run even though the LHR exists.
 * This script starts its own Chrome with a remote debugging port and connects
 * Lighthouse to that instance, so Lighthouse neither launches nor kills processes.
 *
 * Uso:
 *   node scripts/lighthouse-collect.js <url> <mobile|desktop> <corridas> <dirSalida>
 *
 * It requires the `lighthouse` package to be resolvable. When it is not installed in the
 * project, export NODE_PATH pointing at the node_modules that holds it.
 * Variables opcionales: CHROME_PATH, LH_CHROME_PORT.
 */

const fs = require('fs');
const http = require('http');
const path = require('path');
const { spawn } = require('child_process');

const [url, preset, runsArg, outDirArg] = process.argv.slice(2);

if (!url || (preset !== 'mobile' && preset !== 'desktop')) {
  console.error('Uso: node scripts/lighthouse-collect.js <url> <mobile|desktop> <corridas> <dirSalida>');
  process.exit(2);
}

const runs = Number(runsArg || 3);
const outDir = path.resolve(outDirArg || '.lighthouseci/reports');
fs.mkdirSync(outDir, { recursive: true });

const port = Number(process.env.LH_CHROME_PORT || 9222);
const chromePath = process.env.CHROME_PATH ||
  'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const userDataDir = path.resolve(`.lighthouseci/chrome-profile-${port}`);
fs.mkdirSync(userDataDir, { recursive: true });

// Lighthouse 12 publishes the module as ESM: `require()` returns the namespace,
// so the function lives in `.default`, with a fallback in case that changes.
const lighthouseModule = require('lighthouse');
const lighthouse = lighthouseModule.default || lighthouseModule;
const desktopConfigModule = require('lighthouse/core/config/desktop-config.js');
const desktopConfig = desktopConfigModule.default || desktopConfigModule;

function slug(value) {
  return value.replace(/^https?:\/\//, '').replace(/[^a-z0-9]+/gi, '_').replace(/^_+|_+$/g, '');
}

function stamp() {
  return new Date().toISOString().replace(/\..+/, '').replace(/[-:]/g, '').replace('T', '_');
}

function waitForDebugPort(timeoutMs) {
  const deadline = Date.now() + timeoutMs;
  return new Promise((resolve, reject) => {
    const attempt = () => {
      const req = http.get({ host: '127.0.0.1', port, path: '/json/version' }, res => {
        res.resume();
        resolve();
      });
      req.on('error', () => {
        if (Date.now() > deadline) return reject(new Error('Chrome no abrio el puerto ' + port));
        setTimeout(attempt, 500);
      });
    };
    attempt();
  });
}

(async () => {
  const chrome = spawn(chromePath, [
    '--headless=new',
    `--remote-debugging-port=${port}`,
    `--user-data-dir=${userDataDir}`,
    '--no-first-run',
    '--no-default-browser-check',
    '--disable-gpu',
    'about:blank',
  ], { stdio: 'ignore' });

  chrome.on('error', err => {
    console.error('No se pudo lanzar Chrome:', err.message);
    process.exit(1);
  });

  await waitForDebugPort(30000);
  console.log(`Chrome escuchando en 127.0.0.1:${port}`);

  const manifest = [];
  for (let i = 1; i <= runs; i += 1) {
    const flags = {
      port,
      output: ['json', 'html'],
      logLevel: 'error',
      onlyCategories: ['performance', 'accessibility', 'best-practices', 'seo'],
    };
    const config = preset === 'desktop' ? desktopConfig : undefined;
    const result = await lighthouse(url, flags, config);

    const base = `${slug(url)}-${stamp()}-${preset}-run${i}`;
    const jsonPath = path.join(outDir, `${base}.report.json`);
    const htmlPath = path.join(outDir, `${base}.report.html`);
    fs.writeFileSync(jsonPath, result.report[0]);
    fs.writeFileSync(htmlPath, result.report[1]);

    const summary = {};
    for (const [key, category] of Object.entries(result.lhr.categories)) {
      summary[key] = category.score;
    }
    manifest.push({
      url,
      preset,
      isRepresentativeRun: i === 1,
      jsonPath,
      htmlPath,
      fetchTime: result.lhr.fetchTime,
      lighthouseVersion: result.lhr.lighthouseVersion,
      summary,
    });
    console.log(`run ${i}/${runs} (${preset}) -> performance=${summary.performance} accessibility=${summary.accessibility} best-practices=${summary['best-practices']} seo=${summary.seo}`);
  }

  const manifestPath = path.join(outDir, 'manifest.json');
  fs.writeFileSync(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`);
  console.log('manifest:', manifestPath);

  // Best-effort shutdown: when the environment cannot terminate Chrome, the process
  // stays alive but the reports are already written and the script does not fail.
  try {
    chrome.kill();
  } catch (err) {
    console.warn('No se pudo cerrar Chrome (los informes ya estan guardados):', err.message);
  }
  process.exit(0);
})().catch(err => {
  console.error('ERROR:', err && err.stack ? err.stack : err);
  process.exit(1);
});
