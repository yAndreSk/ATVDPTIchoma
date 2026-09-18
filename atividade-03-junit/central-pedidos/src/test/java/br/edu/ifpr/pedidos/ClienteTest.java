package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    @Test
    void deveCriarClienteValido() {
        Cliente cliente = new Cliente(true, false, 2);

        assertAll(
                () -> assertTrue(cliente.vip()),
                () -> assertFalse(cliente.bloqueado()),
                () -> assertEquals(2, cliente.comprasAnteriores()));
    }

    @Test
    void deveAceitarHistoricoIgualAZero() {
        assertDoesNotThrow(() -> new Cliente(false, false, 0));
    }

    @Test
    void deveRejeitarHistoricoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(false, false, -1));
    }
}
