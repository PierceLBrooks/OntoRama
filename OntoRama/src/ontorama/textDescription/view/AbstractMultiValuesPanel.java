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
import javax.swing.Box;

import java.awt.Component;

import ontorama.model.*;
import ontorama.ontologyConfig.*;
import ontorama.OntoramaConfig;

import ontorama.util.event.ViewEventListener;
import org.tockit.events.EventBroker;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */
public abstract class AbstractMultiValuesPanel extends AbstractPropertiesPanel {
    protected JPanel _propValuePanel = new JPanel();

    protected ViewEventListener _viewListener;

    protected EventBroker _eventBroker;

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
    public AbstractMultiValuesPanel (String propName, ViewEventListener viewListener, EventBroker eventBroker) {
        _viewListener = viewListener;
        _eventBroker = eventBroker;

        _propNameLabel.setText(propName);
        
        layoutFirstComponent();

        // add second panel
        _propValuePanel.setLayout(new BoxLayout(_propValuePanel, BoxLayout.X_AXIS));
        _propValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(_propValuePanel);
    }
    
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
        while (propValuesIterator.hasNext()) {
          GraphNode curNode = (GraphNode) propValuesIterator.next();
          JComponent component = createPropertyComponent(curNode);
          _propValuePanel.add(component);
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



