package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/** Ícone vetorial de bomba. */
public class BombaIcone implements Icon {
    private final int tamanho;

    public BombaIcone(int tamanho) {
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

            Shape corpo = new Ellipse2D.Double(x + 4 * s, y + 7 * s, 14 * s, 14 * s);
            g2.setPaint(new GradientPaint(x, y + 6 * (float) s, new Color(90, 96, 110), x, y + 21 * (float) s, new Color(25, 28, 36)));
            g2.fill(corpo);
            g2.setStroke(new BasicStroke(Math.max(1.2f, tamanho * 0.06f)));
            g2.setColor(new Color(210, 220, 230));
            g2.draw(corpo);

            g2.setColor(new Color(235, 240, 245, 80));
            g2.fill(new Ellipse2D.Double(x + 7 * s, y + 10 * s, 4.5 * s, 3.2 * s));

            g2.setStroke(new BasicStroke(Math.max(1.2f, tamanho * 0.07f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(120, 90, 55));
            g2.drawArc((int) (x + 13 * s), (int) (y + 2 * s), (int) (6 * s), (int) (8 * s), 180, 110);

            g2.setColor(new Color(80, 62, 35));
            g2.fillRoundRect((int) (x + 14 * s), (int) (y + 3 * s), (int) (4 * s), (int) (3 * s), (int) (1.5 * s), (int) (1.5 * s));

            GeneralPath faísca = new GeneralPath();
            faísca.moveTo(x + 19 * s, y + 1.2 * s);
            faísca.lineTo(x + 20.5 * s, y + 3.3 * s);
            faísca.lineTo(x + 22.8 * s, y + 2.7 * s);
            faísca.lineTo(x + 21.4 * s, y + 4.8 * s);
            faísca.lineTo(x + 23.2 * s, y + 6.2 * s);
            faísca.lineTo(x + 20.8 * s, y + 5.9 * s);
            faísca.lineTo(x + 19.8 * s, y + 8.1 * s);
            faísca.lineTo(x + 19.1 * s, y + 5.7 * s);
            faísca.lineTo(x + 16.8 * s, y + 5.5 * s);
            faísca.lineTo(x + 18.5 * s, y + 4.2 * s);
            faísca.closePath();
            g2.setPaint(new GradientPaint(x, y, new Color(255, 252, 180), x, y + 8, new Color(255, 170, 40)));
            g2.fill(faísca);
            g2.setColor(new Color(255, 240, 190));
            g2.draw(faísca);
        } finally {
            g2.dispose();
        }
    }
}
