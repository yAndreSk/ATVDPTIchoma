package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {
    private ItemPedido item(String sku, long preco, int quantidade, int estoque, int peso, boolean fragil) {
        return new ItemPedido(sku, preco, quantidade, estoque, peso, fragil);
    }

    @Test
    void deveCalcularSubtotalEPesoIgnorandoValorDoItemInativo() {
        Pedido pedido = new Pedido(List.of(
                item("A", 2_000, 2, 2, 500, false),
                item("B", 9_000, 0, 0, 9_000, true)), "PR", false, null);

        assertAll(
                () -> assertEquals(4_000, pedido.subtotalCentavos()),
                () -> assertEquals(1_000, pedido.pesoGramas()),
                () -> assertFalse(pedido.temFragil()),
                () -> assertTrue(pedido.estoqueSuficiente()));
    }

    @Test
    void deveIdentificarItemFragilAtivo() {
        Pedido pedido = new Pedido(List.of(item("A", 1_000, 1, 1, 500, true)), "PR", false, null);
        assertTrue(pedido.temFragil());
    }

    @Test
    void deveIdentificarFaltaDeEstoqueNoInicioENoFim() {
        ItemPedido disponivel = item("OK", 1_000, 1, 1, 500, false);
        ItemPedido indisponivel = item("SEM", 1_000, 2, 1, 500, false);

        assertFalse(new Pedido(List.of(indisponivel, disponivel), "PR", false, null).estoqueSuficiente());
        assertFalse(new Pedido(List.of(disponivel, indisponivel), "PR", false, null).estoqueSuficiente());
    }

    @Test
    void deveFazerCopiaDefensivaDaLista() {
        ArrayList<ItemPedido> itens = new ArrayList<>();
        itens.add(item("A", 1_000, 1, 1, 500, false));
        Pedido pedido = new Pedido(itens, "PR", false, null);

        itens.clear();

        assertEquals(1, pedido.itens().size());
        assertThrows(UnsupportedOperationException.class,
                () -> pedido.itens().add(item("B", 1_000, 1, 1, 500, false)));
    }

    @Test
    void deveAceitarListaVaziaECodigoDeUfGenerico() {
        Pedido pedido = new Pedido(List.of(), "ZZ", false, "");
        assertEquals(0, pedido.subtotalCentavos());
        assertEquals("ZZ", pedido.uf());
    }

    @Test
    void deveRejeitarListaNulaOuComMaisDeCemLinhas() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, "PR", false, null));
        ItemPedido item = item("A", 1_000, 1, 1, 500, false);
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(Collections.nCopies(101, item), "PR", false, null));
    }

    @Test
    void deveRejeitarElementoNuloNaLista() {
        assertThrows(NullPointerException.class,
                () -> new Pedido(Collections.singletonList(null), "PR", false, null));
    }

    @Test
    void deveRejeitarUfInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), null, false, null));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "pr", false, null));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(List.of(), "PAR", false, null));
    }
}
