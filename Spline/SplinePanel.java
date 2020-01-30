// Email: zakhar.osokin@gmail.com
// Zakhar Osokin (Group 381602)

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

// Следующая строчка нужна при использовании библиотеки Commons Math
import org.apache.commons.math3.linear.*;

public class SplinePanel extends JPanel {
  public final int PANEL_WIDTH = 700;
  public final int PANEL_HEIGHT = 500;

  /**
   * Число узлов в сплайне.
   */
  int numNodes;

  /**
   * Тип линии:
   * C0: непрерывная
   * C1: с непрерывной первой производной
   * C2: c непрерывной второй производной.
   */
  public enum Continuous {
    C0, C1, C2
  }

  /**
   * Тип линии. Данная переменная устанавливается кнопками-переключателями в
   * панели управления. В этом классе менять ее не следует.
   */
  Continuous contType = Continuous.C2;

  /**
   * Тип параметризации. Chord: длины отрезков параметризации равны декартову
   * расстоянию между соседними узлами. Norm: длины всех отрезков равны 1
   * (нормализованный сплайн).
   */
  public enum ParameterLength {
    Chord, Norm
  }

  /**
   * Тип параметризации. Данная переменная устанавливается
   * кнопками-переключателями в панели управления. В этом классе менять ее не
   * следует.
   */
  ParameterLength paramLength = ParameterLength.Chord;

  /**
   * Длины отрезков параметризации для каждого сегмента сплайна. Если сплайн
   * нормализованный (paramLength == ParameterLength.Norm), длины всех отрезков
   * равны 1, в противном случае (paramLength == ParameterLength.Chord) они
   * равны декартову расстоянию между соседними узлами.
   */
  double[] segmentLengths;

  /**
   * Сумма длин отрезков параметризации для каждого сегмента, то есть сумма
   * элементов массива segmentLengths.
   */
  double totalLength;

  /**
   * Размер пикселя в мировых координатах.
   */
  private float pixelSize;

  /**
   * Показывать ли контрольные точки. Данная переменная устанавливается
   * кнопками-переключателями в панели управления. В этом классе менять ее не
   * следует.
   */
  boolean showControl = true;

  /**
   * Список маркеров, изображающих опорные точки (узлы и контрольные точки) в
   * порядке, обратном их наложению (маркер, находящийся под всеми другими,
   * находится в списке первым).
   */
  private LinkedList<Marker> markers = new LinkedList<>();

  /**
   * Массив узлов в порядке их соединения.
   */
  private ArrayList<Node> nodes = new ArrayList<>();

  /**
   * Маркер, который в данный момент двигается мышью.
   */
  private Marker dragged;

  private Point2D shift = null;

  /**
   * Панель со вторыми производными, находящаяся под данной
   */
  private DerivativePanel dp;

  /**
   * Преобразование мировых координат (в которых вычисляются сплайны) в
   * экранные.
   */
  private final AffineTransform at;

  /**
   * Преобразование экранных координат в мировые.
   */
  private AffineTransform inverseAT;

  public SplinePanel(Drawing drawing) {
    setBackground(Color.WHITE);
    setOpaque(true);
    // Разрешение в пикселях на сантиметр.
    double res = Toolkit.getDefaultToolkit().getScreenResolution() / 2.54;
    at = new AffineTransform();
    at.translate(0, PANEL_HEIGHT);
    at.scale(res, -res);
    pixelSize = (float) (1.0 / res);
    try {
      inverseAT = at.createInverse();
    } catch (NoninvertibleTransformException ex) {
      System.out.println("Аффинное преобразование панели вырождено. Этого не должно быть.");
    }
    numNodes = drawing.numNodes;
    segmentLengths = new double[numNodes];

    Marker point, left, right;
    // Первый узел имеет только правую контрольную точку
    point = new CircleMarker(drawing.pointsX[0], drawing.pointsY[0], .2);
    right = new SquareMarker(drawing.controlsRightX[0], drawing.controlsRightY[0], .2);
    markers.add(point);
    markers.add(right);
    nodes.add(new Node(point, null, right));

    // Каждый узел кроме первого и последнего имеет левую и правую контрольную точку.
    // Слова "левая" и "правая" подразумевают, что кривая идет слева направо.
    for (int i = 1; i < numNodes - 1; i++) {
      point = new CircleMarker(drawing.pointsX[i], drawing.pointsY[i], .2);
      left = new SquareMarker(drawing.controlsLeftX[i], drawing.controlsLeftY[i], .2);
      right = new SquareMarker(drawing.controlsRightX[i], drawing.controlsRightY[i], .2);
      markers.add(point);
      markers.add(left);
      markers.add(right);
      nodes.add(new Node(point, left, right));
    }

    // Последний узел имеет только левую контрольную точку
    point = new CircleMarker(drawing.pointsX[numNodes - 1],
                             drawing.pointsY[numNodes - 1], .2);
    left = new SquareMarker(drawing.controlsLeftX[numNodes - 1],
                            drawing.controlsLeftY[numNodes - 1], .2);
    markers.add(point);
    markers.add(left);
    nodes.add(new Node(point, left, null));

    dragged = null;
    MouseActions actions = new MouseActions();
    addMouseListener(actions);
    addMouseMotionListener(actions);
  }

  public static double sqr(double x) {
    return x * x;
  }

  /**
   * Возвращает координату X i-го узла
   *
   * @param i номер узла
   * @return абсцисса узла с номером i
   */
  public double getX(int i) {
    if (i < 0 || i >= numNodes)
      throw new InvalidPointException(i, 0, numNodes - 1);
    return nodes.get(i).getPoint().getX();
  }

  /**
   * Возвращает координату Y i-го узла
   *
   * @param i номер узла
   * @return ордината узла с номером i
   */
  public double getY(int i) {
    if (i < 0 || i >= numNodes)
      throw new InvalidPointException(i, 0, numNodes - 1);
    return nodes.get(i).getPoint().getY();
  }

  /**
   * Возвращает координату X левой контрольной точки, соответствующей i-му узлу.
   * Слово "левая" подразумевает, что сплайн идет слева направо, то есть
   * возвращается координата предпоследней опорной точки кубической кривой
   * Безье, соединяющей (i-1)-й и i-й узлы. Первый узел (i == 0) не имеет левой
   * контрольной точки.
   *
   * @param i номер узла
   * @return абсцисса узла левой контрольной точки узла с номером i
   */
  public double getLeftX(int i) {
    if (i < 1 || i >= numNodes)
      throw new InvalidPointException(i, 1, numNodes - 1);
    return nodes.get(i).getLeft().getX();
  }

  /**
   * Возвращает координату Y левой контрольной точки, соответствующей i-му узлу
   *
   * @param i номер узла
   * @return ордината узла левой контрольной точки узла с номером i
   */
  public double getLeftY(int i) {
    if (i < 1 || i >= numNodes)
      throw new InvalidPointException(i, 1, numNodes - 1);
    return nodes.get(i).getLeft().getY();
  }

  /**
   * Возвращает координату X правой контрольной точки, соответствующей i-му узлу
   * Слово "правая" подразумевает, что сплайн идет слева направо, то есть
   * возвращается координата второй опорной точки кубической кривой Безье,
   * соединяющей i-й и (i+1)-й узлы. Последний узел (i == numNodes - 1) не имеет
   * правой контрольной точки.
   *
   * @param i номер узла
   * @return абсцисса узла правой контрольной точки узла с номером i
   */
  public double getRightX(int i) {
    if (i < 0 || i >= numNodes - 1)
      throw new InvalidPointException(i, 0, numNodes - 2);
    return nodes.get(i).getRight().getX();
  }

  /**
   * Возвращает координату Y правой контрольной точки, соответствующей i-му
   * узлу
   *
   * @param i номер узла
   * @return ордината узла правой контрольной точки узла с номером i
   */
  public double getRightY(int i) {
    if (i < 0 || i >= numNodes - 1)
      throw new InvalidPointException(i, 0, numNodes - 2);
    return nodes.get(i).getRight().getY();
  }

  /**
   * Устанавливает левую контрольную точку i-го узла в (x, y).
   *
   * @param i номер узла, левая контрольная точка которого передвигается
   * @param x абсцисса нового положения
   * @param y ордината нового положения
   */
  private void setLeft(int i, double x, double y) {
    if (i < 1 || i >= numNodes)
      throw new InvalidPointException(i, 1, numNodes - 1);
    nodes.get(i).getLeft().move(x, y);
  }

  /**
   * Устанавливает правую контрольную точку i-го узла в (x, y).
   *
   * @param i номер узла, правая контрольная точка которого передвигается
   * @param x абсцисса нового положения
   * @param y ордината нового положения
   */
  private void setRight(int i, double x, double y) {
    if (i < 0 || i >= numNodes - 1)
      throw new InvalidPointException(i, 0, numNodes - 2);
    nodes.get(i).getRight().move(x, y);
  }

  /**
   * Изменяет координаты левой контрольной точки i-го узла с учетом длин
   * отрезков параметризации так, чтобы сплайн в i-м узле имел непрерывную
   * первую производную. Может использовать segmentLengths[]. Вызывается, когда
   * пользователь двигает правую контрольную точку.
   *
   * @param i номер узла
   */
  public void updateLeft(int i) {
    if (i < 1 || i >= numNodes - 1)
      throw new InvalidPointException(i, 1, numNodes - 2);
    double dxRight = 3 * (getRightX(i) - getX(i)) / segmentLengths[i];
    double dyRight = 3 * (getRightY(i) - getY(i)) / segmentLengths[i];
    setLeft(i,getX(i) - dxRight * segmentLengths[i-1]/3,getY(i) - dyRight * segmentLengths[i-1]/3);
  }

  /**
   * Изменяет координаты правой контрольной точки i-го узла с учетом длин
   * отрезков параметризации так, чтобы сплайн в i-м узле имел непрерывную
   * первую производную. Может использовать segmentLengths[]. Вызывается, когда
   * пользователь двигает левую контрольную точку.
   *
   * @param i номер узла
   */
  public void updateRight(int i) {
    if (i < 1 || i >= numNodes - 1)
      throw new InvalidPointException(i, 1, numNodes - 2);
    double dxLeft = 3 * (getX(i) - getLeftX(i)) / segmentLengths[i-1];
    double dyLeft = 3 * (getY(i) - getLeftY(i)) / segmentLengths[i-1];
    setRight(i,getX(i) + dxLeft * segmentLengths[i]/3,getY(i) + dyLeft * segmentLengths[i]/3);
  }

  /**
   * Соединяет два узла (i-й и (i+1)-й), используя правую контрольную точку
   * первого узла и левую контрольную точку второго.
   *
   * @param g2 графический контекст
   * @param i  номер сегмента
   */
  private void drawSegment(Graphics2D g2, int i) {
    if (i < 0 || i > numNodes - 2)
      throw new InvalidSegmentException(i, numNodes - 2);
    g2.draw(new CubicCurve2D.Double(
            getX(i), getY(i), getRightX(i), getRightY(i),
            getLeftX(i + 1), getLeftY(i + 1), getX(i + 1), getY(i + 1)));
  }

  /**
   * Возвращает длину i-го отрезка параметризации.
   *
   * @param i номер сегмента
   * @return Длина i-го сегмента
   */
  private double segmentLength(int i) {
    if (i < 0 && i > numNodes - 2)
      throw new InvalidSegmentException(i, numNodes - 2);
    switch (paramLength) {
      case Chord:
        return Math.sqrt(sqr(getX(i) - getX(i + 1)) + sqr(getY(i) - getY(i + 1)));
      default: // case Norm
        return 1;
    }
  }

  /**
   * Запоминает длины интервалов параметризации в массиве segmentLengths,
   * чтобы не пересчитывать их, а также вычисляет общую длину
   */
  private void computeLengths() {
    totalLength = 0;
    for (int i = 0; i <= numNodes - 2; i++) {
      segmentLengths[i] = segmentLength(i);
      totalLength += segmentLengths[i];
    }
  }

  /**
   * Устанавливает правую контрольную точку каждого узла, кроме первого и
   * последнего, так, чтобы сплайн имел непрервную первую производную
   */
  public void makeC1() {
    // У первого узла нет левой контрольной точки, а у последнего -- правой,
    // поэтому с ними ничего не надо делать
    for (int i = 1; i <= numNodes - 2; i++)
      updateRight(i);
  }

  /**
   * Вычисляет производные в каждом узле из условия непрерывности второй
   * производной и устанавливает контрольные точки во внутренних узлах
   * соответствующим образом. Можно пользоваться полями segmentLengths и
   * totalLength, потоому что в paintComponent() метод computeLengths()
   * вызывается до makeC2().
   */
  private void makeC2() {
    // Для более короткой записи
    int n = numNodes;
    double[] l = segmentLengths;

    double[][] m = new double[n][n];
    m[0][0] = 1;
    m[n-1][n-1] = 1;
    for (int i = 1; i <= n-2; i++) {
      m[i][i-1] = l[i];
      m[i][i] = 2 * (l[i] + l[i-1]);
      m[i][i + 1] = l[i - 1];
    }
    RealMatrix matrix = new Array2DRowRealMatrix(m, false);
    DecompositionSolver solver = new LUDecomposition(matrix).getSolver();
    double[] cx = new double[n];

    cx[0] = 3 * (getRightX(0) - getX(0)) / l[0];
    cx[n-1] = 3 * (getX(n-1) - getLeftX(n-1)) / l[n-2];
    for (int i = 1; i <= n-2; i++) {
      cx[i] = 3 / (l[i-1]*l[i]) * ( sqr(l[i]) * (getX(i) - getX(i-1)) + sqr(l[i-1]) * (getX(i+1) - getX(i)) );
    }
    RealVector constantsX = new ArrayRealVector(cx, false);
    RealVector solutionX = solver.solve(constantsX);
    double[] cy = new double[n];

    cy[0] = 3 * ((getRightY(0) - getY(0))) / l[0];
    cy[n-1] = 3 * (getY(n-1) - getLeftY(n-1))/l[n-2];
    for (int i = 1; i <= n-2; i++) {
      cy[i] = 3 / (l[i-1] * l[i]) * ( sqr(l[i]) * (getY(i) - getY(i-1)) + sqr(l[i-1]) * (getY(i+1) - getY(i)) );
    }
    RealVector constantsY = new ArrayRealVector(cy, false);
    RealVector solutionY = solver.solve(constantsY);
    for (int i = 1; i <= n-2; i++) {
      setRight(i,getX(i)+solutionX.getEntry(i)*l[i]/3,getY(i)+solutionY.getEntry(i)*l[i]/3);
      setLeft(i,getX(i)-solutionX.getEntry(i)*l[i-1]/3,getY(i)-solutionY.getEntry(i)*l[i-1]/3);
    }
  }

  /**
   * Рисует горизонтальные и вертикальные линии каждый сантиметр.
   */
  private void drawGrid(Graphics2D g2) {
    // Координаты левого нижнего угла в экранных координатах
    Point2D bottomLeftScreen = new Point2D.Double(0, getHeight());

    // Координаты правого верхнего угла в экранных координатах
    Point2D topRightScreen = new Point2D.Double(getWidth(), 0);

    // Координаты левого нижнего угла в мировых координатах
    Point2D bottomLeftWorld = inverseAT.transform(bottomLeftScreen, null);

    // Координаты правого верхнего угла в мировых координатах
    Point2D topRightWorld = inverseAT.transform(topRightScreen, null);

    g2.setPaint(Color.LIGHT_GRAY);
    g2.setStroke(new BasicStroke(pixelSize));

    // Рисует вертикальные линии
    for (double x = 0; x < topRightWorld.getX(); x += 1)
      g2.draw(new Line2D.Double(x, bottomLeftWorld.getY(), x, topRightWorld.getY()));

    // Рисует горизонтальные линии
    for (double y = Math.ceil(bottomLeftWorld.getY()); y < topRightWorld.getY(); y += 1)
      g2.draw(new Line2D.Double(0, y, topRightWorld.getX(), y));

    // Рисует ось Ox
    g2.setStroke(new BasicStroke(2 * pixelSize));
    g2.draw(new Line2D.Double(0, pixelSize, topRightWorld.getX(), pixelSize));

  }

  /**
   * Рисует содержимое панели
   *
   * @param g графический контекст
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.transform(at);

    drawGrid(g2);
    g2.setPaint(Color.BLACK);

    computeLengths();

    switch (contType) {
      case C1 : makeC1(); break;
      case C2 : makeC2();
    }

    // Рисует отрезки, соединяющие узлы с контрольными точками.
    // Для крайних узлов отрезки рисуются в любом случае, для
    // остальных, если showControl == true
    if (showControl)
      for (int i = 1; i < numNodes - 1; i++)
        nodes.get(i).drawNodePins(g2, pixelSize);
    nodes.get(0).drawNodePins(g2, pixelSize);
    nodes.get(numNodes - 1).drawNodePins(g2, pixelSize);

    // Рисует кривые, соединяющие узлы
    g2.setStroke(new BasicStroke(3 * pixelSize));
    for (int i = 0; i < numNodes - 1; i++)
      drawSegment(g2, i);

    // Рисует маркеры, учитывая их наложения.
    // Контрольные точки первого и последнего узла рисуются независимо от showControl
    for (Marker m : markers)
      if (m.getNode() == nodes.get(0) || m.getNode() == nodes.get(numNodes - 1))
        m.draw(g2, true, pixelSize);
      else
        m.draw(g2, showControl, pixelSize);

    // Рисует панель со вторыми производными
    dp.repaint();

    // Для отладки
    //printCoordinates();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
  }

  void setDerivativePanel(DerivativePanel dp) {
    this.dp = dp;
  }

  private class MouseActions extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      Point2D clickedPoint = inverseAT.transform(e.getPoint(), null);
      ListIterator<Marker> it = markers.listIterator(markers.size());
      while (it.hasPrevious()) {
        Marker m = it.previous();
        if (m.contains(clickedPoint)) {
          dragged = m;
          shift = new Point2D.Double(m.getX() - clickedPoint.getX(),
                  m.getY() - clickedPoint.getY());
          it.remove();
          markers.addLast(m);
          repaint();
          break;
        }
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      dragged = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (dragged != null) {
        Point2D mousePoint = new Point2D.Double();
        inverseAT.transform(e.getPoint(), mousePoint);
        double newCenterX = mousePoint.getX() + shift.getX();
        double newCenterY = mousePoint.getY() + shift.getY();
        if (dragged instanceof CircleMarker) // spline node
          dragged.getNode().move(newCenterX, newCenterY);
        else // SquareMarker, i.e., control point
          switch (contType) {
            case C2:
              if (dragged.getNode() == nodes.get(0) ||
                      dragged.getNode() == nodes.get(numNodes - 1))
                dragged.move(newCenterX, newCenterY);
              break;
            case C1:
              dragged.move(newCenterX, newCenterY);
              Node node = dragged.getNode();
              int i = nodes.indexOf(node);
              if (i == 0 || i == numNodes - 1) break;
              if (dragged == node.getLeft())
                updateRight(i);
              else
                updateLeft(i);
              break;
            case C0:
              dragged.move(newCenterX, newCenterY);
          }
        repaint();
      }
    }
  }

  // Для отладки
  private void printCoordinates() {
    int i;
    System.out.print("pointsX = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getX(i));
    System.out.println("]");

    System.out.print("pointsY = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getY(i));
    System.out.println("]");

    System.out.print("controlsLeftX = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getLeftX(i));
    System.out.println("]");

    System.out.print("controlsLeftY = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getLeftY(i));
    System.out.println("]");

    System.out.print("controlsRightX = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getRightX(i));
    System.out.println("]");

    System.out.print("controlsRightY = [");
    for (i = 0; i < numNodes; i++)
      System.out.printf("%.2f, ", getRightY(i));
    System.out.println("]");
  }
}
