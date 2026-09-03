package campominado.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes focados no encapsulamento e nas regras de estado da classe Celula.
 */
class CelulaTest {

    @Test
    void celulaNovaComecaComEstadoPadrao() {
        Celula celula = new Celula(2, 3);

        assertEquals(2, celula.getLinha());
        assertEquals(3, celula.getColuna());
        assertFalse(celula.isMinada());
        assertFalse(celula.isRevelada());
        assertFalse(celula.isMarcada());
        assertEquals(0, celula.getMinasVizinhas());
    }

    @Test
    void marcarComoMinadaAlteraApenasOAtributoMinada() {
        Celula celula = new Celula(0, 0);

        celula.marcarComoMinada();

        assertTrue(celula.isMinada());
        assertFalse(celula.isRevelada());
        assertFalse(celula.isMarcada());
    }

    @Test
    void celulaMarcadaComBandeiraNaoPodeSerRevelada() {
        Celula celula = new Celula(0, 0);

        celula.alternarMarcacao();
        boolean revelou = celula.revelar();

        assertFalse(revelou, "revelar() deveria falhar em celula marcada");
        assertFalse(celula.isRevelada());
        assertTrue(celula.isMarcada());
    }

    @Test
    void celulaJaReveladaNaoPodeSerMarcada() {
        Celula celula = new Celula(0, 0);

        celula.revelar();
        boolean estadoMarcacao = celula.alternarMarcacao();

        assertFalse(estadoMarcacao, "nao deveria ser possivel marcar celula ja revelada");
        assertFalse(celula.isMarcada());
    }

    @Test
    void isVaziaSoEVerdadeiroSemMinaESemVizinhas() {
        Celula vazia = new Celula(0, 0);
        assertTrue(vazia.isVazia());

        Celula comVizinhas = new Celula(0, 1);
        comVizinhas.setMinasVizinhas(2);
        assertFalse(comVizinhas.isVazia());

        Celula minada = new Celula(0, 2);
        minada.marcarComoMinada();
        assertFalse(minada.isVazia(), "celula minada nunca deve ser considerada vazia");
    }

    @Test
    void setMinasVizinhasRejeitaValoresForaDoIntervalo() {
        Celula celula = new Celula(0, 0);
        assertThrows(IllegalArgumentException.class, () -> celula.setMinasVizinhas(-1));
        assertThrows(IllegalArgumentException.class, () -> celula.setMinasVizinhas(9));
    }
}
