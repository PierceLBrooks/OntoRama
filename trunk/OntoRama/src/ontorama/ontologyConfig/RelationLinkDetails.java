package ontorama.ontologyConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Description: Holds all relavant details for Relation Link (such as:
 *  name, symbol, display color, etc)
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 */

public class RelationLinkDetails {

    /**
     * use this setting to display this edge in the graph visualisation
     */
    public static final int DISPLAY_TYPE_GRAPH = 1;
    /**
     * use this setting to display edge in the description window.
     */
    public static final int DISPLAY_TYPE_DESCRIPTION = 2;

    /**
     * name of link and reversed link
     * for example: relation id = 0, link name = 'subtype',
     * reversed link name = 'supertype'
     */
    private String linkName = null;
    private String reversedLinkName = null;

    /**
     * symbols for link and reversed link
     */
    private String linkSymbol = null;
    private String reversedLinkSymbol = null;

    /**
     * display color for this relation
     */
    private Color displayColor;

    /**
     * display symbol for this relation
     */
    private String displaySymbol = null;

    /**
     * display image for this relation.
     * Display image may be built using displayColor and
     * displaySymbol or supplied by user in congiguration
     * xml file by specifying URI to the image.
     */
    private Image displayImage;

    private int displayType = RelationLinkDetails.DISPLAY_TYPE_GRAPH;

    /**
     * Create an object RelationLinkDetails for specified relation link name.
     * Setters can be used to specify all other relation link properties,
     * such as: link symbol, reversed link symbol, display color,
     * display symbol and display image.
     */
    public RelationLinkDetails(String linkName) {
        this.linkName = linkName;
    }

    /**
     * Get relation link name
     * @return  linkName
     */
    public String getLinkName() {
        return this.linkName;
    }

    /**
     * Set relation link symbol
     * @param   linkSymbol
     */
    public void setLinkSymbol(String linkSymbol) {
        this.linkSymbol = linkSymbol;
    }

    /**
     * Get relation link symbol
     * @return  linkSymbol
     */
    public String getLinkSymbol() {
        return this.linkSymbol;
    }

    /**
     * Get Reversed Link Name
     * @return  reversedLinkName
     */
    public String getReversedLinkName() {
        return this.reversedLinkName;
    }

    /**
     * Set reversedLinkName
     * @param   reversedLinkName
     */
    public void setReversedLinkName(String reversedLinkName) {
        this.reversedLinkName = reversedLinkName;
    }

    /**
     * Get reversedLinkSymbol
     * @return  reversedLinkSymbol
     */
    public String getReversedLinkSymbol() {
        return this.reversedLinkSymbol;
    }

    /**
     * Set reversedLinkSymbol
     * @param reversedLinkSymbol
     */
    public void setReversedLinkSymbol(String reversedLinkSymbol) {
        this.reversedLinkSymbol = reversedLinkSymbol;
    }

    /**
     * Set display color
     * @param colorStr  colorStr should be a valid color that can be decoded using Color.decode
     * @see java.awt.Color
     */
    public void setDisplayColor(String colorStr) {
        this.displayColor = Color.decode(colorStr);
    }

    /**
     * Get display color
     * @return Color displayColor
     */
    public Color getDisplayColor() {
        return this.displayColor;
    }

    /**
     * Set display symbol and make an image for it
     * @param  displaySymbol
     */
    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
        makeDisplayImage();
    }

    /**
     * Get displaySymbol
     * @return  displaySymbol string
     */
    public String getDisplaySymbol() {
        return this.displaySymbol;
    }


    /**
     * Set displayImage
     * @param   imageStr
     * @todo    look into parameter for getImage - use relative paths
     */
    public void setDisplayImage(String imageStr) {
        this.displayImage = Toolkit.getDefaultToolkit().getImage(imageStr);
    }

    /**
     * Make an image from given color and symbol
     */
    public void makeDisplayImage() {
        this.displayImage = ImageMaker.getImage(this.displayColor, this.displaySymbol);
    }

    /**
     * Get display image
     * @return  Image displayImage
     */
    public Image getDisplayImage() {
        return this.displayImage;
    }

    /**
     * Get displayIcon
     * @return ImageIcon displayIcon
     */
    public ImageIcon getDisplayIcon() {
        ImageIcon icon = new ImageIcon(this.displayImage);
        return icon;
    }

    /**
     *
     * @param displayType - int (see fields in this class)
     * @see RelationLinkDetails
     */
    public void setDisplayType (int displayType) {
       this.displayType = displayType;
    }

    /**
     *
     * @return displayType - int (see fields in this class)
     * @see RelationLinkDetails
     */
    public int getDisplayType () {
        return this.displayType;
    }

}