/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Sep 9, 2002
 * Time: 1:17:15 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model;

import java.awt.*;

public class RelationNodeType implements NodeType {
    private String _nodeTypeName = "Relation Type";
    private Color _displayColor = Color.green;

    public String getNodeType() {
        return _nodeTypeName;
    }

    public Color getDisplayColor() {
        return _displayColor;
    }
}
