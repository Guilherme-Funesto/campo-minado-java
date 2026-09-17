package view;

import controller.AcoesJogador;
import model.LeituraTabuleiro;
import model.Tabuleiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

/**
 * VIEW da arquitetura MVC: cuida só de desenhar a tela e capturar
 * interações do usuário. Nunca decide o que um clique "significa" em
 * termos de regra de jogo — ela apenas repassa o clique para quem
 * implementa {@link AcoesJogador} (o Controller) e espera ser chamada de
 * volta para atualizar o que aparece na tela.
 */
public class CampoMinadoView extends JFrame {

    private static final Color COR_FUNDO = new Color(24, 25, 31);
    private static final Color COR_FUNDO_CLARO = new Color(38, 40, 50);
    private static final Color COR_DESTAQUE = new Color(74, 166, 255);

    // Célula OCULTA: escura e "elevada" — ainda não foi clicada.
    private static final Color COR_CELULA_OCULTA = new Color(70, 82, 112);
    private static final Color COR_CELULA_OCULTA_HOVER = new Color(92, 112, 150);
    private static final Color COR_BORDA_OCULTA = new Color(116, 132, 172);

    // Célula REVELADA: clara e "afundada" — contraste forte e
    // inconfundível com a célula oculta, como no Campo Minado clássico.
    private static final Color COR_CELULA_REVELADA = new Color(228, 228, 233);
    private static final Color COR_BORDA_REVELADA = new Color(195, 195, 202);
    private static final Color COR_TEXTO_SOBRE_REVELADA = new Color(40, 40, 45);

    private static final Color COR_MINA = new Color(255, 82, 82);
    private static final Color COR_MINA_FUNDO = new Color(78, 24, 28);
    private static final Color COR_VITORIA = new Color(66, 214, 115);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(245, 247, 252);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(180, 186, 205);
    private static final Color COR_BORDA = new Color(92, 100, 122);
    private static final Color COR_CARD = new Color(43, 46, 58);
    private static final Color COR_CARD_HOVER = new Color(58, 64, 82);
    private static final Color COR_BANDEIRA = new Color(255, 202, 64);

    private static final String[] TEMAS_FUNDO = {
            "Escuro", "Claro", "Campo", "Terminal Retrô", "Halloween",
            "Azul", "Roxo", "Vermelho", "Laranja", "Rosa", "Turquesa"
    };
    private static final String[] TEMAS_TABULEIRO = {
            "Clássico", "Noite", "Verde", "Azul", "Roxo", "Vermelho", "Laranja", "Rosa", "Turquesa"
    };
    private static final String[] TEMPOS_JOGO = {"Sem limite", "1 minuto", "2 minutos", "3 minutos", "5 minutos"};
    private static final String[] SKINS_BANDEIRA = {"Bandeira", "Estrela", "Coração", "Alfinete"};

    // Chaves usadas para guardar as preferências do jogador entre novos jogos
    // e também entre diferentes execuções do programa.
    private static final String PREF_TEMA_FUNDO = "temaFundo";
    private static final String PREF_TEMA_TABULEIRO = "temaTabuleiro";
    private static final String PREF_TEMPO = "tempoJogo";
    private static final String PREF_BANDEIRA = "skinBandeira";

    private static final Font FONTE_CELULA = new Font("Trebuchet MS", Font.BOLD, 20);
    private static final Font FONTE_TITULO = new Font("Trebuchet MS", Font.BOLD, 30);
    private static final Font FONTE_SUBTITULO = new Font("Trebuchet MS", Font.BOLD, 17);
    private static final Font FONTE_NORMAL = new Font("Trebuchet MS", Font.PLAIN, 14);
    private static final Font FONTE_NUMERO = new Font("Consolas", Font.BOLD, 19);
    private static final Font FONTE_PEQUENA = new Font("Trebuchet MS", Font.PLAIN, 12);

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
    private JLabel lblEscudos;
    private JProgressBar barraProgresso;

    private JComboBox<String> comboTemaFundo;
    private JComboBox<String> comboTemaTabuleiro;
    private JComboBox<String> comboTempo;
    private JComboBox<String> comboSkinBandeira;

    private final Preferences preferencias = Preferences.userNodeForPackage(CampoMinadoView.class);

    private String simboloBandeira = EMOJI_BANDEIRA;
    private String skinBandeiraAtual = SKINS_BANDEIRA[0];
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

        // Botão de tela cheia também disponível antes de iniciar uma partida.
        JPanel painelTopoInicial = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        painelTopoInicial.setBackground(COR_FUNDO);

        JButton btnTelaCheiaInicial = new JButton(telaCheia ? "Sair da tela cheia" : "Tela cheia");
        btnTelaCheiaInicial.setFont(FONTE_NORMAL);
        btnTelaCheiaInicial.setForeground(COR_TEXTO_PRINCIPAL);
        btnTelaCheiaInicial.setBackground(COR_FUNDO_CLARO);
        btnTelaCheiaInicial.setFocusPainted(false);
        btnTelaCheiaInicial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTelaCheiaInicial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnTelaCheiaInicial.addActionListener(e -> {
            alternarTelaCheia();
            btnTelaCheiaInicial.setText(telaCheia ? "Sair da tela cheia" : "Tela cheia");
        });
        painelTopoInicial.add(btnTelaCheiaInicial);
        add(painelTopoInicial, BorderLayout.NORTH);

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
        painelConteudo.add(Box.createVerticalStrut(15));

        JButton btnPersonalizado = new JButton("Tabuleiro personalizado");
        btnPersonalizado.setFont(FONTE_NORMAL);
        btnPersonalizado.setForeground(COR_TEXTO_PRINCIPAL);
        btnPersonalizado.setBackground(COR_FUNDO_CLARO);
        btnPersonalizado.setFocusPainted(false);
        btnPersonalizado.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPersonalizado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPersonalizado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));
        btnPersonalizado.addActionListener(e -> mostrarDialogoTabuleiroPersonalizado());
        painelConteudo.add(btnPersonalizado);

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

        if (!telaCheia) {
            pack();
            setLocationRelativeTo(null);
        }
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
        configurarComboPersistente(comboTemaFundo, PREF_TEMA_FUNDO, TEMAS_FUNDO[0]);
        painel.add(linha1);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha2 = criarLinhaSelecao("Cor do tabuleiro:", TEMAS_TABULEIRO);
        comboTemaTabuleiro = (JComboBox<String>) linha2.getClientProperty("combo");
        configurarComboPersistente(comboTemaTabuleiro, PREF_TEMA_TABULEIRO, TEMAS_TABULEIRO[0]);
        painel.add(linha2);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha3 = criarLinhaSelecao("Tempo rápido:", TEMPOS_JOGO);
        comboTempo = (JComboBox<String>) linha3.getClientProperty("combo");
        configurarComboPersistente(comboTempo, PREF_TEMPO, TEMPOS_JOGO[0]);
        painel.add(linha3);
        painel.add(Box.createVerticalStrut(10));

        JPanel linha4 = criarLinhaSelecao("Bandeira:", SKINS_BANDEIRA);
        comboSkinBandeira = (JComboBox<String>) linha4.getClientProperty("combo");
        configurarComboPersistente(comboSkinBandeira, PREF_BANDEIRA, SKINS_BANDEIRA[0]);
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

    /**
     * Restaura a última opção escolhida e salva automaticamente qualquer
     * nova seleção feita pelo jogador.
     */
    private void configurarComboPersistente(JComboBox<String> combo, String chave, String valorPadrao) {
        String valorSalvo = preferencias.get(chave, valorPadrao);

        boolean opcaoValida = false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(valorSalvo)) {
                opcaoValida = true;
                break;
            }
        }

        combo.setSelectedItem(opcaoValida ? valorSalvo : valorPadrao);

        combo.addActionListener(e -> {
            Object selecionado = combo.getSelectedItem();
            if (selecionado != null) {
                preferencias.put(chave, selecionado.toString());
            }
        });
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


    /**
     * Permite que o jogador escolha livremente o tamanho do tabuleiro.
     * Os limites evitam tabuleiros pequenos demais ou grandes demais para a tela.
     */
    private void mostrarDialogoTabuleiroPersonalizado() {
        JSpinner spinnerLinhas = new JSpinner(new SpinnerNumberModel(9, 5, 24, 1));
        JSpinner spinnerColunas = new JSpinner(new SpinnerNumberModel(9, 5, 30, 1));
        JSpinner spinnerMinas = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));
        JSpinner spinnerEscudos = new JSpinner(new SpinnerNumberModel(1, 0, 50, 1));

        JPanel painel = new JPanel(new GridLayout(4, 2, 10, 10));
        painel.add(new JLabel("Linhas:"));
        painel.add(spinnerLinhas);
        painel.add(new JLabel("Colunas:"));
        painel.add(spinnerColunas);
        painel.add(new JLabel("Minas:"));
        painel.add(spinnerMinas);
        painel.add(new JLabel("Escudos:"));
        painel.add(spinnerEscudos);

        while (true) {
            int resultado = JOptionPane.showConfirmDialog(
                    this,
                    painel,
                    "Tabuleiro personalizado",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (resultado != JOptionPane.OK_OPTION) {
                return;
            }

            int linhas = (Integer) spinnerLinhas.getValue();
            int colunas = (Integer) spinnerColunas.getValue();
            int minas = (Integer) spinnerMinas.getValue();
            int escudos = (Integer) spinnerEscudos.getValue();
            int quantidadeCelulas = linhas * colunas;

            if (minas >= quantidadeCelulas) {
                JOptionPane.showMessageDialog(
                        this,
                        "A quantidade de minas deve ser menor que o total de células (" + quantidadeCelulas + ").",
                        "Configuração inválida",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            int celulasSeguras = quantidadeCelulas - minas;
            if (escudos > celulasSeguras) {
                JOptionPane.showMessageDialog(
                        this,
                        "A quantidade de escudos não pode ser maior que o total de células seguras (" + celulasSeguras + ").",
                        "Configuração inválida",
                        JOptionPane.WARNING_MESSAGE
                );
                continue;
            }

            if (ouvinte != null) {
                ouvinte.aoEscolherDificuldadePersonalizada(linhas, colunas, minas, escudos);
            }
            return;
        }
    }

    private void mostrarTutorial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Painel principal em BorderLayout: o botão Voltar fica sempre
        // reservado na parte inferior e não é empurrado para fora da janela.
        JPanel painel = new JPanel(new BorderLayout(0, 15));
        painel.setBackground(COR_FUNDO);
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        JLabel titulo = new JLabel("Como jogar Campo Minado", SwingConstants.CENTER);
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(COR_TEXTO_PRINCIPAL);
        painel.add(titulo, BorderLayout.NORTH);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(COR_FUNDO);

        String texto = "1. Escolha uma dificuldade pronta ou crie um tabuleiro personalizado.\n"
                + "2. Clique com o botão esquerdo para revelar uma célula.\n"
                + "3. Clique com o botão direito para marcar/desmarcar uma bandeira.\n"
                + "4. Revele todas as células sem minas para vencer.\n"
                + "5. Algumas células seguras podem esconder escudos. Ao encontrar um, ele protege você de uma mina.\n"
                + "6. Nas dificuldades prontas existe 1 escudo por partida; no modo personalizado você escolhe a quantidade.\n"
                + "7. O escudo vale somente na partida atual e é consumido quando protege você.\n"
                + "8. O tempo selecionado limita a partida; se chegar a zero, você perde.\n";

        JTextArea area = new JTextArea(texto, 10, 42);
        area.setFont(FONTE_NORMAL);
        area.setForeground(COR_TEXTO_PRINCIPAL);
        area.setBackground(COR_FUNDO_CLARO);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollTexto = new JScrollPane(area);
        scrollTexto.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        scrollTexto.setBackground(COR_FUNDO_CLARO);
        scrollTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(scrollTexto);
        conteudo.add(Box.createVerticalStrut(15));

        JLabel dicas = new JLabel("Dicas: use bandeiras para marcar minas e tente abrir áreas sem números.");
        dicas.setFont(FONTE_PEQUENA);
        dicas.setForeground(COR_TEXTO_SECUNDARIO);
        dicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        conteudo.add(dicas);

        painel.add(conteudo, BorderLayout.CENTER);

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(COR_TEXTO_PRINCIPAL);
        voltar.setBackground(COR_FUNDO_CLARO);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(8, 22, 8, 22)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rodape.setBackground(COR_FUNDO);
        rodape.add(voltar);
        painel.add(rodape, BorderLayout.SOUTH);

        add(painel, BorderLayout.CENTER);
        pack();

        // Mantém a tela confortável e garante que o rodapé seja visível
        // mesmo quando o tamanho preferido muda entre sistemas operacionais.
        int larguraMinima = 460;
        int alturaMinima = 480;
        if (getWidth() < larguraMinima || getHeight() < alturaMinima) {
            setSize(Math.max(getWidth(), larguraMinima), Math.max(getHeight(), alturaMinima));
        }

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
                corDestaque = new Color(35, 125, 230);
                corBorda = new Color(180, 180, 190);
            } else if ("Campo".equals(tema)) {
                corFundo = new Color(25, 35, 25);
                corFundoClaro = new Color(45, 65, 45);
                corTextoPrincipal = new Color(220, 230, 200);
                corTextoSecundario = new Color(170, 190, 150);
                corCard = new Color(35, 55, 35);
                corCardHover = new Color(55, 75, 55);
                corDestaque = new Color(145, 230, 120);
                corBorda = new Color(60, 80, 60);
            } else if ("Terminal Retrô".equals(tema)) {
                corFundo = new Color(0, 10, 0);
                corFundoClaro = new Color(5, 25, 5);
                corTextoPrincipal = new Color(70, 255, 70);
                corTextoSecundario = new Color(50, 190, 50);
                corCard = new Color(5, 30, 5);
                corCardHover = new Color(10, 50, 10);
                corDestaque = new Color(95, 255, 105);
                corBorda = new Color(40, 150, 40);
            } else if ("Halloween".equals(tema)) {
                corFundo = new Color(33, 18, 27);
                corFundoClaro = new Color(58, 36, 28);
                corTextoPrincipal = new Color(255, 236, 214);
                corTextoSecundario = new Color(224, 176, 118);
                corCard = new Color(70, 43, 31);
                corCardHover = new Color(98, 60, 40);
                corDestaque = new Color(255, 153, 45);
                corBorda = new Color(120, 80, 55);
            } else if ("Azul".equals(tema)) {
                corFundo = new Color(18, 29, 48);
                corFundoClaro = new Color(28, 45, 72);
                corTextoPrincipal = new Color(225, 238, 255);
                corTextoSecundario = new Color(155, 185, 225);
                corCard = new Color(24, 39, 63);
                corCardHover = new Color(38, 60, 92);
                corDestaque = new Color(70, 175, 255);
                corBorda = new Color(65, 95, 135);
            } else if ("Roxo".equals(tema)) {
                corFundo = new Color(31, 20, 43);
                corFundoClaro = new Color(49, 32, 65);
                corTextoPrincipal = new Color(242, 228, 255);
                corTextoSecundario = new Color(190, 160, 215);
                corCard = new Color(43, 28, 57);
                corCardHover = new Color(66, 43, 85);
                corDestaque = new Color(205, 115, 255);
                corBorda = new Color(105, 72, 130);
            } else if ("Vermelho".equals(tema)) {
                corFundo = new Color(45, 20, 22);
                corFundoClaro = new Color(68, 30, 33);
                corTextoPrincipal = new Color(255, 232, 232);
                corTextoSecundario = new Color(220, 165, 165);
                corCard = new Color(59, 26, 29);
                corCardHover = new Color(86, 39, 43);
                corDestaque = new Color(255, 90, 95);
                corBorda = new Color(135, 65, 70);
            } else if ("Laranja".equals(tema)) {
                corFundo = new Color(48, 30, 16);
                corFundoClaro = new Color(73, 45, 24);
                corTextoPrincipal = new Color(255, 239, 220);
                corTextoSecundario = new Color(225, 185, 145);
                corCard = new Color(62, 39, 21);
                corCardHover = new Color(91, 57, 30);
                corDestaque = new Color(255, 176, 65);
                corBorda = new Color(145, 95, 55);
            } else if ("Rosa".equals(tema)) {
                corFundo = new Color(48, 24, 38);
                corFundoClaro = new Color(72, 36, 56);
                corTextoPrincipal = new Color(255, 233, 246);
                corTextoSecundario = new Color(225, 170, 205);
                corCard = new Color(62, 31, 48);
                corCardHover = new Color(90, 46, 70);
                corDestaque = new Color(255, 115, 195);
                corBorda = new Color(145, 75, 112);
            } else if ("Turquesa".equals(tema)) {
                corFundo = new Color(15, 42, 43);
                corFundoClaro = new Color(24, 63, 64);
                corTextoPrincipal = new Color(225, 255, 252);
                corTextoSecundario = new Color(155, 215, 210);
                corCard = new Color(20, 54, 55);
                corCardHover = new Color(31, 79, 80);
                corDestaque = new Color(55, 235, 220);
                corBorda = new Color(60, 125, 125);
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
                corCelulaOcultaHover = new Color(43, 62, 94);
                corBordaOculta = new Color(70, 90, 120);
                corCelulaRevelada = new Color(55, 65, 80);
                corBordaRevelada = new Color(80, 95, 115);
                corTextoSobreRevelada = new Color(230, 230, 240);
                corMinaFundo = new Color(180, 40, 40);
            } else if ("Verde".equals(tema)) {
                corCelulaOculta = new Color(40, 70, 45);
                corCelulaOcultaHover = new Color(67, 112, 72);
                corBordaOculta = new Color(70, 105, 80);
                corCelulaRevelada = new Color(220, 235, 210);
                corBordaRevelada = new Color(155, 175, 145);
                corTextoSobreRevelada = new Color(25, 45, 25);
                corMinaFundo = new Color(170, 40, 40);
            } else if ("Azul".equals(tema)) {
                corCelulaOculta = new Color(42, 74, 120);
                corCelulaOcultaHover = new Color(66, 111, 176);
                corBordaOculta = new Color(78, 112, 165);
                corCelulaRevelada = new Color(218, 231, 247);
                corBordaRevelada = new Color(155, 183, 218);
                corTextoSobreRevelada = new Color(25, 45, 75);
                corMinaFundo = new Color(150, 35, 45);
            } else if ("Roxo".equals(tema)) {
                corCelulaOculta = new Color(82, 58, 112);
                corCelulaOcultaHover = new Color(123, 88, 165);
                corBordaOculta = new Color(125, 96, 158);
                corCelulaRevelada = new Color(235, 224, 245);
                corBordaRevelada = new Color(192, 165, 214);
                corTextoSobreRevelada = new Color(55, 35, 75);
                corMinaFundo = new Color(150, 35, 45);
            } else if ("Vermelho".equals(tema)) {
                corCelulaOculta = new Color(110, 48, 52);
                corCelulaOcultaHover = new Color(170, 73, 79);
                corBordaOculta = new Color(165, 82, 87);
                corCelulaRevelada = new Color(246, 224, 224);
                corBordaRevelada = new Color(218, 165, 165);
                corTextoSobreRevelada = new Color(80, 30, 32);
                corMinaFundo = new Color(90, 15, 20);
            } else if ("Laranja".equals(tema)) {
                corCelulaOculta = new Color(140, 82, 35);
                corCelulaOcultaHover = new Color(205, 121, 54);
                corBordaOculta = new Color(195, 125, 68);
                corCelulaRevelada = new Color(250, 232, 210);
                corBordaRevelada = new Color(220, 178, 132);
                corTextoSobreRevelada = new Color(90, 50, 20);
                corMinaFundo = new Color(150, 35, 35);
            } else if ("Rosa".equals(tema)) {
                corCelulaOculta = new Color(128, 62, 96);
                corCelulaOcultaHover = new Color(188, 91, 139);
                corBordaOculta = new Color(180, 102, 140);
                corCelulaRevelada = new Color(247, 224, 237);
                corBordaRevelada = new Color(218, 166, 195);
                corTextoSobreRevelada = new Color(78, 35, 58);
                corMinaFundo = new Color(150, 35, 50);
            } else if ("Turquesa".equals(tema)) {
                corCelulaOculta = new Color(30, 105, 105);
                corCelulaOcultaHover = new Color(51, 160, 156);
                corBordaOculta = new Color(65, 155, 155);
                corCelulaRevelada = new Color(215, 242, 240);
                corBordaRevelada = new Color(145, 205, 200);
                corTextoSobreRevelada = new Color(20, 65, 65);
                corMinaFundo = new Color(150, 35, 45);
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
            skinBandeiraAtual = skin != null ? skin : SKINS_BANDEIRA[0];
            if ("Estrela".equals(skinBandeiraAtual)) {
                simboloBandeira = "*";
            } else if ("Coração".equals(skinBandeiraAtual)) {
                simboloBandeira = "<3";
            } else if ("Alfinete".equals(skinBandeiraAtual)) {
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
        lblDim.setFont(new Font("Trebuchet MS", Font.BOLD, 23));
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

        if (!telaCheia) {
            pack();

            // No modo Iniciante o tabuleiro é baixo; sem esta proteção a janela
            // pode ficar pequena demais para o painel lateral em alguns Windows.
            int alturaMinimaJanela = 525;
            if (getHeight() < alturaMinimaJanela) {
                setSize(getWidth(), alturaMinimaJanela);
            }

            setLocationRelativeTo(null);
        }
        revalidate();
        repaint();
    }

    private JPanel criarPainelSuperior() {
        // O painel superior usa duas linhas: os botoes ficam na primeira e
        // a mensagem de status fica centralizada logo abaixo. Assim textos
        // maiores (como os avisos do escudo) nao ficam espremidos entre os botoes.
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JPanel painelBotoes = new JPanel(new BorderLayout());
        painelBotoes.setBackground(corFundo);

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

        JButton btnTelaCheia = new JButton(telaCheia ? "Sair da tela cheia" : "Tela cheia");
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

        painelBotoes.add(btnNovo, BorderLayout.WEST);
        painelBotoes.add(btnTelaCheia, BorderLayout.EAST);

        labelStatus = new JLabel("Boa sorte!", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_SUBTITULO);
        labelStatus.setForeground(corTextoSecundario);
        labelStatus.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        painel.add(painelBotoes, BorderLayout.NORTH);
        painel.add(labelStatus, BorderLayout.CENTER);

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

        // Garante espaço vertical suficiente para todas as estatísticas,
        // a barra de progresso e as instruções do mouse.
        // Antes a altura era 0, então o painel era comprimido no modo 9 x 9.
        int alturaMinima = 435;
        painel.setPreferredSize(new Dimension(largura, alturaMinima));
        painel.setMinimumSize(new Dimension(largura, alturaMinima));

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
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlEscudos = criarItemEstatistica("Escudos ativos", "0");
        lblEscudos = (JLabel) pnlEscudos.getClientProperty("valor");
        painel.add(pnlEscudos);
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

        // Reduz um pouco as células em tabuleiros personalizados maiores,
        // evitando que a janela ultrapasse a área útil da tela.
        int tamanhoCelula = 36;
        if (linhas > 16 || colunas > 20) {
            tamanhoCelula = 30;
        }
        if (linhas > 20 || colunas > 25) {
            tamanhoCelula = 24;
        }

        botoes = new JButton[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = criarBotaoCelula(i, j, tamanhoCelula);
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
    private JButton criarBotaoCelula(int linha, int coluna, int tamanhoCelula) {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(tamanhoCelula, tamanhoCelula));
        int tamanhoFonte = tamanhoCelula <= 24 ? 14 : (tamanhoCelula <= 30 ? 17 : 20);
        botao.setFont(new Font("Trebuchet MS", Font.BOLD, tamanhoFonte));
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

    private int calcularTamanhoIcone(JButton botao) {
        int base = botao.getPreferredSize() != null ? botao.getPreferredSize().width : botao.getWidth();
        if (base <= 0) {
            base = 32;
        }
        return Math.max(14, base - 8);
    }

    private Icon criarIconeBandeira(int tamanho) {
        String skin = skinBandeiraAtual != null ? skinBandeiraAtual : SKINS_BANDEIRA[0];
        if ("Estrela".equals(skin)) {
            return new EstrelaIcone(tamanho);
        }
        if ("Coração".equals(skin)) {
            return new CoracaoIcone(tamanho);
        }
        if ("Alfinete".equals(skin)) {
            return new AlfineteIcone(tamanho);
        }
        return new BandeiraIcone(tamanho);
    }

    private Icon criarIconeBomba(int tamanho) {
        return new BombaIcone(tamanho);
    }

    // ================================================================
    // ATUALIZAÇÕES CHAMADAS PELO CONTROLLER
    // ================================================================

    /** Redesenha uma célula com base no estado atual do tabuleiro. */
    public void atualizarCelula(int linha, int coluna, LeituraTabuleiro leitura) {
        JButton botao = botoes[linha][coluna];
        botao.putClientProperty("revelada", leitura.isRevelada(linha, coluna));

        if (leitura.isMarcada(linha, coluna)) {
            int tamanhoIcone = calcularTamanhoIcone(botao);
            botao.setText("");
            botao.setIcon(criarIconeBandeira(tamanhoIcone));
            botao.setDisabledIcon(criarIconeBandeira(tamanhoIcone));
            botao.setForeground(COR_BANDEIRA);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BANDEIRA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (!leitura.isRevelada(linha, coluna)) {
            botao.setText("");
            botao.setIcon(null);
            botao.setDisabledIcon(null);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaOculta, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (leitura.isMinada(linha, coluna)) {
            int tamanhoIcone = calcularTamanhoIcone(botao);
            botao.setText("");
            botao.setIcon(criarIconeBomba(tamanhoIcone));
            botao.setDisabledIcon(criarIconeBomba(tamanhoIcone));
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
            botao.setIcon(null);
            botao.setDisabledIcon(null);
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

    /** Atualiza quantos escudos o jogador possui nesta partida. */
    public void atualizarEscudos(int escudosAtivos, int totalEscudosPartida) {
        if (lblEscudos != null) {
            lblEscudos.setText(escudosAtivos + " / " + totalEscudosPartida);
        }
    }

    /** Destaca a célula onde um escudo foi encontrado. */
    public void mostrarEscudoEncontrado(int linha, int coluna, int escudosAtivos) {
        JButton botao = botoes[linha][coluna];
        int tamanhoIcone = calcularTamanhoIcone(botao);
        botao.setText("");
        botao.setIcon(new EscudoIcone(tamanhoIcone));
        botao.setDisabledIcon(new EscudoIcone(tamanhoIcone));
        botao.setBackground(new Color(31, 74, 122));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(110, 220, 255), 2),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));

        if (labelStatus != null) {
            labelStatus.setText("Escudo encontrado! Escudos ativos: " + escudosAtivos);
            labelStatus.setForeground(new Color(100, 190, 255));
        }
    }

    /** Informa que um escudo protegeu o jogador de uma mina. */
    public void mostrarEscudoUsado(int escudosRestantes) {
        if (labelStatus != null) {
            labelStatus.setText("Escudo usado! A mina foi marcada. Restantes: " + escudosRestantes);
            labelStatus.setForeground(new Color(100, 190, 255));
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

        // As minas exibidas após a derrota precisam ser tratadas como células
        // reveladas pela View. Assim, o efeito de hover não troca a cor vermelha
        // da mina pela cor de uma célula ainda oculta.
        botao.putClientProperty("revelada", Boolean.TRUE);

        int tamanhoIcone = calcularTamanhoIcone(botao);
        botao.setText("");
        botao.setIcon(criarIconeBomba(tamanhoIcone));
        botao.setDisabledIcon(criarIconeBomba(tamanhoIcone));
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