package ontorama.model;


import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchPropertyException;

import java.util.*;

/**
 * Basic NodeImpl for ontology viewers.
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class NodeImpl implements Cloneable, Node {


    /**
     * Store the name/label of NodeImpl.
     */
    private String name;

    /**
     * Store alternative name of NodeImpl
     * (used in RDF inputs)
     */
    private String fullName;

    /**
     * Stores the depth of the node in the graph (distance to the root element).
     */
    private int depth = 0;

    /**
     * Stores if this view is folded.
     */
    private boolean isFolded = false;

    /**
     * Holds the list of all GraphNodes that refer to the same ontology term..
     */
    private List clones = new LinkedList();

    /**
     * Table of all available properties for this node.
     * List of property names can be found in OntoramaConfig
     */
    private Hashtable nodeDetails = new Hashtable();


    /**
     * Create a new NodeImpl with given name
     *
     * @param name
     */
    public NodeImpl(String name) {
        this(name, name);
    }

    /**
     * Create a new NodeImpl with given name and an alternative name
     *
     * @param name
     * @param fullName
     */
    public NodeImpl(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
        initNodeProperties();
    }

    /**
     * @todo    this method is the same as intiConceptTypeProperties in
     *          OntologyTypeImplementation... check if there is a way to remove
     *          this redunancy!
     */
    private void initNodeProperties() {
        Enumeration propertiesList = OntoramaConfig.getConceptPropertiesTable().keys();
        while (propertiesList.hasMoreElements()) {
            String propName = (String) propertiesList.nextElement();
            nodeDetails.put(propName, new LinkedList());
        }
    }


    /**
     * Return GraphNodes name/title.
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *
     */
    public String getFullName() {
        return fullName;
    }

    /**
     *
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Return true if NodeImpl has clones.
     */
    public boolean hasClones() {
        return !this.clones.isEmpty();
    }

    /**
     *
     */
    /*
   public void hasFocus () {
//        System.out.println("NodeImpl method hasFocus() for graphNode " + this.getName());
       System.out.println("\t\tclones: " + this.clones);

   }
   */

    /**
     * Return an iterator of all clones.
     */
    public List getClones() {
        return clones;
    }

    /**
     * Adds a new clone o this NodeImpl.
     *
     * @param  clone  NodeImpl that is clone for this NodeImpl
     */
    public void addClone(Node clone) {
        clones.add(clone);
    }

    public void addClones(List clones) {
        clones.addAll(clones);
    }


    /**
     * Returns the distance to the root node.
     *
     * @return node depth
     */
    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     *
     */
    public void setFoldState(boolean isFolded) {
        this.isFolded = isFolded;
    }

    /**
     *
     */
    public boolean getFoldedState() {
        return this.isFolded;
    }

    /**
     * Make a clone for this Node (make a new Node with the same
     * name and add new node (clone) to appropriate lists of clones)
     *
     * @return cloneNode
     */
    public Node makeClone() throws NoSuchPropertyException {
        // clone curNode to cloneNode
        Node cloneNode = new NodeImpl(name);

        // make sure all node's properties are copied to clone.
        Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
        while (e.hasMoreElements()) {
            String propName = (String) e.nextElement();
            List nodePropValue = this.getProperty(propName);
            cloneNode.setProperty(propName, nodePropValue);
        }

        // iterate through existing clones and add new clone to all of them
        Iterator it = clones.iterator();
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            //if (!clones.contains(cloneNode)) {
            cur.addClone(cloneNode);
            //}
        }

        // add all clones of this Node to the new node (clone node)
        cloneNode.addClones(this.clones);
        // add the clone to the list of clones of this Node
        this.clones.add(cloneNode);
        // add this Node to the list of clones for clonedNode
        cloneNode.addClone(this);

        return cloneNode;
    }

    /**
     * Set given property for this node.
     *
     * @param   propertyName - name of property to set
     * @param   propertyValue - value for this property
     * @throws   NoSuchPropertyException
     */
    public void setProperty(String propertyName, List propertyValue)
            throws NoSuchPropertyException {
        nodeDetails.put(propertyName, propertyValue);
    }

    /**
     * Get property value for given property name
     *
     * @param   propertyName
     * @throws   NoSuchPropertyException
     */
    public List getProperty(String propertyName) throws NoSuchPropertyException {
        return (List) nodeDetails.get(propertyName);
    }

//    /**
//     *
//     */
//    public int getBranchNodesNum() {
//        LinkedList q = new LinkedList();
//        int count = 0;
//        Iterator it = GraphImpl.getOutboundEdgeNodes(this);
//        while (it.hasNext()) {
//            Node child = (Node) it.next();
//            q.add(child);
//        }
//
//        while (q.size() != 0) {
//            Node cur = (Node) q.remove(0);
//
//            count++;
//            Iterator children = GraphImpl.getOutboundEdgeNodes(cur);
//            while (children.hasNext()) {
//                Node next = (Node) children.next();
//                q.add(next);
//            }
//        }
//        return count;
//    }


    /**
     * toString method
     */
    public String toString() {
        return name;

    }

    /**
     * toString method
     */
    /*
    public String toString() {
       String str = "";
       str = str + "Node: " + this.getName() + "\n";
       str = str + "\t" + "inbound nodes: " + "\n";
       Iterator inboundNodes = EdgeImpl.getInboundEdgeNodes(this);
       while (inboundNodes.hasNext()) {
           Node node = (Node) inboundNodes.next();
           str = str + "\t\t" + node.getName();
       }
       str = str + "\t" + "outbound nodes: " + "\n";
       Iterator outboundNodes = EdgeImpl.getOutboundEdgeNodes(this);
       while (outboundNodes.hasNext()) {
           Node node = (Node) outboundNodes.next();
           str = str + "\t\t" + node.getName();
       }
       return str;
    }
    */

}
