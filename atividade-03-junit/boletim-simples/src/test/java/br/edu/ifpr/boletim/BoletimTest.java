package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveAprovarAlunoComMediaOito() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(8);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaQuatro() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(4);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(2);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaIgualCinco() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(4.5, 7.0);

        assertEquals(5.75, resultado, 0.0001);
    }

    @Test
    void deveContarZeroAprovadosEmArrayVazio() {
        Boletim boletim = new Boletim();

        assertEquals(0, boletim.contarAprovados(new double[] {}));
    }

    @Test
    void deveContarAprovadosIncluindoMediaSete() {
        Boletim boletim = new Boletim();

        assertEquals(3, boletim.contarAprovados(new double[] {8, 5, 7, 6.9, 10}));
    }

    @Test
    void deveVerificarLimitesDaClassificacao() {
        Boletim boletim = new Boletim();

        assertEquals("REPROVADO", boletim.verificarSituacao(3.99));
        assertEquals("RECUPERACAO", boletim.verificarSituacao(4));
        assertEquals("RECUPERACAO", boletim.verificarSituacao(6.99));
        assertEquals("APROVADO", boletim.verificarSituacao(7));

    }
}
