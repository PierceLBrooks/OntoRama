package ontorama.textDescription.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import ontorama.model.graph.controller.NodeSelectedEvent;
import ontorama.model.graph.Node;
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

    public ClonesPanel(String propName, EventBroker eventBroker) {
        super(propName, eventBroker);
    }

    protected JComponent createPropertyComponent(final ontorama.model.graph.Node graphNode) {
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

