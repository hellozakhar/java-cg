import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Интерфейс, описывающий маркер, изображающий опорную
 * (узловую или контрольную) точку сплайна.
 */
public interface Marker {
  /**
   * Помещает маркер в точку с координатами (x, y).
   */
  void move(double x, double y);
  
  /**
   * Помещает маркер в точку point.
   */
  void move(Point2D point);
  
  /**
   * Перемещает маркер на вектор (dx, dy) относительно текущего положения.
   */
  void moveRelative(double dx, double dy);

  /**
   * Изображает маркер.
   * @param g2 графический контекст
   * @param showControl рисовать ли контрольные точки
   * @param pixelSize размер пикселя в мировых координатах
   */
  void draw(Graphics2D g2, boolean showControl, float pixelSize);

  /**
   * Возвращает координату X точки, изображаемой маркером.
   */
  double getX();

  /**
   * Возвращает координату Y точки, изображаемой маркером.
   */
  double getY();

  /**
   * Определяет, находится ли точка p внутри изображения маркера.
   */
  boolean contains(Point2D p);

  /**
   * Добавляет ссылку на node к данному маркеру.
   * Это означает, что данный маркер относится у узлу node.
   */
  void register(Node node);

  /**
   * Возвращает узел, к которому относится данный маркер.
   */
  Node getNode();
}
