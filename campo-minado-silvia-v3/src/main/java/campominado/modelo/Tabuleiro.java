package campominado.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa o tabuleiro do Campo Minado.
 *
 * Uso de array vs. ArrayList (decisao de projeto pedida no relatorio):
 *  - Celula[][] grade : array bidimensional de tamanho fixo. E a estrutura
 *    natural para o tabuleiro porque as dimensoes nao mudam durante a
 *    partida e o acesso e sempre por coordenada (linha, coluna) em O(1).
 *  - ArrayList<Celula> : usado no metodo revelar(), como a fila auxiliar
 *    de celulas "pendentes de revelacao" durante o efeito cascata. Aqui um
 *    array nao serve bem porque o numero de celulas que a cascata vai
 *    revelar so e conhecido em tempo de execucao (pode ser 1 ou pode ser
 *    o tabuleiro quase inteiro).
 *  - ArrayList<int[]> tambem e usado em posicionarMinas(), para sortear
 *    posicoes sem repeticao sem precisar de um loop de "tenta de novo se
 *    ja for mina".
 *
 * Encapsulamento entre camadas: esta classe NUNCA devolve um objeto
 * Celula para quem a chama. Toda informacao que a camada de jogo precisa
 * (se uma posicao esta revelada, marcada, minada, quantas minas vizinhas
 * tem) e exposta atraves de metodos de consulta especificos
 * (isRevelada, isMarcada, isMinada, getMinasVizinhas). Assim,
 * JogoCampoMinado so conversa com Tabuleiro, nunca com Celula.
 */
public class Tabuleiro {

    private final int linhas;
    private final int colunas;
    private final int totalMinas;
    private final Celula[][] grade;
    private final Random sorteio;

    public Tabuleiro(int linhas, int colunas, int totalMinas) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("Dimensoes do tabuleiro devem ser positivas.");
        }
        if (totalMinas < 0 || totalMinas >= linhas * colunas) {
            throw new IllegalArgumentException("Numero de minas invalido para o tamanho do tabuleiro.");
        }

        this.linhas = linhas;
        this.colunas = colunas;
        this.totalMinas = totalMinas;
        this.grade = new Celula[linhas][colunas];
        this.sorteio = new Random();

        criarGrade();
        posicionarMinas();
        calcularMinasVizinhas();
    }

    // ---- Montagem inicial ----

    private void criarGrade() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                grade[l][c] = new Celula(l, c);
            }
        }
    }

    private void posicionarMinas() {
        // ArrayList com todas as posicoes possiveis, para sortear sem
        // repeticao (evita loop de "sorteia de novo se ja for mina").
        List<int[]> posicoesDisponiveis = new ArrayList<>();
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                posicoesDisponiveis.add(new int[]{l, c});
            }
        }

        int minasColocadas = 0;
        while (minasColocadas < totalMinas) {
            int indiceSorteado = sorteio.nextInt(posicoesDisponiveis.size());
            int[] posicao = posicoesDisponiveis.remove(indiceSorteado);
            grade[posicao[0]][posicao[1]].marcarComoMinada();
            minasColocadas++;
        }
    }

    private void calcularMinasVizinhas() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                Celula celula = grade[l][c];
                if (celula.isMinada()) {
                    continue;
                }
                int contagem = 0;
                for (Celula vizinha : obterVizinhas(l, c)) {
                    if (vizinha.isMinada()) {
                        contagem++;
                    }
                }
                celula.setMinasVizinhas(contagem);
            }
        }
    }

    private List<Celula> obterVizinhas(int linha, int coluna) {
        List<Celula> vizinhas = new ArrayList<>();
        for (int dl = -1; dl <= 1; dl++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dl == 0 && dc == 0) {
                    continue;
                }
                int nl = linha + dl;
                int nc = coluna + dc;
                if (dentroDosLimites(nl, nc)) {
                    vizinhas.add(grade[nl][nc]);
                }
            }
        }
        return vizinhas;
    }

    private boolean dentroDosLimites(int linha, int coluna) {
        return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
    }

    // ---- Acoes do jogador ----

    /**
     * Revela a celula em (linha, coluna). Se essa celula for "vazia" (sem
     * minas vizinhas), aplica o efeito cascata: revela tambem, em cadeia,
     * todas as celulas vazias conectadas a ela e a "borda" de celulas com
     * numero > 0 ao redor dessa area.
     *
     * Implementacao iterativa com ArrayList como fila de pendentes (em vez
     * de recursao), percorrida com um indice — evita StackOverflow em
     * tabuleiros grandes com areas vazias muito extensas.
     *
     * Por que a cascata nunca revela uma mina "por engano": uma celula so
     * entra na fila de pendentes se: (a) e vizinha de uma celula que ja
     * era vazia, e (b) o proprio ato de enfileirar so revela aquela
     * celula, e so continua a cadeia se ela TAMBEM for vazia
     * (isVazia() == !minada && minasVizinhas == 0). Uma celula minada
     * nunca satisfaz isVazia() (o primeiro termo ja e false), entao a
     * cascata para exatamente na borda em volta das minas — ela pode
     * revelar uma celula numerada vizinha de uma mina, mas nunca a mina
     * em si.
     */
    public void revelar(int linha, int coluna) {
        if (!dentroDosLimites(linha, coluna)) {
            return;
        }

        Celula origem = grade[linha][coluna];
        if (!origem.revelar()) {
            // ja estava revelada ou estava marcada com bandeira: nada a fazer
            return;
        }

        if (!origem.isVazia()) {
            // celula minada ou numerada: revela so ela mesma, sem cascata
            return;
        }

        List<Celula> pendentes = new ArrayList<>();
        pendentes.add(origem);

        int indice = 0;
        while (indice < pendentes.size()) {
            Celula atual = pendentes.get(indice);
            indice++;

            for (Celula vizinha : obterVizinhas(atual.getLinha(), atual.getColuna())) {
                if (!vizinha.isRevelada() && !vizinha.isMarcada()) {
                    vizinha.revelar();
                    if (vizinha.isVazia()) {
                        pendentes.add(vizinha);
                    }
                }
            }
        }
    }

    public boolean alternarMarcacao(int linha, int coluna) {
        if (!dentroDosLimites(linha, coluna)) {
            return false;
        }
        return grade[linha][coluna].alternarMarcacao();
    }

    // ---- Consultas de estado (sem expor Celula) ----

    public boolean isRevelada(int linha, int coluna) {
        return grade[linha][coluna].isRevelada();
    }

    public boolean isMarcada(int linha, int coluna) {
        return grade[linha][coluna].isMarcada();
    }

    public boolean isMinada(int linha, int coluna) {
        return grade[linha][coluna].isMinada();
    }

    public int getMinasVizinhas(int linha, int coluna) {
        return grade[linha][coluna].getMinasVizinhas();
    }

    /**
     * O jogador vence quando todas as celulas que NAO sao minas estiverem
     * reveladas (regra classica do Campo Minado).
     */
    public boolean verificarVitoria() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                Celula celula = grade[l][c];
                if (!celula.isMinada() && !celula.isRevelada()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void revelarTodasAsMinas() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                if (grade[l][c].isMinada()) {
                    grade[l][c].revelar();
                }
            }
        }
    }

    public int getLinhas() {
        return linhas;
    }

    public int getColunas() {
        return colunas;
    }

    public int getTotalMinas() {
        return totalMinas;
    }

    /**
     * Ponto de acesso usado apenas pelos testes unitarios (mesmo pacote),
     * para poder inspecionar/objetos Celula sem quebrar o encapsulamento
     * em relacao ao restante da aplicacao (JogoCampoMinado nunca chama
     * este metodo).
     */
    Celula getCelula(int linha, int coluna) {
        return grade[linha][coluna];
    }
}
