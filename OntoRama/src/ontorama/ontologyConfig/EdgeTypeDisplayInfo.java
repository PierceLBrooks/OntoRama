/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:12:40
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ontologyConfig;

import javax.swing.*;
import java.awt.*;

public class EdgeTypeDisplayInfo {
    public static final int DISPLAY_IN_GRAPH = 1;
    public static final int DISPLAY_IN_DESCRIPTION_WINDOW = 2;

    private Image _image;
    private Image _reverseEdgeImage;

    private String _displayLabel;

    private int _displayLocationDirective;
    private int _displayLocationDirectiveForReversedEdge;

    /**
     * @todo need to think about display directives more - each direction of the
     * edge could have two different display directives.
     */
    public EdgeTypeDisplayInfo () {
    }

    public void setDisplayLocationDirective(int displayLocationDirective) {
        _displayLocationDirective = displayLocationDirective;
    }



    public int getDisplayLocationDirective() {
        return _displayLocationDirective;
    }

    public Image getImage() {
        return _image;
    }

    public void setImage(Image image) {
        _image = image;
    }

    public void setImage (Color color, String symbol) {
        _image = ImageMaker.getImage(color, symbol);
    }

    public Image getReverseEdgeImage() {
        return _reverseEdgeImage;
    }

    public void setReverseEdgeImage(Image reverseEdgeImage) {
        _reverseEdgeImage = reverseEdgeImage;
    }

    public void setReverseEdgeImage (Color color, String symbol) {
         _reverseEdgeImage = ImageMaker.getImage(color, symbol);
    }

    public String getDisplayLabel() {
        return _displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        _displayLabel = displayLabel;
    }

    public int getDisplayLocationDirectiveForReversedEdge() {
        return _displayLocationDirectiveForReversedEdge;
    }

    public void setDisplayLocationDirectiveForReversedEdge(int displayLocationDirectiveForReversedEdge) {
        _displayLocationDirectiveForReversedEdge = displayLocationDirectiveForReversedEdge;
    }

    public ImageIcon getDisplayIcon () {
        if (_image == null) {
            return null;
        }
        return new ImageIcon(_image);
    }

    public ImageIcon getReverseEdgeDisplayIcon() {
        if (_reverseEdgeImage == null)  {
            return null;
        }
        return new ImageIcon(_reverseEdgeImage);
    }

    public String toString() {
        String str = "EdgeTypeDisplayInfo: ";
        str = str + "_displayLocationDirective = " + _displayLocationDirective + ", _displayLocationDirectiveForReversedEdge = " + _displayLocationDirectiveForReversedEdge + ", image = " + _image + ", reverseEdgeImage = " + _reverseEdgeImage;
        return str;
    }









}
