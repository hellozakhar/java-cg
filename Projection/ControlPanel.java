import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel
    implements ChangeListener, ActionListener {

  private final ProjectionPanel panel;

  private final JSlider thetaSlider;
  private final JSlider phiSlider;
  private final JSlider distanceSlider;

  private final JRadioButton centralButton;
  private final JRadioButton isometryButton;
  private final JRadioButton dimetryButton;
  private final JRadioButton trimetryButton;
  private final JRadioButton frontIsometryButton;
  private final JRadioButton frontDimetryButton;
  private final JRadioButton obliqueDimetryButton;

  public ControlPanel(ProjectionPanel panel) {
    this.panel = panel;

    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JPanel projectionTypesPanel = createPanel("Виды проекций");
    projectionTypesPanel.setAlignmentX(CENTER_ALIGNMENT);
    JPanel parallelPanel = createPanel("Параллельная");
    JPanel orthogonalPanel = createPanel("Ортогональная");
    JPanel obliquePanel = createPanel("Косоугольная");
    orthogonalPanel.setAlignmentX(LEFT_ALIGNMENT);
    obliquePanel.setAlignmentX(LEFT_ALIGNMENT);

    thetaSlider = createAngleSlider(SwingConstants.VERTICAL);
    thetaSlider.setAlignmentX(CENTER_ALIGNMENT);
    phiSlider = createAngleSlider(SwingConstants.HORIZONTAL);
    phiSlider.setAlignmentX(CENTER_ALIGNMENT);

    distanceSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 5);
    distanceSlider.setMajorTickSpacing(5);
    distanceSlider.setPaintTicks(true);
    distanceSlider.setPaintLabels(true);
    distanceSlider.addChangeListener(this);
    distanceSlider.setAlignmentX(LEFT_ALIGNMENT);
    distanceSlider.setEnabled(false);

    ButtonGroup group = new ButtonGroup();
    centralButton = createRadioButton("Центральная", false, group);
    isometryButton = createRadioButton("Изометрическая", false, group);
    dimetryButton = createRadioButton("Диметрическая", false, group);
    trimetryButton = createRadioButton("Триметрическая", true, group);
    frontIsometryButton = createRadioButton("Фронтальная изометрическая", false, group);
    frontDimetryButton = createRadioButton("Фронтальная диметрическая", false, group);
    obliqueDimetryButton = createRadioButton("Произвольная диметрическая", false, group);

    orthogonalPanel.add(isometryButton);
    orthogonalPanel.add(dimetryButton);
    orthogonalPanel.add(trimetryButton);

    obliquePanel.add(frontIsometryButton);
    obliquePanel.add(frontDimetryButton);
    obliquePanel.add(obliqueDimetryButton);

    parallelPanel.add(orthogonalPanel);
    parallelPanel.add(obliquePanel);

    projectionTypesPanel.add(centralButton);
    projectionTypesPanel.add(distanceSlider);
    projectionTypesPanel.add(parallelPanel);

    add(thetaSlider);
    //add(Box.createRigidArea(new Dimension(0, 20)));
    add(phiSlider);
    //add(Box.createRigidArea(new Dimension(0, 20)));
    add(projectionTypesPanel);
  }

  private JPanel createPanel(String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder(title));
    return panel;
  }

  private JRadioButton createRadioButton(String text, boolean selected, ButtonGroup group) {
    JRadioButton button = new JRadioButton(text);
    button.setSelected(selected);
    button.setAlignmentX(LEFT_ALIGNMENT);
    button.addActionListener(this);
    group.add(button);
    return button;
  }

  private JSlider createAngleSlider(int direction) {
    JSlider slider = new JSlider(direction, -90, 90, 0);
    slider.setMajorTickSpacing(30);
    slider.setMinorTickSpacing(10);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(this);
    return slider;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JRadioButton source = (JRadioButton)e.getSource();
    // Все объекты-кнопки различны, поэтому выполняется не более одного if'а.
    // В конце нужно перерисовать панель с рисунком.
    // Нельзя использовать switch, потому что source имеет ссылочный тип.
    if (source == centralButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.Central;
      phiSlider.setEnabled(true);
      thetaSlider.setEnabled(true);
      distanceSlider.setEnabled(true);
    }
    if (source == isometryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.Isometry;
      phiSlider.setValue((int)Math.toDegrees(panel.phiIsometry));
      thetaSlider.setValue((int)Math.toDegrees(panel.thetaIsometry));
      phiSlider.setEnabled(false);
      thetaSlider.setEnabled(false);
      distanceSlider.setEnabled(false);
    }
    if (source == dimetryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.Dimetry;
      phiSlider.setValue((int)Math.toDegrees(panel.phiDimetry));
      thetaSlider.setValue((int)Math.toDegrees(panel.thetaDimetry));
      phiSlider.setEnabled(false);
      thetaSlider.setEnabled(false);
      distanceSlider.setEnabled(false);
    }
    if (source == trimetryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.Trimetry;
      phiSlider.setEnabled(true);
      thetaSlider.setEnabled(true);
      distanceSlider.setEnabled(false);
    }
    if (source == frontIsometryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.FrontIsometry;
      phiSlider.setValue((int)Math.toDegrees(panel.phiFrontIsometry));
      thetaSlider.setValue((int)Math.toDegrees(panel.thetaFrontIsometry));
      phiSlider.setEnabled(false);
      thetaSlider.setEnabled(false);
      distanceSlider.setEnabled(false);
    }
    if (source == frontDimetryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.FrontDimetry;
      phiSlider.setValue((int)Math.toDegrees(panel.phiFrontDimetry));
      thetaSlider.setValue((int)Math.toDegrees(panel.thetaFrontDimetry));
      phiSlider.setEnabled(false);
      thetaSlider.setEnabled(false);
      distanceSlider.setEnabled(false);
    }
    if (source == obliqueDimetryButton) {
      panel.projectionType = ProjectionPanel.ProjectionType.ObliqueDimetry;
      phiSlider.setEnabled(true);
      thetaSlider.setEnabled(true);
      distanceSlider.setEnabled(false);
    }

    panel.repaint();
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JSlider source = (JSlider)e.getSource();
//    if (!source.getValueIsAdjusting()) {
      if (source == thetaSlider)
        panel.theta = Math.toRadians(source.getValue());
      if (source == phiSlider)
        panel.phi = Math.toRadians(source.getValue());
      if (source == distanceSlider)
        panel.distance = source.getValue();
      panel.repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(350, 400);
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(300, 400);
  }
}
