

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
 */
public class GraphNode implements Cloneable, NodeObservable {

    /**
     * Store the name/label of GraphNode.
     */
    private String name;

    /**
     * Hold the list of observers.
     */
    private List observers = new LinkedList();

    /**
     * Hold the list of children.
     */
    private List children = new LinkedList();

    /**
     * Holds a list of parents.
     */
    private List parents = new LinkedList();

    /**
     * Stores the depth of the node in the graph (distance to the root element).
     */
    private int depth = 0;


    /**
     * Holds the list of all GraphNodes that refer to the same ontology term..
     */
    private List clones = new LinkedList();

    /**
     * Table of all available properties for this node.
     * List of property names can be found in OntoramaConfig
     */
    private Hashtable nodeDetails = new Hashtable();


//    /**
//     * Description string
//     */
//    private String description = null;
//
//    /**
//     * Creator string
//     */
//    private String creator = null;

    /**
     *
     */
    public GraphNode( String name ) {
        this.name = name;
    }

    /**
     * Add observers to the list of observers.
     */
    public void addObserver( Object observer ) {
        this.observers.add( observer );
    }

    /**
     * Update all view of change.
     */
    public void notifyChange() {
        Iterator it = observers.iterator();
        while(it.hasNext()) {
            NodeObserver cur = (NodeObserver) it.next();
            cur.update( cur );
        }
    }

    /**
     * Return GraphNodes name/title.
     */
    public String getName() {
        return name;
    }

    /**
     * Return Iterator of children.
     */
    public Iterator getChildrenIterator() {
        return children.iterator();
        //return parents.iterator();
    }

    /**
     * Return an iterator of child GraphNodes.
     */
    public List getChildrenList() {
        return children;
    }

    /**
     * Add parent to GraphNode.
     */
    public void addParent( GraphNode parent ) {
        this.parents.add(parent);
    }

    /**
     *
     */
    public void hasFocus () {
        System.out.println("GraphNode method hasFocus() for graphNode " + this.getName());
        Enumeration e = nodeDetails.keys();
        while (e.hasMoreElements()) {
            String curPropName = (String) e.nextElement();
            String curPropValue = (String) nodeDetails.get(curPropName);
            System.out.println("\t\t" + curPropName + " = " + curPropValue);
        }

        //System.out.println("\t\tdescription = " + this.getDescription());
        //System.out.println("\t\tcreator = " + this.getCreator());
        System.out.println("\t\tclones: " + this.clones);

        notifyChange();
    }

    /**
     * Add child to GraphNode.
     */
    public void addChild( GraphNode child ) {
        this.children.add(child);
    }

    /**
     * Test method: print out all GraphNodes.
     */
    public void printNodeNames() {
        ListIterator listIterator = children.listIterator( 0 );
        while( listIterator.hasNext() ) {
            GraphNode node = (GraphNode)listIterator.next();
            node.printNodeNames();
        }
    }

    /**
     * Returns the number of parents.
     */
    public int getNumberOfParents() {
        return parents.size();
    }

    /**
     * Returns the number of children.
     */
    public int getNumberOfChildren() {
        return children.size();
    }

    /**
     * Return an iterator of parent GraphNodes.
     */
    public Iterator getParents() {
        return parents.iterator();
    }

    /**
     * Checks to see if a child exists befor adding it to the list.
     */
    public boolean hasChild( GraphNode child ) {
      return this.children.contains( child );
    }

    /**
     * Checks to see if a parent exists befor adding it to the list.
     */
    public boolean hasParent( GraphNode parent ) {
      return this.parents.contains( parent );
    }

    /**
     * Sets this GraphNode to have only the given parent.
     */
    public void setParent(GraphNode parent) {
        parents.clear();
        parents.add(parent);
    }

    /**
     * Removes a parent from this GraphNode.
     */
    public void removeParent(GraphNode parent) {
        parents.remove(parent);
    }

    /**
     * Removes a child from this GraphNode.
     */
    public void removeChild(GraphNode child) {
        children.remove(child);
    }

    /**
     * Return an iterator of all clones.
     */
    public Iterator getClones() {
        return clones.iterator();
    }

    /**
     * Adds a new clone o this GraphNode.
     */
    public void addClone(GraphNode clone) {
        clones.add(clone);
    }


    /**
     * Calculated the depths of all children in respect to this node.
     */
    public void calculateDepths() {
        this.setDepth(0);
    }

    /**
     * Sets the depth of the node in the tree.
     */
    protected void setDepth(int depth) {
        this.depth = depth;
        Iterator it = Edge.getOutboundEdgeNodes(this);
        while (it.hasNext()) {
            GraphNode outboundNode = (GraphNode) it.next();
            outboundNode.setDepth(depth + 1);
        }
        /*
        Iterator it = children.iterator();
        while(it.hasNext()) {
            GraphNode child = (GraphNode) it.next();
            child.setDepth(depth+1);
        }
        */
        //this.nodeColor = new Color(0,0,depth*80);
    }

    /**
     * Returns the distance to the root node.
     */
    public int getDepth() {
        return this.depth;
    }


    /**
     * Returns the distance to the other GraphNode.
     */
     /*
    public double distance(GraphNode other) {
        return this.position.distance(other.position);
    }
    */

    /**
     * Returns a deep copy of the GraphNode and its children.
     *
     * The parents of both GraphNodes will be exactly the same, they
     * will have the same clones plus each other.
     */
    public GraphNode deepCopy() {
        GraphNode retVal = new GraphNode(name);
        retVal.parents.addAll(this.parents);
        retVal.clones.addAll(this.clones);
        this.clones.add(retVal);
        retVal.clones.add(this);
        Iterator it = children.iterator();
        while(it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            GraphNode newGraphNode = cur.deepCopy();
            newGraphNode.removeParent(this);
            newGraphNode.addParent(retVal);
            retVal.children.add(newGraphNode);
        }
        return retVal;
    }

    /**
     * Make a clone for this GraphNode (make a new GraphNode with the same
     * name and add new node (clone) to appropriate lists of clones)
     * @return cloneNode
     */
    public GraphNode makeClone ()   {
        // clone curNode to cloneNode
        GraphNode cloneNode = new GraphNode(name);
        // add all clones of this GraphNode to the new node (clone node)
        cloneNode.clones.addAll(this.clones);
        // add the clone to the list of clones of this GraphNode
        this.clones.add(cloneNode);
        // add this GraphNode to the list of clones for clonedNode
        cloneNode.clones.add(this);

        return cloneNode;
    }

    /**
     *
     */
    public void setProperty (String propertyName, String propertyValue) {
        nodeDetails.put(propertyName, propertyValue);
    }

    /**
     *
     */
    public String getProperty (String propertyName) {
        if (nodeDetails.contains(propertyName)) {
            return (String) nodeDetails.get(propertyName);
        }
        return null;
    }

//    /**
//     * Set description for this node
//     * @param   descriptionStr
//     */
//    public void setDescription (String descriptionStr) {
//        this.description = descriptionStr;
//    }
//
//    /**
//     * Get description string
//     * @return description string
//     */
//    public String getDescription () {
//        return this.description;
//    }
//
//    /**
//     * Set creator
//     * @param creatorStr
//     */
//     public void setCreator (String creatorStr) {
//        this.creator = creatorStr;
//     }
//
//     /**
//      * Get creator string
//      * @return creator
//      */
//      public String getCreator () {
//        return this.creator;
//      }
//

    /**
     * toString method
     */

    public String toString() {
        String p = "", c = "";
        Iterator it = parents.iterator();
        while( it.hasNext() ) {
            GraphNode GraphNode = (GraphNode)it.next();
            p = p + GraphNode.getName() + " ";
        }
        it = children.iterator();
        while( it.hasNext() ) {
            GraphNode GraphNode = (GraphNode)it.next();
            c = c + GraphNode.getName() + " ";
        }
//        return "GraphNode name: " + name +
//                " Parents: " + p +
//                " Children: " + c;
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