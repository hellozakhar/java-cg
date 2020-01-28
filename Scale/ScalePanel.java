import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Панель, на которой изображается рисунок, заданный в мировых координатах.
 */
public class ScalePanel extends JPanel {
  
  /** Тип, содержащий возможные положения рисунка в панели.
   */
  public enum Position {
    BOTTOM_LEFT,  // нижний левый угол
    BOTTOM_RIGHT, // нижний правый угол
    TOP_LEFT,     // верхний левый угол
    TOP_RIGHT,    // верхний правый угол
    CENTER        // середина панели
  }
  
  /**
   * Положение рисунка в панели.
   * Значение этого поля удобно анализировать в операторе switch.
   * switch (position) {
   *   case BOTTOM_LEFT :
   *     ...
   *     break;
   *   case BOTTOM_RIGHT :
   *     ...
   *     break;
   * }
   * Не забывайте заканчивать каждый вариант оператором break.
   * Это поле меняется из класса ControlPanel в соответствии с кнопками,
   * выбранными пользователем. Самостоятельно менять его не нужно.
   */
  public Position position;

  /**
   * Определяет, зависит ли масштаб от размера панели.
   * Если истинно, то масштаб выбирается таким образом, чтобы единичный
   * отрезок в рисунке имел длину 1 см на экране. Если ложно, то рисунок
   * максимизируется по ширине, по высоте или в обоих направлениях.
   * Если истинно, то подразумевается, что fixedAspectRatio тоже истинно.
   * Это поле меняется из класса ControlPanel в соответствии с кнопками,
   * выбранными пользователем. Самостоятельно менять его не нужно.
   */
  public boolean fixedScale;
  
  /**
   * Определяет, сохраняется ли отношение высоты рисунка к его ширине.
   * Если истинно, то отношение такое же, как в исходном рисунке. Если ложно,
   * то рисунок максимизируется по высоте и по ширине.
   * Это поле меняется из класса ControlPanel в соответствии с кнопками,
   * выбранными пользователем. Самостоятельно менять его не нужно.
   */
  public boolean fixedAspectRatio;
  
  /**
   * Рисунок, изображаемый на панели, в мировых координатах.
   * Может быть объектом любого класса, реализующего интерфейс Shape,
   * например, Path2D.Double или других классов пакета java.awt.geom.
   * Определяется в конструкторе и больше не меняется.
   */
  private final Shape drawing;
  
  /**
   * Габаритный прямоугольник, содержащий рисунок, в мировых координатах.
   * Определяется в конструкторе и больше не меняется.
   */
  private final Rectangle2D drawingBoundingBox;
  
  /**
   * Разрешение экрана в пикселях на сантиметр.
   */
  private final double screenResolution;
  
  public ScalePanel(Shape dr) {
    setOpaque(true);
    setBackground(Color.WHITE);
    // Устанавливает пустую границу панели шириной 10 пикселей сверху,
    // 20 пикселей слева, 30 пикселей снизу и 40 пикселей справа.
    setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 40));
    position = Position.BOTTOM_LEFT;
    fixedScale = true;
    fixedAspectRatio = true;
    // Вычисляет количество пикселей в сантиметре
    screenResolution =
      (double)Toolkit.getDefaultToolkit().getScreenResolution() / 2.54;
    drawing  = dr;
    drawingBoundingBox = drawing.getBounds2D();
  } 
    
  /**
   * Вычисляет аффинное преобразование, которое применяется к рисунку drawing
   * прежде, чем он изображается на данной панели.
   * Преобразование определяется значениями полей fixedScale, fixedAspectRatio
   * и position (см. документацию по ним выше), а также шириной границы.
   * 
   * Задание: дописать данный метод.
   */
  private AffineTransform findTransform() {
    double xScale;
    double yScale;
    // Размеры рисунка в мировых координатах
    double widthWorld = drawingBoundingBox.getWidth();
    double heightWorld = drawingBoundingBox.getHeight();

    // Размеры панели, включая границу, в пикселях
    int widthScreen = getWidth();
    int heightScreen = getHeight();
    
    // Нижний левый угол рисунка в мировых координатах
    double xBottomLeftWorld = drawingBoundingBox.getX();
    double yBottomLeftWorld = drawingBoundingBox.getY();

    // Размер границы (пустого пространства) по краям панели.
    // Класс java.awt.Insets имеет открытые поля top, bottom, left и right,
    // содержащие ширину (в пикселях) верхней, нижней, левой и правой границ панели,
    // соответственно. Граница была установлена в конструкоре и может меняться
    // из других классов, поскольку метод setBorder является открытым.
    // Следовательно, нельзя полагаться на значения, установленные в конструкторе.
    // Граница исключается из области панели, куда можно поместить рисунок.
    
    Insets insets = getInsets();

    // insets.left содержит ширину левой границы в пикселях и т.д.

    AffineTransform at = new AffineTransform();

    if (fixedScale) {
      xScale = screenResolution;
      yScale = screenResolution;
    } else {
      double xs = (double)(widthScreen - insets.left - insets.right) / widthWorld;
      double ys = (double)(heightScreen - insets.top - insets.bottom) / heightWorld;
      if (fixedAspectRatio) {
        xScale = Math.min(xs, ys);
        yScale = Math.min(xs, ys);
      } else {
        xScale = xs;
        yScale = ys;
      }
    }
    int xs = 0;
    int ys = 0;
    double xw = 0.0;
    double yw = 0.0;
    switch (position) {
      case BOTTOM_LEFT: {
        xs = insets.left;
        ys = heightScreen - insets.bottom;
        xw = xBottomLeftWorld;
        yw = yBottomLeftWorld;
        break;
      }
      case BOTTOM_RIGHT: {
        xs = widthScreen - insets.right;
        ys = heightScreen - insets.bottom;
        xw = xBottomLeftWorld + widthWorld;
        yw = yBottomLeftWorld;
        break;
      }
      case TOP_LEFT: {
        xs = insets.left;
        ys = insets.top;
        xw = xBottomLeftWorld;
        yw = yBottomLeftWorld + heightWorld;
        break;
      }
      case TOP_RIGHT: {
        xs = widthScreen - insets.right;
        ys = insets.top;
        xw = xBottomLeftWorld + widthWorld;
        yw = yBottomLeftWorld + heightWorld;
        break;
      }
      case CENTER: {
        xs = (widthScreen + insets.left - insets.right) / 2;
        ys = (heightScreen + insets.top - insets.bottom) / 2;
        xw = xBottomLeftWorld + widthWorld / 2.0;
        yw = yBottomLeftWorld + heightWorld / 2.0;
      }
    }
    at.translate(xs, ys);
    at.scale(xScale, - yScale);
    at.translate(- xw, - yw);
    return at;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    // Следующий вызов активизирует сглаживание линий
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(new BasicStroke(1));

    AffineTransform at = findTransform();
    Shape transformedDrawing = at.createTransformedShape(drawing);
    // Можно было также
    // Shape transformedDrawing = new Path2D.Double(drawing, at);
    g2.draw(transformedDrawing);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 600);
  }
}
