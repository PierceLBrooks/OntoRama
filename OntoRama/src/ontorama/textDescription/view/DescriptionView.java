package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.LayoutManager;
import java.awt.GridLayout;

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
    initLabels();

    // set layout manager
    this.setLayout(new GridLayout(labels.size(),1));

    // add labels to the view
    Enumeration labelsEnum = labels.keys();
    while (labelsEnum.hasMoreElements()) {
      JLabel curLabel = (JLabel) labelsEnum.nextElement();
      this.add(curLabel);
      this.add( (JLabel) labels.get(curLabel));
    }

  }

  /**
   * Initialise labels that describe concept/node properties
   * @todo  think of a way to not hardcode clones
   */
  private void initLabels() {
    Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
    while (e.hasMoreElements()) {
      String detailsName = (String) e.nextElement();
      JLabel label = new JLabel(detailsName);
      this.labels.put(label, new JLabel(""));
    }
    // add clones
    this.labels.put(new JLabel(clonesLabelName),new JLabel());
  }

  /**
   * Method called to update observer.
   * Communication from GraphNode to DescriptionView
   * @todo  think of a way to not hardcode clones
   */
  public void update( Object observer, Object observable ) {
    GraphNode node = (GraphNode) observable;
    //System.out.println(".......method update, node = " + node.getName());
    Enumeration e = this.labels.keys();
    while (e.hasMoreElements()) {
      JLabel keyLabel = (JLabel) e.nextElement();
      JLabel valueLabel = (JLabel) this.labels.get(keyLabel);

      if (keyLabel.getText().equals(clonesLabelName)) {
        Iterator clonesIterator = node.getClones();
        String clonesString = "";
        while (clonesIterator.hasNext()) {
          GraphNode clone = (GraphNode) clonesIterator.next();
          clonesString = clonesString + clone.getName() + " ";
        }
        valueLabel.setText(clonesString);
        continue;
      }

      String propertyName = keyLabel.getText();
      valueLabel.setText( node.getProperty(propertyName));
    }

  }

}