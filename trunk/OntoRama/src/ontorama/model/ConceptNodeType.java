/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 9, 2002
 * Time: 1:12:34 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import ontorama.model.NodeType;

import java.awt.*;

public class ConceptNodeType implements NodeType {
    private String _nodeTypeName = "Concept Type";
    private Color _displayColor = Color.blue;

    public String getNodeType() {
        return _nodeTypeName;
    }

    public Color getDisplayColor() {
        return _displayColor;
    }
}
