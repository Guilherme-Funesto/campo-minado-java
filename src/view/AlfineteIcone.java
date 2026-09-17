package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/** Ícone vetorial de alfinete. */
public class AlfineteIcone implements Icon {
    private final int tamanho;

    public AlfineteIcone(int tamanho) {
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

            Shape topo = new Ellipse2D.Double(x + 5 * s, y + 3 * s, 14 * s, 10 * s);
            g2.setPaint(new GradientPaint(x, y, new Color(255, 217, 115), x, y + tamanho, new Color(214, 120, 20)));
            g2.fill(topo);
            g2.setColor(new Color(255, 240, 195));
            g2.setStroke(new BasicStroke(Math.max(1.1f, tamanho * 0.06f)));
            g2.draw(topo);

            GeneralPath ponta = new GeneralPath();
            ponta.moveTo(x + 12 * s, y + 12 * s);
            ponta.lineTo(x + 14.5 * s, y + 16 * s);
            ponta.lineTo(x + 12.6 * s, y + 21 * s);
            ponta.lineTo(x + 10.6 * s, y + 16 * s);
            ponta.closePath();
            g2.setColor(new Color(245, 245, 250));
            g2.fill(ponta);
            g2.setColor(new Color(180, 180, 190));
            g2.draw(ponta);

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fill(new Ellipse2D.Double(x + 8 * s, y + 5 * s, 5 * s, 3 * s));
        } finally {
            g2.dispose();
        }
    }
}
