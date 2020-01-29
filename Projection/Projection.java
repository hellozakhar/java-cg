/*
* ЗАДАЧА
* Реализовать аналогично Projection.jar
*/

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Projection {

  private static void createAndShowGUI() {
    JFrame f = new JFrame("Проекция");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ProjectionPanel leftPanel = new ProjectionPanel(new Cube());
    f.getContentPane().add(leftPanel, BorderLayout.CENTER);
    
    ControlPanel rightPanel = new ControlPanel(leftPanel);
    f.getContentPane().add(rightPanel, BorderLayout.LINE_END);

    f.pack();
    f.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() { createAndShowGUI(); }
    });
//    Для тестирования
//    Point3D z = new Point3D(0,0,1);
//    Point3D x = new Point3D(1,0,0);
//    Point3D y = new Point3D(0,1,0);
//
//    ProjectiveTransform pt = new ProjectiveTransform();
//    pt.rotateY(Math.PI/2);
//    System.out.println("x |-> " + pt.transform(x, null));
//    System.out.println("y |-> " + pt.transform(y, null));
//    System.out.println("z |-> " + pt.transform(z, null));
  }
}
