/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: NewRelatedNodeCreator.java,v 1.8 2002-11-24 23:48:37 nataliya Exp $
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
import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeImpl;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.model.graph.GraphModificationException;
import ontorama.webkbtools.NoSuchRelationLinkException;

public class NewRelatedNodeCreator {
    private JDialog dialog;
    private SimpleHyperView view;
    private ontorama.model.graph.Node graphNode;
    private ontorama.model.graph.EdgeType edgeType;
    private JTextField textField;

    public NewRelatedNodeCreator(SimpleHyperView view, ontorama.model.graph.Node graphNode, ontorama.model.graph.EdgeType edgeType) {
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

    public ontorama.model.graph.Node createNewRelatedNode() {
        dialog.show();

        ontorama.model.graph.Node newNode = new ontorama.model.graph.NodeImpl(textField.getText());
        List newEdges = new ArrayList();
        try {
            newEdges.add(new ontorama.model.graph.EdgeImpl(graphNode, newNode, edgeType));
            List clones = graphNode.getClones();
            for (Iterator iterator = clones.iterator(); iterator.hasNext();) {
                ontorama.model.graph.Node node = (ontorama.model.graph.Node) iterator.next();
                newEdges.add(new ontorama.model.graph.EdgeImpl(node, newNode.makeClone(), edgeType));
            }
        } catch (NoSuchRelationLinkException e) {
            /// @todo what to do here? Do we get this?
            e.printStackTrace();
            return null;
        }

        ontorama.model.graph.Graph graph = view.getGraph();
        try {
            for (Iterator iterator = newEdges.iterator(); iterator.hasNext();) {
                ontorama.model.graph.Edge edge = (ontorama.model.graph.Edge) iterator.next();
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
