package ontorama.textDescription.view;

import ontorama.controller.NodeSelectedEvent;
import ontorama.model.Node;
import org.tockit.events.EventBroker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    protected JComponent createPropertyComponent(final Node graphNode) {
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

