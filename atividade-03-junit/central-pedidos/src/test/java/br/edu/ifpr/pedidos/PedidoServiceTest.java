package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {
    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
            () -> assertEquals("PAGO", resultado.status()),
            () -> assertEquals(10_000L, resultado.subtotalCentavos()),
            () -> assertEquals(0L, resultado.descontoCentavos()),
            () -> assertEquals(1_200L, resultado.freteCentavos()),
            () -> assertEquals(11_200L, resultado.totalCentavos()),
            // A lista comprova uma única cobrança, com o valor correto.
            () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void devePararAntesDosItensQuandoClienteEstaBloqueado() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });
        Pedido pedidoVazio = new Pedido(List.of(), "PR", false, "CUPOM-INVALIDO");

        ResultadoPedido resultado = service.fechar(pedidoVazio, new Cliente(false, true, 0));

        assertAll(
                () -> assertEquals("BLOQUEADO", resultado.status()),
                () -> assertEquals(0, resultado.subtotalCentavos()),
                () -> assertEquals(0, resultado.descontoCentavos()),
                () -> assertEquals(0, resultado.freteCentavos()),
                () -> assertEquals(0, resultado.totalCentavos()),
                () -> assertEquals(0, chamadas.get()));
    }

    @Test
    void deveRejeitarPedidoSemItensAtivos() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });
        ItemPedido inativo = new ItemPedido("INATIVO", 1_000, 0, 0, 500, false);
        Pedido pedido = new Pedido(List.of(inativo), "PR", false, null);

        assertThrows(IllegalArgumentException.class,
                () -> service.fechar(pedido, new Cliente(false, false, 1)));
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRetornarSemEstoqueAntesDeValidarCupom() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });
        ItemPedido item = new ItemPedido("SEM-ESTOQUE", 1_000, 2, 1, 500, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "CUPOM-INVALIDO");

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertEquals("SEM_ESTOQUE", resultado.status());
        assertEquals(0, resultado.totalCentavos());
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRetornarRevisaoSemTentarPagamento() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });
        ItemPedido item = new ItemPedido("ITEM", 10_000, 1, 1, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 0));

        assertAll(
                () -> assertEquals("REVISAO", resultado.status()),
                () -> assertEquals(10_000, resultado.subtotalCentavos()),
                () -> assertEquals(0, resultado.descontoCentavos()),
                () -> assertEquals(2_700, resultado.freteCentavos()),
                () -> assertEquals(12_700, resultado.totalCentavos()),
                () -> assertEquals(0, chamadas.get()));
    }

    @Test
    void deveInformarPagamentoRecusado() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return false;
        });
        ItemPedido item = new ItemPedido("ITEM", 10_000, 1, 1, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertEquals("PAGAMENTO_RECUSADO", resultado.status());
        assertEquals(1, chamadas.get());
    }

    @Test
    void deveTentarPagamentoTresVezesQuandoProcessadorEstaIndisponivel() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            throw new IllegalStateException("indisponível");
        });
        ItemPedido item = new ItemPedido("ITEM", 10_000, 1, 1, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        ResultadoPedido resultado = service.fechar(pedido, new Cliente(false, false, 1));

        assertEquals("PAGAMENTO_RECUSADO", resultado.status());
        assertEquals(3, chamadas.get());
    }

    @Test
    void devePropagarErroDeCupomSemCobrar() {
        AtomicInteger chamadas = new AtomicInteger();
        PedidoService service = new PedidoService(total -> {
            chamadas.incrementAndGet();
            return true;
        });
        ItemPedido item = new ItemPedido("ITEM", 10_000, 1, 1, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, "INVALIDO");

        assertThrows(IllegalArgumentException.class,
                () -> service.fechar(pedido, new Cliente(false, false, 1)));
        assertEquals(0, chamadas.get());
    }

    @Test
    void deveRejeitarReferenciasNulas() {
        PedidoService service = new PedidoService(total -> true);
        Pedido pedido = new Pedido(List.of(), "PR", false, null);
        Cliente cliente = new Cliente(false, false, 1);

        assertThrows(NullPointerException.class, () -> service.fechar(null, cliente));
        assertThrows(NullPointerException.class, () -> service.fechar(pedido, null));
        assertThrows(NullPointerException.class, () -> new PedidoService(null));
    }
}
