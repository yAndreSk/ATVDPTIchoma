package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {
    @Test
    void deveCalcularTotalEInformarDisponibilidade() {
        ItemPedido item = new ItemPedido("ABC", 2_500, 3, 3, 500, true);

        assertAll(
                () -> assertEquals(7_500, item.totalCentavos()),
                () -> assertTrue(item.disponivel()),
                () -> assertEquals("ABC", item.sku()),
                () -> assertTrue(item.fragil()));
    }

    @Test
    void deveInformarFaltaDeEstoque() {
        ItemPedido item = new ItemPedido("ABC", 1_000, 2, 1, 500, false);
        assertFalse(item.disponivel());
    }

    @Test
    void deveAceitarQuantidadeZero() {
        ItemPedido item = new ItemPedido("ABC", 1, 0, 0, 1, false);
        assertEquals(0, item.totalCentavos());
        assertTrue(item.disponivel());
    }

    @Test
    void deveRejeitarSkuNuloOuEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido(null, 1_000, 1, 1, 500, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("  ", 1_000, 1, 1, 500, false));
    }

    @Test
    void deveRejeitarPrecoForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 0, 1, 1, 500, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000_001, 1, 1, 500, false));
    }

    @Test
    void deveRejeitarQuantidadeForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000, -1, 1, 500, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000, 101, 101, 500, false));
    }

    @Test
    void deveRejeitarEstoqueNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000, 1, -1, 500, false));
    }

    @Test
    void deveRejeitarPesoForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000, 1, 1, 0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("A", 1_000, 1, 1, 100_001, false));
    }
}
