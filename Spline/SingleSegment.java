/**
 * Одна кубическая кривая Безье
 */
public class SingleSegment extends Drawing {
  public SingleSegment() {
//    pointsX = new double[] {2, 4};
//    pointsY = new double[] {1, 1};
//    controlsLeftX = new double[] {0, 5};
//    controlsLeftY = new double[] {0, 4};
//    controlsRightX = new double[] {1, 0};
//    controlsRightY = new double[] {4, 0};
    numNodes = 2;
    // кривая с изломом
    pointsX = new double[] {1, 11};
    pointsY = new double[] {1, 1};
    controlsLeftX = new double[] {0, 1};
    controlsLeftY = new double[] {0, 9};
    controlsRightX = new double[] {11, 0};
    controlsRightY = new double[] {9, 0};
  }
}
