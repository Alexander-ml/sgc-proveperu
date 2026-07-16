import { test, expect } from '@playwright/test';

test('login y navegacion principal', async ({ page }) => {
  await page.goto('/login');

  await page.locator('.login-card input[type="text"]').fill('admin@proveperu.com');
  await page.locator('.login-card input[type="password"]').fill('prueba');

  await page.getByRole('button', { name: /ingresar/i }).click();

  await expect(page).toHaveURL(/\/home/, { timeout: 15000 });
  await expect(page.locator('h1').filter({ hasText: 'Panel Principal' })).toBeVisible({
    timeout: 15000,
  });

  await page.goto('/ventas');
  await expect(page.locator('.page-title').filter({ hasText: /Ventas/i })).toBeVisible({
    timeout: 15000,
  });

  await page.goto('/compras');
  await expect(page.locator('.page-title').filter({ hasText: /Compras/i })).toBeVisible({
    timeout: 15000,
  });

  await page.goto('/caja');
  await expect(page.locator('h1').filter({ hasText: /Caja/i })).toBeVisible({
    timeout: 15000,
  });

  await page.goto('/clientes');
  await expect(page.locator('.page-title').filter({ hasText: /Clientes/i })).toBeVisible({
    timeout: 15000,
  });
});