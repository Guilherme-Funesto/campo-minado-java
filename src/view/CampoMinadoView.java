package view;

import controller.AcoesJogador;
import model.LeituraTabuleiro;
import model.Tabuleiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * VIEW da arquitetura MVC: cuida só de desenhar a tela e capturar
 * interações do usuário. Nunca decide o que um clique "significa" em
 * termos de regra de jogo — ela apenas repassa o clique para quem
 * implementa {@link AcoesJogador} (o Controller) e espera ser chamada de
 * volta para atualizar o que aparece na tela.
 */
public class CampoMinadoView extends JFrame {

    private static final Color COR_FUNDO = new Color(30, 30, 35);
    private static final Color COR_FUNDO_CLARO = new Color(45, 45, 52);
    private static final Color COR_DESTAQUE = new Color(70, 130, 180);

    // Célula OCULTA: escura e "elevada" — ainda não foi clicada.
    private static final Color COR_CELULA_OCULTA = new Color(72, 78, 96);
    private static final Color COR_CELULA_OCULTA_HOVER = new Color(90, 97, 118);
    private static final Color COR_BORDA_OCULTA = new Color(100, 107, 128);

    // Célula REVELADA: clara e "afundada" — contraste forte e
    // inconfundível com a célula oculta, como no Campo Minado clássico.
    private static final Color COR_CELULA_REVELADA = new Color(228, 228, 233);
    private static final Color COR_BORDA_REVELADA = new Color(195, 195, 202);
    private static final Color COR_TEXTO_SOBRE_REVELADA = new Color(40, 40, 45);

    private static final Color COR_MINA = new Color(220, 60, 60);
    private static final Color COR_MINA_FUNDO = new Color(60, 20, 20);
    private static final Color COR_VITORIA = new Color(50, 180, 80);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(230, 230, 235);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(150, 150, 160);
    private static final Color COR_BORDA = new Color(80, 80, 90);
    private static final Color COR_CARD = new Color(50, 50, 58);
    private static final Color COR_CARD_HOVER = new Color(65, 65, 78);
    private static final Color COR_BANDEIRA = new Color(230, 180, 50);

    private static final String[] TEMAS_FUNDO = {"Escuro", "Claro", "Campo", "Terminal Retrô"};
    private static final String[] TEMAS_TABULEIRO = {"Clássico", "Noite", "Verde"};
    private static final String[] TEMPOS_JOGO = {"Sem limite", "1 minuto", "2 minutos", "3 minutos", "5 minutos"};
    private static final String[] SKINS_BANDEIRA = {"Bandeira", "Estrela", "Coração", "Alfinete"};

    private static final Font FONTE_CELULA = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_NUMERO = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    private static final String EMOJI_BOMBA = "*";
    private static final String EMOJI_BANDEIRA = "F";
    private static final String EMOJI_TROFEU = "V";
    private static final String EMOJI_EXPLOSAO = "X";
    private static final String EMOJI_RELOGIO = "T";
    private static final String EMOJI_JOGADA = ">";
    // Antes havia um "quadradinho" (\u25A0) usado como ícone de estatística.
    // Trocado pelo emoji de bomba, como pedido.
    private static final String EMOJI_ICONE_ESTATISTICA = EMOJI_BOMBA;

    // Esquema clássico do Campo Minado, pensado para boa leitura sobre o
    // fundo claro (COR_CELULA_REVELADA) da célula já revelada.
    private static final Color[] CORES_NUMEROS = {
            null,
            new Color(25, 118, 210),   // 1 - azul
            new Color(56, 142, 60),    // 2 - verde
            new Color(211, 47, 47),    // 3 - vermelho
            new Color(13, 71, 161),    // 4 - azul-marinho
            new Color(136, 14, 14),    // 5 - vinho
            new Color(0, 131, 143),    // 6 - teal
            new Color(33, 33, 33),     // 7 - preto
            new Color(97, 97, 97)      // 8 - cinza-escuro
    };

    private AcoesJogador ouvinte;
    private JButton[][] botoes;
    private JLabel labelStatus;

    private JLabel lblTempo;
    private JLabel lblMinasRestantes;
    private JLabel lblCelulasReveladas;
    private JLabel lblJogadas;
    private JProgressBar barraProgresso;

    private JComboBox<String> comboTemaFundo;
    private JComboBox<String> comboTemaTabuleiro;
    private JComboBox<String> comboTempo;
    private JComboBox<String> comboSkinBandeira;

    private String simboloBandeira = EMOJI_BANDEIRA;
    private boolean telaCheia = false;
    private Rectangle limitesJanelaNormal;

    private Color corFundo = COR_FUNDO;
    private Color corFundoClaro = COR_FUNDO_CLARO;
    private Color corDestaque = COR_DESTAQUE;
    private Color corTextoPrincipal = COR_TEXTO_PRINCIPAL;
    private Color corTextoSecundario = COR_TEXTO_SECUNDARIO;
    private Color corCard = COR_CARD;
    private Color corCardHover = COR_CARD_HOVER;
    private Color corBorda = COR_BORDA;
    private Color corCelulaOculta = COR_CELULA_OCULTA;
    private Color corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
    private Color corBordaOculta = COR_BORDA_OCULTA;
    private Color corCelulaRevelada = COR_CELULA_REVELADA;
    private Color corBordaRevelada = COR_BORDA_REVELADA;
    private Color corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
    private Color corMinaFundo = COR_MINA_FUNDO;

    public CampoMinadoView() {
        super("Campo Minado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COR_FUNDO);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /** Define quem recebe os eventos de clique/escolha (o Controller). */
    public void setOuvinte(AcoesJogador ouvinte) {
        this.ouvinte = ouvinte;
    }

    // ================================================================
    // TELA INICIAL
    // ================================================================

    public void mostrarTelaInicial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(COR_FUNDO);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Campo Minado");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(titulo);

        JLabel subtitulo = new JLabel("Escolha sua dificuldade");
        subtitulo.setFont(FONTE_NORMAL);
        subtitulo.setForeground(COR_TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        painelConteudo.add(subtitulo);

        JPanel painelCards = new JPanel(new GridLayout(1, 3, 15, 0));
        painelCards.setBackground(COR_FUNDO);
        painelCards.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelCards.add(criarCardDificuldade("Iniciante", "9 x 9", "10 minas", 9, 9, 10));
        painelCards.add(criarCardDificuldade("Intermediário", "16 x 16", "40 minas", 16, 16, 40));
        painelCards.add(criarCardDificuldade("Avançado", "16 x 30", "99 minas", 16, 30, 99));

        painelConteudo.add(painelCards);

        JLabel dica = new JLabel("<html><center>Esquerdo: revelar - Direito: bandeira</center></html>");
        dica.setFont(FONTE_PEQUENA);
        dica.setForeground(COR_TEXTO_SECUNDARIO);
        dica.setAlignmentX(Component.CENTER_ALIGNMENT);
        dica.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        painelConteudo.add(dica);
        painelConteudo.add(Box.createVerticalStrut(20));
        painelConteudo.add(criarPainelOpcoes());

        painelCentral.add(painelConteudo);
        add(painelCentral, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelOpcoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel linha1 = criarLinhaSelecao("Tema de fundo:", TEMAS_FUNDO);
        comboTemaFundo = (JComboBox<String>) linha1.getClientProperty("combo");
        painel.add(linha1);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha2 = criarLinhaSelecao("Cor do tabuleiro:", TEMAS_TABULEIRO);
        comboTemaTabuleiro = (JComboBox<String>) linha2.getClientProperty("combo");
        painel.add(linha2);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha3 = criarLinhaSelecao("Tempo rápido:", TEMPOS_JOGO);
        comboTempo = (JComboBox<String>) linha3.getClientProperty("combo");
        painel.add(linha3);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha4 = criarLinhaSelecao("Bandeira:", SKINS_BANDEIRA);
        comboSkinBandeira = (JComboBox<String>) linha4.getClientProperty("combo");
        painel.add(linha4);
        painel.add(Box.createVerticalStrut(10));

        JButton btnTutorial = new JButton("Ver tutorial");
        btnTutorial.setFont(FONTE_NORMAL);
        btnTutorial.setForeground(COR_TEXTO_PRINCIPAL);
        btnTutorial.setBackground(COR_FUNDO_CLARO);
        btnTutorial.setFocusPainted(false);
        btnTutorial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnTutorial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutorial.addActionListener(e -> mostrarTutorial());
        btnTutorial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnTutorial.setBackground(COR_CARD_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnTutorial.setBackground(COR_FUNDO_CLARO);
            }
        });
        painel.add(btnTutorial);

        return painel;
    }

    private JPanel criarLinhaSelecao(String texto, String[] opcoes) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(COR_FUNDO);
        painel.setMaximumSize(new Dimension(320, 40));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_PEQUENA);
        lbl.setForeground(COR_TEXTO_SECUNDARIO);
        painel.add(lbl, BorderLayout.WEST);

        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setFont(FONTE_PEQUENA);
        combo.setBackground(COR_FUNDO_CLARO);
        combo.setForeground(COR_TEXTO_PRINCIPAL);
        combo.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        painel.add(combo, BorderLayout.EAST);
        painel.putClientProperty("combo", combo);

        return painel;
    }

    private void mostrarTutorial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Como jogar Campo Minado");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        String texto = "1. Escolha uma dificuldade e um tempo rápido.\n"
                + "2. Clique com o botão esquerdo para revelar uma célula.\n"
                + "3. Clique com o botão direito para marcar/desmarcar uma bandeira.\n"
                + "4. Revele todas as células sem minas para vencer.\n"
                + "5. Se explodir uma mina, o jogo termina em derrota.\n"
                + "6. O tempo selecionado limita a partida; se chegar a zero, você perde.\n";

        JTextArea area = new JTextArea(texto);
        area.setFont(FONTE_NORMAL);
        area.setForeground(COR_TEXTO_PRINCIPAL);
        area.setBackground(COR_FUNDO_CLARO);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(area);
        painel.add(Box.createVerticalStrut(15));

        JLabel dicas = new JLabel("Dicas: use bandeiras para marcar minas e tente abrir áreas sem números.");
        dicas.setFont(FONTE_PEQUENA);
        dicas.setForeground(COR_TEXTO_SECUNDARIO);
        dicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(dicas);
        painel.add(Box.createVerticalStrut(25));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(COR_TEXTO_PRINCIPAL);
        voltar.setBackground(COR_FUNDO_CLARO);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public int getTempoLimiteSegundosSelecionado() {
        if (comboTempo == null) {
            return 0;
        }
        String selecionado = (String) comboTempo.getSelectedItem();
        if (selecionado == null || selecionado.startsWith("Sem")) {
            return 0;
        }
        if (selecionado.contains("1 minuto")) {
            return 60;
        }
        if (selecionado.contains("2 minutos")) {
            return 120;
        }
        if (selecionado.contains("3 minutos")) {
            return 180;
        }
        if (selecionado.contains("5 minutos")) {
            return 300;
        }
        return 0;
    }

    public void aplicarTemaSelecionado() {
        if (comboTemaFundo != null) {
            String tema = (String) comboTemaFundo.getSelectedItem();
            if ("Claro".equals(tema)) {
                corFundo = new Color(245, 245, 250);
                corFundoClaro = new Color(230, 230, 235);
                corTextoPrincipal = new Color(25, 25, 30);
                corTextoSecundario = new Color(95, 95, 110);
                corCard = new Color(245, 245, 250);
                corCardHover = new Color(225, 225, 235);
                corDestaque = new Color(35, 100, 190);
                corBorda = new Color(180, 180, 190);
            } else if ("Campo".equals(tema)) {
                corFundo = new Color(25, 35, 25);
                corFundoClaro = new Color(45, 65, 45);
                corTextoPrincipal = new Color(220, 230, 200);
                corTextoSecundario = new Color(170, 190, 150);
                corCard = new Color(35, 55, 35);
                corCardHover = new Color(55, 75, 55);
                corDestaque = new Color(140, 200, 120);
                corBorda = new Color(60, 80, 60);
            } else if ("Terminal Retrô".equals(tema)) {
                corFundo = new Color(0, 10, 0);
                corFundoClaro = new Color(5, 25, 5);
                corTextoPrincipal = new Color(70, 255, 70);
                corTextoSecundario = new Color(50, 190, 50);
                corCard = new Color(5, 30, 5);
                corCardHover = new Color(10, 50, 10);
                corDestaque = new Color(80, 255, 80);
                corBorda = new Color(40, 150, 40);
            } else {
                corFundo = COR_FUNDO;
                corFundoClaro = COR_FUNDO_CLARO;
                corTextoPrincipal = COR_TEXTO_PRINCIPAL;
                corTextoSecundario = COR_TEXTO_SECUNDARIO;
                corCard = COR_CARD;
                corCardHover = COR_CARD_HOVER;
                corDestaque = COR_DESTAQUE;
                corBorda = COR_BORDA;
            }
        }

        if (comboTemaTabuleiro != null) {
            String tema = (String) comboTemaTabuleiro.getSelectedItem();
            if ("Noite".equals(tema)) {
                corCelulaOculta = new Color(20, 30, 45);
                corCelulaOcultaHover = new Color(35, 50, 75);
                corBordaOculta = new Color(70, 90, 120);
                corCelulaRevelada = new Color(55, 65, 80);
                corBordaRevelada = new Color(80, 95, 115);
                corTextoSobreRevelada = new Color(230, 230, 240);
                corMinaFundo = new Color(180, 40, 40);
            } else if ("Verde".equals(tema)) {
                corCelulaOculta = new Color(40, 70, 45);
                corCelulaOcultaHover = new Color(60, 95, 65);
                corBordaOculta = new Color(70, 105, 80);
                corCelulaRevelada = new Color(220, 235, 210);
                corBordaRevelada = new Color(155, 175, 145);
                corTextoSobreRevelada = new Color(25, 45, 25);
                corMinaFundo = new Color(170, 40, 40);
            } else {
                corCelulaOculta = COR_CELULA_OCULTA;
                corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
                corBordaOculta = COR_BORDA_OCULTA;
                corCelulaRevelada = COR_CELULA_REVELADA;
                corBordaRevelada = COR_BORDA_REVELADA;
                corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
                corMinaFundo = COR_MINA_FUNDO;
            }
        }

        if (comboSkinBandeira != null) {
            String skin = (String) comboSkinBandeira.getSelectedItem();
            if ("Estrela".equals(skin)) {
                simboloBandeira = "*";
            } else if ("Coração".equals(skin)) {
                simboloBandeira = "<3";
            } else if ("Alfinete".equals(skin)) {
                simboloBandeira = "!";
            } else {
                simboloBandeira = EMOJI_BANDEIRA;
            }
        }

        getContentPane().setBackground(corFundo);
    }

    private JPanel criarCardDificuldade(String titulo, String dimensao, String minasTexto,
                                         int linhas, int colunas, int minas) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(COR_DESTAQUE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblDim = new JLabel(dimensao);
        lblDim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDim.setForeground(COR_TEXTO_PRINCIPAL);
        lblDim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDim.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        card.add(lblDim);

        JLabel lblMinas = new JLabel(minasTexto);
        lblMinas.setFont(FONTE_NORMAL);
        lblMinas.setForeground(COR_TEXTO_SECUNDARIO);
        lblMinas.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblMinas);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COR_CARD_HOVER);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_DESTAQUE, 2),
                        BorderFactory.createEmptyBorder(19, 24, 19, 24)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COR_CARD);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COR_BORDA, 1),
                        BorderFactory.createEmptyBorder(20, 25, 20, 25)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (ouvinte != null) {
                    ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
                }
            }
        });

        return card;
    }

    // ================================================================
    // TELA DE JOGO
    // ================================================================

    /**
     * Monta a tela de jogo do zero para um tabuleiro de {@code linhas} x
     * {@code colunas}. Não recebe o {@link Tabuleiro}, apenas as
     * dimensões — quem decide o que cada célula mostra depois é sempre
     * o Controller, chamando {@link #atualizarCelula}.
     */
    public void iniciarTelaDeJogo(int linhas, int colunas, int totalMinas, int totalCelulas, int tempoLimiteSegundos) {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        add(criarPainelSuperior(), BorderLayout.NORTH);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 0));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        painelPrincipal.add(criarPainelTabuleiro(linhas, colunas), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelEstatisticas(totalMinas, totalCelulas), BorderLayout.EAST);

        add(painelPrincipal, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JButton btnNovo = new JButton("Novo Jogo");
        btnNovo.setFont(FONTE_NORMAL);
        btnNovo.setForeground(corTextoPrincipal);
        btnNovo.setBackground(corFundoClaro);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirNovoJogo();
            }
        });
        btnNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNovo.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnNovo.setBackground(corFundoClaro);
            }
        });

        labelStatus = new JLabel("Boa sorte!", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_SUBTITULO);
        labelStatus.setForeground(corTextoSecundario);

        JButton btnTelaCheia = new JButton("Tela cheia");
        btnTelaCheia.setFont(FONTE_NORMAL);
        btnTelaCheia.setForeground(corTextoPrincipal);
        btnTelaCheia.setBackground(corFundoClaro);
        btnTelaCheia.setFocusPainted(false);
        btnTelaCheia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnTelaCheia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTelaCheia.addActionListener(e -> {
            alternarTelaCheia();
            btnTelaCheia.setText(telaCheia ? "Sair da tela cheia" : "Tela cheia");
        });

        painel.add(btnNovo, BorderLayout.WEST);
        painel.add(labelStatus, BorderLayout.CENTER);
        painel.add(btnTelaCheia, BorderLayout.EAST);

        return painel;
    }

    private JPanel criarPainelEstatisticas(int totalMinas, int totalCelulas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundoClaro);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        int largura = 180 + Math.min(100, totalMinas * 2);
        painel.setPreferredSize(new Dimension(largura, 0));

        JLabel lblTitulo = new JLabel("Estatísticas");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(COR_DESTAQUE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

        JPanel pnlTempo = criarItemEstatistica("Tempo", "00:00");
        lblTempo = (JLabel) pnlTempo.getClientProperty("valor");
        painel.add(pnlTempo);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlMinas = criarItemEstatistica("Minas", String.valueOf(totalMinas));
        lblMinasRestantes = (JLabel) pnlMinas.getClientProperty("valor");
        painel.add(pnlMinas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlReveladas = criarItemEstatistica("Reveladas", "0 / " + totalCelulas);
        lblCelulasReveladas = (JLabel) pnlReveladas.getClientProperty("valor");
        painel.add(pnlReveladas);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlJogadas = criarItemEstatistica("Jogadas", "0");
        lblJogadas = (JLabel) pnlJogadas.getClientProperty("valor");
        painel.add(pnlJogadas);
        painel.add(Box.createVerticalStrut(20));

        JLabel lblProgTitulo = new JLabel("Progresso");
        lblProgTitulo.setFont(FONTE_NORMAL);
        lblProgTitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblProgTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblProgTitulo);

        barraProgresso = new JProgressBar(0, Math.max(totalCelulas, 1));
        barraProgresso.setValue(0);
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("0%");
        barraProgresso.setForeground(corDestaque);
        barraProgresso.setBackground(corFundo);
        barraProgresso.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        barraProgresso.setPreferredSize(new Dimension(150, 20));
        barraProgresso.setMaximumSize(new Dimension(150, 20));
        barraProgresso.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(barraProgresso);
        painel.add(Box.createVerticalStrut(15));

        painel.add(Box.createVerticalGlue());

        JLabel lblDica = new JLabel("<html><center>Esquerdo: revelar<br>Direito: bandeira</center></html>");
        lblDica.setFont(FONTE_PEQUENA);
        lblDica.setForeground(corTextoSecundario);
        lblDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDica);

        return painel;
    }

    /**
     * Cria um item de estatística (título + valor) como um único painel,
     * guardando a referência ao label de valor via putClientProperty para
     * que possa ser atualizado depois. (Antes o valor era retornado
     * "solto", sem o painel-pai ser adicionado à tela — corrigido aqui.)
     */
    private JPanel criarItemEstatistica(String titulo, String valorInicial) {
        JPanel painelItem = new JPanel();
        painelItem.setLayout(new BoxLayout(painelItem, BoxLayout.Y_AXIS));
        painelItem.setBackground(corFundoClaro);
        painelItem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_PEQUENA);
        lblTitulo.setForeground(corTextoSecundario);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(FONTE_NUMERO);
        lblValor.setForeground(corTextoPrincipal);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelItem.add(lblTitulo);
        painelItem.add(lblValor);
        painelItem.putClientProperty("valor", lblValor);

        return painelItem;
    }

    private JPanel criarPainelTabuleiro(int linhas, int colunas) {
        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        grade.setBackground(corFundo);

        botoes = new JButton[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = criarBotaoCelula(i, j);
                botoes[i][j] = botao;
                grade.add(botao);
            }
        }
        return grade;
    }

    /**
     * Cria o botão de uma célula. AQUI ESTAVA O BUG DA BANDEIRA: o código
     * original detectava o clique direito em mousePressed. Em trackpads
     * (Mac, e alguns drivers de notebook Windows/Linux) o clique direito
     * simulado por toque com dois dedos nem sempre reporta corretamente
     * qual botão foi pressionado no evento de "pressed" — só fica
     * confiável no evento de "released". Por isso o primeiro clique
     * direito costumava funcionar e os seguintes eram ignorados ou
     * tratados como clique esquerdo. A correção é ouvir mouseReleased.
     */
    private JButton criarBotaoCelula(int linha, int coluna) {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(36, 36));
        botao.setFont(FONTE_CELULA);
        botao.setFocusPainted(false);
        botao.setBackground(corCelulaOculta);
        botao.setForeground(corTextoPrincipal);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.putClientProperty("revelada", Boolean.FALSE);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Só aplica o realce de "hover" em células ainda ocultas;
                // caso contrário isso sobrescreveria a cor clara da
                // célula já revelada sempre que o mouse passasse por cima.
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOcultaHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOculta);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                if (ouvinte == null) {
                    return;
                }
                // e.getButton() é checado explicitamente além de
                // SwingUtilities.isRightMouseButton para cobrir cliques
                // direitos simulados por trackpad de forma confiável.
                boolean botaoDireito = SwingUtilities.isRightMouseButton(evento)
                        || evento.getButton() == MouseEvent.BUTTON3;
                if (botaoDireito) {
                    ouvinte.aoMarcarCelula(linha, coluna);
                } else if (SwingUtilities.isLeftMouseButton(evento)) {
                    ouvinte.aoRevelarCelula(linha, coluna);
                }
            }
        });
        return botao;
    }

    // ================================================================
    // ATUALIZAÇÕES CHAMADAS PELO CONTROLLER
    // ================================================================

    /** Redesenha uma célula com base no estado atual do tabuleiro. */
    public void atualizarCelula(int linha, int coluna, LeituraTabuleiro leitura) {
        JButton botao = botoes[linha][coluna];
        botao.putClientProperty("revelada", leitura.isRevelada(linha, coluna));

        if (leitura.isMarcada(linha, coluna)) {
            botao.setText(simboloBandeira);
            botao.setForeground(COR_BANDEIRA);
            botao.setBackground(COR_CELULA_OCULTA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BANDEIRA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (!leitura.isRevelada(linha, coluna)) {
            botao.setText("");
            botao.setBackground(COR_CELULA_OCULTA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaOculta, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (leitura.isMinada(linha, coluna)) {
            botao.setText("X");
            botao.setBackground(corMinaFundo);
            botao.setForeground(COR_MINA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_MINA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        } else {
            // Célula revelada e segura: fundo claro e "afundado",
            // nitidamente diferente do fundo escuro da célula oculta.
            botao.setBackground(corCelulaRevelada);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaRevelada, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            int vizinhas = leitura.getMinasVizinhas(linha, coluna);
            if (vizinhas == 0) {
                botao.setText("");
                botao.setForeground(corTextoSobreRevelada);
            } else {
                botao.setText(String.valueOf(vizinhas));
                botao.setForeground(CORES_NUMEROS[vizinhas]);
            }
        }
    }

    public void atualizarTempo(String texto) {
        if (lblTempo != null) {
            lblTempo.setText(texto);
        }
    }

    public void atualizarEstatisticas(int minasRestantes, int celulasReveladas, int totalCelulas, int jogadas) {
        if (lblMinasRestantes != null) {
            lblMinasRestantes.setText(String.valueOf(minasRestantes));
        }
        if (lblCelulasReveladas != null) {
            lblCelulasReveladas.setText(celulasReveladas + " / " + totalCelulas);
        }
        if (lblJogadas != null) {
            lblJogadas.setText(String.valueOf(jogadas));
        }

        int progresso = totalCelulas > 0 ? (int) ((celulasReveladas * 100.0) / totalCelulas) : 0;
        if (barraProgresso != null) {
            barraProgresso.setValue(celulasReveladas);
            barraProgresso.setString(progresso + "%");
            if (progresso < 30) {
                barraProgresso.setForeground(new Color(220, 80, 80));
            } else if (progresso < 70) {
                barraProgresso.setForeground(new Color(220, 180, 60));
            } else {
                barraProgresso.setForeground(COR_VITORIA);
            }
        }
    }

    public void mostrarDerrota() {
        labelStatus.setText("Voce perdeu!");
        labelStatus.setForeground(COR_MINA);
    }

    public void mostrarVitoria() {
        labelStatus.setText("Voce venceu!");
        labelStatus.setForeground(COR_VITORIA);
    }

    public void piscarFundoDeExplosao(boolean explodindo) {
        getContentPane().setBackground(explodindo ? COR_MINA_FUNDO : COR_FUNDO);
    }

    public void marcarMinaExplodida(int linha, int coluna) {
        JButton botao = botoes[linha][coluna];
        botao.setText("X");
        botao.setForeground(COR_MINA);
        botao.setBackground(COR_MINA_FUNDO);
        botao.setBorder(BorderFactory.createLineBorder(COR_MINA, 1));
    }

    public void destacarCelulaVencedora(int linha, int coluna) {
        botoes[linha][coluna].setBackground(new Color(40, 100, 60));
    }
    /** Mostra um aviso quando há mais marcações do que minas no tabuleiro. */
    public void mostrarAvisoExcessoBandeiras(int marcadas, int totalMinas) {
        if (labelStatus != null) {
            labelStatus.setText("Atenção: " + marcadas + " bandeiras para " + totalMinas + " minas!");
            labelStatus.setForeground(new Color(255, 180, 60));
        }
        JOptionPane.showMessageDialog(
                this,
                "Você marcou " + marcadas + " células, mas existem apenas " + totalMinas + " minas.",
                "Excesso de bandeiras",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /** Alterna entre janela normal e tela cheia. */
    private void alternarTelaCheia() {
        GraphicsDevice dispositivo = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        dispose();
        if (!telaCheia) {
            limitesJanelaNormal = getBounds();
            setUndecorated(true);
            setResizable(true);
            dispositivo.setFullScreenWindow(this);
            telaCheia = true;
        } else {
            dispositivo.setFullScreenWindow(null);
            setUndecorated(false);
            setResizable(false);
            if (limitesJanelaNormal != null) {
                setBounds(limitesJanelaNormal);
            }
            telaCheia = false;
        }
        setVisible(true);
    }

}