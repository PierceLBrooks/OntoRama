package ontorama.model.graph;

import java.awt.Color;
import java.awt.Shape;

public interface NodeType {
    public String getDisplayName();

    /**
     * The shape used to display the node.
     * 
     * The shape is centered around the (0,0) point, its size can be arbitrary.
     */
    public Shape getDisplayShape();
    public Color getDisplayColor();
}
