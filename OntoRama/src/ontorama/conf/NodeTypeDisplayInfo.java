/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:25:48
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.conf;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;

public class NodeTypeDisplayInfo {

    private Color _color;
    private Shape _shape;
    private Image _image;

    public NodeTypeDisplayInfo() {
    }

    public Color getColor() {
        return _color;
    }

    public void setColor(Color color) {
        _color = color;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image image) {
        _image = image;
    }
    
    public void setShape (Shape shape) {
    	_shape = shape;
    }
    
    public Shape getShape () {
    	return _shape;
    }



}
