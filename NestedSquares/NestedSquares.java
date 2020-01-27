/*
 * ЗАДАЧА
 * Реализовать рисование вписанных квадратов как в NestedSquares.jar
 */

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class NestedSquares {
  
  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Nested squares");
    frame.getContentPane().add(new SquaresPanel());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
