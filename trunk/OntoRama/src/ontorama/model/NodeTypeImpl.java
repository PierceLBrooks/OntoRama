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

public class NodeTypeImpl implements NodeTypeInterface {
    private String _nodeTypeName;

    public NodeTypeImpl (String typeName) {
        _nodeTypeName = typeName;
    }

    public String getNodeType() {
        return _nodeTypeName;
    }

}
