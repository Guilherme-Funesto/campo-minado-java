package controller;

import model.Tabuleiro;
import view.CampoMinadoView;

import javax.swing.Timer;
import java.util.List;

/**
 * CONTROLLER da arquitetura MVC: é o único ponto que conhece tanto o
 * {@link Tabuleiro} (Model) quanto a {@link CampoMinadoView} (View).
 * Recebe notificações de clique da View através de {@link AcoesJogador},
 * aplica a jogada no Model e manda a View se redesenhar. A View nunca
 * toca no Model diretamente, e o Model nunca conhece a View.
 */
public class CampoMinadoController implements AcoesJogador {

    private final CampoMinadoView view;

    private Tabuleiro tabuleiro;
    private int totalMinas;
    private int totalCelulas;
    private int celulasReveladas;
    private int jogadas;
    private boolean jogoIniciado;
    private long tempoInicio;
    private int limiteSegundos;
    private Timer timerJogo;

    public CampoMinadoController(CampoMinadoView view) {
        this.view = view;
        this.view.setOuvinte(this);
    }

    public void iniciar() {
        view.mostrarTelaInicial();
        view.setVisible(true);
    }

    // ================================================================
    // AcoesJogador — chamado pela View
    // ================================================================

    @Override
    public void aoEscolherDificuldade(int linhas, int colunas, int minas) {
        this.tabuleiro = new Tabuleiro(linhas, colunas, minas);
        this.totalMinas = minas;
        this.totalCelulas = linhas * colunas - minas;
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;

        pararTimer();
        view.aplicarTemaSelecionado();

        this.limiteSegundos = view.getTempoLimiteSegundosSelecionado();
        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
    }

    @Override
    public void aoPedirNovoJogo() {
        pararTimer();
        view.mostrarTelaInicial();
    }

    @Override
    public void aoMarcarCelula(int linha, int coluna) {
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }
        tabuleiro.alternarMarcacao(linha, coluna);
        view.atualizarCelula(linha, coluna, tabuleiro);
        atualizarEstatisticasNaView();

        int marcadas = contarMarcadas();
        if (marcadas > totalMinas) {
            view.mostrarAvisoExcessoBandeiras(marcadas, totalMinas);
        }
    }

    @Override
    public void aoRevelarCelula(int linha, int coluna) {
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }

        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        if (limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return;
        }

        jogadas++;
        List<int[]> reveladas = tabuleiro.revelar(linha, coluna);
        celulasReveladas = contarCelulasReveladas();

        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    // ================================================================
    // Contagens e sincronização com a View
    // ================================================================

    private int contarCelulasReveladas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int contarMarcadas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMarcada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void atualizarEstatisticasNaView() {
        int restantes = totalMinas - contarMarcadas();
        view.atualizarEstatisticas(restantes, celulasReveladas, totalCelulas, jogadas);
    }

    // ================================================================
    // Timer do cronômetro
    // ================================================================

    private void iniciarTimer() {
        timerJogo = new Timer(1000, e -> atualizarTempo());
        timerJogo.start();
    }

    private void pararTimer() {
        if (timerJogo != null) {
            timerJogo.stop();
        }
    }

    private long obterSegundosPassados() {
        return (System.currentTimeMillis() - tempoInicio) / 1000;
    }

    private void atualizarTempo() {
        long segundosPassados = obterSegundosPassados();
        if (limiteSegundos > 0) {
            long restantes = Math.max(0, limiteSegundos - segundosPassados);
            view.atualizarTempo(String.format("-%02d:%02d", restantes / 60, restantes % 60));
            if (restantes <= 0) {
                encerrarPorTempo();
                return;
            }
        } else {
            view.atualizarTempo(String.format("%02d:%02d", segundosPassados / 60, segundosPassados % 60));
        }
    }

    private void encerrarPorTempo() {
        pararTimer();
        if (tabuleiro != null && !tabuleiro.isJogoEncerrado()) {
            tabuleiro = new Tabuleiro(tabuleiro.getLinhas(), tabuleiro.getColunas(), tabuleiro.getNumMinas());
            // Não reiniciamos o tabuleiro; apenas exibimos derrota devido ao tempo.
        }
        view.mostrarDerrota();
        labelStatusTempoEsgotado();
    }

    private void labelStatusTempoEsgotado() {
        view.mostrarDerrota();
    }

    // ================================================================
    // Animações (o Controller decide o ritmo; a View só desenha um passo)
    // ================================================================

    private void animarRevelacao(List<int[]> celulas, int indice, int atraso) {
        if (indice >= celulas.size()) {
            finalizarJogada();
            return;
        }
        int[] posicao = celulas.get(indice);
        view.atualizarCelula(posicao[0], posicao[1], tabuleiro);

        Timer timer = new Timer(atraso, e -> animarRevelacao(celulas, indice + 1, atraso));
        timer.setRepeats(false);
        timer.start();
    }

    private void finalizarJogada() {
        atualizarEstatisticasNaView();

        if (!tabuleiro.isJogoEncerrado()) {
            return;
        }

        pararTimer();

        if (tabuleiro.isDerrota()) {
            view.mostrarDerrota();
            animarExplosao();
        } else {
            view.mostrarVitoria();
            animarVitoria();
        }
    }

    private void animarExplosao() {
        Timer piscar = new Timer(100, null);
        int[] contador = {0};
        piscar.addActionListener(e -> {
            contador[0]++;
            view.piscarFundoDeExplosao(contador[0] % 2 == 1);
            if (contador[0] >= 6) {
                piscar.stop();
                view.piscarFundoDeExplosao(false);
                revelarMinasComAnimacao();
            }
        });
        piscar.start();
    }

    private void revelarMinasComAnimacao() {
        List<int[]> minasNaoReveladas = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMinada(i, j) && !tabuleiro.isRevelada(i, j)) {
                    minasNaoReveladas.add(new int[]{i, j});
                }
            }
        }
        revelarMinasPasso(minasNaoReveladas, 0);
    }

    private void revelarMinasPasso(List<int[]> minas, int indice) {
        if (indice >= minas.size()) {
            return;
        }
        int[] posicao = minas.get(indice);
        view.marcarMinaExplodida(posicao[0], posicao[1]);

        Timer timer = new Timer(80, e -> revelarMinasPasso(minas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }

    private void animarVitoria() {
        List<int[]> celulasSeguras = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    celulasSeguras.add(new int[]{i, j});
                }
            }
        }
        vitoriaPasso(celulasSeguras, 0);
    }

    private void vitoriaPasso(List<int[]> celulas, int indice) {
        if (indice >= celulas.size()) {
            return;
        }
        int[] atual = celulas.get(indice);
        view.destacarCelulaVencedora(atual[0], atual[1]);

        Timer timer = new Timer(8, e -> vitoriaPasso(celulas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }
}
