package ontorama.tree.view;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.ImageMaker;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.tree.model.OntoTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @todo    clean up methods, remove unneeded variables
 */
public class OntoTreeRenderer extends DefaultTreeCellRenderer {

    /**
     * node and line colors
     */
    private final static Color _nodeColor = Color.blue;
    private final static Color _cloneNodeColor = Color.red;
    private final static Color _lineColor = Color.gray;
    private static Color _bgColor;

    /**
     * icon for a tree node
     */
    private ImageIcon _nodeImageIcon;

    /**
     *
     */
    private ImageIcon _cloneNodeImageIcon;

    /**
     * icon drawing line between relation link
     * icon and node icon
     */
    private ImageIcon _lineIcon;

    /**
     * map relation link to image
     * store an image for each link.
     * keys - rel link int
     * values - corresponding images
     */
    private static Hashtable _relLinksImages = new Hashtable();

    /**
     * Renderer for OntoTree View
     */
    public OntoTreeRenderer() {
        //tutorialIcon = new ImageIcon("images/middle.gif");
        _bgColor = this.backgroundNonSelectionColor;
        System.out.println("OntoTreeRenderer: backgroundNonSelectionColor = " + this.backgroundNonSelectionColor + ", backgroundSelectionColor = " + this.backgroundSelectionColor);

        int iconW = ImageMaker.getWidth();
        int iconH = ImageMaker.getHeight();
        _nodeImageIcon = makeNodeIcon(iconW / 2, iconH, _nodeColor);
        _cloneNodeImageIcon = makeNodeIcon(iconW / 2, iconH, _cloneNodeColor);

        _lineIcon = makeLineIcon(iconW / 2, iconH);

        initRelationLinkImages();
    }

    /**
     *
     */
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        OntoTreeNode treeNode = (OntoTreeNode) value;
        int relLinkInt = treeNode.getRelLink();
        RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(relLinkInt);

        //setBackgroundNonSelectionColor(isChild(relLinkDetails));
        //setBackgroundSelectionColor(isChild(relLinkDetails));

        setToolTipText(getToolTipText(value, relLinkDetails));

        if (treeNode.getTreePath().getPathCount() == 1) {
            setIcon(_nodeImageIcon);
        } else if (treeNode.getGraphNode().hasClones()) {
            //System.out.println("clone: " + treeNode);
            setIcon(getIcon(relLinkInt, true));
        } else {
            setIcon(getIcon(relLinkInt, false));
        }

        return this;
    }


    /**
     * get icon for the given relation link
     */
    protected Icon getIcon(int relLinkInt, boolean isClone) {
        Image nodeImage = null;

        if (isClone) {
            nodeImage = makeImageForRelLink(relLinkInt, true);
        } else {
            nodeImage = (Image) _relLinksImages.get(new Integer(relLinkInt));
        }

        Icon icon = new ImageIcon(nodeImage);

        //Icon icon = _nodeImageIcon;

        return icon;
    }

    /**
     * get tool tip text for given object and relation link
     */
    protected String getToolTipText(Object value, RelationLinkDetails relLinkDetails) {
        OntoTreeNode treeNode = (OntoTreeNode) value;
        String relLinkName = relLinkDetails.getLinkName();
        String result = "";
        //result = "Concept: " + treeNode.getGraphNode().getName();
        //result = result + "\n";
        result = result + "Relation Type: " + relLinkName;
        return result;
    }

    /**
     * initialise relation link images - build an image
     * for each relation link. Image consist from relation
     * link image + node image connected by line
     */
    private void initRelationLinkImages() {
        HashSet relLinksSet = OntoramaConfig.getRelationLinksSet();
        Iterator it = relLinksSet.iterator();
        while (it.hasNext()) {
            Integer cur = (Integer) it.next();
            Image nodeImage = makeImageForRelLink(cur.intValue(), false);
            _relLinksImages.put(cur, nodeImage);
        }
    }

    /**
     *
     */
    private Image makeImageForRelLink(int relLinkInt, boolean isClone) {
        RelationLinkDetails relLinkDetails = OntoramaConfig.getRelationLinkDetails(relLinkInt);
        //System.out.println("relLinkInt = " + relLinkInt + ", relLinkDetails = " + relLinkDetails);
        Image relImage = relLinkDetails.getDisplayImage();
        Image nodeImage = makeCombinedIcon(isClone, relImage);
        return nodeImage;
    }

    /**
     * combine relation link image and node image connected
     * by image drawing connecting line.
     */
    private Image makeCombinedIcon(boolean isClone, Image relImage) {

        ImageIcon relImageIcon = new ImageIcon(relImage);
        ImageObserver relImageObserver = relImageIcon.getImageObserver();

        int relImageWidth = ImageMaker.getWidth();
        int relImageHeight = ImageMaker.getHeight();

        int totalWidth = relImageWidth * 2;
        int totalHeight = relImageHeight;

        BufferedImage image = new BufferedImage(totalWidth + 1, totalHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, totalWidth, totalHeight);
        g2.drawRect(0, 0, totalWidth, totalHeight);

        g2.drawImage(relImage, 0, 0, relImageWidth, relImageHeight, relImageObserver);
        g2.drawImage(_lineIcon.getImage(), relImageWidth, 0, _lineIcon.getIconWidth(), totalHeight,
                _lineIcon.getImageObserver());
        int w = relImageWidth + _lineIcon.getIconWidth();
        if (isClone) {
            g2.drawImage(_cloneNodeImageIcon.getImage(), w, 0,
                    _cloneNodeImageIcon.getIconWidth(), totalHeight,
                    _cloneNodeImageIcon.getImageObserver());
        } else {
            g2.drawImage(_nodeImageIcon.getImage(), w, 0,
                    _nodeImageIcon.getIconWidth(), totalHeight,
                    _nodeImageIcon.getImageObserver());
        }
        return image;
    }

    /**
     * make icon for nodes
     */
    private ImageIcon makeNodeIcon(int width, int height, Color color) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, width, height);
        g2.drawRect(0, 0, width, height);

        int ovalSize = width - (width * 12) / 100;
        int ovalX = 0;
        int ovalY = (height - ovalSize) / 2;
        //System.out.println("\n\novalSize = " + ovalSize + ", height = " + height);

        g2.setColor(color);
        //g2.fillOval(ovalX, ovalY, ovalSize, ovalSize);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D circle = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
        g2.fill(circle);

        //Ellipse2D circleOutline = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
        g2.setColor(Color.black);
        g2.draw(circle);

        return (new ImageIcon(image));
    }

    /**
     * make icon for a line connecting relation
     * link image and node image
     */
    private ImageIcon makeLineIcon(int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.white);
        g2.fillRect(0, 0, width, height);
        g2.drawRect(0, 0, width, height);

        int x1 = 0;
        int y1 = height / 2;
        int x2 = width;
        int y2 = y1;

        float dash1[] = {1.0f};
        BasicStroke dashed = new BasicStroke(0.01f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                1.0f, dash1, 0.0f);


        g2.setColor(_lineColor);
        g2.setStroke(dashed);
        g2.drawLine(x1, y1, x2, y2);

        return (new ImageIcon(image));
    }

}