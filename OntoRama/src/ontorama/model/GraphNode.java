package ontorama.model;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Hashtable;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchPropertyException;

/**
 * Basic GraphNode for ontology viewers.
 *
 * <p>
 * Copyright:    Copyright (c) 2002
 * <br>
 * Company:     DSTC
 */
public class GraphNode implements Cloneable {

    /**
     * Store the name/label of GraphNode.
     */
    private String name;

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
     * Create a new GraphNode with given name
     *
     * @param name
     */
    public GraphNode( String name ) {
        this.name = name;
    }

    /**
     * Return GraphNodes name/title.
     */
    public String getName() {
        return name;
    }

    /**
     * Return true if GraphNode has clones.
     */
    public boolean hasClones(){
      return !this.clones.isEmpty();
    }

    /**
     *
     */
	 /*
    public void hasFocus () {
//        System.out.println("GraphNode method hasFocus() for graphNode " + this.getName());
        System.out.println("\t\tclones: " + this.clones);

    }
	*/

    /**
     * Return an iterator of all clones.
     */
    public Iterator getClones() {
        return clones.iterator();
    }

    /**
     * Adds a new clone o this GraphNode.
     *
     * @param GraphNode clone
     */
    private void addClone(GraphNode clone) {
        clones.add(clone);
    }


    /**
     * Calculate the depths of all children in respect to this node.
     */
    public void calculateDepths() {
        this.setDepth(0);
    }

    /**
     * Sets the depth of the node in the tree.
     *
     * @param depth
     */
    protected void setDepth(int depth) {
        this.depth = depth;
        Iterator it = Edge.getOutboundEdgeNodes(this);
        while (it.hasNext()) {
            GraphNode outboundNode = (GraphNode) it.next();
            outboundNode.setDepth(depth + 1);
        }
    }

    /**
     * Returns the distance to the root node.
     *
     * @return node depth
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     *
     */
    public void setFoldState (boolean isFolded) {
        this.isFolded = isFolded;
    }

    /**
     *
     */
    public boolean getFoldedState () {
        return this.isFolded;
    }

    /**
     * Make a clone for this GraphNode (make a new GraphNode with the same
     * name and add new node (clone) to appropriate lists of clones)
     *
     * @return cloneNode
     */
    public GraphNode makeClone ()   {
        // clone curNode to cloneNode
        GraphNode cloneNode = new GraphNode(name);

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
            GraphNode cur = (GraphNode) it.next();
            //if (!clones.contains(cloneNode)) {
                cur.clones.add(cloneNode);
            //}
        }

        // add all clones of this GraphNode to the new node (clone node)
        cloneNode.clones.addAll(this.clones);
        // add the clone to the list of clones of this GraphNode
        this.clones.add(cloneNode);
        // add this GraphNode to the list of clones for clonedNode
        cloneNode.clones.add(this);


        return cloneNode;
    }

    /**
     * Set given property for this node.
     *
     * @param   propertyName - name of property to set
     * @param   propertyValue - value for this property
     * @todo    should we throw NoSuchPropertyException?
     */
    public void setProperty (String propertyName, List propertyValue) {
        nodeDetails.put(propertyName, propertyValue);
    }

    /**
     * Get property value for given property name
     *
     * @param   propertyName
     * @todo    should we throw NoSuchPropertyException?
     * @todo    not sure if it's a good idea to return new LinkedList
     *          if our property has not been set. If we don't return
     *          new list - then we will get null and null pointer exceptions
     *          everywhere and will need to check it every time we use this
     *          method => not easily maintainable code.
     *          If we assume that all graph nodes are created from ontology
     *          type, then this situation shouldn't occur. But is it a safe
     *          assumption????...  probably not, one case i can think of - test
     *          cases for graph node where graph nodes are not created
     *          from an ont.type
     *          Or should graph node be initialised with initialised set of
     *          concept properties (as it is in ontology type)? In this case
     *          it looks as graph node and ontology type will have redunant
     *          code...
     */
    public List getProperty (String propertyName) {
        if (nodeDetails.containsKey(propertyName)) {
            return (List) nodeDetails.get(propertyName);
        }
        return new LinkedList();
        //return null;
    }

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
        str = str + "GraphNode: " + this.getName() + "\n";
        str = str + "\t" + "inbound nodes: " + "\n";
        Iterator inboundNodes = Edge.getInboundEdgeNodes(this);
        while (inboundNodes.hasNext()) {
            GraphNode node = (GraphNode) inboundNodes.next();
            str = str + "\t\t" + node.getName();
        }
        str = str + "\t" + "outbound nodes: " + "\n";
        Iterator outboundNodes = Edge.getOutboundEdgeNodes(this);
        while (outboundNodes.hasNext()) {
            GraphNode node = (GraphNode) outboundNodes.next();
            str = str + "\t\t" + node.getName();
        }
        return str;
     }
     */

}
