package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComponent;

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
public class ClonesPanel extends AbstractMultiValuesPanel {
	
    public ClonesPanel (String clonesPropName, ViewEventListener viewListener) {
    	super(clonesPropName, viewListener);
    }

   private JComponent createPropertyComponent (GraphNode graphNode) {
          JButton button = new JButton();
          button.setText(graphNode.getName());
          _componentToPropValueMapping.put(button, graphNode);
          button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphNode node = (GraphNode) _componentToPropValueMapping.get(e.getSource());
                _viewListener.notifyChange(node, ViewEventListener.MOUSE_SINGLECLICK);
            }
          });
          return (JComponent) button;
    }

}

