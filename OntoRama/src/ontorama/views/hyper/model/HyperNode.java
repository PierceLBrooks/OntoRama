package ontorama.views.hyper.model;

/**
 * HyperNode represents a concept in the hyperbolic model
 * HyperNode wraps around a GrapNode. Communication between different
 * HyperNode wraps around a GrapNode. Communication between different
 * views is via GrapNode hasFocus() method.
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.tree.TreeNode;

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
    private TreeNode _treeNode;

    /**
     * Constructor
     */
    public HyperNode(TreeNode treeNode) {
        _treeNode = treeNode;
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
     * Returns NodeImpl.
     */
    public TreeNode getTreeNode() {
        return _treeNode;
    }

    /**
     * Return true if NodeImpl has clones.
     */
    public boolean hasClones() {
    	if (_treeNode.getClones().size() > 0) {
    		return true;
    	}
        return false;
    }

    /**
     * Get the concept name.
     */
    public String getName() {
        return _treeNode.getName();
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
     * Notify ui of change of position.
     */
    public void setLocation(double x, double y) {
        position.setLocation(x, y);
        notifyPositionMoved();
    }

    public String toString() {
        return getName();
    }
}
