package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipacaoTest {
    private final Participacao participacao = new Participacao();

    @Test
    void deveSomarTresPontosQuandoEntregouEParticipou() {
        assertEquals(3, participacao.calcularPontos(true, true));
    }

    @Test
    void deveSomarDoisPontosQuandoSomenteEntregou() {
        assertEquals(2, participacao.calcularPontos(true, false));
    }

    @Test
    void deveSomarUmPontoQuandoSomenteParticipou() {
        assertEquals(1, participacao.calcularPontos(false, true));
    }

    @Test
    void deveRetornarZeroQuandoNaoEntregouNemParticipou() {
        assertEquals(0, participacao.calcularPontos(false, false));
    }
}
