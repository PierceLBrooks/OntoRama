/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 9, 2002
 * Time: 1:12:34 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph;

import java.awt.Color;
import java.awt.Shape;

public class NodeTypeImpl implements NodeType {
    private String _displayName;
    private Shape _displayShape;
    private Color _displayColor;
    private boolean _forceUprightShape;

	/**
	 * The initialisation constructor.
	 * 
	 * Note that the shape should be centered around (0,0).
	 */
    public NodeTypeImpl(String displayName, Shape displayShape, boolean forceUprightShape, Color displayColor) {
        _displayName = displayName;
        _displayShape = displayShape;
        _displayColor = displayColor;
        _forceUprightShape = forceUprightShape;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public String toString () {
        return _displayName;
    }
    public Shape getDisplayShape() {
        return _displayShape;
    }
    public Color getDisplayColor() {
        return _displayColor;
    }
    
    public boolean forceUprightShape() {
        return _forceUprightShape;
    }
}
