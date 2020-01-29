import java.awt.geom.Path2D;
import java.util.ArrayList;

/**
 * Объединение нескольких ломаных в трехмерном пространстве. Представляет собой
 * последовательность точек, возможно соединенных отрезками.
 */
public class Path3D {

  /**
   * Массив точек
   */
  ArrayList<Point3D> points;

  /**
   * Определяет, соединены ли точки отрезками. Если i-й элемент
   * {@code connected[i]} есть {@code true}, то (i-1)-я и i-я точки соединены
   * отрезком.
   */
  ArrayList<Boolean> connected;

  /**
   * Создает объект и инициализирует {@code points} и {@code connected} пустыми
   * массивами.
   */
  public Path3D() {
    points = new ArrayList<>();
    connected = new ArrayList<>();
  }

  /**
   * Создает объект и копирует в него ломаную {@code path}. Используется
   * глубокое копирование, т.е. заново создаются не только новые массивы
   * {@code points} и {@code connected}, но и все объекты {@code Point3D},
   * ссылки на которые хранятся в {@code points}. Для этого используется
   * конструктор копирования класса {@code Point3D}. Таким образом, если к
   * скопированной ломаной применить аффинное преобразование, то исходная
   * ломаная {@code path} останется неизменной.
   *
   * @param path ломаная, которую нужно скопировать в данный объект
   * @see java.awt.geom.Path2D.Double#Double(java.awt.Shape)
   */
  public Path3D(Path3D path) {
    this();
    for (Point3D p : path.points)
      points.add(new Point3D(p));
    for (Boolean c : path.connected)
      connected.add(c);
  }

  public void append(Path3D path, boolean connect) {
    int ps = path.points.size();
    if (ps > 0) {
      points.add(new Point3D(path.points.get(0)));
      connected.add(connect);
      for (int i = 1; i < ps; i++) {
        points.add(new Point3D(path.points.get(i)));
        connected.add(path.connected.get(i));
      }
    }
  }

  /**
   * Добавляет отрезок к ломаной. Отрезок соединяет последнюю точку ломаной с
   * новой точкой.
   *
   * @param x координата x новой точки
   * @param y координата y новой точки
   * @param z координата z новой точки
   * @see java.awt.geom.Path2D.Double#lineTo(double, double)
   */
  public void lineTo(double x, double y, double z) {
    points.add(new Point3D(x, y, z));
    connected.add(true);
  }

  /**
   * Добавляет новую точку, не соединяя ее отрезком с концом ломаной.
   *
   * @param x координата x новой точки
   * @param y координата y новой точки
   * @param z координата z новой точки
   * @see java.awt.geom.Path2D.Double#moveTo(double, double)
   */
  public void moveTo(double x, double y, double z) {
    points.add(new Point3D(x, y, z));
    connected.add(false);
  }

  /**
   * Возвращает проекцию ломаной на плоскость xy. Возвращается объект класса
   * {@code Path2D.Double}, который реализует интерфейс {@code Shape}.
   *
   * @return проекцию ломаной на плоскость xy
   */
  public Path2D.Double projectXY() {
    Path2D.Double p2d = new Path2D.Double();
    for (int i = 0; i < points.size(); i++) {
      Point3D p = points.get(i);
      if (connected.get(i)) {
        p2d.lineTo(p.x, p.y);
      }
      else {
        p2d.moveTo(p.x, p.y);
      }
    }
    return p2d;
  }
}
