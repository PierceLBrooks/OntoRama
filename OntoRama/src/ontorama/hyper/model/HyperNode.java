package ontorama.hyper.model;

/**
 * HyperNode represents a concept in the hyperbolic model
 * HyperNode wraps around a GrapNode. Communication between different
 * HyperNode wraps around a GrapNode. Communication between different
 * views is via GrapNode hasFocus() method.
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.Node;

public class HyperNode implements PositionChangedObservable {

    /**
     * Store all the Hyper observers
     */
    private List positionChangedObserver = new LinkedList();

    /**
     * Store focus observer.
     */
    private List focusListener = new LinkedList();

    /**
     * Store the position of the node.
     */
    private Position3D position;

    /**
     * Store node radius.
     */
    private double nodeRadius = 15;


    /**
     * Store the graph node for this hyper node.
     */
    private ontorama.model.graph.Node graphNode;

    /**
     * Constructor
     */
    public HyperNode(ontorama.model.graph.Node graphNode) {
        this.graphNode = graphNode;
        /*
        this.graphNode.addObserver( this );
        */
        this.position = new Position3D();
    }

    /**
     * Add focus observers to list.
     */
    public void addFocusChangedObserver(Object obj) {
        focusListener.add(obj);
    }

    /**
     * Add a Hyper observer.
     */
    public void addPositionChangedObserver(PositionChangedObserver observer) {
        positionChangedObserver.add(observer);
    }

    /**
     * Tell all Hyper observers of change.
     */
    public void notifyPositionMoved() {
        Iterator it = positionChangedObserver.iterator();
        while (it.hasNext()) {
            PositionChangedObserver hno = (PositionChangedObserver) it.next();
            hno.positionChanged();
        }
    }
    /**
     * Update method called from obserable (NodeImpl)
     */
    /*
   public void update( Object observer, Object observable ) {
       Iterator it = focusListener.iterator();
       while( it.hasNext() ) {
           FocusChangedObserver fo = (FocusChangedObserver)it.next();
           fo.focusChanged( this );
       }
   }
   */

    /**
     * Returns NodeImpl.
     */
    public ontorama.model.graph.Node getGraphNode() {
        return graphNode;
    }

    /**
     * Return true if NodeImpl has clones.
     */
    public boolean hasClones() {
        return this.graphNode.hasClones();
    }

    /**
     * Get the concept name.
     */
    public String getName() {
        return this.graphNode.getName();
    }

    /**
     * Return the current x position.
     */
    public double getX() {
        return position.getX();
    }

    /**
     * Return the current y position.
     */
    public double getY() {
        return position.getY();
    }

    /**
     * Get the nodes position.
     */
    public Position3D getPosition() {
        return position;
    }

    /**
     * Get the nodes radius.
     */
    public double getNodeRadius() {
        return nodeRadius;
    }

    /**
     * Notify NodeImpl that it has focus.
     */
    /*
   public void hasFocus() {
       System.out.println("HyperNode hasFocus()");
       graphNode.hasFocus();
   }
   */

    /**
     * Move node position by offset x and y.
     */
    public void move(double x, double y) {
        setLocation(position.getX() - x, position.getY() - y);
    }

    /**
     * Rotate node about the center (0, 0) by angle passed.
     */
    public void rotate(double angle) {
        double nodeAngle = Math.atan2(this.getX(), this.getY()) + angle * -1;
        double r = this.getPosition().distance(0, 0);
        double x = r * Math.sin(nodeAngle);
        double y = r * Math.cos(nodeAngle);
        setLocation(x, y);
    }

    /**
     * Returns the distance to the other node.
     */
    public double distance(HyperNode other) {
        return this.position.distance(other.position);
    }

    /**
     * Set a new location for the Hypernode.
     *
     * Notify view of change of position.
     */
    public void setLocation(double x, double y) {
        position.setLocation(x, y);
        notifyPositionMoved();
    }

    public String toString() {
        return this.graphNode.getName();
    }
}
