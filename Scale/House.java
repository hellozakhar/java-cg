/*
* ЗАДАЧА
* Реализовать аналогично House.jar
 */

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class House extends Path2D.Double {

 public void lineToRelative(double x, double y) {
   Point2D p = getCurrentPoint();
   lineTo(p.getX() + x, p.getY() + y);
 }

 public House() {
   moveTo(3, 1); lineTo(19, 1); // Земля
   append(new Rectangle2D.Double(4, 1, 6, 6), false); // Дом
   moveTo(3, 6); lineTo(7, 10); lineTo(11, 6);          // Крыша
   append(new Rectangle2D.Double(5, 3, 2, 2), false); // Окно
     double alpha = Math.toDegrees(Math.asin(0.16666666666666666));
     append(new Arc2D.Double(13.0, 2.0 + Math.sqrt(0.9722222222222222), 6.0, 8.0, - alpha + 90.0, 2.0 * alpha - 360.0, 0), false);
   // Крона дерева
   // Ствол дерева
   moveTo(15.5, 1); lineTo(15.5, 3);
   moveTo(16.5, 1); lineTo(16.5, 3);
   // Труба
   moveTo(8, 9); lineTo(8, 10); lineTo(9, 10); lineTo(9, 8);
 }
}

