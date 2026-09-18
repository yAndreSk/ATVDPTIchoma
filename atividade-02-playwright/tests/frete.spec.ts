import { test, expect } from '@playwright/test';

test.describe('cálculo de frete', () => {
  const casosValidos = [
    { cep: '80000000', valor: '199,99', resultado: 'Frete: R$ 15,00', descricao: 'CEP iniciado por 8' },
    { cep: '70000000', valor: '199,99', resultado: 'Frete: R$ 25,00', descricao: 'demais CEPs' },
    { cep: '80000000', valor: '200,00', resultado: 'Frete grátis', descricao: 'limite do frete grátis' },
    { cep: '70000000', valor: '200.01', resultado: 'Frete grátis', descricao: 'valor acima do limite' },
  ];

  for (const caso of casosValidos) {
    test(`calcula corretamente para ${caso.descricao}`, async ({ page }) => {
      await page.goto('/frete');
      await page.getByLabel('CEP').fill(caso.cep);
      await page.getByLabel('Valor do pedido').fill(caso.valor);
      await page.getByRole('button', { name: 'Calcular frete' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText(caso.resultado);
      await expect(resultado).toHaveAttribute('role', 'status');
    });
  }

  const casosInvalidos = [
    { cep: '', valor: '100,00', descricao: 'CEP vazio' },
    { cep: '8000000', valor: '100,00', descricao: 'CEP com sete dígitos' },
    { cep: '800000000', valor: '100,00', descricao: 'CEP com nove dígitos' },
    { cep: '8000A000', valor: '100,00', descricao: 'CEP com letra' },
    { cep: '80000000', valor: '', descricao: 'valor vazio' },
    { cep: '80000000', valor: '0', descricao: 'valor igual a zero' },
    { cep: '80000000', valor: '-1', descricao: 'valor negativo' },
    { cep: '80000000', valor: '10,999', descricao: 'valor com três casas decimais' },
    { cep: '80000000', valor: 'cem', descricao: 'valor não numérico' },
  ];

  for (const caso of casosInvalidos) {
    test(`rejeita ${caso.descricao}`, async ({ page }) => {
      await page.goto('/frete');
      await page.getByLabel('CEP').fill(caso.cep);
      await page.getByLabel('Valor do pedido').fill(caso.valor);
      await page.getByRole('button', { name: 'Calcular frete' }).click();

      const resultado = page.locator('#resultado');
      await expect(resultado).toBeVisible();
      await expect(resultado).toHaveText('Dados inválidos');
      await expect(resultado).toHaveAttribute('role', 'alert');
    });
  }
});
