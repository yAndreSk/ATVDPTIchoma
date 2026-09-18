package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {
    private final AnaliseRisco analise = new AnaliseRisco();

    @Test
    void deveRecusarClienteBloqueado() {
        assertEquals("RECUSADO", analise.avaliar(new Cliente(false, true, 0), 1_000, false));
    }

    @Test
    void deveMandarClienteNovoParaRevisaoPorValorAlto() {
        assertEquals("REVISAO", analise.avaliar(new Cliente(false, false, 0), 100_001, false));
    }

    @Test
    void deveMandarClienteNovoParaRevisaoPorEntregaExpressa() {
        assertEquals("REVISAO", analise.avaliar(new Cliente(false, false, 0), 10_000, true));
    }

    @Test
    void deveAprovarClienteNovoEmCompraNormal() {
        assertEquals("APROVADO", analise.avaliar(new Cliente(false, false, 0), 100_000, false));
    }

    @Test
    void deveMandarClienteComHistoricoParaRevisaoPorValorAlto() {
        assertEquals("REVISAO", analise.avaliar(new Cliente(false, false, 1), 500_001, false));
    }

    @Test
    void deveAprovarVipComHistoricoMesmoEmValorAlto() {
        assertEquals("APROVADO", analise.avaliar(new Cliente(true, false, 1), 500_001, false));
    }

    @Test
    void deveAprovarClienteComHistoricoNoLimite() {
        assertEquals("APROVADO", analise.avaliar(new Cliente(false, false, 1), 500_000, false));
    }

    @Test
    void deveRejeitarTotalNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> analise.avaliar(new Cliente(false, false, 1), -1, false));
    }
}
