/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:25:48
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ontologyConfig;

import java.awt.*;

public class NodeTypeDisplayInfo {
    private static final int CONCEPT_TYPE = 1;
    private static final int RELATION_TYPE = 2;

    private Color _color;
    private Image _image;
    private int _type;

    public NodeTypeDisplayInfo(int type) {
        this._type = type;
    }



}
