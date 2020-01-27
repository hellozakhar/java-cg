/*
 * ЗАДАЧА
 * Реализовать рисование вписанных квадратов как в NestedSquares.jar
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

public class SquaresPanel extends JPanel {
  
  // Количество рисуемых квадратов
  private static final int SQUARES = 30;
  // Параметр лямбда
  private static final double L = .05;
  
  public SquaresPanel() {
    setOpaque(true);
    setBackground(Color.WHITE);
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(600, 600);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    // Устанавливает сглаживание линий
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    double side = 0.75 * (double)Math.min(this.getWidth(), this.getHeight());
    Shape square = new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
    g2.translate(this.getWidth() / 2, this.getHeight() / 2);
    g2.scale(side, - side);
    g2.translate(-0.5, -0.5);
    g2.setStroke(new BasicStroke((float)(1.0 / side)));
    AffineTransform at = new AffineTransform(1-L, -L, L, 1-L, 0.0, L);
    for (int i = 0; i < 30; ++i) {
      g2.draw(square);
      square = at.createTransformedShape(square);
    }
  }
}
