
package ontorama.hyper.model;

import ontorama.hyper.canvas.FocusChangedObservable;
import ontorama.hyper.canvas.FocusChangedObserver;
import ontorama.model.GraphNode;
import ontorama.model.NodeObserver;
import ontorama.hyper.model.PositionChaingedObservable;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class HyperNode implements NodeObserver, PositionChaingedObservable,  FocusChangedObservable {

    /**
     * Store all the Hyper observers
     */
    private List positionChaingedObserver = new LinkedList();

    /**
     * Store focus observer.
     */
    private List focusListener = new LinkedList();

    /**
     * Store the position of the node.
     */
    private Position3D position;

    /**
     * Store the name/label of node.
     */
    private int nodeRadius = 25;


    /**
     * Store the graph node for this hyper node.
     */
    private GraphNode graphNode;

    /**
     * Constructor
     */
    public HyperNode( GraphNode graphNode ) {
      this.graphNode = graphNode;
      this.graphNode.addObserver( this );
      this.position = new Position3D();
    }

    /**
     * Add focus observers to list.
     */
    public void addFocusChangedObserver( Object obj ) {
        focusListener.add( obj );
    }

    /**
     * Add a Hyper observer.
     */
    public void addPositionChaingedObserver( Object obj ) {
        positionChaingedObserver.add(obj);
    }

    /**
     * Tell all Hyper observers of change.
     */
    public void notifyPositionMoved( double x, double y ) {
        Iterator it = positionChaingedObserver.iterator();
        while( it.hasNext() ) {
            PositionChaingedObserver hno = (PositionChaingedObserver)it.next();
            hno.positionUpdate( x, y );
        }
    }
    /**
     * Update method called from obserable (GraphNode)
     */
    public void update( Object obj ) {
        Iterator it = focusListener.iterator();
        while( it.hasNext() ) {
            FocusChangedObserver fo = (FocusChangedObserver)it.next();
            fo.focusChanged( this );
        }
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

    public void setLocation( double x, double y ) {
        position.setLocation( x, y );
        notifyPositionMoved( x, y);
    }


    public String toString() {
        return this.graphNode.getName();
    }

}
