/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com),
 * $id$
 */
package ontorama.util;

import java.awt.geom.*;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.jdom.Element;

/**
 * This class imports simple SVG images into AWT Shape objects.
 * 
 * Supported are only rectangles, ellipses and polygon objects. No recursion is
 * done, do group and other nestings are ignored. No styles are supported, just
 * the pure shapes are imported.
 * 
 * @todo adding line and path would greatly enhance this
 */
public class SVG2Shape {
	public static GeneralPath importShape(Element svgElement) {
		GeneralPath shape = new GeneralPath();
		Iterator it = svgElement.getChildren().iterator();
		while(it.hasNext()) {
			Element cur = (Element) it.next();
			if(cur.getName().equals("rect")) {
			    double x = Double.parseDouble(cur.getAttributeValue("x"));
			    double y = Double.parseDouble(cur.getAttributeValue("y"));
			    double width = Double.parseDouble(cur.getAttributeValue("width"));
			    double height = Double.parseDouble(cur.getAttributeValue("height"));
				shape.append(new Rectangle2D.Double(x, y, width, height),false);
		    } else if(cur.getName().equals("circle")) {
			    double cx = Double.parseDouble(cur.getAttributeValue("cx"));
			    double cy = Double.parseDouble(cur.getAttributeValue("cy"));
			    double r = Double.parseDouble(cur.getAttributeValue("r"));
			    shape.append(new Ellipse2D.Double(cx - r, cy - r, r, r),false);
		    } else if(cur.getName().equals("ellipse")) {
			    double cx = Double.parseDouble(cur.getAttributeValue("cx"));
			    double cy = Double.parseDouble(cur.getAttributeValue("cy"));
			    double rx = Double.parseDouble(cur.getAttributeValue("rx"));
			    double ry = Double.parseDouble(cur.getAttributeValue("ry"));
			    shape.append(new Ellipse2D.Double(cx - rx, cy - ry, rx, ry),false);
		    } else if(cur.getName().equals("polygon")) {
		    	StringTokenizer tokenizer = new StringTokenizer(cur.getAttributeValue("points"), " ,");
		    	boolean first = true;
		    	while(tokenizer.hasMoreElements()) {
		    	    float x = Float.parseFloat(tokenizer.nextToken());
		    	    float y = Float.parseFloat(tokenizer.nextToken());
		    	    if(first) {
		    	    	shape.moveTo(x,y);
		    	    	first = false;
		    	    } else {
		    	    	shape.lineTo(x,y);
		    	    }
		    	}
		    } else if(cur.getName().equals("g")) {
		    	shape.append(importShape(cur),false);
			}
		}
        return centerShape(shape);
	}
	
    private static GeneralPath centerShape(GeneralPath shape) {
    	GeneralPath retVal = new GeneralPath();
    	Rectangle2D bounds = shape.getBounds2D();
        float xOffset = (float) (-bounds.getX() - bounds.getWidth()/2);
        float yOffset = (float) (-bounds.getY() - bounds.getHeight()/2);
        PathIterator path = shape.getPathIterator(null);
        retVal.setWindingRule(path.getWindingRule());
        float[] points = new float[6];
        while(!path.isDone()) {
            int segType = path.currentSegment(points);
            switch(segType) {
                case PathIterator.SEG_LINETO:
                    retVal.lineTo(points[0] + xOffset, points[1] + yOffset);
                    break;
                case PathIterator.SEG_MOVETO:
                    retVal.moveTo(points[0] + xOffset, points[1] + yOffset);
                    break;
                case PathIterator.SEG_QUADTO:
                    retVal.quadTo(points[0] + xOffset, points[1] + yOffset,
                                  points[2] + xOffset, points[3] + yOffset);
                    break;
                case PathIterator.SEG_CUBICTO:
                    retVal.curveTo(points[0] + xOffset, points[1] + yOffset,
                                   points[2] + xOffset, points[3] + yOffset,
                                   points[4] + xOffset, points[5] + yOffset);
                    break;
                case PathIterator.SEG_CLOSE:
                    retVal.closePath();
                    break;
            }
            path.next();
        }
        return retVal;
    }

    public static GeneralPath importShape(Element svgElement, double width, double height) {
    	GeneralPath untransformedShape = importShape(svgElement);
        float scaleX = (float) (width / untransformedShape.getBounds2D().getWidth());
        float scaleY = (float) (height / untransformedShape.getBounds2D().getHeight());
        if(scaleX > scaleY) {
        	return scaleShape(untransformedShape, scaleY);
        } else {
            return scaleShape(untransformedShape, scaleX);
        }
    }
    
    private static GeneralPath scaleShape(GeneralPath shape, float scale) {
    	GeneralPath retVal = new GeneralPath();
        PathIterator path = shape.getPathIterator(null);
        retVal.setWindingRule(path.getWindingRule());
        float[] points = new float[6];
        while(!path.isDone()) {
            int segType = path.currentSegment(points);
            switch(segType) {
                case PathIterator.SEG_LINETO:
                    retVal.lineTo(points[0] * scale, points[1] * scale);
                    break;
                case PathIterator.SEG_MOVETO:
                	retVal.moveTo(points[0] * scale, points[1] * scale);
                    break;
                case PathIterator.SEG_QUADTO:
                	retVal.quadTo(points[0] * scale, points[1] * scale, 
                	              points[2] * scale, points[3] * scale);
                    break;
                case PathIterator.SEG_CUBICTO:
                	retVal.curveTo(points[0] * scale, points[1] * scale,
                	               points[2] * scale, points[3] * scale, 
                	               points[4] * scale, points[5] * scale);
                    break;
                case PathIterator.SEG_CLOSE:
                	retVal.closePath();
                    break;
            }
            path.next();
        }
        return retVal;
    }
}
