package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JLabel;
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
public class NodePropertiesPanel extends AbstractPropertiesPanel {
    String _propName = null;
    List _propValue = new LinkedList();
    //JLabel _propNameLabel = new JLabel();
    JLabel _propValueLabel = new JLabel();
    int _minPadding = 15;

    public NodePropertiesPanel (String propName, List propValue) {
        _propName = propName;
        _propValue = propValue;
        initLabels();
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
        // add second label
        add(_propValueLabel);
    }

    public JPanel getPanel() {
        return this;
    }

    private void initLabels() {
        _propNameLabel.setText(_propName);
        _propValueLabel.setText("");
    }

    public void clear () {
      _propValueLabel.setText("");
    }

    public void update (List propValueList) {
        setPropValue(propValueList);
    }

    private void setPropValue (List propValueList) {
        //_propValue = _propValue;
        _propValue = propValueList;
        String propertyValue = "";
        Iterator propValueIterator = propValueList.iterator();
        while (propValueIterator.hasNext()) {
            propertyValue = propertyValue + (String) propValueIterator.next() + " ";
        }
        _propValueLabel.setText( propertyValue);
        _propValueLabel.setToolTipText( propertyValue);
    }

}

