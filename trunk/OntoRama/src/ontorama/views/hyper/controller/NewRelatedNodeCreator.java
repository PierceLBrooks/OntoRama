/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NewRelatedNodeCreator.java,v 1.5 2002-12-03 03:35:01 nataliya Exp $
 */
package ontorama.views.hyper.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeNode;
import ontorama.ontotools.NoSuchRelationLinkException;
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

    public TreeNode createNewRelatedNode() {
//        dialog.show();
//    	TreeNode newTreeNode = null;
//
//    	Tree tree = view.getTree();
//
//        Node newGraphNode = new NodeImpl(textField.getText());
//        List newEdges = new ArrayList();
//        try {
//        	Edge newGraphEdge = new EdgeImpl(treeNode.getGraphNode(), newGraphNode, edgeType);
//        	
//			newTreeNode = tree.addNode(treeNode, newGraphEdge, newGraphNode);
//        	
////            newEdges.add(newGraphEdge);
////            
////            newTreeNode = new TreeNodeImpl(newGraphNode);
////            TreeEdge newTreeEdge = new TreeEdgeImpl(newGraphEdge, newTreeNode);
////            treeNode.addChild(newTreeEdge);
////            
////            List clones = treeNode.getClones();
////            for (Iterator iterator = clones.iterator(); iterator.hasNext();) {
////                TreeNode node = (TreeNode) iterator.next();
////                TreeNode childCloneNode = new TreeNodeImpl (newGraphNode);
////                TreeEdge childCloneEdge = new TreeEdgeImpl (newGraphEdge, childCloneNode);
////                node.addChild(childCloneEdge);
////            }
//        } catch (NoSuchRelationLinkException e) {
//            /// @todo what to do here? Do we get this?
//            e.printStackTrace();
//            return null;
//        }
//
//        
////        try {
//            for (Iterator iterator = newEdges.iterator(); iterator.hasNext();) {
//                Edge edge = (Edge) iterator.next();
//                /// @todo modify graph here!
//                //graph.addEdge(edge);
//            }
////        } catch (GraphModificationException e) {
////            /// @todo what to do here? Do we get this?
////            e.printStackTrace();
////            return null;
////        } catch (NoSuchRelationLinkException e) {
////            /// @todo what to do here? Do we get this?
////            e.printStackTrace();
////            return null;
////        }
//        return newTreeNode;
return null;
    }
    
}
