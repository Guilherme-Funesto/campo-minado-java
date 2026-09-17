package controller;

/**
 * Contrato de ações que a View dispara em resposta à interação do
 * jogador. Quem implementa esta interface é sempre o Controller — a View
 * nunca decide o que essas ações significam, apenas avisa que elas
 * aconteceram.
 */
public interface AcoesJogador {

    /** Disparado quando o jogador escolhe uma dificuldade pronta na tela inicial. */
    void aoEscolherDificuldade(int linhas, int colunas, int minas);

    /**
     * Disparado quando o jogador cria um tabuleiro personalizado.
     * Nesse modo também é possível escolher quantos escudos existirão na partida.
     */
    void aoEscolherDificuldadePersonalizada(int linhas, int colunas, int minas, int escudos);

    /** Disparado no clique esquerdo sobre uma célula (revelar). */
    void aoRevelarCelula(int linha, int coluna);

    /** Disparado no clique direito sobre uma célula (marcar/desmarcar bandeira). */
    void aoMarcarCelula(int linha, int coluna);

    /** Disparado quando o jogador pede para voltar à tela inicial. */
    void aoPedirNovoJogo();
}
