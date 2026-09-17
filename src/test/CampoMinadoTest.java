import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários cobrindo contagem de minas vizinhas, o efeito cascata
 * e a condição de vitória, conforme pedido no enunciado.
 * <p>
 * Todos os testes usam o construtor de {@link Tabuleiro} que recebe as
 * posições das minas explicitamente, para que o resultado seja
 * determinístico (sem depender do sorteio aleatório).
 */
public class CampoMinadoTest {

    @Test
    void testContagemDeMinasVizinhas() {
        // Tabuleiro 3x3 com uma única mina no centro (1,1).
        // Todas as 8 células ao redor devem contar exatamente 1 mina vizinha.
        int[][] minas = { { 1, 1 } };
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, minas);

        assertEquals(1, tabuleiro.getCelula(0, 0).getMinasVizinhas());
        assertEquals(1, tabuleiro.getCelula(0, 1).getMinasVizinhas());
        assertEquals(1, tabuleiro.getCelula(2, 2).getMinasVizinhas());
        // A própria célula minada não conta a si mesma.
        assertEquals(0, tabuleiro.getCelula(1, 1).getMinasVizinhas());
    }

    @Test
    void testContagemDeMinasVizinhasComDuasMinasAdjacentes() {
        // Duas minas lado a lado: a célula (0,2) tem ambas como vizinhas.
        int[][] minas = { { 0, 0 }, { 0, 1 } };
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, minas);

        assertEquals(2, tabuleiro.getCelula(1, 0).getMinasVizinhas());
        assertEquals(2, tabuleiro.getCelula(0, 2).getMinasVizinhas());
        assertEquals(1, tabuleiro.getCelula(2, 2).getMinasVizinhas());
    }

    @Test
    void testCascataRevelaTodasAsCelulasSemMinasProximas() {
        // Tabuleiro 4x4 sem nenhuma mina: revelar qualquer célula deve
        // revelar o tabuleiro inteiro via cascata.
        int[][] semMinas = {};
        Tabuleiro tabuleiro = new Tabuleiro(4, 4, semMinas);

        tabuleiro.revelar(0, 0);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertTrue(tabuleiro.getCelula(i, j).isRevelada(),
                        "Célula (" + i + "," + j + ") deveria ter sido revelada pela cascata");
            }
        }
    }

    @Test
    void testCascataNaoRevelaMinaNemPassaDelaAdiante() {
        // Mina isolada em (2,2) de um tabuleiro 5x5. Revelar (0,0), que
        // está longe da mina, deve espalhar a cascata mas nunca revelar
        // a célula minada.
        int[][] minas = { { 2, 2 } };
        Tabuleiro tabuleiro = new Tabuleiro(5, 5, minas);

        tabuleiro.revelar(0, 0);

        assertFalse(tabuleiro.getCelula(2, 2).isRevelada(),
                "A célula minada nunca deve ser revelada pela cascata");
        // As células vizinhas à mina, por terem minasVizinhas > 0, devem
        // ter sido reveladas (mostram o número), mas a cascata para nelas
        // e não avança para dentro da mina.
        assertTrue(tabuleiro.getCelula(1, 2).isRevelada());
    }

    @Test
    void testRevelarCelulaMinadaEncerraOJogoComDerrota() {
        int[][] minas = { { 1, 1 } };
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, minas);

        tabuleiro.revelar(1, 1);

        assertTrue(tabuleiro.isJogoEncerrado());
        assertTrue(tabuleiro.isDerrota());
        assertTrue(tabuleiro.getCelula(1, 1).isRevelada());
    }

    @Test
    void testMarcarEDesmarcarCelulaComBandeira() {
        int[][] minas = { { 0, 0 } };
        Tabuleiro tabuleiro = new Tabuleiro(3, 3, minas);

        assertFalse(tabuleiro.getCelula(1, 1).isMarcada());

        tabuleiro.alternarMarcacao(1, 1);
        assertTrue(tabuleiro.getCelula(1, 1).isMarcada());

        tabuleiro.alternarMarcacao(1, 1);
        assertFalse(tabuleiro.getCelula(1, 1).isMarcada());
    }

    @Test
    void testCelulaMarcadaNaoPodeSerRevelada() {
        int[][] semMinas = {};
        Tabuleiro tabuleiro = new Tabuleiro(2, 2, semMinas);

        tabuleiro.alternarMarcacao(0, 0);
        tabuleiro.revelar(0, 0);

        assertFalse(tabuleiro.getCelula(0, 0).isRevelada(),
                "Uma célula marcada com bandeira não deve ser revelada");
    }

    @Test
    void testVerificarVitoriaQuandoTodasAsCelulasSeguraForamReveladas() {
        // Tabuleiro 2x2 com uma mina: vitória ocorre quando as outras
        // 3 células (sem mina) forem reveladas.
        int[][] minas = { { 0, 0 } };
        Tabuleiro tabuleiro = new Tabuleiro(2, 2, minas);

        assertFalse(tabuleiro.verificarVitoria());

        tabuleiro.revelar(0, 1);
        tabuleiro.revelar(1, 0);
        tabuleiro.revelar(1, 1);

        assertTrue(tabuleiro.verificarVitoria());
        assertTrue(tabuleiro.isJogoEncerrado());
        assertFalse(tabuleiro.isDerrota());
    }

    @Test
    void testVerificarVitoriaEhFalsaEnquantoHouverCelulaSeguraNaoRevelada() {
        int[][] minas = { { 0, 0 } };
        Tabuleiro tabuleiro = new Tabuleiro(2, 2, minas);

        tabuleiro.revelar(0, 1);

        assertFalse(tabuleiro.verificarVitoria());
    }
}
