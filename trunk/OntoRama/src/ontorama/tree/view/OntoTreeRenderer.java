package ontorama.tree.view;

import ontorama.OntoramaConfig;
import ontorama.model.EdgeType;
import ontorama.model.Node;
import ontorama.model.NodeType;
import ontorama.ontologyConfig.ImageMaker;
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
 * Renderer for TreeView
 */
public class OntoTreeRenderer extends DefaultTreeCellRenderer {

    /**
     * node and line colors
     */
    private final static Color _nodeColor = Color.blue;
    private final static Color _cloneNodeColor = Color.red;
    private final static Color _lineColor = Color.gray;

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
     * keys - rel link type
     * values - corresponding images
     */
    private static Hashtable _relLinksImages = new Hashtable();

    /**
     * Renderer for OntoTree View
     */
    public OntoTreeRenderer() {
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
        EdgeType relLinkDetails = treeNode.getRelLink();

        setToolTipText(getToolTipText(value, relLinkDetails));

        if (treeNode.getTreePath().getPathCount() == 1) {
            setIcon(_nodeImageIcon);
        } else if (treeNode.getGraphNode().hasClones()) {
            setIcon(getIcon(relLinkDetails, true));
        } else {
            setIcon(getIcon(relLinkDetails, false));
        }

        return this;
    }


    /**
     * get icon for the given relation link
     */
    protected Icon getIcon(EdgeType edgeType, boolean isClone) {
        Image nodeImage = null;

        if (isClone) {
            nodeImage = makeImageForRelLink(edgeType, true);
        } else {
            nodeImage = (Image) _relLinksImages.get(edgeType);
        }

        Icon icon = new ImageIcon(nodeImage);
        return icon;
    }

    /**
     * get tool tip text for given object and relation link
     */
    protected String getToolTipText(Object value, EdgeType edgeType) {
        String result = "";
        OntoTreeNode treeNode = (OntoTreeNode) value;
        Node node = treeNode.getGraphNode();
        NodeType nodeType = node.getNodeType();
        if (nodeType == null) {
            result = "???";
        }
        else {
            result = result + "type: " + nodeType.getNodeType();
        }

//        if (edgeType == null) {
//            result = result + "Node: " + treeNode.getGraphNode().getName();
//        }
//        else {
//            String edgeTypeName = edgeType.getName();
//            result = result + "Relation Type: " + edgeTypeName;
//        }
        return result;
    }

    /**
     * initialise relation link images - build an image
     * for each relation link. Image consist from relation
     * link image + node image connected by line
     */
    private void initRelationLinkImages() {
        HashSet relLinksSet = OntoramaConfig.getEdgeTypesSet();
        Iterator it = relLinksSet.iterator();
        while (it.hasNext()) {
            EdgeType cur = (EdgeType) it.next();
            if (! OntoramaConfig.getEdgeDisplayInfo(cur).isDisplayInGraph()) {
                continue;
            }
            Image nodeImage = makeImageForRelLink(cur, false);
            _relLinksImages.put(cur, nodeImage);
        }
    }

    /**
     *
     */
    private Image makeImageForRelLink(EdgeType relLinkType, boolean isClone) {
        Image relImage = OntoramaConfig.getEdgeDisplayInfo(relLinkType).getImage();
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
        g2.setColor(color);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D circle = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
        g2.fill(circle);
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
        BasicStroke dashed = new BasicStroke(0.01f, BasicStroke.CAP_BUTT,
                                        BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f);

        g2.setColor(_lineColor);
        g2.setStroke(dashed);
        g2.drawLine(x1, y1, x2, y2);

        return (new ImageIcon(image));
    }

}
