/*
Данная программа рисует узлы (красные круги), каждому из которых соответствуют контрольные точки (синие квадраты), соединенные с узлом отрезками. Узлы и контрольные точки можно двигать мышью. Соседние узлы соединяются кубическими кривыми Безье в соответствие с положениями контрольных точек.

Есть три режима рисования кривых, которые определяются кнопками справа.

C0 — контрольные точки можно передвигать независимо. Получающаяся кривая непрерывна.
C1 — контрольные точки расположены на одной линии с узлом и симметрично относительно него, если сплайн нормализованный, то есть длины отрезков параметризации равны единице. Получающаяся кривая имеет непрерывную первую производную.
C2 — кривая имеет непрерывную вторую производную. Контрольные точки во внутренних узлах вычисляются автоматически, поэтому их передвигать нельзя.
Можно также менять длину отрезков параметризации и управлять показом контрольных точек в промежуточных узлах.

Под панелью со сплайном показаны графики вторых производных по x и y.
В режиме C2 эти графики должны быть непрерывными ломаными.
Обратите внимание, что первый узел имеет только правую контрольную точку, а последний — только левую.

По вектору из узла в контрольную точку, учитывая длину отрезка параметризации,
можно найти производные в первом и последнем узлах.
Эти значения задают краевые условия сплайна и используются для вычисления производных в промежуточных узлах.
Когда эти производные вычислены, положения контрольных точках в промежуточных узлах с учетом длин отрезков параметризации
следует установить с помощью методов setLeft(i, x, y) и setRight(i, x, y).
Нужно добиться того, чтобы в режиме С2 графики вторых производных под сплайном были непрерывными.
 */

import javax.swing.*;
import java.awt.*;

/**
 * Главный класс программы
 */
public class Spline {
  
  private static void createAndShowGUI() {
    JFrame frame = new JFrame("Кубический сплайн");
    // Аргументом конструктора SplinePanel на следующей строчке
    // может быть объект одного из классов Ampersand, Bat или SingleSegment.
    // В общем случае это может быть объект подкласса Drawing.
    SplinePanel leftPanel = new SplinePanel(new Ampersand());
    frame.add(leftPanel, BorderLayout.CENTER);
    frame.add(new ControlPanel(leftPanel), BorderLayout.LINE_END);
    DerivativePanel bottomPanel = new DerivativePanel(leftPanel);
    frame.add(bottomPanel, BorderLayout.PAGE_END);
    leftPanel.setDerivativePanel(bottomPanel);
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
