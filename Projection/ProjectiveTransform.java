/**
 * Представляет трехмерное проективное преобразование. В однородных
 * координатах представляется матрицей 4x4. Точка с координатами [x y z]
 * отображается в точку с координатами  [x' y' z'] согласно формуле
 * <pre>
 * [ x']   [  m00  m01  m02  m03 ] [ x ]
 * [ y'] = [  m10  m11  m12  m13 ] [ y ]
 * [ z'] = [  m20  m21  m22  m23 ] [ z ]
 * [ 1 ]   [  m30  m31  m32  m33 ] [ 1 ]
 * <pre>
 *
 * @see java.awt.geom.AffineTransform
 */
public class ProjectiveTransform {
  /**
   * Размер матрицы
   */
  public final int dim = 4;

  /**
   * Матрица отображения
   */
  double m[][] = new double[dim][dim];

  /**
   * Создает тождественное отображение
   */
  public ProjectiveTransform() {
    // В Java элементы числового массива инициализируются нулями,
    // поэтому достаточно изменить ненулевые элементы.
    for (int i = 0; i < dim; i++) m[i][i] = 1;
  }

  /**
   * Создает отображение с матрицей m. Матрица копируется.
   * @param m матрица отображения
   */
  public ProjectiveTransform(double[][] m) {
    for (int i = 0; i < dim; i++)
      for (int j = 0; j < dim; j++)
        this.m[i][j] = m[i][j];
  }

  /**
   * Заменяет отображение на композицию текущего отображения и поворота вокруг
   * оси Oz. Матрица поворота умножается справа.
   * @param theta угол поворота в радианах
   */
  public void rotateZ(double theta) {
    double sin = Math.sin(theta);
    double cos = Math.cos(theta);
    // Изменяем коэффициенты матрицы на месте, т.е. не создавая новую матрицу.
    // Сохраняем m00, т.к. он переписывается.
    for (int i = 0; i < dim; i++) {
      double m0 = m[i][0];
      m[i][0] = m0 * cos + m[i][1] * sin;
      m[i][1] = m0 * (-sin) + m[i][1] * cos;
    }
  }

  /**
   * Заменяет отображение на композицию текущего отображения и поворота вокруг
   * оси Ox. Матрица поворота умножается справа.
   * @param theta угол поворота в радианах
   */
  public void rotateX(double theta) {
    double sin = Math.sin(theta);
    double cos = Math.cos(theta);
    for (int i = 0; i < dim; ++i) {
      double m1 = m[i][1];
      m[i][1] = m1 * cos + m[i][2] * sin;
      m[i][2] = m1 * (-sin) + m[i][2] * cos;
    }
  }

  /**
   * Заменяет отображение на композицию текущего отображения и поворота вокруг
   * оси Oy. Матрица поворота умножается справа.
   * @param theta угол поворота в радианах
   */
  public void rotateY(double theta) {
    double sin = Math.sin(theta);
    double cos = Math.cos(theta);
      for (int i = 0; i < dim; ++i) {
        double m0 = m[i][0];
        m[i][0] = m0 * cos + m[i][2] * (-sin);
        m[i][2] = m0 * sin + m[i][2] * cos;
      }
  }

  /**
   * Заменяет отображение на композицию текущего отображения и нового отображения pt.
   * Матрица pt умножается справа.
   * @param pt новое отображение
   */
  public void concatenate(ProjectiveTransform pt) {
    double sum = 0.0;
    double[] line = new double[dim];
    for (int i = 0; i < dim; ++i) {
      for (int j = 0; j < dim; ++j) {
        line[j] = m[i][j];
      }
      for (int j = 0; j < dim; ++j) {
        sum = 0.0;
        for (int k = 0; k < dim; ++k) {
          sum += line[k] * pt.m[k][j];
        }
        m[i][j] = sum;
      }
    }
  }

  /**
   * Применят отображение к точке ptSrc и помещает результат в ptDst, а также
   * возвращает ptDst. Предполагается, что результат применения отображения --
   * собственная точка.
   * Если ptDst есть null, аллокируется новый объект Point3D
   * и в него записывается результат. Если ptSrc и ptDsr -- один и тот же объект,
   * ptDst переписывается результатом применения отображения к ptSrc.
   * @param ptSrc точка, к которой применяется отображение
   * @param ptDst результат применения отображения
   * @return результат применения отображения
   * @see java.awt.geom.AffineTransform#transform(java.awt.geom.Point2D, java.awt.geom.Point2D)
   */
  public Point3D transform(Point3D ptSrc, Point3D ptDst) {
    if (ptDst == null) ptDst = new Point3D();
    double[] ps = {ptSrc.x, ptSrc.y, ptSrc.z, 1};
    double[] pd = new double[dim];
    for (int i = 0; i < dim; i++)
      for (int j = 0; j < dim; j++)
        pd[i] += m[i][j] * ps[j];
    ptDst.x = pd[0] / pd[3];
    ptDst.y = pd[1] / pd[3];
    ptDst.z = pd[2] / pd[3];

    return ptDst;
  }

  /**
   * Применяет данное отображение к ломаной {@code path} и возвращает результат.
   *
   * @param path ломаная, к которой применяется отображение
   * @see java.awt.geom.AffineTransform#createTransformedShape(java.awt.Shape)
   * )
   */
  public Path3D createTransformedPath(Path3D path) {
    Path3D newPath = new Path3D(path);
    for (Point3D p : newPath.points) {
      transform(p, p);
    }
    return newPath;
  }

  // Для тестирования
  void printTransform() {
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++)
        System.out.printf("%.2f ", m[i][j]);
      System.out.println();
    }
  }
}
