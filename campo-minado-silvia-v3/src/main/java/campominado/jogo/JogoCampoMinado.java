package campominado.jogo;

import campominado.modelo.Tabuleiro;

import java.util.Scanner;

/**
 * Controla o laco principal de uma partida de Campo Minado no console.
 *
 * Importante (regra de arquitetura do enunciado): esta classe conversa
 * exclusivamente com Tabuleiro. Ela nunca importa nem manipula a classe
 * Celula — toda pergunta sobre o estado de uma posicao (revelada, marcada,
 * minada, minas vizinhas) e feita atraves de metodos de consulta do
 * proprio Tabuleiro.
 */
public class JogoCampoMinado {

    private final Scanner leitor;

    public JogoCampoMinado() {
        this.leitor = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("=== CAMPO MINADO DA SILVIA ===");
        Tabuleiro tabuleiro = configurarTabuleiro();

        boolean jogoAcabou = false;
        boolean vitoria = false;

        while (!jogoAcabou) {
            exibirTabuleiro(tabuleiro, false);
            System.out.println();
            System.out.print("Comando (r linha coluna = revelar | m linha coluna = marcar | s = sair): ");

            String[] entrada = leitor.nextLine().trim().split("\\s+");
            if (entrada.length == 0 || entrada[0].isEmpty()) {
                continue;
            }

            String comando = entrada[0].toLowerCase();

            if (comando.equals("s")) {
                System.out.println("Jogo encerrado pelo jogador.");
                return;
            }

            if (!comando.equals("r") && !comando.equals("m")) {
                System.out.println("Comando invalido. Use 'r', 'm' ou 's'.");
                continue;
            }

            if (entrada.length < 3) {
                System.out.println("Informe linha e coluna. Ex.: r 2 3");
                continue;
            }

            int linha;
            int coluna;
            try {
                linha = Integer.parseInt(entrada[1]);
                coluna = Integer.parseInt(entrada[2]);
            } catch (NumberFormatException e) {
                System.out.println("Linha e coluna devem ser numeros.");
                continue;
            }

            if (linha < 0 || linha >= tabuleiro.getLinhas() || coluna < 0 || coluna >= tabuleiro.getColunas()) {
                System.out.println("Posicao fora do tabuleiro.");
                continue;
            }

            if (comando.equals("m")) {
                tabuleiro.alternarMarcacao(linha, coluna);
                continue;
            }

            // comando "r": revelar
            if (tabuleiro.isMarcada(linha, coluna)) {
                System.out.println("Essa celula esta marcada com bandeira. Desmarque antes de revelar.");
                continue;
            }

            tabuleiro.revelar(linha, coluna);

            if (tabuleiro.isMinada(linha, coluna)) {
                jogoAcabou = true;
                vitoria = false;
            } else if (tabuleiro.verificarVitoria()) {
                jogoAcabou = true;
                vitoria = true;
            }
        }

        exibirTabuleiro(tabuleiro, true);
        System.out.println();
        if (vitoria) {
            System.out.println("Parabens! Voce revelou todas as celulas seguras. VITORIA!");
        } else {
            tabuleiro.revelarTodasAsMinas();
            exibirTabuleiro(tabuleiro, true);
            System.out.println("Voce clicou em uma mina. DERROTA!");
        }
    }

    private Tabuleiro configurarTabuleiro() {
        int linhas = lerInteiro("Numero de linhas: ", 1, 50);
        int colunas = lerInteiro("Numero de colunas: ", 1, 50);

        int maxMinas = (linhas * colunas) - 1;
        int minas = lerInteiro("Numero de minas (max " + maxMinas + "): ", 1, maxMinas);

        return new Tabuleiro(linhas, colunas, minas);
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            System.out.print(mensagem);
            String linha = leitor.nextLine().trim();
            try {
                int valor = Integer.parseInt(linha);
                if (valor < minimo || valor > maximo) {
                    System.out.println("Valor deve estar entre " + minimo + " e " + maximo + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero valido.");
            }
        }
    }

    private void exibirTabuleiro(Tabuleiro tabuleiro, boolean fimDeJogo) {
        System.out.println();
        System.out.print("    ");
        for (int c = 0; c < tabuleiro.getColunas(); c++) {
            System.out.printf("%3d", c);
        }
        System.out.println();

        for (int l = 0; l < tabuleiro.getLinhas(); l++) {
            System.out.printf("%3d ", l);
            for (int c = 0; c < tabuleiro.getColunas(); c++) {
                System.out.print(" " + simboloDaCelula(tabuleiro, l, c, fimDeJogo) + " ");
            }
            System.out.println();
        }
    }

    private String simboloDaCelula(Tabuleiro tabuleiro, int linha, int coluna, boolean fimDeJogo) {
        boolean revelada = tabuleiro.isRevelada(linha, coluna);
        boolean marcada = tabuleiro.isMarcada(linha, coluna);

        if (!revelada) {
            if (marcada) {
                return "F";
            }
            return fimDeJogo && tabuleiro.isMinada(linha, coluna) ? "*" : ".";
        }

        if (tabuleiro.isMinada(linha, coluna)) {
            return "*";
        }

        int minasVizinhas = tabuleiro.getMinasVizinhas(linha, coluna);
        return minasVizinhas == 0 ? " " : String.valueOf(minasVizinhas);
    }
}
