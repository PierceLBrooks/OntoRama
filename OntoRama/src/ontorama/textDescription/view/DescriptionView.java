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
import ontorama.tree.controller.GraphViewFocusEventHandler;

import ontorama.webkbtools.util.NoSuchPropertyException;

import ontorama.util.event.ViewEventListener;
import ontorama.util.event.ViewEventObserver;
import org.tockit.events.EventBroker;

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
	private Hashtable _nodePropertiesPanels = new Hashtable();

	/**
	 *
	 */
	private ClonesPanel _clonesPanel;

	/**
	 * @todo  think of a way to not hardcode clones
	 */
	String _clonesLabelName = "Clones";
	
	/**
	 * @todo  think of a way to not hardcode _fullUrlPropName
	 */
	private String _fullUrlPropName = "Full Url ";
	
	/**
	 * @todo  think of a way to not hardcode _parents
	 * 
	 * @todo	shouldn't hardcode parent relation, see line 
	 * because if someone changed config file for supertype/subtype relation to
	 * be some other integer - this won't work. there is also other scenarios for
	 * failure. maybe should have some rules for constructing config.xml file
	 * or some other way to tell which relation is supertype/subtype. 
	 * 
	 */
	private String _reverseRelationLinkName;
	private int _firstRelationLink = 1;
	
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
	private Dimension _propertyNameLabelsDimension;

	/**
	 *
	 */
	private ViewEventListener _viewListener;
    private EventBroker _eventBroker;

	/**
	 *
	 */
	public DescriptionView(ViewEventListener viewListener, EventBroker eventBroker) {
		_viewListener = viewListener;
		_viewListener.addObserver(this);

        _eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);

		initReverseRelation();

		initPropertiesPanels();
		_fullUrlPanel = 
			new NodePropertiesPanel(_fullUrlPropName, new LinkedList());
		_clonesPanel = new ClonesPanel(_clonesLabelName,_viewListener, _eventBroker);
		_parentsPanel = new ParentsPanel(_reverseRelationLinkName, _viewListener, _eventBroker);
		
		_propertyNameLabelsDimension = calcLabelSize();
		setLabelSizesForNodePropertiesPanels();

		setLayout(new GridLayout(1,2));
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
	
		JPanel leftSubPanel = new JPanel();
		leftSubPanel.setLayout(new BoxLayout(leftSubPanel, BoxLayout.Y_AXIS));
		leftSubPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel rightSubPanel = new JPanel();
		rightSubPanel.setLayout(new BoxLayout(rightSubPanel, BoxLayout.Y_AXIS));
		rightSubPanel.setBorder(BorderFactory.createEtchedBorder());
		
		// add panels to the view
		Enumeration propPanelsEnum = _nodePropertiesPanels.keys();
		while (propPanelsEnum.hasMoreElements()) {
			String propName = (String) propPanelsEnum.nextElement();
			JPanel propPanel = (JPanel) _nodePropertiesPanels.get(propName);
			leftSubPanel.add(propPanel);
		}
		
		rightSubPanel.add(_clonesPanel);		
		rightSubPanel.add(_parentsPanel);		
		rightSubPanel.add(_fullUrlPanel);
		
		add(leftSubPanel);
		add(rightSubPanel);
	}
	

	/**
	 *
	 */
	public void setGraph(Graph graph) {

	}
	
	/**
	 * 
	 */
	private void initReverseRelation () {
		RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(_firstRelationLink);
		_reverseRelationLinkName = relLinkDetails.getReversedLinkName();
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
			_nodePropertiesPanels.put(propName, propPanel);
		}
	}

	/**
	 * find max label width
	 */
	private int getMaxLabelWidth() {
		Iterator it = _nodePropertiesPanels.values().iterator();
		//Enumeration e = labels.keys();
		int length = 0;
		while (it.hasNext()) {
			NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
			//JLabel curLabel = curPanel.getPropNameLabel();
			//JLabel curLabel = (JLabel) e.nextElement();
			int width = curPanel.getPropNameLabelWidth();
			if (width > length) {
				length = width;
			}
		}
		int clonesWidth = _clonesPanel.getPropNameLabelWidth();
		if (clonesWidth > length) {
			length = clonesWidth;
		}
		int parentsWidth = _parentsPanel.getPropNameLabelWidth();
		if (parentsWidth > length) {
			length = parentsWidth;
		}
		int fullUrlWidth = _fullUrlPanel.getPropNameLabelWidth();
		if (fullUrlWidth > length) {
			length = fullUrlWidth;
		}
		return length;
	}

	/**
	 *
	 */
	private void setLabelSizesForNodePropertiesPanels() {
		Iterator it = _nodePropertiesPanels.values().iterator();
		while (it.hasNext()) {
			NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
			curPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
//			JLabel curLabel = curPanel.getPropNameLabel();
//			curLabel.setSize(_propertyNameLabelsDimension);
//			curLabel.setMinimumSize(_propertyNameLabelsDimension);
//			curLabel.setMaximumSize(_propertyNameLabelsDimension);
//			curLabel.setPreferredSize(_propertyNameLabelsDimension);
		}
		_clonesPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
		_parentsPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
		_fullUrlPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
	}

	/**
	 *
	 */
	private Dimension calcLabelSize() {
		int padding = 5;
		int maxSize = getMaxLabelWidth() + padding;
		Iterator it = _nodePropertiesPanels.values().iterator();
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
		Enumeration e = _nodePropertiesPanels.keys();
		while (e.hasMoreElements()) {
			String propertyName = (String) e.nextElement();
			NodePropertiesPanel propPanel =
				(NodePropertiesPanel) _nodePropertiesPanels.get(propertyName);
			propPanel.clear();
		}
		_clonesPanel.clear();
		_parentsPanel.clear();
	}

	/**
	 * @todo  check if its safe to ignore NoSuchPropertyException (probably is safe
	 *        for browsing the ontology only. Could be a different story when we
	 *        are editing it )
	 */
	public void setFocus(GraphNode node) {
		Enumeration e = _nodePropertiesPanels.keys();
		while (e.hasMoreElements()) {
			String propertyName = (String) e.nextElement();
			try {
				NodePropertiesPanel propPanel =
					(NodePropertiesPanel) _nodePropertiesPanels.get(
						propertyName);
				propPanel.update(node.getProperty(propertyName));
			} catch (NoSuchPropertyException exc) {
				// this exception should have been caught when building the graph
				// we are displaying, so it should be safe to ignore it here
				System.err.println("NoSuchPropertyException exception: " + exc);
			}
		}
		// deal with clones
		_clonesPanel.update(node.getClones());
		//this.repaint();
		

		List fullUrlPropList = new LinkedList();
		fullUrlPropList.add(node.getFullName());
		_fullUrlPanel.update(fullUrlPropList);

//        Iterator it = Edge.getInboundEdgeNodes(node,_firstRelationLink);
//        while (it.hasNext()) {
//          GraphNode parent = (GraphNode) it.next();
//          System.out.println("  parent: " + parent.getName());
//        }

		_parentsPanel.update(Edge.getInboundEdgeNodes(node,_firstRelationLink));
	}
	
	/**
	 * 
	 */
	public void enableDynamicFields () {
		_parentsPanel.setVisible(true);
	}
	
	/**
	 * 
	 */
	public void disableDynamicFields () {
		_parentsPanel.setVisible(false);
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
