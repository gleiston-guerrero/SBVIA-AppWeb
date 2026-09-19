#!/usr/bin/env node
/**
 * Recorre N corridas reales de Lighthouse contra una URL y guarda TODAS las
 * corridas (JSON + HTML) mas un manifest.json agregado.
 *
 * Por que no se invoca `lhci autorun`: en este entorno Windows el proceso no
 * puede terminar los hijos de Chrome (taskkill denegado por el sandbox), de modo
 * que chrome-launcher falla al borrar su perfil temporal (rmSync EPERM) DESPUES
 * de generar el informe, y lhci descarta la corrida aunque el LHR ya exista.
 * Este script arranca su propio Chrome con puerto de depuracion remota y conecta
 * Lighthouse a esa instancia, de modo que Lighthouse no lanza ni mata procesos.
 *
 * Uso:
 *   node scripts/lighthouse-collect.js <url> <mobile|desktop> <corridas> <dirSalida>
 *
 * Requiere que el paquete `lighthouse` sea resoluble. Si no esta instalado en el
 * proyecto, exportar NODE_PATH apuntando al node_modules que lo contiene.
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

// Lighthouse 12 publica el modulo como ESM: `require()` devuelve el namespace,
// por lo que la funcion esta en `.default` (con fallback por si cambia).
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

  // Cierre best-effort: si el entorno no permite terminar Chrome, el proceso
  // queda vivo pero los informes ya estan escritos y el script no falla.
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
