/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com).
 *
 * $id$
 */
package ontorama.views.hyper.view;

import java.awt.Shape;
import java.awt.geom.Point2D;

public interface Projection {
	Shape project(Shape shape, double xpos, double ypos);
	Point2D project(double xpos, double ypos);
}
