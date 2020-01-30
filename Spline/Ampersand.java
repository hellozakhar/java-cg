/**
 * Фигура, похожая на символ &. Значения полей см. в классе Drawing.
 */
public class Ampersand extends Drawing {
  public Ampersand() {
    pointsX = new double[] {10.5, 4.8, 8.1, 6.6, 11.4};
    pointsY = new double[] {5.8, 2.6, 8.6, 8.6, 2.0};
    controlsLeftX = new double[] {11.0, 6.4, 7.7, 7.2, 9.4};
    controlsLeftY = new double[] {7.5, 0.7, 7.7, 9.6, 1.7};
    controlsRightX = new double[] {10.0, 3.2, 8.5, 6.0, 13.5};
    controlsRightY = new double[] {4.0, 4.5, 9.5, 7.5, 2.3};
    numNodes = 5;
  }
}
