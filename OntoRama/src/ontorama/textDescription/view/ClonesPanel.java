package ontorama.textDescription.view;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

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
public class ClonesPanel extends JPanel {
    String clonesNameString = "Clones";
    Iterator clones;
    JLabel clonesNameLabel = new JLabel();
    JPanel clonesValuePanel = new JPanel();
    int minPadding = 30;

    Hashtable buttonCloneMapping = new Hashtable();

    private ViewEventListener viewListener;

    public ClonesPanel (ViewEventListener viewListener) {
        this.viewListener = viewListener;
        //this.clones = clones;

        initLabels();

        // create and set layout
        LayoutManager curLayout = new BoxLayout(this,BoxLayout.X_AXIS);
        this.setLayout(curLayout);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        // add first label
        this.add(clonesNameLabel);

        // work out size of rigid area and set it
        //int curRigitAreaWidth = maxLeftLabelWidth - getLabelWidth(propNameLabel) + this.minPadding;
        int curRigitAreaWidth = this.minPadding;
        Dimension d = new Dimension(curRigitAreaWidth,0);
        this.add(Box.createRigidArea(d));

        // add second panel
        clonesValuePanel.setLayout(new BoxLayout(clonesValuePanel, BoxLayout.X_AXIS));
        clonesValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(clonesValuePanel);
    }

    public JPanel getPanel() {
        return this;
    }

    private void initLabels() {
        clonesNameLabel.setText(this.clonesNameString);
    }

    public void setNameLabelSize (Dimension d) {
      this.clonesNameLabel.setPreferredSize(d);
      this.clonesNameLabel.setMinimumSize(d);
    }

    public void update (Iterator clonesIterator) {
        //System.out.println("ClonesPanel, method update");
        this.clones = clonesIterator;
        clonesValuePanel.removeAll();
        // need updateUI, otherwise it seems that when a user clicks
        // on a clone button and clone is focused - we don't get a 'clone
        // button'
        clonesValuePanel.updateUI();
        this.buttonCloneMapping = new Hashtable();
        while (clonesIterator.hasNext()) {
          GraphNode clone = (GraphNode) clonesIterator.next();
          //System.out.println("clone = " + clone.getName());
          JButton button = new JButton();
          button.setText(clone.getName());
          buttonCloneMapping.put(button,clone);
          button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphNode node = (GraphNode) buttonCloneMapping.get(e.getSource());
                //System.out.println("CLONE button is sending event ViewEventListener.MOUSE_SINGLECLICK for node " + node);
                viewListener.notifyChange(node, ViewEventListener.MOUSE_SINGLECLICK);
            }
          });
          clonesValuePanel.add(button);
        }
        clonesValuePanel.update(clonesValuePanel.getGraphics());
        //clonesValuePanel.repaint();
        //this.repaint();
    }

    public void clear () {
      clonesValuePanel.removeAll();
    }

}

