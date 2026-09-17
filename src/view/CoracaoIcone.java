package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/** Ícone vetorial de coração. */
public class CoracaoIcone implements Icon {
    private final int tamanho;

    public CoracaoIcone(int tamanho) {
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
            GeneralPath heart = new GeneralPath();
            heart.moveTo(x + 12 * s, y + 21 * s);
            heart.curveTo(x + 2 * s, y + 14 * s, x + 2 * s, y + 6.5 * s, x + 7 * s, y + 5 * s);
            heart.curveTo(x + 10 * s, y + 4 * s, x + 11.5 * s, y + 6.5 * s, x + 12 * s, y + 7.5 * s);
            heart.curveTo(x + 12.5 * s, y + 6.5 * s, x + 14 * s, y + 4 * s, x + 17 * s, y + 5 * s);
            heart.curveTo(x + 22 * s, y + 6.5 * s, x + 22 * s, y + 14 * s, x + 12 * s, y + 21 * s);
            heart.closePath();
            g2.setPaint(new GradientPaint(x, y, new Color(255, 140, 165), x, y + tamanho, new Color(215, 45, 85)));
            g2.fill(heart);
            g2.setStroke(new BasicStroke(Math.max(1.2f, tamanho * 0.07f)));
            g2.setColor(new Color(255, 225, 232));
            g2.draw(heart);
        } finally {
            g2.dispose();
        }
    }
}
