package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.border.*;
import javax.swing.BorderFactory;
import javax.swing.Box;

import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;


import ontorama.model.*;
import ontorama.ontologyConfig.*;
import ontorama.OntoramaConfig;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

public class DescriptionView extends JPanel implements NodeObserver  {

  /**
   * this hashtable will hold labels of concept property names as keys
   * and labels of corresponding values for a node as values
   */
  private Hashtable labels = new Hashtable();

  /**
   * Keys - name of property
   * Values - panel
   */
  private Hashtable nodePropertiesPanels = new Hashtable();

  /**
   *
   */
   private ClonesPanel clonesPanel;

  /**
   * @todo  think of a way to not hardcode clones
   */
  String clonesLabelName = "Clones";

  public DescriptionView(Graph graph) {

    // add this view to the list of GraphNode  observers
    List graphNodesList = graph.getNodesList();
    Iterator graphNodesIterator = graphNodesList.iterator();
    while (graphNodesIterator.hasNext()) {
      GraphNode curNode = (GraphNode) graphNodesIterator.next();
      curNode.addObserver(this);
    }

    // set up hashtable of labels
    //initLabels();
    initPropertiesPanels();

    // set layout manager
    this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

    // add labels to the view
//    Enumeration labelsEnum = labels.keys();
//    int maxLeftLabelWidth = getMaxLabelWidth();
//    int minPadding = 15;
//    while (labelsEnum.hasMoreElements()) {
//      JLabel curLabel = (JLabel) labelsEnum.nextElement();
//      JLabel curValueLabel = (JLabel) labels.get(curLabel);
//      // create a panel for a pair
//      JPanel curPanel = new JPanel();
//      // create and set layout
//      LayoutManager curLayout = new BoxLayout(curPanel,BoxLayout.X_AXIS);
//      curPanel.setLayout(curLayout);
//      curPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//      // add first label
//      curPanel.add(curLabel);
//      // work out size of rigid area and set it
//      int curRigitAreaWidth = maxLeftLabelWidth - getLabelWidth(curLabel) + minPadding;
//      Dimension d = new Dimension(curRigitAreaWidth,0);
//      curPanel.add(Box.createRigidArea(d));
//      // add second label
//      curPanel.add(curValueLabel);
//      // finally, add created panel to the main layout
//      this.add(curPanel);
//    }
    Enumeration propPanelsEnum = nodePropertiesPanels.keys();
    while (propPanelsEnum.hasMoreElements()) {
        String propName = (String) propPanelsEnum.nextElement();
        JPanel propPanel = (JPanel) nodePropertiesPanels.get(propName);
        this.add(propPanel);
    }
    this.add(clonesPanel);
  }

  /**
   * Initialise labels that describe concept/node properties
   * @todo  think of a way to not hardcode clones
   */
//  private void initLabels() {
//    Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
//    while (e.hasMoreElements()) {
//      String detailsName = (String) e.nextElement();
//      JLabel label = new JLabel(detailsName);
//      JLabel valuesLabel = new JLabel("");
//      this.labels.put(label, valuesLabel);
//    }
//    // add clones
//    JLabel clonesLabel = new JLabel(clonesLabelName);
//    JLabel clonesValuesLabel = new JLabel("");
//    this.labels.put(clonesLabel,clonesValuesLabel);
//  }

  /**
   *
   */
   private void initPropertiesPanels() {
        Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
        while (e.hasMoreElements()) {
            String propName = (String) e.nextElement();
            NodePropertiesPanel propPanel = new NodePropertiesPanel(propName,new LinkedList());
            nodePropertiesPanels.put(propName,propPanel);
        }
        this.clonesPanel = new ClonesPanel();
   }

  /**
   * find max label width
   */
   private int getMaxLabelWidth() {
    Enumeration e = labels.keys();
    int length = 0;
    while (e.hasMoreElements()) {
      JLabel curLabel = (JLabel) e.nextElement();
      int width = getLabelWidth(curLabel);
      if (width > 0) {
        length = width;
      }
    }
    return length;
   }

   /**
    * get label width
    */
    private int getLabelWidth (JLabel label) {
      Font font = label.getFont();
      FontMetrics fontMetrics = label.getFontMetrics(font);
      int width = fontMetrics.stringWidth(label.getText());
      return width;
    }

  /**
   * Method called to update observer.
   * Communication from GraphNode to DescriptionView
   * @todo  think of a way to not hardcode clones
   */
  public void update( Object observer, Object observable ) {
    GraphNode node = (GraphNode) observable;
    System.out.println(".......method update, node = " + node.getName());
    Enumeration e = this.nodePropertiesPanels.keys();
    while (e.hasMoreElements()) {
        String propertyName = (String) e.nextElement();
      //JLabel keyLabel = (JLabel) e.nextElement();
      //JLabel valueLabel = (JLabel) this.labels.get(keyLabel);

//      if (keyLabel.getText().equals(clonesLabelName)) {
//        Iterator clonesIterator = node.getClones();
//        String clonesString = "";
//        while (clonesIterator.hasNext()) {
//          GraphNode clone = (GraphNode) clonesIterator.next();
//          clonesString = clonesString + clone.getName() + " ";
//        }
//        //valueLabel.setText(clonesString);
//        continue;
//      }

      //String propertyName = keyLabel.getText();
      NodePropertiesPanel propPanel = (NodePropertiesPanel) nodePropertiesPanels.get(propertyName);
      propPanel.update(node.getProperty(propertyName));

//      String propertyValue = "";
//      Iterator propertyValueIterator = node.getProperty(propertyName).iterator();
//      while (propertyValueIterator.hasNext()) {
//        propertyValue = propertyValue + (String) propertyValueIterator.next() + " ";
//      }
//      valueLabel.setText( propertyValue);

    }
    // deal with clones
    clonesPanel.update(node.getClones());
  }

}