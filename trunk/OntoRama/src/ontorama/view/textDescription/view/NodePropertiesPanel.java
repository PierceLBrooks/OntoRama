package ontorama.views.textDescription.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import ontorama.model.graph.Node;

/**
 *
 * Class responsible for building a panel to display concept/node
 * properties. This panel should consist of two parts: label on
 * the left stating property name and on the rigth hand side -
 * value of that property for this node.
 *
 * For example, for Description property, label on the left should
 * have text "Description" and on the right for node wn#cat there
 * should be value something like "cat is blah blah blah...".
 * Needless to say that value on the right hand side should change
 * for each node when views are focused on this node.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */
public class NodePropertiesPanel extends AbstractPropertiesPanel {
    String _propName = null;
    List _propValue = new LinkedList();
    //JLabel _propNameLabel = new JLabel();
    JLabel _propValueLabel = new JLabel();

    public NodePropertiesPanel(String propName, List propValue) {
        _propName = propName;
        System.out.println("nodePropertiesPanel, propName = " + propName);
        _propValue = propValue;
        initLabels();

        layoutFirstComponent();

        // add second label
        add(_propValueLabel);
    }

    private void initLabels() {
        _propNameLabel.setText(_propName);
        _propValueLabel.setText("");
    }

    public void clear() {
        _propValueLabel.setText("");
    }

    public void update(List propValueList) {
        setPropValue(propValueList);
    }

    private void setPropValue(List propValueList) {
        //_propValue = _propValue;
        _propValue = propValueList;
        String propertyValue = "";
        Iterator propValueIterator = propValueList.iterator();
        while (propValueIterator.hasNext()) {
            Object obj = propValueIterator.next();
            if (obj.getClass().equals(java.lang.String.class)) {
                String propValueString = (String) obj;
                propertyValue = propertyValue + propValueString;
            }
            else {
                ontorama.model.graph.Node node = (ontorama.model.graph.Node) obj;
                propertyValue =  propertyValue + node.getName() + " ";
            }
        }
        _propValueLabel.setText(propertyValue);
        _propValueLabel.setToolTipText(propertyValue);
    }

}
