import java.awt.geom.Point2D;

/**
 * Собственная точка в трехмерном проективном пространстве с координатами типа
 * {@code double}.
 */
public class Point3D {
  /**
   * Координата x данной точки
   */
  public double x;

  /**
   * Координата y данной точки
   */
  public double y;

  /**
   * Координата z данной точки
   */
  public double z;
  
  /**
   * Создает объект и инициализирует его данными координатами.
   * 
   * @param x координата x создаваемого объекта
   * @param y координата y создаваемого объекта
   * @param z координата z создаваемого объекта
   */
  public Point3D(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  /**
   * Создает объект и инициализирует его координатами (0, 0, 0).
   * Это конструктор по умолчанию.
   */
  public Point3D() {
    this(0, 0, 0);
  }
  
  /**
   * Создает объект и инициализирует его координатами данной точки.
   * Это конструктор копирования.
   * 
   * @param p точка, копией которой является создаваемый объект
   */
  public Point3D(Point3D p) {
    this(p.x, p.y, p.z);
  }
  
  /**
   * Возвращает проекцию точки на плоскость xy.
   * 
   * @return проекция данной точки на плоскость xy. 
   */
  public Point2D.Double projectXY() {
    return new Point2D.Double(x, y);
  }
  
  /**
   * Возвращает проекцию точки на плоскость yz.
   * 
   * @return проекция данной точки на плоскость yz. 
   */
  public Point2D.Double projectYZ() {
    return new Point2D.Double(y, z);
  }

  /**
   * Возвращает проекцию точки на плоскость zx.
   * 
   * @return проекция данной точки на плоскость zx. 
   */
  public Point2D.Double projectZX() {
    return new Point2D.Double(z, x);
  }

  /**
   * Возвращает текстовое представление данной точки.
   * 
   * @return представление точки в виде {@code String}.
   */
  @Override
  public String toString() {
    return "Point3D[" + x + ", " + y + ", " + z + "]";
  }
}

