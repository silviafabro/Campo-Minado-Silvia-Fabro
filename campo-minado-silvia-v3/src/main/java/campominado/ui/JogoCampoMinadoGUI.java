package campominado.ui;

import campominado.modelo.Tabuleiro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Interface Grafica em Swing para o Campo Minado da Silvia.
 * Design moderno com gradiente vibrante, cards arredondados e elementos com emojis.
 */
public class JogoCampoMinadoGUI extends JFrame {

    private Tabuleiro tabuleiro;
    private JButton[][] botoes;
    private JPanel painelTabuleiro;
    private JPanel painelSelecao;
    private JPanel painelPrincipalJogo;

    // Estatisticas
    private JLabel lblStatus;
    private JLabel lblTempo;
    private JLabel lblMinas;
    private JLabel lblReveladas;
    private JLabel lblJogadas;

    private Timer timer;
    private int tempoSegundos;
    private int jogadas;
    private boolean jogoIniciado;
    private boolean jogoFinalizado;

    // Paleta de Cores do Design Moderno — rosa bebê e amarelo
    private static final Color COR_TEXTO_TITULO = new Color(120, 40, 70);
    private static final Color COR_CARD_BG = new Color(255, 251, 235);
    private static final Color COR_CELULA_OCULTA = new Color(255, 182, 204);
    private static final Color COR_CELULA_REVELADA = new Color(255, 244, 179);
    private static final Color COR_ACCENT_ROSA = new Color(219, 39, 119);

    public JogoCampoMinadoGUI() {
        setTitle("🌸 Campo da Silvia 💣");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        exibirTelaSelecao();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void exibirTelaSelecao() {
        if (painelPrincipalJogo != null) remove(painelPrincipalJogo);
        if (painelSelecao != null) remove(painelSelecao);

        PainelGradiente painelFundo = new PainelGradiente();
        painelFundo.setLayout(new BorderLayout(20, 20));
        painelFundo.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Container Central
        PainelArredondado cardCentral = new PainelArredondado(25, new Color(140, 50, 90, 190));
        cardCentral.setLayout(new BoxLayout(cardCentral, BoxLayout.Y_AXIS));
        cardCentral.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblTitulo = new JLabel("🌸 Campo da Silvia", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Escolha sua dificuldade", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSub.setForeground(new Color(220, 220, 240));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel gridDificuldade = new JPanel(new GridLayout(1, 3, 15, 0));
        gridDificuldade.setOpaque(false);
        gridDificuldade.setMaximumSize(new Dimension(500, 120));

        JButton btnFacil = criarCardDificuldade("Fácil", "9 × 9", "10 minas", new Color(255, 224, 130), 9, 9, 10);
        JButton btnMedia = criarCardDificuldade("Média", "16 × 16", "40 minas", new Color(255, 182, 204), 16, 16, 40);
        JButton btnDificil = criarCardDificuldade("Difícil", "16 × 30", "99 minas", COR_ACCENT_ROSA, 16, 30, 99);

        gridDificuldade.add(btnFacil);
        gridDificuldade.add(btnMedia);
        gridDificuldade.add(btnDificil);

        JLabel lblInstrucoes = new JLabel("🖱️ Botão esquerdo revela • Botão direito marca com 🌸", SwingConstants.CENTER);
        lblInstrucoes.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblInstrucoes.setForeground(new Color(200, 200, 220));
        lblInstrucoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPersonalizado = new JButton("⚙️ Modo Personalizado");
        btnPersonalizado.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPersonalizado.setForeground(Color.WHITE);
        btnPersonalizado.setBackground(COR_ACCENT_ROSA);
        btnPersonalizado.setFocusPainted(false);
        btnPersonalizado.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPersonalizado.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnPersonalizado.addActionListener(e -> abrirDialogoPersonalizado());

        cardCentral.add(lblTitulo);
        cardCentral.add(Box.createVerticalStrut(5));
        cardCentral.add(lblSub);
        cardCentral.add(Box.createVerticalStrut(20));
        cardCentral.add(gridDificuldade);
        cardCentral.add(Box.createVerticalStrut(20));
        cardCentral.add(lblInstrucoes);
        cardCentral.add(Box.createVerticalStrut(15));
        cardCentral.add(btnPersonalizado);

        painelFundo.add(cardCentral, BorderLayout.CENTER);
        painelSelecao = painelFundo;

        setContentPane(painelSelecao);
        revalidate();
        repaint();
        pack();
        setLocationRelativeTo(null);
    }

    private JButton criarCardDificuldade(String titulo, String tamanho, String minas, Color corAcento, int l, int c, int m) {
        JButton btn = new JButton("<html><center>"
                + "<font color='" + toHex(corAcento) + "'>━━━━━</font><br>"
                + "<font size='4'><b>" + titulo + "</b></font><br><br>"
                + "<font size='5'><b>" + tamanho + "</b></font><br>"
                + "<font size='2' color='#CCCCCC'>💣 " + minas + "</font>"
                + "</center></html>");
        btn.setBackground(new Color(255, 245, 225));
        btn.setForeground(new Color(90, 40, 60));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255, 200, 220), 2, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> iniciarJogo(l, c, m));
        return btn;
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private void abrirDialogoPersonalizado() {
        try {
            String strL = JOptionPane.showInputDialog(this, "Número de Linhas (5 a 30):", "10");
            if (strL == null) return;
            String strC = JOptionPane.showInputDialog(this, "Número de Colunas (5 a 40):", "10");
            if (strC == null) return;
            String strM = JOptionPane.showInputDialog(this, "Número de Minas:", "15");
            if (strM == null) return;

            int l = Integer.parseInt(strL);
            int c = Integer.parseInt(strC);
            int m = Integer.parseInt(strM);

            if (l < 5 || c < 5 || m < 1 || m >= l * c) {
                JOptionPane.showMessageDialog(this, "Configurações inválidas!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            iniciarJogo(l, c, m);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarJogo(int linhas, int colunas, int minas) {
        tabuleiro = new Tabuleiro(linhas, colunas, minas);
        botoes = new JButton[linhas][colunas];
        jogadas = 0;
        tempoSegundos = 0;
        jogoIniciado = false;
        jogoFinalizado = false;

        PainelGradiente painelFundo = new PainelGradiente();
        painelFundo.setLayout(new BorderLayout(15, 15));
        painelFundo.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Cabeçalho
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);

        JButton btnNovoJogo = criarBotaoEstilizado("← Novo Jogo", COR_ACCENT_ROSA);
        btnNovoJogo.addActionListener(e -> {
            pararTimer();
            exibirTelaSelecao();
        });

        lblStatus = new JLabel("✨ Boa sorte!", SwingConstants.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblStatus.setForeground(COR_TEXTO_TITULO);

        painelTopo.add(btnNovoJogo, BorderLayout.WEST);
        painelTopo.add(lblStatus, BorderLayout.CENTER);

        // Grade de botões
        PainelArredondado cardGrid = new PainelArredondado(20, new Color(255, 255, 255, 220));
        cardGrid.setLayout(new BorderLayout());
        cardGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelTabuleiro = new JPanel(new GridLayout(linhas, colunas, 3, 3));
        painelTabuleiro.setOpaque(false);
        inicializarGrade(linhas, colunas);
        cardGrid.add(painelTabuleiro, BorderLayout.CENTER);

        // Painel Lateral de Estatísticas
        PainelArredondado cardStats = new PainelArredondado(20, COR_CARD_BG);
        cardStats.setLayout(new BoxLayout(cardStats, BoxLayout.Y_AXIS));
        cardStats.setBorder(new EmptyBorder(15, 18, 15, 18));
        cardStats.setPreferredSize(new Dimension(160, 0));

        JLabel lblTituloStats = new JLabel("📊 Estatísticas", SwingConstants.CENTER);
        lblTituloStats.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloStats.setForeground(COR_ACCENT_ROSA);
        lblTituloStats.setAlignmentX(Component.CENTER_ALIGNMENT);

        int totalSeguro = (linhas * colunas) - minas;

        lblTempo = criarLabelStat("⏱️ Tempo", "00:00");
        lblMinas = criarLabelStat("💣 Minas", String.valueOf(minas));
        lblReveladas = criarLabelStat("🎯 Reveladas", "0 / " + totalSeguro);
        lblJogadas = criarLabelStat("🖱️ Jogadas", "0");

        cardStats.add(lblTituloStats);
        cardStats.add(Box.createVerticalStrut(15));
        cardStats.add(lblTempo);
        cardStats.add(Box.createVerticalStrut(12));
        cardStats.add(lblMinas);
        cardStats.add(Box.createVerticalStrut(12));
        cardStats.add(lblReveladas);
        cardStats.add(Box.createVerticalStrut(12));
        cardStats.add(lblJogadas);

        painelFundo.add(painelTopo, BorderLayout.NORTH);
        painelFundo.add(cardGrid, BorderLayout.CENTER);
        painelFundo.add(cardStats, BorderLayout.EAST);

        painelPrincipalJogo = painelFundo;
        setContentPane(painelPrincipalJogo);

        configurarTimer();

        revalidate();
        repaint();
        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarGrade(int linhas, int colunas) {
        int tamanhoCelula = (colunas > 20 || linhas > 15) ? 28 : 38;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(tamanhoCelula, tamanhoCelula));
                btn.setFont(new Font("SansSerif", Font.BOLD, tamanhoCelula / 2));
                btn.setBackground(COR_CELULA_OCULTA);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createEmptyBorder());

                final int l = i;
                final int c = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (jogoFinalizado) return;

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            cliqueEsquerdo(l, c);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            cliqueDireito(l, c);
                        }
                    }
                });

                botoes[i][j] = btn;
                painelTabuleiro.add(btn);
            }
        }
    }

    private void cliqueEsquerdo(int l, int c) {
        if (tabuleiro.isMarcada(l, c) || tabuleiro.isRevelada(l, c)) return;

        if (!jogoIniciado) {
            jogoIniciado = true;
            timer.start();
        }

        jogadas++;
        tabuleiro.revelar(l, c);
        atualizarInterface();

        if (tabuleiro.isMinada(l, c) && tabuleiro.isRevelada(l, c)) {
            jogoFinalizado = true;
            pararTimer();
            lblStatus.setText("💣 Você perdeu!");
            tabuleiro.revelarTodasAsMinas();
            atualizarInterface();
        } else if (tabuleiro.verificarVitoria()) {
            jogoFinalizado = true;
            pararTimer();
            lblStatus.setText("🎯 Você venceu!");
            atualizarInterface();
        }
    }

    private void cliqueDireito(int l, int c) {
        if (tabuleiro.isRevelada(l, c)) return;

        if (!jogoIniciado) {
            jogoIniciado = true;
            timer.start();
        }

        tabuleiro.alternarMarcacao(l, c);
        atualizarInterface();
    }

    private void atualizarInterface() {
        int reveladas = 0;
        int linhas = tabuleiro.getLinhas();
        int colunas = tabuleiro.getColunas();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton btn = botoes[i][j];
                boolean estRevelada = tabuleiro.isRevelada(i, j);
                boolean estMarcada = tabuleiro.isMarcada(i, j);
                boolean estMinada = tabuleiro.isMinada(i, j);

                if (estRevelada) {
                    btn.setBackground(COR_CELULA_REVELADA);
                    if (estMinada) {
                        btn.setText("💣");
                        btn.setBackground(new Color(255, 138, 173));
                    } else {
                        reveladas++;
                        int vizinhas = tabuleiro.getMinasVizinhas(i, j);
                        if (vizinhas > 0) {
                            btn.setText(String.valueOf(vizinhas));
                            btn.setForeground(obterCorNumero(vizinhas));
                        } else {
                            btn.setText("");
                        }
                    }
                } else if (estMarcada) {
                    btn.setText("🌸");
                    btn.setForeground(COR_ACCENT_ROSA);
                    btn.setBackground(COR_CELULA_OCULTA);
                } else {
                    btn.setText("");
                    btn.setBackground(COR_CELULA_OCULTA);
                }
            }
        }

        int totalSeguro = (linhas * colunas) - tabuleiro.getTotalMinas();
        lblReveladas.setText("<html><center>🎯 Reveladas<br><b>" + reveladas + " / " + totalSeguro + "</b></center></html>");
        lblJogadas.setText("<html><center>🖱️ Jogadas<br><b>" + jogadas + "</b></center></html>");
    }

    private Color obterCorNumero(int numero) {
        switch (numero) {
            case 1: return new Color(25, 118, 210);
            case 2: return new Color(56, 142, 60);
            case 3: return new Color(211, 47, 47);
            case 4: return new Color(123, 31, 162);
            case 5: return new Color(255, 111, 0);
            case 6: return new Color(0, 150, 136);
            default: return new Color(97, 97, 97);
        }
    }

    private JLabel criarLabelStat(String titulo, String valorInicial) {
        JLabel label = new JLabel("<html><center>" + titulo + "<br><b>" + valorInicial + "</b></center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(60, 60, 80));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton criarBotaoEstilizado(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(cor);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void configurarTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            tempoSegundos++;
            int min = tempoSegundos / 60;
            int seg = tempoSegundos % 60;
            lblTempo.setText(String.format("<html><center>⏱️ Tempo<br><b>%02d:%02d</b></center></html>", min, seg));
        });
    }

    private void pararTimer() {
        if (timer != null) timer.stop();
    }

    // Painéis Customizados de Design
    private static class PainelGradiente extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 179, 205), getWidth(), getHeight(), new Color(255, 224, 130));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static class PainelArredondado extends JPanel {
        private final int raio;
        private final Color corFundo;

        public PainelArredondado(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(corFundo);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), raio, raio));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JogoCampoMinadoGUI());
    }
}
