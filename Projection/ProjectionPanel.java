import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

/**
 * Панель, на которой рисуется проекция. Пусть Ox'y'z' есть декартова система
 * координат в пространстве. Рассмотрим поворот вокруг Oy' на угол phi,
 * за которым следует поворот вокруг новой оси Ox' на угол -theta.
 * Это отображение переводит систему Ox'y'z' в Ox''y''z''.
 * Пусть вектор v является единичным направляющим вектором оси Oz''.
 * Прямоугольная проекция осуществляется вдоль v на плоскость Ox''y''.
 * Косоугольная проекция осуществляется вдоль v на плоскость Ox'y'.
 * Центральная проекция осуществляется из точки с радиус-вектором distance * v
 * на плоскость Ox'y'. Вектор v используется в описании элементов данного класса.
 *
 * Формулы проекций описаны в лекции 5.
 */
public class ProjectionPanel extends JPanel {

  /**
   * Угол в радианах между проекцией вектора v на плоскость Ox'z' и осью Oz'.
   */
  double phi;

  /**
   * Угол в радианах между вектором v и плоскостью Ox'z'
   */
  double theta;

  /**
   * Тип проекции (в скобках указаны разделы ГОСТ 2.317-2011):
   * Ортогональная изометрия (5.1)
   * Ортогональная диметрия  (5.2)
   * Ортонональная триметрия
   * Фронтальная изометрия   (6.1)
   * Фронтальная диметрия    (6.3)
   * Фронтальная диметрия, не являющаяся кавальерной или кабинетной
   */
  enum ProjectionType {
    // Центральная проекция
    Central,
    // Ортогональные проекции
    Isometry,
    Dimetry,
    Trimetry,
    // Косоугольные проекции
    FrontIsometry,
    FrontDimetry,
    ObliqueDimetry
  }

  ProjectionType projectionType;

  /**
   * Угол phi, соответствующий ортогональной изометрии
   */
  public static final double phiIsometry = - Math.PI / 4;

  /**
   * Угол theta, соответствующий ортогональной изометрии
   */
  public static final double thetaIsometry = Math.asin(Math.sqrt(3.0) / 3.0);

  /**
   * Угол phi, соответствующий ортогональной диметрии
   */
  public static final double phiDimetry = - Math.asin(1.0 / Math.sqrt(8.0));

  /**
   * Угол theta, соответствующий ортогональной диметрии
   */
  public static final double thetaDimetry = Math.asin(1.0/3);

  /**
   * Угол phi, соответствующий фронтальной изометрии
   */
  public static final double phiFrontIsometry = - Math.atan(Math.sqrt(2.0) / 2.0);

  /**
   * Угол theta, соответствующий фронтальной изометрии
   */
  public static final double thetaFrontIsometry = 0.5235987755982988;

  /**
   * Угол phi, соответствующий фронтальной диметрии
   */
  public final double phiFrontDimetry = - Math.atan(Math.sqrt(2.0) / 4.0);

  /**
   * Угол theta, соответствующий фронтальной диметрии
   */
  public final double thetaFrontDimetry = Math.acos(3.0 / Math.sqrt(10.0));

  /**
   * Расстояние от центра проекции до начала координат.
   * Используется при центральной проекции.
   */
  double distance;

  /**
   * Ломаная, проекция которой рисуется на данной панели
   */
  Path3D path;

  /**
   * Разрешение экрана в пикселях на сантиметр
   */
  private final double screenResolution;

  /**
   * Матрица перехода из ОСК Oxyz в начальный вариант МСК Ox'y'z' (до поворотов).
   * Совпадает с обратной матрицей.
   * В лекции обозначалась через C^{E'}_E = C^E_{E'}.
   */
  final ProjectiveTransform worldToObject;

  public ProjectionPanel(Path3D p) {
    path = p;
    theta = 0;
    phi = 0;
    projectionType = ProjectionType.Trimetry;
    distance = 5;
    setBackground(Color.WHITE);
    screenResolution =
      (double) Toolkit.getDefaultToolkit().getScreenResolution() / 2.54;
    double[][] m =
      {{-1, 0, 0, 0},
       {0, 0, 1, 0},
       {0, 1, 0, 0},
       {0, 0, 0, 1}};
    worldToObject = new ProjectiveTransform(m);
  }

  /**
   * Возвращает преобразование плоскости Oxy или Ox'y' из мировых координат в экранные.
   * Начало координат мировой системы координат находится в центре панели,
   * ось Ox направлена вправо, ось Oy -- вверх. Единица мировой системы координат
   * соответствует {@code screenResolution} пикселям.
   * 
   * @return преобразование плоскости Oxy из мировых координат в экранные
   */
  public AffineTransform createTransform2D() {
    AffineTransform at = new AffineTransform();
    at.translate(getWidth() / 2, getHeight() / 2);
    at.scale(screenResolution, - screenResolution);
    return at;
  }

  /**
   * Возвращает отображение, переводящее координаты точки в объектной системе
   * координат Oxyz в координаты той же точки в мировой системе координат Ox''y''z''.
   * Ортогональная проекция заключается в последующем отбрасывании координаты z''.
   * Ортогональная проекция не включена в это отображение.
   * См. лекцию 5, с. 4.
   *
   * @return найденное отображение
   * @see #phi
   */
  public ProjectiveTransform createTransformOrthogonal3D() {
    ProjectiveTransform pt = new ProjectiveTransform();
    pt.rotateX(theta);
    pt.rotateY(- phi);
    pt.concatenate(worldToObject);
    return pt;
  }

  /**
   * Возвращает отображение, осуществляющее косоугольную проекцию вдоль
   * вектора v на плоскость Ox'y'. Матрица отображения рассматривается в
   * Ox'y'z', то есть в базисе E'.
   * См. лекцию 5, с. 5.
   *
   * @return найденное отображение
   */
  public ProjectiveTransform createTransformOblique3D() {
    ProjectiveTransform pt = new ProjectiveTransform();
    pt.m[0][2] = -Math.tan(phi);
    pt.m[1][2] = (-Math.tan(theta)) / Math.cos(phi);
    pt.m[2][2] = 0.0;
    pt.concatenate(worldToObject);
    return pt;
  }

  /**
   * Возвращает проективное отображение, осуществляющее центральную проекцию
   * с центром в точке с радиус-вектором (distance * v), где v есть единичный
   * вектор вдоль Oz'', на плоскость Ox'y'. Матрица отображения рассматривается в
   * Ox'y'z'.
   * См. лекцию 7, с. 2.
   *
   * @return найденное отображение
   */
  public ProjectiveTransform createTransformCentral() {
    ProjectiveTransform pt = new ProjectiveTransform();
    pt.m[0][2] = - Math.tan(phi);
    pt.m[1][2] = (- Math.tan(theta)) / Math.cos(phi);
    pt.m[2][2] = 0.0;
    pt.m[3][2] = -1.0 / (distance * Math.cos(theta) * Math.cos(phi));
    pt.concatenate(worldToObject);
    return pt;
  }

  /**
   * Рисует данную панель
   * 
   * @param g графический контекст
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(new BasicStroke(1f));

    AffineTransform at = createTransform2D();
    ProjectiveTransform pt = new ProjectiveTransform();

    switch (projectionType) {
      case Central:
        pt = createTransformCentral();
        break;
      case Isometry:
        phi = phiIsometry;
        theta = thetaIsometry;
        pt = createTransformOrthogonal3D();
        break;
      case Dimetry:
        phi = phiDimetry;
        theta = thetaDimetry;
        pt = createTransformOrthogonal3D();
        break;
      case Trimetry:
        pt = createTransformOrthogonal3D();
        break;
      case FrontIsometry:
        phi = phiFrontIsometry;
        theta = thetaFrontIsometry;
        pt = createTransformOblique3D();
        break;
      case FrontDimetry:
        phi = phiFrontDimetry;
        theta = thetaFrontDimetry;
        pt = createTransformOblique3D();
        break;
      default: // obliqueDimetry
        pt = createTransformOblique3D();
        break;
    }

    // Осуществляем проекцию
    Shape p2d = pt.createTransformedPath(path).projectXY();
    // Переводим фигуру из мировой системы координат в экранную и рисуем ее
    g2.draw(at.createTransformedShape(p2d));
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(400, 600);
  }
}
