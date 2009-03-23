package ontorama.model.graph;


import java.net.URI;

/**
 * Basic NodeImpl for ontology viewers.
 *
 * Copyright:    Copyright (c) 2002
 * Company:     DSTC
 */
public class NodeImpl implements Cloneable, Node {

    /**
     * Store the name/label of NodeImpl.
     */
    private final String name;

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
     * get node unique identifier
     */
    public String getIdentifier() {
        return fullName;
    }

    /**
     * set node unique identifier
     */
    public void setIdentifier(String identifier) {
        this.fullName = identifier;
    }

    /**
     * get creatorUri for this node. creatorUri is URI for a creator of this node.
     */
    public URI getCreatorUri() {
        return this.creatorUri;
    }

    /**
     * set creatorUri for this node.
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
