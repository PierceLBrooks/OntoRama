package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import ontorama.model.*;
import ontorama.ontologyConfig.*;
import ontorama.OntoramaConfig;

import ontorama.util.event.ViewEventListener;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */
public abstract class AbstractMultiValuesPanel extends JPanel {
    JLabel _propNameLabel = new JLabel();
    JPanel _propValuePanel = new JPanel();
    int _minPadding = 40;

    Hashtable _componentToPropValueMapping = new Hashtable();

    ViewEventListener _viewListener;
    
    /**
     * Create multi values panel that can be used as 
     * a component in the description view.
     * This panel will consist of label that describes
     * name of the property we want to display and of
     * another subpanel that will contain list of values
     * corresponding to this property. Each of values
     * should be able to respond to some action.
     * 
     * For example, extension of this class could be 
     * clones panel which will have label "clones"
     * and subpanel will contain all nodes that are
     * clones to given node. each of the cloned nodes
     * will get a button and if this button is clicked - 
     * it should call "focus" action for corresponding 
     * node.
     * 
     * To achieve this behaviour, each extension of this class
     * should override method createPropertyComponent called
     * from update() method. createPropertyComponent() method
     * should create GUI component required and implement
     * action listener for this component.
     */
    public AbstractMultiValuesPanel (String propName, ViewEventListener viewListener) {
        _viewListener = viewListener;
        
        _propNameLabel.setText(propName);

        // create and set layout
        LayoutManager curLayout = new BoxLayout(this,BoxLayout.X_AXIS);
        setLayout(curLayout);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        // add first label
        add(_propNameLabel);

        // work out size of rigid area and set it
        //int curRigitAreaWidth = maxLeftLabelWidth - getLabelWidth(propNameLabel) + this.minPadding;
        int curRigitAreaWidth = _minPadding;
        Dimension d = new Dimension(curRigitAreaWidth,0);
        add(Box.createRigidArea(d));

        // add second panel
        _propValuePanel.setLayout(new BoxLayout(_propValuePanel, BoxLayout.X_AXIS));
        _propValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(_propValuePanel);
    }


//    public void setNameLabelSize (Dimension d) {
//      this.clonesNameLabel.setPreferredSize(d);
//      this.clonesNameLabel.setMinimumSize(d);
//    }

	/**
	 * Update property values panel with nodes
	 * passed in the iterator.
	 * The idea is to some GUI components to the panel
	 * for each node that implement some behaviour.
	 * For example, if this is clones panel, then we
	 * want to add buttons and each button should 
	 * recall focus action on the corresponding node
	 * when pressed
	 */
    public void update (Iterator propValuesIterator) {
        clear();
        // need updateUI, otherwise it seems that when a user clicks
        // on a clone button and clone is focused - we don't get a 'clone
        // button'
        _propValuePanel.updateUI();
        _componentToPropValueMapping = new Hashtable();
        while (propValuesIterator.hasNext()) {
          GraphNode curNode = (GraphNode) propValuesIterator.next();
          JComponent component = createPropertyComponent(curNode);
          _propValuePanel.add(component);
          //_componentToPropValueMapping.put(component, curNode);
        }
        _propValuePanel.update(_propValuePanel.getGraphics());
    }
    
    /**
     * Clear all property values
     */
    public void clear () {
      _propValuePanel.removeAll();
    }
    
    /**
     * method to be overriden by all overriding classes
     * 
     * this method should create a component (buttons, etc) 
     * for given node and implement action listener for this 
     * component
     */
    protected abstract JComponent createPropertyComponent (GraphNode node) ;
    

}



