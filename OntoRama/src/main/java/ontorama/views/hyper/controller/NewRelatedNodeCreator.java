/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NewRelatedNodeCreator.java,v 1.7 2003-03-31 04:57:08 nataliya Exp $
 */
package ontorama.views.hyper.controller;


import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ontorama.model.graph.EdgeType;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeModificationException;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeNodeImpl;
import ontorama.views.hyper.view.SimpleHyperView;

public class NewRelatedNodeCreator {
    private JDialog dialog;
    private SimpleHyperView view;
    private TreeNode treeNode;
    private EdgeType edgeType;
    private JTextField textField;

    public NewRelatedNodeCreator(SimpleHyperView view, TreeNode treeNode, EdgeType edgeType) {
        this.view = view;
        this.treeNode = treeNode;
        this.edgeType = edgeType;

        JLabel label = new JLabel("Please enter a new node identifier: ");
        textField = new JTextField(50);
        label.setLabelFor(textField);

        Object[] panel = {label, textField};
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );

        dialog = pane.createDialog(view, "New Node");
    }

    public TreeNode createNewRelatedNode() throws TreeModificationException {
        dialog.show();
        String newNodeName = textField.getText();
        
        TreeNode newTreeNode = new TreeNodeImpl(newNodeName, treeNode.getNodeType());
        
        Tree tree = view.getTree();
        
    	tree.addNode(newTreeNode, treeNode, this.edgeType);
        return newTreeNode;
    }
    
}
