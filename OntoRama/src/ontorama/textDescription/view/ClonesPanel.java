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
import ontorama.controller.NodeSelectedEvent;

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
public class ClonesPanel extends AbstractMultiValuesPanel {

    public ClonesPanel(String propName, ViewEventListener viewListener, EventBroker eventBroker) {
        super(propName, viewListener, eventBroker);
    }

    protected JComponent createPropertyComponent (final GraphNode graphNode) {
          JButton button = new JButton();
          button.setText(graphNode.getName());
          button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _eventBroker.processEvent(new NodeSelectedEvent(graphNode));
            }
          });
          return (JComponent) button;
    }

}

