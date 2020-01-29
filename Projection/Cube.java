public class Cube extends Path3D {
  private static final double ex = 0.2;
  private static final double axis = 4.3;

  public Cube() {
    moveTo(-2, -2, -2); // Грань y = -2
    lineTo(-2, -2, 2);
    lineTo(2, -2, 2);
    lineTo(2, -2, -2);
    lineTo(-2, -2, -2);
    lineTo(-2, 2, -2);  // Ребро, соединяющее y = -2 and y = 2. Затем: грань y = 2
    lineTo(-2, 2, 2);
    lineTo(2, 2, 2);
    lineTo(2, 2, -2);
    lineTo(-2, 2, -2);
    moveTo(-2, -2, 2); // Оставшиеся три ребра, соединяющие y = -2 and y = 2
    lineTo(-2, 2, 2);
    moveTo(2, -2, 2);
    lineTo(2, 2, 2);
    moveTo(2, -2, -2);
    lineTo(2, 2, -2);

    // Оси
    moveTo(0, 0, 0); // Ox
    lineTo(axis, 0, 0);
    moveTo(0, 0, 0); // Ox
    lineTo(0, axis, 0);
    moveTo(0, 0, 0); // Ox
    lineTo(0, 0, axis);

    double d = 1.1 * axis;

    // Буква X
    Path3D x = new Path3D();
    x.moveTo(d + ex, 0, ex);
    x.lineTo(d - ex, 0, -ex);
    x.moveTo(d - ex, 0, ex);
    x.lineTo(d + ex, 0, -ex);

    // Буква Y
    Path3D y = new Path3D();
    y.moveTo(-ex, d, ex);
    y.lineTo(1.5*ex, d, -1.5*ex);
    y.moveTo(ex, d, ex);
    y.lineTo(0, d, 0);

    // Буква Z
    Path3D z = new Path3D();
    z.moveTo(ex, 0, d + ex);
    z.lineTo(-ex, 0, d + ex);
    z.lineTo(ex, 0, d - ex);
    z.lineTo(-ex, 0, d - ex);

    append(x, false);
    append(y, false);
    append(z, false);
  }
}
