package ontorama.ontologyConfig;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * Title:
 * Description: Holds all relavant details for Relation Link (such as:
 *  name, symbol, display color, etc)
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class RelationLinkDetails {

    /**
     *
     */
    private String linkName = null;
    private String reversedLinkName = null;

    /**
     *
     */
    private String linkSymbol = null;
    private String reversedLinkSymbol = null;

    /**
     *
     */
    private Color displayColor;

    /**
     *
     */
    private String displaySymbol = null;
    private String reversedLinkDisplaySymbol = null;

    /**
     *
     */
    private Image displayImage;

    /**
     *
     */
    public RelationLinkDetails(String linkName) {
        this.linkName = linkName;
    }

    /**
     *
     */
    public String getLinkName () {
        return this.linkName;
    }

    /**
     *
     */
    public void setLinkSymbol (String linkSymbol) {
        this.linkSymbol = linkSymbol;
    }

    /**
     *
     */
    public String getLinkSymbol () {
        return this.linkSymbol;
    }

    /**
     *
     */
    public String getReversedLinkName () {
        return this.reversedLinkName;
    }

    /**
     *
     */
    public void setReversedLinkName(String reversedLinkName) {
        this.reversedLinkName = reversedLinkName;
    }

    /**
     *
     */
    public String getReversedLinkSymbol () {
        return this.reversedLinkSymbol;
    }

    /**
     *
     */
    public void setReversedLinkSymbol (String reversedLinkSymbol) {
        this.reversedLinkSymbol = reversedLinkSymbol;
    }

    /**
     *
     */
    public void setDisplayColor (String colorStr) {
        this.displayColor = Color.decode(colorStr);
    }

    /**
     *
     */
    public Color getDisplayColor () {
        return this.displayColor;
    }

    /**
     *
     */
    public void setDisplaySymbol (String displaySymbol) {
        this.displaySymbol = displaySymbol;
        makeDisplayImage();
    }

    /**
     *
     */
    public String getDisplaySymbol () {
        return this.displaySymbol;
    }


    /**
     * @todo    look into parameter for getImage - use relative paths
     */
    public void setDisplayImage (String imageStr) {
        this.displayImage = Toolkit.getDefaultToolkit().getImage(imageStr);
    }

    /**
     * Make an image from given color and symbol
     */
    public void makeDisplayImage () {
        this.displayImage = ImageMaker.getImage(this.displayColor,this.displaySymbol);
    }

    /**
     *
     */
     public Image getDisplayImage () {
        return this.displayImage;
     }
}