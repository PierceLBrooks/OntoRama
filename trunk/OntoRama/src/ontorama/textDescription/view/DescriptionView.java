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
import javax.swing.JScrollPane;

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
    Enumeration propPanelsEnum = nodePropertiesPanels.keys();
    while (propPanelsEnum.hasMoreElements()) {
        String propName = (String) propPanelsEnum.nextElement();
        JPanel propPanel = (JPanel) nodePropertiesPanels.get(propName);
        this.add(propPanel);
    }
    this.add(clonesPanel);
  }

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
    //System.out.println(".......method update, node = " + node.getName());
    Enumeration e = this.nodePropertiesPanels.keys();
    while (e.hasMoreElements()) {
      String propertyName = (String) e.nextElement();
      NodePropertiesPanel propPanel = (NodePropertiesPanel) nodePropertiesPanels.get(propertyName);
      propPanel.update(node.getProperty(propertyName));

    }
    // deal with clones
    clonesPanel.update(node.getClones());
  }
}