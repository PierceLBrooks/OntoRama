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
import javax.swing.ToolTipManager;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;


import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

import ontorama.webkbtools.datamodel.*;
import ontorama.webkbtools.util.*;

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
  private ButtonGroup group;

  private JRadioButton selectedButton;

  public AmbuguousChoiceDialog(List typesList, Frame frame) {
    super(frame, "title here");

    this.choiceList = typesList;
    this.numChoices = choiceList.size();

    //System.out.println ("choice list = " + choiceList);
    JRootPane rootPane = getRootPane();
    Container contentPane = rootPane.getContentPane();

    JPanel choicesPanel = buildChoicePanel();

    contentPane.add(choicesPanel);

    setModal(true);
    setLocationRelativeTo(frame);
    pack();
    setVisible(true);
  }

  /**
   *
   */
  private JPanel buildChoicePanel () {
    JRadioButton[] radioButtons = new JRadioButton[choiceList.size()];
    group = new ButtonGroup();

    for (int i = 0; i < numChoices; i++) {
      OntologyType curType = (OntologyType) choiceList.get(i);
      //JRadioButton curButton = new JRadioButton( (String) choiceList.get(i));
      JRadioButton curButton = new JRadioButton( curType.getName());
      radioButtons[i] = curButton;
      if (i == 0) {
        curButton.setSelected(true);
        selectedButton = curButton;
      }
      curButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          JRadioButton selected = (JRadioButton) ae.getSource();
          //System.out.println("button action: " + selected);
          selectedButton = selected;
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

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent ae) {
        closeDialog();
      }
    });
    pane.add(okButton, BorderLayout.SOUTH);

    return pane;
  }

  /**
   *
   */
  public void closeDialog () {
    setVisible(false);
  }


  /**
   *
   */
  public String getSelected () {
    //Object[] selectedObjects = group.getSelection().getSelectedObjects();
    //System.out.println("\n\n\nselectedObjects  = " + selectedObjects);
    //JRadioButton selectedButton = (JRadioButton) selectedObjects[0];
    System.out.println("\n\n\nselectedButton = " + selectedButton.getText());
    return selectedButton.getText();
  }


}