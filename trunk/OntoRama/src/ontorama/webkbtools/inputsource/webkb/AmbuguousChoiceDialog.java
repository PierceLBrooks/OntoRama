package ontorama.webkbtools.inputsource.webkb;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;


import java.util.List;
import java.util.Iterator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class AmbuguousChoiceDialog extends JDialog {

  private List choiceList;
  private int numChoices;

  public AmbuguousChoiceDialog(List choiceList) {
    this.choiceList = choiceList;
    this.numChoices = choiceList.size();

    System.out.println ("choice list = " + choiceList);
    JRootPane rootPane = getRootPane();
    Container contentPane = rootPane.getContentPane();

    JPanel choicesPanel = buildChoicePanel();

    contentPane.add(choicesPanel);

    setModal(true);
    pack();
    setVisible(true);
  }

  /**
   *
   */
  private JPanel buildChoicePanel () {
    JRadioButton[] radioButtons = new JRadioButton[choiceList.size()];
    ButtonGroup group = new ButtonGroup();

    for (int i = 0; i < numChoices; i++) {
      JRadioButton curButton = new JRadioButton( (String) choiceList.get(i));
      radioButtons[i] = curButton;
      if (i == 0) {
        curButton.setSelected(true);
      }
      curButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          JRadioButton selected = (JRadioButton) ae.getSource();
          System.out.println("button action: " + selected);
        }
      });
      group.add(curButton);
    }

    JPanel box = new JPanel();
    JLabel label = new JLabel("Search term is ambuguous...Please choose one of the following ");

    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    box.add(label);

    for (int i = 0; i < numChoices; i++) {
        box.add(radioButtons[i]);
    }

    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(box, BorderLayout.NORTH);
    pane.add(new JButton("OK"), BorderLayout.SOUTH);

    return pane;
  }

}