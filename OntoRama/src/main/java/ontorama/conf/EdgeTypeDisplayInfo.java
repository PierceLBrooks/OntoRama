package ontorama.conf;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class EdgeTypeDisplayInfo {

    private Image _image;
    private Image _reverseEdgeImage;

    private String _displayLabel;


    private boolean _displayInGraph = false;
    private boolean _displayInDescription = false;
    private boolean _displayReverseEdgeInDescription = false;

    /**
     * applies only to edge type displayed in the description window.
     * if _queryOn is true - then this edge type will be clickable and on
     * click will execute query for the displayed value in the current ontology
     */
    private boolean _queryOn = false;


    public EdgeTypeDisplayInfo () {
    	// we use setters at the moment
    }

    public boolean isDisplayInGraph() {
        return _displayInGraph;
    }

    public void setDisplayInGraph(boolean displayInGraph) {
        _displayInGraph = displayInGraph;
    }

    public boolean isDisplayInDescription() {
        return _displayInDescription;
    }

    public void setDisplayInDescription(boolean displayInDescription) {
        _displayInDescription = displayInDescription;
    }

    public boolean isDisplayReverseEdgeInDescription() {
        return _displayReverseEdgeInDescription;
    }

    public void setDisplayReverseEdgeInDescription(boolean displayReverseEdgeInDescription) {
        _displayReverseEdgeInDescription = displayReverseEdgeInDescription;
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

    public boolean isQueryOn() {
        return _queryOn;
    }

    public void setQueryOn(boolean queryOn) {
        _queryOn = queryOn;
    }

    @Override
	public String toString() {
        String str = "EdgeTypeDisplayInfo: ";
        str = str + "_displayInGraph = " + _displayInGraph+ ", _displayReverseEdgeInDescription = " + _displayReverseEdgeInDescription + ", image = " + _image + ", reverseEdgeImage = " + _reverseEdgeImage;
        return str;
    }









}
