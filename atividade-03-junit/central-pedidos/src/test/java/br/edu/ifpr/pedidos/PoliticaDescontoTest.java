package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {
    private final PoliticaDesconto politica = new PoliticaDesconto();

    @Test
    void deveDarDezPorCentoParaVip() {
        assertEquals(1_000, politica.calcular(new Cliente(true, false, 1), 10_000, null));
    }

    @Test
    void deveDarCincoPorCentoParaClienteComumAPartirDeQuinhentosReais() {
        Cliente cliente = new Cliente(false, false, 1);
        assertEquals(0, politica.calcular(cliente, 49_999, null));
        assertEquals(2_500, politica.calcular(cliente, 50_000, null));
    }

    @Test
    void deveManterDescontoComCupomNuloOuEmBranco() {
        Cliente vip = new Cliente(true, false, 1);
        assertEquals(1_000, politica.calcular(vip, 10_000, null));
        assertEquals(1_000, politica.calcular(vip, 10_000, "   "));
    }

    @Test
    void deveAplicarCupomBemVindoQuandoElegivel() {
        Cliente novo = new Cliente(false, false, 0);
        assertEquals(2_000, politica.calcular(novo, 10_000, " bemvindo "));
    }

    @Test
    void naoDeveAplicarBemVindoParaClienteAntigoOuSubtotalAbaixoDoLimite() {
        assertEquals(0, politica.calcular(new Cliente(false, false, 1), 10_000, "BEMVINDO"));
        assertEquals(0, politica.calcular(new Cliente(false, false, 0), 9_999, "BEMVINDO"));
    }

    @Test
    void deveAplicarExtraDeDezPorCentoNoLimite() {
        Cliente comum = new Cliente(false, false, 1);
        assertEquals(2_000, politica.calcular(comum, 20_000, "extra10"));
        assertEquals(0, politica.calcular(comum, 19_999, "EXTRA10"));
    }

    @Test
    void deveLimitarDescontoAteVintePorCento() {
        Cliente vipNovo = new Cliente(true, false, 0);
        assertEquals(2_000, politica.calcular(vipNovo, 10_000, "BEMVINDO"));
    }

    @Test
    void deveRejeitarCupomDesconhecido() {
        assertThrows(IllegalArgumentException.class,
                () -> politica.calcular(new Cliente(false, false, 1), 10_000, "INVALIDO"));
    }

    @Test
    void deveRejeitarSubtotalNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> politica.calcular(new Cliente(false, false, 1), -1, null));
    }
}
