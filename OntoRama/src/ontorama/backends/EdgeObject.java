package ontorama.backends;

import ontorama.webkbtools.util.NoSuchRelationLinkException;



/**
 * Description: Edge between nodes. Edges correspong to relation links between concept types.
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @version 1.0
 */

public class EdgeObject {

    /**
     * inboundNode
     */
    GraphNode fromNode;

    /**
     * outboundNodes
     */
    GraphNode toNode;

    /**
     * type
     */
    int type;
    
    /**
     * namespace for type
     */
    String namespace;

    /**
     *
     */
    public EdgeObject(GraphNode fromNode, GraphNode toNode, int type,String namespace) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.type = type;
		this.namespace = namespace;
    }

    /**
     *
     */
    public GraphNode getFromNode() {
        return this.fromNode;
    }

    /**
     *
     */
    public GraphNode getToNode() {
        return this.toNode;
    }

    /**
     *
     */
    public int getType() {
        return this.type;
    }

	public String getNamespace() {
		return this.namespace;	
	}
	
	
	public GraphNode getNode(boolean isFromNode) {
		if (isFromNode) {
			return this.fromNode;	
		} else {
			return this.toNode;
		}	
	}			
	
	/**
     *
     */
    public String toString() {
        String str = "Edge from '" + this.fromNode.getName() + "' to '" + this.toNode.getName() + "', type = " + type;
        //String str = "Edge from '" + this.fromNode.getName() + "' = " + this.fromNode +  " to '" + this.toNode.getName() + "' = " + this.toNode + ", type = " + type;
        return str;
    }
    
    public EdgeObject makeCopy() {
    	EdgeObject copyObj = null;
		try {
			copyObj = new EdgeObject(
									this.fromNode.makeCopy(),
									this.toNode.makeCopy(),
									this.type,
									new String(this.namespace));
		} catch (NoSuchRelationLinkException e) {
			//TODO some errorhandling
		}

    	return copyObj;	
    }
}
