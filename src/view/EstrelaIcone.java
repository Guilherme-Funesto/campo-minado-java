package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/** Ícone vetorial de estrela. */
public class EstrelaIcone implements Icon {
    private final int tamanho;

    public EstrelaIcone(int tamanho) {
        this.tamanho = tamanho;
    }

    @Override public int getIconWidth() { return tamanho; }
    @Override public int getIconHeight() { return tamanho; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double cx = x + tamanho / 2.0;
            double cy = y + tamanho / 2.0;
            double outer = tamanho * 0.42;
            double inner = outer * 0.45;
            GeneralPath star = new GeneralPath();
            for (int i = 0; i < 10; i++) {
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                double r = (i % 2 == 0) ? outer : inner;
                double px = cx + Math.cos(angle) * r;
                double py = cy + Math.sin(angle) * r;
                if (i == 0) star.moveTo(px, py); else star.lineTo(px, py);
            }
            star.closePath();
            g2.setPaint(new GradientPaint(x, y, new Color(255, 245, 150), x, y + tamanho, new Color(234, 176, 27)));
            g2.fill(star);
            g2.setStroke(new BasicStroke(Math.max(1.3f, tamanho * 0.08f)));
            g2.setColor(new Color(255, 250, 220));
            g2.draw(star);
        } finally {
            g2.dispose();
        }
    }
}
