
package ontorama.hyper.model;

import ontorama.model.NodeObserver;
import ontorama.model.GraphNode;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;


import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class HyperNode {
    /**
     * Store the position of the node.
     */
    private Position3D position;

    /**
     * Store the name/label of node.
     */
    private int nodeRadius = 25;

    private GraphNode node;

    /**
     * Constructor
     */
    public HyperNode( GraphNode node ) {
      this.node = node;
      this.position = new Position3D();
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
     * Recursive call to move all nodes..
     */
    public void move( double x, double y ) {
        setLocation(position.getX() - x, position.getY() - y );
        //Iterator it = children.iterator();
        Iterator it = node.getChildrenIterator();
        while( it.hasNext() ) {
            HyperNode cur = (HyperNode)it.next();
            cur.move( x, y );
        }
    }

    /**
     * Returns the distance to the other node.
     */
    public double distance(HyperNode other) {
        return this.position.distance(other.position);
    }

    public void setLocation(double x, double y ) {
        position.setLocation( x, y );
        //node.notifyChange();
    }

    public void draw( Graphics2D g2d) {
      //Iterator it = children.iterator();
      Iterator it = node.getChildrenIterator();
      while( it.hasNext() ) {
        HyperNode child = (HyperNode)it.next();
        child.draw(g2d);
      }
      g2d.setColor(Color.blue);
      g2d.fill( new Ellipse2D.Double(getX(), getY(), (double)(nodeRadius/2), (double)(nodeRadius/2)));

    }

}
