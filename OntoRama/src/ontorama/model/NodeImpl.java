package ontorama.model;


import java.util.*;
import java.net.URI;

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
     * store creatorURI
     */
    private URI creatorUri;

    /**
     * store type of this node
     */
    private NodeType nodeType;

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * set node name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * get node unique identifier
     * @return
     */
    public String getIdentifier() {
        return fullName;
    }

    /**
     * set node unique identifier
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.fullName = identifier;
    }

    /**
     * Return true if NodeImpl has clones.
     */
    public boolean hasClones() {
        return !this.clones.isEmpty();
    }

    /**
     * Return a list of all clones.
     * @return clones list
     */
    public List getClones() {
        return clones;
    }

    /**
     * Adds a new clone o this Node.
     * @param  clone  Node that is clone for this Node
     */
    public void addClone(Node clone) {
        if (! clones.contains(clone)) {
            clones.add(clone);
        }
    }

    /**
     * add list of new clones to this node's clones list
     * @param clones
     */
    public void addClones(List clones) {
        Iterator it = clones.iterator();
        while (it.hasNext()) {
            Node cloneNode = (Node) it.next();
            addClone(cloneNode);
        }
    }

    /**
     * Returns the distance to the root node.
     * @return node depth
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * set distance to the root node
     * @param depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * set node's folded state
     * @param isFolded
     * @todo perhaps this shouldn't be in the model
     */
    public void setFoldState(boolean isFolded) {
        this.isFolded = isFolded;
    }

    /**
     * returns true if this node is folded.
     * @return isFolded
     * @todo perhaps this shouldn't be in the model
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
        cloneNode.setNodeType(this.getNodeType());

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

    /**
     * get creatorUri for this node. creatorUri is URI for a creator of this node.
     * @return creatorUri
     */
    public URI getCreatorUri() {
        return this.creatorUri;
    }

    /**
     * set creatorUri for this node.
     * @param creatorUri
     */
    public void setCreatorUri(URI creatorUri) {
        this.creatorUri = creatorUri;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public NodeType getNodeType() {
        return this.nodeType;
    }

    /**
     * toString method
     */
    public String toString() {
        String str = "Node: " + name;
        return str;

    }
}
