
package ontorama.hyper.model;

/**
 * Position3D holds the 3 dimension nodes position.
 */

public class Position3D {

	/**
	 * Holds the nodes x coordinate.
	 */
	private double x;

	/**
	 * Holds the nodes y coordinate.
	 */
	private double y;

	/**
	 * Holds the nodes z coordinate.
	 */
	private double z;

	/**
	 * Default constructor sets positions to zero..
	 */
	public Position3D() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Constructor to set x and y coordinate.
	 */
	public Position3D(int x, int y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Gets the current x position.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the current y position.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gets the current z position.
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Get the distance from this node.
	 */
	public double distance(Position3D to) {
		return distance( to.getX(), to.getY() );
	}

	/**
	 * Get the distance from a coordinate to this node.
	 */
	public double distance(double x, double y) {
		return Math.sqrt( sqr( getX() - x ) + sqr( getY() - y ) );
	}

	/**
	 * Calc square.
	 */
	private double sqr(double a) {
		return a * a;
	}

	/**
	 * Set the current position.
	 */
	public void setLocation(double x, double y ) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the current z coordinate.
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Returns the radius for polar coordinates.
	 */
	public double getRadius() {
		return distance( 0d, 0d );
	}

	/**
	 * Returns the angle for polar coordinates.
	 */
	public double getTheta() {
		return Math.atan2(this.x, this.y);
	}

	/**
	 * Sets the polar coordinates.
	 */
	public void setPolarCoordinates(double radius, double theta) {
		this.x = radius * Math.sin( theta );
		this.y = radius * Math.cos (theta );
	}
}