/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com).
 *
 * $id$
 */
package ontorama.views.hyper.view;

import java.awt.Shape;

public interface Projection {
	Shape project(Shape shape, double xpos, double ypos);
}
