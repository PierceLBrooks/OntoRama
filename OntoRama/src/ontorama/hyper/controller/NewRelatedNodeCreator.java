/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NewRelatedNodeCreator.java,v 1.5 2002-11-18 04:28:44 nataliya Exp $
 */
package ontorama.hyper.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.model.Edge;
import ontorama.model.EdgeImpl;
import ontorama.model.EdgeType;
import ontorama.model.Graph;
import ontorama.model.Node;
import ontorama.model.NodeImpl;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

public class NewRelatedNodeCreator {
    private JDialog dialog;
    private SimpleHyperView view;
    private Node graphNode;
    private EdgeType edgeType;
    private JTextField textField;

    public NewRelatedNodeCreator(SimpleHyperView view, Node graphNode, EdgeType edgeType) {
        this.view = view;
        this.graphNode = graphNode;
        this.edgeType = edgeType;

        JLabel label = new JLabel("Please enter a new node identifier: ");
        textField = new JTextField(50);
        label.setLabelFor(textField);

        Object[] panel = {label, textField};
        JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );

        dialog = pane.createDialog(view, "New Node");
    }

    public Node createNewRelatedNode() {
        dialog.show();

        Node newNode = new NodeImpl(textField.getText());
        List newEdges = new ArrayList();
        try {
            newEdges.add(new EdgeImpl(graphNode, newNode, edgeType));
            List clones = graphNode.getClones();
            for (Iterator iterator = clones.iterator(); iterator.hasNext();) {
                Node node = (Node) iterator.next();
                newEdges.add(new EdgeImpl(node, newNode.makeClone(), edgeType));
            }
        } catch (NoSuchRelationLinkException e) {
            /// @todo what to do here? Do we get this?
            e.printStackTrace();
            return null;
        }

        Graph graph = view.getGraph();
        try {
            for (Iterator iterator = newEdges.iterator(); iterator.hasNext();) {
                Edge edge = (Edge) iterator.next();
                graph.addEdge(edge);
            }
        } catch (GraphModificationException e) {
            /// @todo what to do here? Do we get this?
            e.printStackTrace();
            return null;
        } catch (NoSuchRelationLinkException e) {
            /// @todo what to do here? Do we get this?
            e.printStackTrace();
            return null;
        }
        return newNode;
    }
}
