package br.edu.ifpr.pedidos;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {
    private final CalculadoraFrete calculadora = new CalculadoraFrete();
    private final Cliente comum = new Cliente(false, false, 1);

    private Pedido pedido(String uf, boolean expresso, ItemPedido... itens) {
        return new Pedido(List.of(itens), uf, expresso, null);
    }

    private ItemPedido item(int peso, boolean fragil) {
        return new ItemPedido("ITEM", 1_000, 1, 1, peso, fragil);
    }

    @Test
    void deveCalcularFreteBaseDoParana() {
        assertEquals(1_200, calculadora.calcular(pedido("PR", false, item(2_000, false)), comum, 10_000));
    }

    @Test
    void deveCalcularFreteBaseDeSaoPauloEDoRio() {
        assertEquals(2_000, calculadora.calcular(pedido("SP", false, item(1_000, false)), comum, 10_000));
        assertEquals(2_000, calculadora.calcular(pedido("RJ", false, item(1_000, false)), comum, 10_000));
    }

    @Test
    void deveUsarTarifaPadraoParaOutraUf() {
        assertEquals(3_000, calculadora.calcular(pedido("SC", false, item(1_000, false)), comum, 10_000));
    }

    @Test
    void deveCobrarQuiloAdicionalOuFracao() {
        assertEquals(1_500, calculadora.calcular(pedido("PR", false, item(2_001, false)), comum, 10_000));
        assertEquals(1_800, calculadora.calcular(pedido("PR", false, item(3_001, false)), comum, 10_000));
    }

    @Test
    void deveDarFreteGratisNoLimiteParaEntregaNormal() {
        assertEquals(0, calculadora.calcular(pedido("PR", false, item(4_000, false)), comum, 30_000));
    }

    @Test
    void deveDarMetadeDoFreteBaseParaClienteVip() {
        Cliente vip = new Cliente(true, false, 1);
        assertEquals(600, calculadora.calcular(pedido("PR", false, item(1_000, false)), vip, 10_000));
    }

    @Test
    void deveSomarTaxasDeEntregaExpressaEItemFragil() {
        Pedido pedido = pedido("PR", true, item(1_000, true));
        assertEquals(3_200, calculadora.calcular(pedido, comum, 30_000));
    }

    @Test
    void deveSomarAdicionalFragilMesmoComFreteGratis() {
        assertEquals(500, calculadora.calcular(pedido("PR", false, item(1_000, true)), comum, 30_000));
    }

    @Test
    void deveRejeitarValorLiquidoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(pedido("PR", false, item(1_000, false)), comum, -1));
    }
}
