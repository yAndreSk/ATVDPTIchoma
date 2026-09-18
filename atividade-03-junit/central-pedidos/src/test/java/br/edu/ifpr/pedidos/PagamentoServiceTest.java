package br.edu.ifpr.pedidos;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {
    @Test
    void deveAprovarNaPrimeiraTentativa() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.incrementAndGet();
            assertEquals(12_300, total);
            return true;
        });

        assertTrue(service.pagar(12_300, 3));
        assertEquals(1, chamadas.get());
    }

    @Test
    void deveRecusarSemRepetirQuandoRetornoForFalso() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.incrementAndGet();
            return false;
        });

        assertFalse(service.pagar(1_000, 3));
        assertEquals(1, chamadas.get());
    }

    @Test
    void deveRepetirAposIndisponibilidadeEConcluir() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            if (chamadas.incrementAndGet() == 1) {
                throw new IllegalStateException("temporariamente indisponível");
            }
            return true;
        });

        assertTrue(service.pagar(1_000, 3));
        assertEquals(2, chamadas.get());
    }

    @Test
    void deveRetornarFalsoDepoisDeEsgotarTentativas() {
        AtomicInteger chamadas = new AtomicInteger();
        PagamentoService service = new PagamentoService(total -> {
            chamadas.incrementAndGet();
            throw new IllegalStateException("temporariamente indisponível");
        });

        assertFalse(service.pagar(1_000, 3));
        assertEquals(3, chamadas.get());
    }

    @Test
    void devePropagarExcecaoQueNaoRepresentaIndisponibilidade() {
        PagamentoService service = new PagamentoService(total -> {
            throw new IllegalArgumentException("erro definitivo");
        });

        assertThrows(IllegalArgumentException.class, () -> service.pagar(1_000, 3));
    }

    @Test
    void deveRejeitarProcessadorNulo() {
        assertThrows(NullPointerException.class, () -> new PagamentoService(null));
    }

    @Test
    void deveRejeitarTotalNaoPositivo() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 1));
        assertThrows(IllegalArgumentException.class, () -> service.pagar(-1, 1));
    }

    @Test
    void deveRejeitarLimiteDeTentativasForaDoIntervalo() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1_000, 0));
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1_000, 4));
    }
}
