package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/** Ícone vetorial de bandeira. */
public class BandeiraIcone implements Icon {
    private final int tamanho;

    public BandeiraIcone(int tamanho) {
        this.tamanho = tamanho;
    }

    @Override public int getIconWidth() { return tamanho; }
    @Override public int getIconHeight() { return tamanho; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double s = tamanho / 24.0;

            g2.setStroke(new BasicStroke((float) Math.max(1.3, 1.8 * s), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(220, 230, 235));
            g2.drawLine((int) (x + 7 * s), (int) (y + 3 * s), (int) (x + 7 * s), (int) (y + 20 * s));

            GeneralPath pano = new GeneralPath();
            pano.moveTo(x + 8 * s, y + 4 * s);
            pano.curveTo(x + 13 * s, y + 2.8 * s, x + 15.8 * s, y + 6.2 * s, x + 20 * s, y + 4.6 * s);
            pano.lineTo(x + 20 * s, y + 12.5 * s);
            pano.curveTo(x + 15.8 * s, y + 14.2 * s, x + 13.2 * s, y + 10.8 * s, x + 8 * s, y + 12.2 * s);
            pano.closePath();

            GradientPaint gp = new GradientPaint(x, y, new Color(255, 110, 90), x, y + tamanho, new Color(210, 45, 45));
            g2.setPaint(gp);
            g2.fill(pano);
            g2.setColor(new Color(255, 214, 205));
            g2.draw(pano);
        } finally {
            g2.dispose();
        }
    }
}
