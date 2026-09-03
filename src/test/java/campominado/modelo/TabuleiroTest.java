package campominado.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios da classe Tabuleiro: posicionamento de minas, contagem
 * de vizinhas, efeito cascata e condicoes de vitoria/derrota.
 *
 * Observacao sobre determinismo: o posicionamento das minas e aleatorio
 * (Tabuleiro usa java.util.Random internamente), entao os testes evitam
 * depender de QUAL posicao especifica sera minada. Em vez disso, cada
 * teste ou usa uma configuracao em que a posicao nao importa (ex.: 0
 * minas, ou "todas menos uma"), ou descobre a posicao em tempo de
 * execucao consultando o proprio Tabuleiro antes de agir.
 */
class TabuleiroTest {

    @Test
    void construtorCriaGradeComDimensoesEQuantidadeDeMinasCorretas() {
        Tabuleiro tabuleiro = new Tabuleiro(5, 6, 7);

        assertEquals(5, tabuleiro.getLinhas());
        assertEquals(6, tabuleiro.getColunas());
        assertEquals(7, tabuleiro.getTotalMinas());

        int minasEncontradas = 0;
        for (int l = 0; l < tabuleiro.getLinhas(); l++) {
            for (int c = 0; c < tabuleiro.getColunas(); c++) {
                if (tabuleiro.isMinada(l, c)) {
                    minasEncontradas++;
                }
                assertFalse(tabuleiro.isRevelada(l, c), "nenhuma celula deveria comecar revelada");
                assertFalse(tabuleiro.isMarcada(l, c), "nenhuma celula deveria comecar marcada");
            }
        }
        assertEquals(7, minasEncontradas, "quantidade de minas no tabuleiro deve bater com o construtor");
    }

    @Test
    void construtorRejeitaParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Tabuleiro(0, 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new Tabuleiro(5, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Tabuleiro(2, 2, 4)); // minas >= total de celulas
        assertThrows(IllegalArgumentException.class, () -> new Tabuleiro(2, 2, -1));
    }

    @Test
    void minasVizinhasDeCadaCelulaCorrespondeAContagemReal() {
        Tabuleiro tabuleiro = new Tabuleiro(6, 6, 10);

        for (int l = 0; l < tabuleiro.getLinhas(); l++) {
            for (int c = 0; c < tabuleiro.getColunas(); c++) {
                if (tabuleiro.isMinada(l, c)) {
                    continue;
                }
                int contagemEsperada = 0;
                for (int dl = -1; dl <= 1; dl++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dl == 0 && dc == 0) continue;
                        int nl = l + dl;
                        int nc = c + dc;
                        if (nl >= 0 && nl < tabuleiro.getLinhas() && nc >= 0 && nc < tabuleiro.getColunas()) {
                            if (tabuleiro.isMinada(nl, nc)) {
                                contagemEsperada++;
                            }
                        }
                    }
                }
                assertEquals(contagemEsperada, tabuleiro.getMinasVizinhas(l, c),
                        "minasVizinhas incorreto na posicao (" + l + "," + c + ")");
            }
        }
    }

    @Test
    void revelarComZeroMinasRevelaOTabuleiroInteiroPelaCascata() {
        // Sem minas, toda celula e "vazia" (0 vizinhas), entao revelar
        // qualquer posicao deve propagar a cascata para o tabuleiro todo.
        Tabuleiro tabuleiro = new Tabuleiro(5, 5, 0);

        tabuleiro.revelar(2, 2);

        for (int l = 0; l < tabuleiro.getLinhas(); l++) {
            for (int c = 0; c < tabuleiro.getColunas(); c++) {
                assertTrue(tabuleiro.isRevelada(l, c), "celula (" + l + "," + c + ") deveria ter sido revelada pela cascata");
            }
        }
        assertTrue(tabuleiro.verificarVitoria());
    }

    @Test
    void revelarUmaMinaNaoDisparaCascataENaoRevelaOutrasMinas() {
        // Tabuleiro pequeno quase todo minado: sobra 1 celula livre.
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, 8);

        int linhaMina = -1;
        int colunaMina = -1;
        int minasNaoReveladasAntes = 0;
        for (int l = 0; l < 3 && linhaMina == -1; l++) {
            for (int c = 0; c < 3; c++) {
                if (tabuleiro.isMinada(l, c)) {
                    if (linhaMina == -1) {
                        linhaMina = l;
                        colunaMina = c;
                    }
                }
            }
        }
        assertNotEquals(-1, linhaMina, "deveria existir ao menos uma mina neste tabuleiro");

        tabuleiro.revelar(linhaMina, colunaMina);

        assertTrue(tabuleiro.isRevelada(linhaMina, colunaMina), "a mina clicada deve ficar revelada");

        int minasAindaEscondidas = 0;
        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                if (tabuleiro.isMinada(l, c) && !tabuleiro.isRevelada(l, c)) {
                    minasAindaEscondidas++;
                }
            }
        }
        assertEquals(7, minasAindaEscondidas,
                "revelar uma mina nao pode disparar cascata que revele as outras 7 minas");
    }

    @Test
    void celulaMarcadaNaoERevelivelAteSerDesmarcada() {
        Tabuleiro tabuleiro = new Tabuleiro(4, 4, 0);

        tabuleiro.alternarMarcacao(1, 1);
        tabuleiro.revelar(1, 1);
        assertFalse(tabuleiro.isRevelada(1, 1), "celula marcada nao deve ser revelada");

        tabuleiro.alternarMarcacao(1, 1); // desmarca
        tabuleiro.revelar(1, 1);
        assertTrue(tabuleiro.isRevelada(1, 1), "apos desmarcar, a celula deve poder ser revelada");
    }

    @Test
    void verificarVitoriaSoRetornaTrueQuandoTodasAsCelulasSemMinaEstaoReveladas() {
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, 3);
        assertFalse(tabuleiro.verificarVitoria(), "nao deveria haver vitoria logo no inicio da partida");

        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                if (!tabuleiro.isMinada(l, c)) {
                    tabuleiro.revelar(l, c);
                }
            }
        }

        assertTrue(tabuleiro.verificarVitoria(),
                "revelar todas as celulas sem mina deve resultar em vitoria");
    }

    @Test
    void revelarPosicaoForaDosLimitesNaoLancaExcecaoENaoAlteraOTabuleiro() {
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, 0);

        assertDoesNotThrow(() -> tabuleiro.revelar(-1, 0));
        assertDoesNotThrow(() -> tabuleiro.revelar(0, 99));

        for (int l = 0; l < 3; l++) {
            for (int c = 0; c < 3; c++) {
                assertFalse(tabuleiro.isRevelada(l, c));
            }
        }
    }
}
