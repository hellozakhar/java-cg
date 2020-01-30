import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

/**
 * Класс Node представляет узел сплайна. Состоит из точки (собственно
 * узла, через который проходит сплайн) и двух контрольных точек,
 * определяющих, как кубические кривые Безье входят и выходят из данного
 * узла.
 */
public class Node {
  /**
   * Узловая точка.
   */
  private final Marker point;
  
  /**
   * Левая контрольная точка.
   */
  private final Marker left;

  /**
   * Правая контрольная точка.
   */
  private final Marker right;
  
  public Node(Marker point, Marker left, Marker right) {
    this.point = point;
    this.left = left;
    this.right = right;
    point.register(this);
    if (left != null) left.register(this);
    if (right != null) right.register(this);
  }

  /**
   * Возвращает маркер, представляющий данный узел.
   * @return маркер данного узла
   */
  public Marker getPoint() {
    return point;
  }

  /**
   * Возвращает маркер, представляющий левую контрольную точку данного узла.
   * Точка является левой в предположении, что сплайн идет слева направо.
   * @return маркер левой контрольной точки данного узла
   */
  public Marker getLeft() {
    return left;
  }

  /**
   * Возвращает маркер, представляющий правую контрольную точку данного узла.
   * Точка является правую в предположении, что сплайн идет слева направо.
   * @return маркер правой контрольной точки данного узла
   */
  public Marker getRight() {
    return right;
  }
  
  /**
   * Рисует отрезки прямых, соединяющие узел с контрольными точками.
   * @param g2 графический контекст
   * @param pixelSize размер пикселя в мировых координатах
   */
  public void drawNodePins(Graphics2D g2, float pixelSize) {
    g2.setStroke(new BasicStroke(pixelSize));
    if (left != null)
      g2.draw(new Line2D.Double(point.getX(), point.getY(), left.getX(), left.getY()));
    if (right != null)
      g2.draw(new Line2D.Double(point.getX(), point.getY(), right.getX(), right.getY()));
    // Сами маркеры не рисуются, потому что при рисовании
    // должен учитываться порядок маркеров в разных узлах
  }
  
  /**
   * Перемещает узел вместе с контрольными точками в точку с данными координатами.
   * Положение контрольных точек относительно узла сохраняется.
   * @param newX новая координата X узла
   * @param newY новая координата Y узла
   */
  public void move(double newX, double newY) {
    double dx = newX - point.getX();
    double dy = newY - point.getY();
    point.move(newX, newY);
    if (left != null) left.moveRelative(dx, dy);
    if (right != null) right.moveRelative(dx, dy);
  }
  
//  /**
//   * Устанавливает контрольную точку control в точку (x, y),
//   * а другую контрольную точку -- в точку, симметричную control относительно узла
//   * @param control контрольная точка, помещаемая в (x, y)
//   * @param x новая координата X контрольной точки
//   * @param y новая координата Y контрольной точки
//   */
//  public void updateControls(Marker control, double x, double y) {
//    Marker otherControl = (control == left)? right : left;
//    control.move(x, y);
//    if (otherControl != null)
//      otherControl.move(2 * point.getX() - x, 2 * point.getY() - y);
//  }
//
//  /**
//   * Не двигая данную контрольную точку, устанавливает вторую контрольную
//   * точку симметрично данной относительно узла
//   * @param right если true, то не двигать правую контрольную точку,
//   *              если left, то не двигать левую
//   */
//  public void updateOtherControl(boolean right) {
//    Marker control = right? this.right : this.left;
//    updateControls(control, control.getX(), control.getY());
//  }
}
