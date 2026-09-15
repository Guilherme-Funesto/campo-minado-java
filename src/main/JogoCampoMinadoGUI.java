package main;

import controller.CampoMinadoController;
import view.CampoMinadoView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada do Campo Minado com interface grafica.
 */
public class JogoCampoMinadoGUI {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Nao foi possivel aplicar o tema padrao do Java.");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                CampoMinadoView view = new CampoMinadoView();
                CampoMinadoController controller = new CampoMinadoController(view);
                controller.iniciar();
            } catch (Throwable e) {
                System.out.println("Erro ao iniciar o Campo Minado:");
                e.printStackTrace();
            }
        });
    }
}