import { test, expect } from '@playwright/test';

test.describe('cadastro de senha', () => {
  const casosValidos = [
    { senha: 'Abc12345', descricao: 'oito caracteres' },
    { senha: 'Abc12345678901234567', descricao: 'vinte caracteres' },
  ];

  for (const caso of casosValidos) {
    test(`cadastra senha válida com ${caso.descricao}`, async ({ page }) => {
      await page.goto('/senha');
      await page.getByLabel('Nova senha').fill(caso.senha);
      await page.getByLabel('Confirmar senha').fill(caso.senha);
      await page.getByRole('button', { name: 'Cadastrar senha' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText('Senha cadastrada');
      await expect(resultado).toHaveAttribute('role', 'status');
      await expect(page.getByLabel('Nova senha')).toHaveValue('');
      await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
    });
  }

  const casosForaDoPadrao = [
    { senha: '', descricao: 'vazia' },
    { senha: 'Abc1234', descricao: 'com sete caracteres' },
    { senha: 'Abc123456789012345678', descricao: 'com vinte e um caracteres' },
    { senha: 'abc12345', descricao: 'sem letra maiúscula' },
    { senha: 'ABC12345', descricao: 'sem letra minúscula' },
    { senha: 'Abcdefgh', descricao: 'sem número' },
    { senha: 'Abc 12345', descricao: 'com espaço' },
  ];

  for (const caso of casosForaDoPadrao) {
    test(`rejeita senha ${caso.descricao}`, async ({ page }) => {
      await page.goto('/senha');
      await page.getByLabel('Nova senha').fill(caso.senha);
      await page.getByLabel('Confirmar senha').fill(caso.senha);
      await page.getByRole('button', { name: 'Cadastrar senha' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText('Senha fora do padrão');
      await expect(resultado).toHaveAttribute('role', 'alert');
    });
  }

  test('rejeita confirmação diferente da senha', async ({ page }) => {
    await page.goto('/senha');
    await page.getByLabel('Nova senha').fill('Abc12345');
    await page.getByLabel('Confirmar senha').fill('Abc12346');
    await page.getByRole('button', { name: 'Cadastrar senha' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toHaveText('As senhas não coincidem');
    await expect(resultado).toHaveAttribute('role', 'alert');
  });
});
