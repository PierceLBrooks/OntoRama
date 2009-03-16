package ontorama.model.graph;


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
     * node's description
     */
    private String description;


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

//    /**
//     * toString method
//     */
//    public String toString() {
//        String str = "Node: " + name + "(" + fullName + ")";
//        return str;
//
//    }

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
