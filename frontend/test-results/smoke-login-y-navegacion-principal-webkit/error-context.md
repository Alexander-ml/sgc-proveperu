# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: smoke.spec.js >> login y navegacion principal
- Location: tests\smoke.spec.js:3:1

# Error details

```
Error: expect(page).toHaveURL(expected) failed

Expected pattern: /\/home/
Received string:  "https://localhost/login"
Timeout: 15000ms

Call log:
  - Expect "toHaveURL" with timeout 15000ms
    33 × unexpected value "https://localhost/login"

```

```yaml
- heading "SGC ProvePeru" [level=1]
- paragraph: Ingrese sus credenciales
- text: Usuario o contraseña incorrectos Usuario o correo
- textbox "admin@proveperu.com"
- text: Contraseña
- textbox "Ingrese su contraseña": prueba
- button "Ingresar"
```

# Test source

```ts
  1  | import { test, expect } from '@playwright/test';
  2  | 
  3  | test('login y navegacion principal', async ({ page }) => {
  4  |   await page.goto('/login');
  5  | 
  6  |   await page.locator('.login-card input[type="text"]').fill('admin@proveperu.com');
  7  |   await page.locator('.login-card input[type="password"]').fill('prueba');
  8  | 
  9  |   await page.getByRole('button', { name: /ingresar/i }).click();
  10 | 
> 11 |   await expect(page).toHaveURL(/\/home/, { timeout: 15000 });
     |                      ^ Error: expect(page).toHaveURL(expected) failed
  12 |   await expect(page.locator('h1').filter({ hasText: 'Panel Principal' })).toBeVisible({
  13 |     timeout: 15000,
  14 |   });
  15 | 
  16 |   await page.goto('/ventas');
  17 |   await expect(page.locator('.page-title').filter({ hasText: /Ventas/i })).toBeVisible({
  18 |     timeout: 15000,
  19 |   });
  20 | 
  21 |   await page.goto('/compras');
  22 |   await expect(page.locator('.page-title').filter({ hasText: /Compras/i })).toBeVisible({
  23 |     timeout: 15000,
  24 |   });
  25 | 
  26 |   await page.goto('/caja');
  27 |   await expect(page.locator('h1').filter({ hasText: /Caja/i })).toBeVisible({
  28 |     timeout: 15000,
  29 |   });
  30 | 
  31 |   await page.goto('/clientes');
  32 |   await expect(page.locator('.page-title').filter({ hasText: /Clientes/i })).toBeVisible({
  33 |     timeout: 15000,
  34 |   });
  35 | });
```