package ontorama.model.graph;

import java.awt.Color;
import java.awt.Shape;

public interface NodeType {
    String getDisplayName();

    /**
     * The shape used to display the node.
     * 
     * The shape is centered around the (0,0) point, its size can be arbitrary.
     */
    Shape getDisplayShape();
    
    /**
     * Returns true if the shape object has to be displayed upright.
     * 
     * This shold be true for labels or text shapes, false otherwise.
     */
    boolean forceUprightShape();
    
    Color getDisplayColor();
}
