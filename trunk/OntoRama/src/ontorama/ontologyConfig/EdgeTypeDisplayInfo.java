/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 18/09/2002
 * Time: 09:12:40
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ontologyConfig;

import java.awt.*;

public class EdgeTypeDisplayInfo {
    private static final int DISPLAY_IN_GRAPH = 1;
    private static final int DISPLAY_IN_DESCRIPTION_WINDOW = 2;

    private Image _image;
    private Image _reverseEdgeImage;

    private String _displayLabel;

    private int _displayLocationDirective;

    public EdgeTypeDisplayInfo (int displayLocationDirective) {
        _displayLocationDirective = displayLocationDirective;
    }

    public int get_displayLocationDirective() {
        return _displayLocationDirective;
    }

    public Image get_image() {
        return _image;
    }

    public void set_image(Image _image) {
        this._image = _image;
    }

    public Image get_reverseEdgeImage() {
        return _reverseEdgeImage;
    }

    public void set_reverseEdgeImage(Image _reverseEdgeImage) {
        this._reverseEdgeImage = _reverseEdgeImage;
    }

    public String get_displayLabel() {
        return _displayLabel;
    }

    public void set_displayLabel(String _displayLabel) {
        this._displayLabel = _displayLabel;
    }








}
