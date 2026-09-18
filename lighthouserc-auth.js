const baseUrl = (process.env.LHCI_BASE_URL || 'https://sbvia-frontend.onrender.com').replace(/\/$/, '');
const preset = process.env.SBVIA_LIGHTHOUSE_PRESET || 'desktop';

module.exports = {
  ci: {
    collect: {
      url: [`${baseUrl}/dashboard`],
      puppeteerScript: './scripts/lighthouse-auth.js',
      numberOfRuns: 1,
      settings: { preset: preset },
    },
    upload: {
      target: 'filesystem',
      outputDir: process.env.LHCI_OUTPUT_DIR || '.lighthouseci/reports',
    },
  },
};
