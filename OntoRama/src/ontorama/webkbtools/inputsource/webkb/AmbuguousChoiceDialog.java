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

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
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

  /**
   * list to hold objects to choose from
   * (in our case - ontology types)
   */
  private List choiceList;
  private int numChoices;

  /**
   * group of choice buttons
   */
  private ButtonGroup group;

  /**
   * remember selected button
   */
  private JRadioButton selectedButton;

  /**
   * title string
   */
  private String titleStr = "Search Result is ambiguous";

  /**
   * 'explanation' string that will appear above choices
   */
  private String topLabelStr = "Search term is ambiguous...Please choose one of the following ";

  /**
   * name of property used in tool tips
   * @todo think of a way not to hard code property name here, because if it is
   * changed in the config.xml - we simply silently loose this feature. Or think
   * of a way to inform about this (where catching NoSuchPropertyException)
   */
  private String descrPropName = "Description";

  /**
   * Create a dialog that will display all choices and propmt user
   * to make a selection.
   * @param typesList - list holding all alternative ontology types
   * @param frame - owner frame
   */
  public AmbuguousChoiceDialog(List typesList, Frame frame) {
    super(frame);
    setTitle(titleStr);

    this.choiceList = typesList;
    this.numChoices = choiceList.size();

    //System.out.println ("choice list = " + choiceList);
    JRootPane rootPane = getRootPane();
    Container contentPanel = rootPane.getContentPane();

    JPanel choicesPanel = buildChoicePanel();

    //contentPanel.add(choicesPanel);

    //JPanel mainPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.add(choicesPanel, BorderLayout.NORTH);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent ae) {
        closeDialog();
      }
    });
    contentPanel.add(okButton, BorderLayout.SOUTH);

    setModal(true);
    setLocationRelativeTo(frame);
    pack();
    setVisible(true);
  }

  /**
   *
   */
  private JPanel buildChoicePanel () {
    JPanel choiceButtonsPanel = new JPanel();
    JLabel label = new JLabel(topLabelStr);

    choiceButtonsPanel.setLayout(new BoxLayout(choiceButtonsPanel, BoxLayout.Y_AXIS));
    choiceButtonsPanel.add(label);

    JRadioButton[] radioButtons = new JRadioButton[choiceList.size()];
    group = new ButtonGroup();

    for (int i = 0; i < numChoices; i++) {
      OntologyType curType = (OntologyType) choiceList.get(i);
      //JRadioButton curButton = new JRadioButton( (String) choiceList.get(i));
      JRadioButton curButton = new JRadioButton( curType.getName());
      try {
        List descrPropValue = curType.getTypeProperty(descrPropName);
        if (descrPropValue.size() > 0 ) {
          curButton.setToolTipText( (String) descrPropValue.get(0));
        }
      }
      catch (NoSuchPropertyException e) {
        // just do nothing - don't display any tool tips
        ///todo: should find a way to unobtrusively complain about this
      }
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
      choiceButtonsPanel.add(curButton);
    }
    return choiceButtonsPanel;
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