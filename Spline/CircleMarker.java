import java.awt.*;
import java.awt.geom.Ellipse2D;

public class CircleMarker  extends RectMarker {
  public CircleMarker(double centerX, double centerY, double size) {
    super(new Ellipse2D.Double(centerX - size / 2, centerY - size / 2, size, size),
          Color.RED);
  }

  /**
   * Изображает маркер.
   * @param g2 графический контекст
   * @param showControl рисовать ли контрольные точки.
   *                    Данные класс реализует узловые точки, которые рисуются всегда,
   *                    поэтому этот параметр игнорируется.
   * @param pixelSize размер пикселя в мировых координатах
   */
  @Override
  public void draw(Graphics2D g2, boolean showControl, float pixelSize) {
    super.draw(g2, true, pixelSize);
  }
}
