/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 9, 2002
 * Time: 1:12:34 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.model.NodeTypeInterface;

import java.awt.*;

public class NodeType implements NodeTypeInterface {
    private String _nodeTypeName;
    private Color _displayColor;

    public NodeType (String typeName, Color displayColor) {
        _nodeTypeName = typeName;
        _displayColor = displayColor;
    }

    public String getNodeType() {
        return _nodeTypeName;
    }

    public Color getDisplayColor() {
        return _displayColor;
    }
}
