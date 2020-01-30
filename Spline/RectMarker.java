import java.awt.*;
import java.awt.geom.*;

/**
 * Реализация интерфейса Marker на основе класса RectangularShape.
 * Описывает маркер в виде прямоугольника или эллипса.
 */
public abstract class RectMarker implements Marker {
  protected RectangularShape shape;
  protected final double halfWidth;
  protected final double halfHeight;
  protected final Color drawColor = Color.BLACK;
  protected final Color fillColor;
  protected Node node;
  
  public RectMarker(RectangularShape shape, Color fillColor) {
    this.shape = shape;
    this.fillColor = fillColor;
    halfWidth = shape.getWidth() / 2;
    halfHeight = shape.getHeight() / 2;
  }
  
  /**
   * Помещает маркер в точку с координатами (x, y).
   */
  @Override
  public void move(double x, double y) {
    shape.setFrameFromCenter(x, y, x - halfWidth, y - halfHeight);
  }
  
  /**
   * Помещает маркер в точку point.
   */
  @Override
  public void move(Point2D point) {
    move(point.getX(), point.getY());
  }
  
  /**
   * Перемещает маркер на вектор (dx, dy) относительно текущего положения.
   */
  @Override
  public void moveRelative(double dx, double dy) {
    move(shape.getCenterX() + dx, shape.getCenterY() + dy);
  }

  /**
   * Возвращает координату X точки, изображаемой маркером.
   */
  @Override
  public double getX() {
    return shape.getCenterX();
  }

  /**
   * Возвращает координату Y точки, изображаемой маркером.
   */
  @Override
  public double getY() {
    return shape.getCenterY();
  }
  
  @Override
  public boolean contains(Point2D p) {
    return shape.contains(p);
  }
  
  /**
   * Добавляет ссылку на node к данному маркеру.
   * Это означает, что данный маркер относится у узлу node.
   */
  @Override
  public void register(Node node) {
    this.node = node;
  }
  
  /**
   * Возвращает узел, к которому относится данный маркер.
   */
  @Override
  public Node getNode() {
    return node;
  }

  /**
   * Изображает маркер.
   * @param g2 графический контекст
   * @param showControl рисовать ли контрольные точки.
   *                    Этот параметр используется в подклассах.
   * @param pixelSize размер пикселя в мировых координатах
   */
  @Override
  public void draw(Graphics2D g2, boolean showControl, float pixelSize) {
    g2.setPaint(fillColor);
    g2.fill(shape);
    g2.setStroke(new BasicStroke(pixelSize));
    g2.setPaint(drawColor);
    g2.draw(shape);
  }

}
