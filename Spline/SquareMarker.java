import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SquareMarker extends RectMarker {
  public SquareMarker(double centerX, double centerY, double size) {
    super(new Rectangle2D.Double(centerX - size / 2, centerY - size / 2, size, size),
          Color.BLUE);
  }

  /**
   * Изображает маркер.
   * @param g2 графический контекст
   * @param showControl рисовать ли контрольные точки
   * @param pixelSize размер пикселя в мировых координатах
   */
  @Override
  public void draw(Graphics2D g2, boolean showControl, float pixelSize) {
    if (showControl)
      super.draw(g2, true, pixelSize);
  }
}
