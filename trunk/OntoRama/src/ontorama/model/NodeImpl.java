package ontorama.model;


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
        if (! clones.contains(clone)) {
            clones.add(clone);
        }
    }

    public void addClones(List clones) {
        Iterator it = clones.iterator();
        while (it.hasNext()) {
            Node cloneNode = (Node) it.next();
            addClone(cloneNode);
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
    public Node makeClone() {
        // clone curNode to cloneNode
        Node cloneNode = new NodeImpl(name);

        // iterate through existing clones and add new clone to all of them
        Iterator it = clones.iterator();
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            cur.addClone(cloneNode);
        }

        // add all clones of this Node to the new node (clone node)
        cloneNode.addClones(this.clones);
        // add the clone to the list of clones of this Node
        this.addClone(cloneNode);
        // add this Node to the list of clones for clonedNode
        cloneNode.addClone(this);

        return cloneNode;
    }



//    /**
//     * toString method
//     */
//    public String toString() {
//        String str = "Node: " + name;
//        return str;
//
//    }
}
