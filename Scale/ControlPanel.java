import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

class ControlPanel extends JPanel implements ActionListener, ItemListener {
  static final String bottomLeftString = "BL";
  static final String bottomRightString = "BR";
  static final String topLeftString = "TL";
  static final String topRightString = "TR";
  static final String centerString = "C";
  //static final String fixedScaleString = "FS";
  //static final String fixedAspectRatioString = "FAR";
  
  private final ScalePanel drawingPanel;
  private final JCheckBox fixedScaleBox;
  private final JCheckBox fixedAspectRatioBox;

  public ControlPanel(ScalePanel drawingPanel) {
    this.drawingPanel = drawingPanel;

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JRadioButton bottomLeftButton = new JRadioButton("Внизу cлева");
    bottomLeftButton.setSelected(true);
    drawingPanel.position = ScalePanel.Position.BOTTOM_LEFT;
    bottomLeftButton.setActionCommand(bottomLeftString);
    JRadioButton bottomRightButton = new JRadioButton("Внизу справа");
    bottomRightButton.setActionCommand(bottomRightString);
    JRadioButton topLeftButton = new JRadioButton("Вверху слева");
    topLeftButton.setActionCommand(topLeftString);
    JRadioButton topRightButton = new JRadioButton("Вверху справа");
    topRightButton.setActionCommand(topRightString);
    JRadioButton centerButton = new JRadioButton("В центре");
    centerButton.setActionCommand(centerString);

    ButtonGroup group = new ButtonGroup();
    group.add(bottomLeftButton);
    group.add(bottomRightButton);
    group.add(topLeftButton);
    group.add(topRightButton);
    group.add(centerButton);

    JPanel radioPanel = new JPanel(new GridLayout(0, 1));
    radioPanel.add(bottomLeftButton);
    radioPanel.add(bottomRightButton);
    radioPanel.add(topLeftButton);
    radioPanel.add(topRightButton);
    radioPanel.add(centerButton);
    radioPanel.setBorder(BorderFactory.createTitledBorder("Положение"));
    radioPanel.setAlignmentX(LEFT_ALIGNMENT);
    radioPanel.setMaximumSize(radioPanel.getPreferredSize());

    bottomLeftButton.addActionListener(this);
    bottomRightButton.addActionListener(this);
    topLeftButton.addActionListener(this);
    topRightButton.addActionListener(this);
    centerButton.addActionListener(this);

    fixedScaleBox = new JCheckBox("Фиксированный масштаб");
    fixedScaleBox.setSelected(true);
    fixedAspectRatioBox = new JCheckBox("Фиксированное отношение");
    checkBoxStateChanged();

    JPanel checkPanel = new JPanel(new GridLayout(0, 1));
    checkPanel.add(fixedScaleBox);
    checkPanel.add(fixedAspectRatioBox);
    checkPanel.setBorder(BorderFactory.createTitledBorder("Масштаб"));
    checkPanel.setAlignmentX(LEFT_ALIGNMENT);
    checkPanel.setMaximumSize(checkPanel.getPreferredSize());
    
    fixedScaleBox.addItemListener(this);
    fixedAspectRatioBox.addItemListener(this);

    add(radioPanel);
    add(Box.createRigidArea(new Dimension(0, 20)));
    add(checkPanel);
    add(Box.createVerticalGlue());
  }
    
  @Override
  public Dimension getMinimumSize() {
    return new Dimension(250, 300);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(280, 300);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case bottomLeftString : drawingPanel.position = ScalePanel.Position.BOTTOM_LEFT; break;
      case bottomRightString : drawingPanel.position = ScalePanel.Position.BOTTOM_RIGHT; break;
      case topLeftString : drawingPanel.position = ScalePanel.Position.TOP_LEFT; break;
      case topRightString : drawingPanel.position = ScalePanel.Position.TOP_RIGHT; break;
      case centerString : drawingPanel.position = ScalePanel.Position.CENTER; break;
    }
    drawingPanel.repaint();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    checkBoxStateChanged();
  }
  
  private void checkBoxStateChanged() {
    if (fixedScaleBox.isSelected()) {
      drawingPanel.fixedScale = true;
      drawingPanel.fixedAspectRatio = true;
      fixedAspectRatioBox.setSelected(true);
      fixedAspectRatioBox.setEnabled(false);
    }
    else {
      drawingPanel.fixedScale = false;
      fixedAspectRatioBox.setEnabled(true);
      drawingPanel.fixedAspectRatio = fixedAspectRatioBox.isSelected();
    }
    drawingPanel.repaint();
  }
}
