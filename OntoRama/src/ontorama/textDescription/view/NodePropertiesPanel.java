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
import java.awt.Font;
import java.awt.FontMetrics;


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
public class NodePropertiesPanel extends JPanel {
    String propName = null;
    List propValue = new LinkedList();
    JLabel propNameLabel = new JLabel();
    JLabel propValueLabel = new JLabel();
    int minPadding = 15;

    public NodePropertiesPanel (String propName, List propValue) {
        this.propName = propName;
        this.propValue = propValue;
        initLabels();
        // create and set layout
        LayoutManager curLayout = new BoxLayout(this,BoxLayout.X_AXIS);
        this.setLayout(curLayout);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        // add first label
        this.add(propNameLabel);
        // work out size of rigid area and set it
        //int curRigitAreaWidth = maxLeftLabelWidth - getLabelWidth(propNameLabel) + this.minPadding;
        int curRigitAreaWidth = this.minPadding;
        Dimension d = new Dimension(curRigitAreaWidth,0);
        this.add(Box.createRigidArea(d));
        // add second label
        this.add(propValueLabel);
    }

    public JPanel getPanel() {
        return this;
    }

    private void initLabels() {
        propNameLabel.setText(this.propName);
        propValueLabel.setText("");
    }

    public String getPropName () {
        return this.propName;
    }

    public void update (List propValueList) {
        setPropValue(propValueList);
    }

    private void setPropValue (List propValueList) {
        this.propValue = propValue;
        String propertyValue = "";
        Iterator propValueIterator = propValueList.iterator();
        while (propValueIterator.hasNext()) {
            propertyValue = propertyValue + (String) propValueIterator.next() + " ";
        }
        propValueLabel.setText( propertyValue);
    }

}

