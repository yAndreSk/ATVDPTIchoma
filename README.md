# Atividades — Avaliação Prática P1

Aluno: André Augusto Silva Domingues

Este repositório reúne as quatro atividades solicitadas na avaliação prática de Projeto, Implementação e Teste de Software.

## Entregas

1. Artefatos de teste: [plano de teste](Plano_de_Teste_Reservas_de_Salas_Final.pdf) e [casos de teste](Casos_de_Teste_Reservas_de_Salas_Final.pdf).
2. [Teste funcional com Playwright](atividade-02-playwright/).
3. [Teste estrutural e unitário com JUnit](atividade-03-junit/).
4. [Resolução dos exercícios de Grafo de Fluxo de Controle](atividade-04-gfc/respostas_grafos_fluxo_controle.md).

## Playwright

Foram acrescentados testes para as telas de frete e senha, cobrindo caminhos válidos, classes inválidas e valores-limite. A suíte completa possui 34 testes em quatro arquivos.

```bash
cd atividade-02-playwright
npm install
npm run browsers
npm test
```

## JUnit e JaCoCo

A pasta possui o exercício introdutório `boletim-simples` e o laboratório completo `central-pedidos`.

```bash
cd atividade-03-junit/boletim-simples
mvn clean test

cd ../central-pedidos
mvn clean test
```

- `boletim-simples`: 11 testes, com 100% de linhas e ramos.
- `central-pedidos`: 62 testes, com 100% de linhas, ramos, métodos e classes concretas.
- O relatório detalhado, os CFGs e a matriz de testes estão em [`RELATORIO.md`](atividade-03-junit/central-pedidos/RELATORIO.md).
