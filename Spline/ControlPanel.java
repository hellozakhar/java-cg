import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.*;

class ControlPanel extends JPanel implements ActionListener {
  static final String c0String = "C0";
  static final String c1String = "C1";
  static final String c2String = "C2";

  static final String chordString = "Длина хорды";
  static final String normString = "Единица";

  static final String yesString = "Да";
  static final String noString = "Нет";

  private final SplinePanel splinePanel;

  public ControlPanel(SplinePanel splinePanel) {
    this.splinePanel = splinePanel;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    /* Кнопки переключения типа непрерывности */
    JPanel contButtonPanel = new JPanel();
    contButtonPanel.setLayout(new BoxLayout(contButtonPanel, BoxLayout.Y_AXIS));
    contButtonPanel.setBorder(BorderFactory.createTitledBorder("Непрерывность"));

    JRadioButton c0Button = new JRadioButton(c0String);
    c0Button.setActionCommand(c0String);
    c0Button.setSelected(true);
    splinePanel.contType = SplinePanel.Continuous.C0;

    JRadioButton c1Button = new JRadioButton(c1String);
    c1Button.setActionCommand(c1String);

    JRadioButton c2Button = new JRadioButton(c2String);
    c2Button.setActionCommand(c2String);

    ButtonGroup contGroup = new ButtonGroup();
    contGroup.add(c0Button);
    contGroup.add(c1Button);
    contGroup.add(c2Button);
    
    c0Button.addActionListener(this);
    c1Button.addActionListener(this);
    c2Button.addActionListener(this);

    contButtonPanel.add(c0Button);
    contButtonPanel.add(c1Button);
    contButtonPanel.add(c2Button);

    contButtonPanel.setPreferredSize(new Dimension(180, 90));
    contButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

    /* Кнопки переключения длины интервала параметризации*/
    JPanel paramButtonPanel = new JPanel();
    paramButtonPanel.setLayout(new BoxLayout(paramButtonPanel, BoxLayout.Y_AXIS));
    paramButtonPanel.setBorder(BorderFactory.createTitledBorder("Длина отрезка парам."));

    JRadioButton chordButton = new JRadioButton(chordString);
    chordButton.setActionCommand(chordString);
    chordButton.setSelected(true);
    splinePanel.paramLength = SplinePanel.ParameterLength.Chord;

    JRadioButton normButton = new JRadioButton(normString);
    normButton.setActionCommand(normString);

    ButtonGroup paramGroup = new ButtonGroup();
    paramGroup.add(chordButton);
    paramGroup.add(normButton);

    chordButton.addActionListener(this);
    normButton.addActionListener(this);

    paramButtonPanel.add(chordButton);
    paramButtonPanel.add(normButton);

    paramButtonPanel.setPreferredSize(new Dimension(180, 70));
    paramButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

    /* Кнопки управления показом контрольных точек */
    JPanel showControlPanel = new JPanel();
    showControlPanel.setLayout(new BoxLayout(showControlPanel, BoxLayout.Y_AXIS));
    showControlPanel.setBorder(BorderFactory.createTitledBorder("Показ контр. точек"));

    JRadioButton yesButton = new JRadioButton(yesString);
    yesButton.setActionCommand(yesString);
    yesButton.setSelected(true);
    splinePanel.showControl = true;

    JRadioButton noButton = new JRadioButton(noString);
    noButton.setActionCommand(noString);

    ButtonGroup controlGroup = new ButtonGroup();
    controlGroup.add(yesButton);
    controlGroup.add(noButton);

    yesButton.addActionListener(this);
    noButton.addActionListener(this);

    showControlPanel.add(yesButton);
    showControlPanel.add(noButton);

    showControlPanel.setPreferredSize(new Dimension(180, 70));
    showControlPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

    /* Компоновка панели управления */
    add(Box.createVerticalGlue());
    add(contButtonPanel);
    add(Box.createRigidArea(new Dimension(0,50)));
    add(paramButtonPanel);
    add(Box.createRigidArea(new Dimension(0,50)));
    add(showControlPanel);
    add(Box.createVerticalGlue());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case c0String:
        splinePanel.contType = SplinePanel.Continuous.C0;
        break;
      case c1String:
        splinePanel.contType = SplinePanel.Continuous.C1;
        break;
      case c2String:
        splinePanel.contType = SplinePanel.Continuous.C2;
        break;
      case chordString:
        splinePanel.paramLength = SplinePanel.ParameterLength.Chord;
        break;
      case normString:
        splinePanel.paramLength = SplinePanel.ParameterLength.Norm;
        break;
      case yesString:
        splinePanel.showControl = true;
        break;
      case noString:
        splinePanel.showControl = false;
        break;
    }
    splinePanel.repaint();
  }

//  @Override
//  public Dimension getPreferredSize() {
//    return new Dimension(100, splinePanel.PANEL_HEIGHT);
//  }
//
//  @Override
//  public Dimension getMinimumSize() {
//    return new Dimension(90, splinePanel.PANEL_HEIGHT);
//  }
}
