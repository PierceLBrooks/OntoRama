package ontorama.backends;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchPropertyException;

import java.util.*;

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
     * Store alternative name of GraphNode
     * (used in RDF inputs)
     */
    private String fullName;

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
     * Table of namespace for every property
     * Only ONE namespace per property
     */
    private Hashtable nodeDetailsNamespace = new Hashtable();


	/**
	 * THe depth of this node (only used in ontorama
	 */
	private int depth;

    /**
     * Create a new GraphNode with given name
     *
     * @param name
     */
    public GraphNode(String name) {
        this.name = new String(name);
        this.fullName = new String(name);
		this.depth = -1;
    }

    /**
     * Create a new GraphNode with given name and an alternative name
     *
     * @param name, fullName
     */
    public GraphNode(String name, String fullName) {
        this.name = name;
        this.fullName = fullName;
        initNodeProperties();
		this.depth = -1;
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
     */
    public String getFullName() {
        return this.fullName;
    }

    /**
     *
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Return true if GraphNode has clones.
     */
    public boolean hasClones() {
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
        return this.clones.iterator();
    }

    /**
     * Adds a new clone o this GraphNode.
     *
     * @param GraphNode clone
     */
    public void addClone(GraphNode clone) {
        this.clones.add(clone);
    }


    /**
     * Adds a clones  this GraphNode.
     *
     * @param GraphNode clone
     */
    public void addAllClone(List listOfClones) {
        this.clones.addAll(listOfClones);
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
     * Make a clone for this GraphNode (make a new GraphNode with the same
     * name and add new node (clone) to appropriate lists of clones)
     *
     * @return cloneNode
     */
    public GraphNode makeClone() throws NoSuchPropertyException {
        // clone curNode to cloneNode
        GraphNode cloneNode = new GraphNode(name);
        
        // make sure all node's properties are copied to clone.
        Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
        while (e.hasMoreElements()) {
            String propName = (String) e.nextElement();
			if (this.nodeDetails.containsKey(propName)) {
	            List nodePropValue = this.getProperty(propName);
    	        String namespace = this.getPropertyNamespace(propName);
				if (namespace == null) {
					namespace = "";	
				}
        	    cloneNode.setProperty(propName,namespace, nodePropValue);
			}
        }

        // iterate through existing clones and add new clone to all of them
        Iterator it = this.clones.iterator();
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            //if (!clones.contains(cloneNode)) {
            cur.addClone(cloneNode);
            //}
        }

        // add all clones of this GraphNode to the new node (clone node)
        cloneNode.addAllClone(this.clones);
        // add the clone to the list of clones of this GraphNode
        this.addClone(cloneNode);
        // add this GraphNode to the list of clones for clonedNode
        cloneNode.clones.add(this);


        return cloneNode;
    }

    /**
     * Set given property for this node.
     *
     * @param   propertyName - name of property to set
     * @param   propertyValue - value for this property
     * @trows   NoSuchPropertyException
     */
    public void setProperty(String propertyName, String namespace, List propertyValue)
            throws NoSuchPropertyException {
        nodeDetails.put(propertyName, propertyValue);
        nodeDetailsNamespace.put(propertyName,namespace);
    }

    /**
     * Get property value for given property name
     *
     * @param   propertyName
     * @throw   NoSuchPropertyException
     */
    public List getProperty(String propertyName) throws NoSuchPropertyException {
		if (nodeDetails.containsKey(propertyName)) {
	        return (List) nodeDetails.get(propertyName);
		} else {
			throw new NoSuchPropertyException(propertyName, this.nodeDetails.keys());	
		}
    }


    /**
     * Get namespace for given property name
     *
     * @param   propertyName
     * @throw   NoSuchPropertyException
     */
    public String getPropertyNamespace(String propertyName) throws NoSuchPropertyException {
        return (String) nodeDetailsNamespace.get(propertyName);
    }


    /**
     * toString method
     */
    public String toString() {
        return name + ";" + fullName + ";" + nodeDetails.get("Creator");

    }


	public void update(GraphNode newNode) {
		this.name = newNode.getName();
		this.fullName = newNode.getFullName();
        Enumeration enum = OntoramaConfig.getConceptPropertiesTable().keys();
        while (enum.hasMoreElements()) {
            String propName = (String) enum.nextElement();

			if (newNode.nodeDetails.containsKey(propName) && 
				newNode.nodeDetailsNamespace.containsKey(propName)) {

					try {
						this.setProperty(propName, 
								newNode.getPropertyNamespace(propName),
								newNode.getProperty(propName));
					} catch (NoSuchPropertyException e) {
						//TODO something
					}
			}
        }
	}
	
    public GraphNode makeCopy() throws NoSuchPropertyException{
        // clone curNode to cloneNode
        GraphNode copyNode = new GraphNode(this.getName(),this.getFullName());
										
		copyNode.setFoldState(this.isFolded);

        // make sure all node's properties are copied to clone.
        Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
        while (e.hasMoreElements()) {
            String propName = (String) e.nextElement();
			if (this.nodeDetails.containsKey(propName) && 
				this.nodeDetailsNamespace.containsKey(propName)) {
				copyNode.setProperty(propName, 
									this.getPropertyNamespace(propName),
									this.getProperty(propName));
			}
        }

        return copyNode;
    }

	public void setClones(List clones) {
		this.clones = clones;	
	}
	
	public void setNodeDetails(Hashtable details) {
		this.nodeDetails = details;	
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
    
     /**
     * Sets the depth of the node in the tree.
     *
     * @param depth
     * @param graph
     */
    protected void setDepth(int depth, Graph graph) {
        this.depth = depth;
        Iterator it = graph.getEdge().getOutboundEdgeNodes(this);
        while (it.hasNext()) {
            GraphNode outboundNode = (GraphNode) it.next();
            outboundNode.setDepth(depth + 1,graph);
        }
    }

	public int getDepth() {
		return this.depth;	
	}

}
