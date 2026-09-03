package campominado.modelo;

/**
 * Representa uma unica celula do tabuleiro do Campo Minado.
 *
 * Decisao de encapsulamento: os quatro atributos de estado exigidos pelo
 * enunciado (minada, revelada, marcada, minasVizinhas) sao privados.
 * Nenhuma classe externa altera esse estado diretamente — toda alteracao
 * passa por um metodo que aplica a regra de negocio correspondente:
 *  - marcarComoMinada() so deve ser usado na montagem do tabuleiro;
 *  - revelar() nao revela uma celula marcada com bandeira;
 *  - alternarMarcacao() nao marca uma celula ja revelada.
 * Isso garante que, por construcao, o objeto Celula nunca fica em um
 * estado inconsistente (ex.: revelada E marcada ao mesmo tempo).
 */
public class Celula {

    private final int linha;
    private final int coluna;

    private boolean minada;
    private boolean revelada;
    private boolean marcada;
    private int minasVizinhas;

    public Celula(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
        this.minada = false;
        this.revelada = false;
        this.marcada = false;
        this.minasVizinhas = 0;
    }

    // ---- Posicao (somente leitura, definida na criacao) ----

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    // ---- Minada ----

    public boolean isMinada() {
        return minada;
    }

    /**
     * Usado apenas durante o posicionamento inicial das minas pelo
     * Tabuleiro. Nao existe um "setMinada(boolean)" generico de proposito:
     * uma celula nunca deveria "deixar" de ser minada durante uma partida.
     */
    public void marcarComoMinada() {
        this.minada = true;
    }

    // ---- Revelada ----

    public boolean isRevelada() {
        return revelada;
    }

    /**
     * Revela a celula, respeitando a regra de que uma celula marcada com
     * bandeira nao pode ser revelada diretamente (o jogador precisa
     * desmarcar primeiro). Retorna true se a celula foi de fato revelada
     * agora; false se nao havia o que fazer (ja revelada ou marcada).
     */
    public boolean revelar() {
        if (marcada || revelada) {
            return false;
        }
        this.revelada = true;
        return true;
    }

    // ---- Marcada (bandeira) ----

    public boolean isMarcada() {
        return marcada;
    }

    /**
     * Alterna a bandeira da celula. Nao e possivel marcar uma celula ja
     * revelada. Retorna o novo estado de marcacao.
     */
    public boolean alternarMarcacao() {
        if (revelada) {
            return marcada;
        }
        this.marcada = !this.marcada;
        return this.marcada;
    }

    // ---- Minas vizinhas ----

    public int getMinasVizinhas() {
        return minasVizinhas;
    }

    public void setMinasVizinhas(int minasVizinhas) {
        if (minasVizinhas < 0 || minasVizinhas > 8) {
            throw new IllegalArgumentException(
                    "Quantidade de minas vizinhas invalida: " + minasVizinhas);
        }
        this.minasVizinhas = minasVizinhas;
    }

    /**
     * Uma celula "vazia" e uma celula sem mina e sem nenhuma mina vizinha.
     * E essa condicao que dispara o efeito cascata em Tabuleiro.revelar().
     */
    public boolean isVazia() {
        return !minada && minasVizinhas == 0;
    }

    @Override
    public String toString() {
        return "Celula{(" + linha + "," + coluna + ")"
                + ", minada=" + minada
                + ", revelada=" + revelada
                + ", marcada=" + marcada
                + ", minasVizinhas=" + minasVizinhas + "}";
    }
}
