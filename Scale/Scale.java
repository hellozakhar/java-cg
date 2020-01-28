/*
 * ЗАДАЧА
 * Реализовать аналогично House.jar
 */

/*
import scale.ControlPanel;
import scale.House;
import scale.ScalePanel;
*/

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Scale {

  private static void createAndShowGUI() {
    // JFrame -- это окно приложения
    JFrame f = new JFrame("Масштабирование");
    // По умолчанию при закрытии окна приложение продолжает работать.
    // Делаем так, чтобы приложение завершало работу, если окно закрывается.
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Создаем панель с рисунком и добавляем его в центр окна
    ScalePanel leftPanel = new ScalePanel(new House());
    f.add(leftPanel, BorderLayout.CENTER);

    // Создаем управляющую панель и добавляем ее справа.
    ControlPanel rightPanel = new ControlPanel(leftPanel);
    f.add(rightPanel, BorderLayout.LINE_END);
    // Делаем так, чтобы размеры окна соответствовали размерам содержащихся в нем
    // компонент
    f.pack();
    // Показываем окно
    f.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() { createAndShowGUI(); }
    });
  }
}

