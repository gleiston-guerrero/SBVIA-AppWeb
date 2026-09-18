/**
 * Script de Puppeteer para autenticar antes de correr Lighthouse
 */
module.exports = async (browser, context) => {
  const page = await browser.newPage();
  
  // Usar la URL base de lhci o localhost
  const baseUrl = (process.env.LHCI_BASE_URL || 'https://sbvia-frontend.onrender.com').replace(/\/$/, '');
  
  console.log(`[Lighthouse Auth] Esperando inicializacion del navegador...`);
  await new Promise(r => setTimeout(r, 2000));
  
  console.log(`[Lighthouse Auth] Navegando a login...`);
  await page.goto(`${baseUrl}/login`, { waitUntil: 'networkidle2' });
  
  // Esperar a que cargue el formulario
  await page.waitForSelector('input[type="password"]', { timeout: 30000 });
  
  // Extraer credenciales desde variables de entorno inyectadas por el Makefile
  const email = process.env.TEST_USER_EMAIL;
  const password = process.env.TEST_USER_PASSWORD;
  
  if (!email || !password) {
    throw new Error('Las variables TEST_USER_EMAIL o TEST_USER_PASSWORD no estan definidas');
  }
  
  console.log(`[Lighthouse Auth] Iniciando sesion como: ${email}`);
  
  // Encontrar el input de email/identificador (el primer input visible de tipo text o email)
  const usernameInput = await page.$('input[type="text"], input[type="email"], input[name="identificador"], input[formControlName="correo"]');
  await usernameInput.type(email);
  
  // Escribir contraseña
  await page.type('input[type="password"]', password);
  
  // Hacer submit
  await page.click('button[type="submit"]');
  
  // Esperar a que la redireccion al dashboard termine
  console.log(`[Lighthouse Auth] Esperando redireccion al dashboard...`);
  await page.waitForNavigation({ waitUntil: 'networkidle0', timeout: 60000 });
  console.log(`[Lighthouse Auth] Sesion iniciada exitosamente.`);
};
