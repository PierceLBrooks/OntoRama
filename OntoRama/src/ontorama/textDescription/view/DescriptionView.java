package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Collection;

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

import ontorama.webkbtools.util.NoSuchPropertyException;

import ontorama.util.event.ViewEventListener;
import ontorama.util.event.ViewEventObserver;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

public class DescriptionView extends JPanel implements ViewEventObserver {

	/**
	 * this hashtable will hold labels of concept property names as keys
	 * and labels of corresponding values for a node as values
	 */
	//private Hashtable labels = new Hashtable();

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
	
	/**
	 * @todo  think of a way to not hardcode _fullUrlPropName
	 */
	private String _fullUrlPropName = "Full Url ";
	
	/**
	 * @todo  think of a way to not hardcode _parents
	 */
	private String _parentsLabelName = "Parents";
	
	/**
	 * 
	 */
	private ParentsPanel _parentsPanel;
	
	/**
	 * 
	 */
	private NodePropertiesPanel _fullUrlPanel;

	/**
	 *
	 */
	private Dimension propertyNameLabelsDimension;

	/**
	 *
	 */
	private ViewEventListener viewListener;

	/**
	 *
	 */
	public DescriptionView(ViewEventListener viewListener) {
		this.viewListener = viewListener;
		this.viewListener.addObserver(this);

		// set up hashtable of panels
		//initLabels();
		initPropertiesPanels();
		initFullUrlPanel();
		this.propertyNameLabelsDimension = calcLabelSize();
		setLabelSizes();

		// set layout manager
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// add panels to the view
		Enumeration propPanelsEnum = nodePropertiesPanels.keys();
		while (propPanelsEnum.hasMoreElements()) {
			String propName = (String) propPanelsEnum.nextElement();
			JPanel propPanel = (JPanel) nodePropertiesPanels.get(propName);
			this.add(propPanel);
		}
		this.add(clonesPanel);
		this.add(_fullUrlPanel);
		this.add(_parentsPanel);
	}

	/**
	 *
	 */
	public void setGraph(Graph graph) {

	}

	/**
	 * initialise concept properties panels
	 */
	private void initPropertiesPanels() {
		Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
		while (e.hasMoreElements()) {
			String propName = (String) e.nextElement();
			NodePropertiesPanel propPanel =
				new NodePropertiesPanel(propName, new LinkedList());
			nodePropertiesPanels.put(propName, propPanel);
		}
		this.clonesPanel = new ClonesPanel(clonesLabelName,viewListener);
		_parentsPanel = new ParentsPanel(_parentsLabelName, viewListener);
	}

	/**
	 * initialise panel for displaying full url 
	 */
	private void initFullUrlPanel() {
		_fullUrlPanel = 
			new NodePropertiesPanel(_fullUrlPropName, new LinkedList());
	}

	/**
	 * find max label width
	 */
	private int getMaxLabelWidth() {
		Iterator it = nodePropertiesPanels.values().iterator();
		//Enumeration e = labels.keys();
		int length = 0;
		while (it.hasNext()) {
			NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
			JLabel curLabel = curPanel.getPropNameLabel();
			//JLabel curLabel = (JLabel) e.nextElement();
			int width = curPanel.getPropNameLabelWidth();
			if (width > 0) {
				length = width;
			}
		}
		return length;
	}

	/**
	 *
	 */
	private void setLabelSizes() {
		Iterator it = nodePropertiesPanels.values().iterator();
		while (it.hasNext()) {
			NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
			JLabel curLabel = curPanel.getPropNameLabel();
			//Dimension d = curLabel.getSize();
			//System.out.println("d = " + d + ", width = " + maxSize + ", height = " + curPanel.getPropNameLabelHeight());
			curLabel.setSize(this.propertyNameLabelsDimension);
			curLabel.setMinimumSize(this.propertyNameLabelsDimension);
			curLabel.setMaximumSize(this.propertyNameLabelsDimension);
			curLabel.setPreferredSize(this.propertyNameLabelsDimension);
		}
		//this.clonesPanel.setNameLabelSize(this.propertyNameLabelsDimension);
	}

	/**
	 *
	 */
	private Dimension calcLabelSize() {
		int padding = 5;
		int maxSize = getMaxLabelWidth() + padding;
		Iterator it = nodePropertiesPanels.values().iterator();
		if (it.hasNext()) {
			NodePropertiesPanel panel = (NodePropertiesPanel) it.next();
			return (new Dimension(maxSize, panel.getPropNameLabelHeight()));
		}
		return new Dimension(50, 20);
	}

	/**
	 * clear description value panel from any leftover properties (left from previous example)
	 */
	public void clear() {
		Enumeration e = this.nodePropertiesPanels.keys();
		while (e.hasMoreElements()) {
			String propertyName = (String) e.nextElement();
			NodePropertiesPanel propPanel =
				(NodePropertiesPanel) nodePropertiesPanels.get(propertyName);
			propPanel.clear();
		}
		clonesPanel.clear();
		_parentsPanel.clear();
	}

	/**
	 * @todo  check if its safe to ignore NoSuchPropertyException (probably is safe
	 *        for browsing the ontology only. Could be a different story when we
	 *        are editing it )
	 */
	public void setFocus(GraphNode node) {
		Enumeration e = this.nodePropertiesPanels.keys();
		while (e.hasMoreElements()) {
			String propertyName = (String) e.nextElement();
			try {
				NodePropertiesPanel propPanel =
					(NodePropertiesPanel) nodePropertiesPanels.get(
						propertyName);
				propPanel.update(node.getProperty(propertyName));
			} catch (NoSuchPropertyException exc) {
				// this exception should have been caught when building the graph
				// we are displaying, so it should be safe to ignore it here
				System.err.println("NoSuchPropertyException exception: " + exc);
			}
		}
		// deal with clones
		clonesPanel.update(node.getClones());
		//this.repaint();
		

		List fullUrlPropList = new LinkedList();
		fullUrlPropList.add(node.getFullName());
		_fullUrlPanel.update(fullUrlPropList);

        Iterator it = Edge.getInboundEdgeNodes(node,1);
        while (it.hasNext()) {
          GraphNode parent = (GraphNode) it.next();
          System.out.println("  parent: " + parent.getName());
        }
		_parentsPanel.update(Edge.getInboundEdgeNodes(node,1));
	}

	//////////////////////////ViewEventObserver interface implementation////////////////

	/**
	 *
	 */
	public void focus(GraphNode node) {
		/*
		System.out.println();
		System.out.println(
			"******* desciptionView got focus for node "
				+ node.getName()
				+ ", address = "
				+ node);
		*/
		setFocus(node);
		
	}

	/**
	 *
	 */
	public void toggleFold(GraphNode node) {
	}

	/**
	 *
	 */
	public void query(GraphNode node) {
	}

}
