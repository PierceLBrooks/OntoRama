/* * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). * Please read licence.txt in the toplevel source directory for licensing information. * * $Id: NodeAddedEvent.java,v 1.3 2002/12/02 05:04:01 nataliya Exp $ */package ontorama.model.graph.events;import ontorama.model.graph.Graph;import ontorama.model.graph.Node;import ontorama.model.graph.events.*;public class GraphNodeAddedEvent extends GraphExtendedEvent {    private Node node;    public GraphNodeAddedEvent(Graph subject, Node node) {        super(subject);        System.out.println("GraphNodeAddedEvent for node " + node + " and graph " + subject);        this.node = node;    }    public Node getNode() {        return node;    }}