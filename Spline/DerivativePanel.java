import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.IntToDoubleFunction;

// import static src.spline.SplinePanel.sqr;

public class DerivativePanel extends JPanel {
  public final int PANEL_HEIGHT = 200;

//  public static final BasicStroke divider =
//          new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
//                  10.0f, new float[] {10, 8}, 0.0f);
  public static final BasicStroke divider = new BasicStroke(1.0f);
  public static final BasicStroke graph = new BasicStroke(2.0f);

  private SplinePanel sp;

  // Вторая производная кубического многочлена есть линейная функция.
  // Вторые производные d2x(t)/dt^2 в начале каждого сегмента
  private double[] d2xLeft;

  // Вторые производные d2x(t)/dt^2 в конце каждого сегмента
  private double[] d2xRight;

  // Вторые производные d2y(t)/dt^2 в начале каждого сегмента
  private double[] d2yLeft;

  // Вторые производные d2y(t)/dt^2 в конце каждого сегмента
  private double[] d2yRight;

  public DerivativePanel(SplinePanel sp) {
    setBackground(Color.WHITE);
    setOpaque(true);
    this.sp = sp;
    d2xLeft = new double[sp.numNodes];
    d2xRight = new double[sp.numNodes];
    d2yLeft = new double[sp.numNodes];
    d2yRight = new double[sp.numNodes];
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(0, PANEL_HEIGHT);
  }

  private double computeSecondDerivatives(
          IntToDoubleFunction p,  // point, т.е. возвращает координату X или Y i-го узла
          IntToDoubleFunction l,  // left, т.е. координата левой контрольной точки i-го узла
          IntToDoubleFunction r,  // right, т.е. координата правой контрольной точки i-го узла
          double[] d2Left,        // вторые производные в начале каждого сегмента
          double[] d2Right) {     // вторые производные в конце каждого сегмента
    double[] len = sp.segmentLengths;
    int n = sp.numNodes; // количество узлов для краткости
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    int i, i1;
    for (i = 0; i < n - 1; i++) {
      i1 = i + 1;
      d2Left[i] = 6 / SplinePanel.sqr(len[i]) *
              (p.applyAsDouble(i) - 2*r.applyAsDouble(i) + l.applyAsDouble(i1));
      d2Right[i] = 6 / SplinePanel.sqr(len[i]) *
              (p.applyAsDouble(i1) + r.applyAsDouble(i) - 2*l.applyAsDouble(i1));
      min = Math.min(min, d2Left[i]);
      min = Math.min(min, d2Right[i]);
      max = Math.max(max, d2Left[i]);
      max = Math.max(max, d2Right[i]);
    }
    // Для изображения нужно только превышение производных над минимумом
    for (i = 0; i < n; i++) {
      d2Left[i] -= min;
      d2Right[i] -= min;
    }
    return max - min; // Для определения масштаба
  }

  private void drawGrid(Graphics2D g2, double xScale, double[] len) {
    g2.setStroke(divider);
    g2.setPaint(Color.LIGHT_GRAY);
    g2.draw(new Line2D.Double(0, getHeight() / 2, getWidth(), getHeight() / 2));
    double x = 0;
    double xs = 0;
    for (int i = 0; i < sp.numNodes - 2; i++) {
      x += len[i];
      xs = x * xScale;
      g2.draw(new Line2D.Double(xs, 0, xs, getHeight()));
    }
  }

  private void drawDerivatives(Graphics2D g2, AffineTransform at,
                               double[] d2Left, double[] d2Right) {
    g2.setStroke(graph);
    g2.setPaint(Color.BLACK);

    // Начало графика второй производной на данном интервале в мировых координатах
    Point2D p1 = new Point2D.Double();
    // Конец графика второй производной на данном интервале в мировых координатах
    Point2D p2 = new Point2D.Double();
    Point2D p1s = new Point2D.Double(); // p1 в экранных координатах
    Point2D p2s = new Point2D.Double(); // p2 в экранных координатах
    for (int i = 0; i < sp.numNodes - 1; i++) {
      p1.setLocation(p2.getX(), d2Left[i]);
      p2.setLocation(p1.getX() + sp.segmentLengths[i], d2Right[i]);
      at.transform(p1, p1s);
      at.transform(p2, p2s);
      g2.draw(new Line2D.Double(p1s, p2s));
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g2, getWidth() / sp.totalLength, sp.segmentLengths);
    double xRange = computeSecondDerivatives(sp::getX, sp::getLeftX, sp::getRightX, d2xLeft, d2xRight);
    double yRange = computeSecondDerivatives(sp::getY, sp::getLeftY, sp::getRightY, d2yLeft, d2yRight);
    AffineTransform at = new AffineTransform();
    at.translate(0, getHeight() / 2);
    at.scale(getWidth() / sp.totalLength, -getHeight() / xRange / 2);
    drawDerivatives(g2, at, d2xLeft, d2xRight);
    at.setToIdentity();
    at.translate(0, getHeight());
    at.scale(getWidth() / sp.totalLength, -getHeight() / yRange / 2);
    drawDerivatives(g2, at, d2yLeft, d2yRight);

    // Надпись
    Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    g2.setFont(font);
    g2.setStroke(divider);
    String label = "Вторые производные по x и y";
    Rectangle2D textBound = font.getStringBounds(label, g2.getFontRenderContext());
    float baseLine = -(float)textBound.getY();
    textBound.setFrame(
            (getWidth() - textBound.getWidth()) / 2,
            0,
            textBound.getWidth(),
            textBound.getHeight());
    g2.setColor(Color.WHITE);
    g2.fill(textBound);
    g2.setColor(Color.BLACK);
//    g2.draw(textBound);
    g2.drawString(label, (float)textBound.getX(), baseLine);
  }
}
