/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com).
 *
 * $id$
 */
package ontorama.views.hyper.view;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * @todo move color calculation (or at least depths) in here
 * @todo check that the interface is used most (all?) of the time
 */
public class SphericalProjection implements Projection {
	/**
	 * The radius of the sphere we project upon.
	 */
	private double sphereRadius = 300;

	/**
	 * Used if setProjection(double) is called to determine the focal depth.
	 */
	private double relativeFocus = .8;

	/**
	 * The focal depth.
	 */
	private double focalDepth = sphereRadius * relativeFocus;
	
	public SphericalProjection(double sphereRadius, double relativeFocus) {
		this.sphereRadius = sphereRadius;
		this.relativeFocus = relativeFocus;
		this.focalDepth = this.sphereRadius * this.relativeFocus;
	}
	
	public double getSphereRadius() {
		return this.sphereRadius;
	}

	public double getFocalDepth() {
		return this.focalDepth;
	}

	public Shape project(Shape inShape, double xpos, double ypos) {
		GeneralPath outShape = new GeneralPath();
		PathIterator path = inShape.getPathIterator(null);
		outShape.setWindingRule(path.getWindingRule());
		float[] points = new float[6];
		while(!path.isDone()) {
			int segType = path.currentSegment(points);
			points = transform(points, xpos, ypos);
			switch(segType) {
				case PathIterator.SEG_LINETO:
					outShape.lineTo(points[0], points[1]);
					break;
				case PathIterator.SEG_MOVETO:
					outShape.moveTo(points[0], points[1]);
					break;
				case PathIterator.SEG_QUADTO:
					outShape.quadTo(points[0], points[1], points[2], points[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					outShape.curveTo(points[0], points[1], points[2], points[3], points[4], points[5]);
					break;
				case PathIterator.SEG_CLOSE:
					outShape.closePath();
					break;
			}
			path.next();
		}
		return outShape;
	}

	private float[] transform(float[] in, double xpos, double ypos) {
		float[] retval = new float[in.length];
		for (int i = 0; i < in.length; i+=2) {
			double x = xpos + in[i];
			double y = ypos + in[i+1];
			Point2D newCoord = project(x,y);
			retval[i] = (float) newCoord.getX();
			retval[i+1] = (float) newCoord.getY();
		}
		return retval;
	}

	public Point2D project(double x, double y) {
		double length = Math.sqrt(x * x + y * y + focalDepth * focalDepth);
		double scale = sphereRadius / length;
		return new Point2D.Double(scale * x, scale * y);
	}

	public double getRelativeFocus() {
		return this.relativeFocus;
	}

	public void setRelativeFocus(double d) {
		this.relativeFocus = d;
		this.focalDepth = this.sphereRadius * this.relativeFocus;
	}
}
