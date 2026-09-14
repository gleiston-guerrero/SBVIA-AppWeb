/**
 * Script de Puppeteer para autenticar antes de correr Lighthouse
 */
module.exports = async (browser, context) => {
  const page = await browser.newPage();
  
  // Usar la URL base de lhci o localhost
  const baseUrl = (process.env.LHCI_BASE_URL || 'http://localhost:4200').replace(/\/$/, '');
  
  console.log(`[Lighthouse Auth] Navegando a login...`);
  await page.goto(`${baseUrl}/login`);
  
  // Esperar a que cargue el formulario
  await page.waitForSelector('input[formControlName="correo"]', { timeout: 10000 });
  
  // Extraer credenciales desde variables de entorno inyectadas por el Makefile
  const email = process.env.TEST_USER_EMAIL;
  const password = process.env.TEST_USER_PASSWORD;
  
  if (!email || !password) {
    throw new Error('Las variables TEST_USER_EMAIL o TEST_USER_PASSWORD no estan definidas');
  }
  
  console.log(`[Lighthouse Auth] Iniciando sesion como: ${email}`);
  await page.type('input[formControlName="correo"]', email);
  await page.type('input[formControlName="password"]', password);
  
  // Hacer submit
  await page.click('button[type="submit"]');
  
  // Esperar a que la redireccion al dashboard termine
  console.log(`[Lighthouse Auth] Esperando redireccion al dashboard...`);
  await page.waitForNavigation({ waitUntil: 'networkidle0', timeout: 15000 });
  console.log(`[Lighthouse Auth] Sesion iniciada exitosamente.`);
};
