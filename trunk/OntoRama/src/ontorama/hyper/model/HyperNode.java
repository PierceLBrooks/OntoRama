
package ontorama.hyper.model;

import ontorama.model.NodeObserver;
import ontorama.hyper.model.HyperNodeObservable;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class HyperNode implements NodeObserver, HyperNodeObservable {

    /**
     * Store all the Hyper observers
     */
    private List HyperObservers = new LinkedList();

    /**
     * Store the position of the node.
     */
    private Position3D position;

    /**
     * Store the name/label of node.
     */
    private int nodeRadius = 25;

    //private GraphNode node;

    private String name;

    /**
     * Constructor
     */
    public HyperNode( String name ) {
      //this.node = node;
      this.name = name;
      this.position = new Position3D();
    }

    /**
     * Add a Hyper observer.
     */
    public void addHyperObserver( Object obj ) {
        HyperObservers.add(obj);
    }

    /**
     * Tell all Hyper observers of change.
     */
    public void notifyChange() {
        Iterator it = HyperObservers.iterator();
        while( it.hasNext() ) {
            HyperNodeObserver hno = (HyperNodeObserver)it.next();
            hno.update( hno );
        }
    }
    /**
     * Update method called from obserable (GraphNode)
     */
    public void update( Object obj ) {

    }

    /**
     * Get the current NodeObserver.
     */
    public NodeObserver getNodeObserver() {
        return this;
    }

    /**
     * Get the concept name.
     */
    public String getName() {
        return name;
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
    public int getNoderadius() {
        return nodeRadius;
    }

    /**
     * Move node position by offset.
     */
    public void move( double x, double y ) {
        setLocation(position.getX() - x, position.getY() - y );
    }

    /**
     * Returns the distance to the other node.
     */
    public double distance(HyperNode other) {
        return this.position.distance(other.position);
    }

    public void setLocation(double x, double y ) {
        position.setLocation( x, y );
        notifyChange();
    }


    public String toString() {
        return name;
    }

}
