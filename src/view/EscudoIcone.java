package view;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * Ícone vetorial de escudo desenhado pelo próprio Java.
 * Não depende de emoji, fonte especial ou arquivo de imagem externo.
 */
public class EscudoIcone implements Icon {

    private final int tamanho;

    public EscudoIcone(int tamanho) {
        this.tamanho = tamanho;
    }

    @Override
    public int getIconWidth() {
        return tamanho;
    }

    @Override
    public int getIconHeight() {
        return tamanho;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            double s = tamanho / 24.0;

            GeneralPath escudo = new GeneralPath();
            escudo.moveTo(x + 12 * s, y + 1.5 * s);
            escudo.curveTo(x + 15.5 * s, y + 3.8 * s, x + 19.2 * s, y + 4.0 * s, x + 22 * s, y + 4.8 * s);
            escudo.lineTo(x + 21.2 * s, y + 12.6 * s);
            escudo.curveTo(x + 20.7 * s, y + 17.0 * s, x + 17.2 * s, y + 20.8 * s, x + 12 * s, y + 23 * s);
            escudo.curveTo(x + 6.8 * s, y + 20.8 * s, x + 3.3 * s, y + 17.0 * s, x + 2.8 * s, y + 12.6 * s);
            escudo.lineTo(x + 2 * s, y + 4.8 * s);
            escudo.curveTo(x + 4.8 * s, y + 4.0 * s, x + 8.5 * s, y + 3.8 * s, x + 12 * s, y + 1.5 * s);
            escudo.closePath();

            Paint preenchimento = new GradientPaint(
                    x, y + 2 * (float) s, new Color(83, 214, 255),
                    x, y + 23 * (float) s, new Color(24, 92, 190)
            );
            g2.setPaint(preenchimento);
            g2.fill(escudo);

            g2.setStroke(new BasicStroke((float) Math.max(1.4, 1.8 * s), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(205, 248, 255));
            g2.draw(escudo);

            // Brilho interno para deixar o símbolo mais destacado.
            GeneralPath brilho = new GeneralPath();
            brilho.moveTo(x + 12 * s, y + 5.2 * s);
            brilho.lineTo(x + 17.5 * s, y + 7.0 * s);
            brilho.lineTo(x + 16.8 * s, y + 12.7 * s);
            brilho.curveTo(x + 16.5 * s, y + 15.3 * s, x + 14.7 * s, y + 17.4 * s, x + 12 * s, y + 18.8 * s);
            brilho.curveTo(x + 9.3 * s, y + 17.4 * s, x + 7.5 * s, y + 15.3 * s, x + 7.2 * s, y + 12.7 * s);
            brilho.lineTo(x + 6.5 * s, y + 7.0 * s);
            brilho.closePath();

            g2.setColor(new Color(255, 255, 255, 58));
            g2.fill(brilho);

            // Pequeno losango central: simples, legível e sem depender de caracteres.
            Polygon centro = new Polygon(
                    new int[]{(int) (x + 12 * s), (int) (x + 15 * s), (int) (x + 12 * s), (int) (x + 9 * s)},
                    new int[]{(int) (y + 8 * s), (int) (y + 12 * s), (int) (y + 16 * s), (int) (y + 12 * s)},
                    4
            );
            g2.setColor(new Color(240, 252, 255));
            g2.fillPolygon(centro);
        } finally {
            g2.dispose();
        }
    }
}
