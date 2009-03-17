package ontorama.views.tree.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import ontorama.OntoramaConfig;
import ontorama.conf.ImageMaker;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.views.tree.model.OntoTreeNode;

/**
 * Renderer for TreeView
 */
@SuppressWarnings("serial")
public class OntoTreeRenderer extends DefaultTreeCellRenderer {

    private final static Color _lineColor = Color.gray;

    private ImageIcon _cloneNodeImageIcon;

    /**
     * icon drawing line between relation link
     * icon and node icon
     */
    private ImageIcon _lineIcon;

    /**
     * Renderer for OntoTree View
     */
    public OntoTreeRenderer() {

        int iconW = ImageMaker.getWidth();
        int iconH = ImageMaker.getHeight();

        _lineIcon = makeLineIcon(iconW / 2, iconH);
    }

    public Component getTreeCellRendererComponent(
                                        JTree tree,
                                        Object value,
                                        boolean sel,
                                        boolean expanded,
                                        boolean leaf,
                                        int row,
                                        boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        OntoTreeNode treeNode = (OntoTreeNode) value;
        EdgeType edge = treeNode.getEdgeType();

        NodeType nodeType = treeNode.getModelTreeNode().getNodeType();

        String nodeTextStr = treeNode.getModelTreeNode().getName();

        setText(nodeTextStr);

        setToolTipText(getToolTipText(value, edge));

        ImageIcon image;
        if (treeNode.getModelTreeNode().getClones().size() != 0) {
        	image = new ImageIcon(OntoramaConfig.getNodeTypeDisplayInfo(nodeType).getCloneImage());
        } else {
            image = new ImageIcon(OntoramaConfig.getNodeTypeDisplayInfo(nodeType).getImage());
        }
        
        if (treeNode.getTreePath().getPathCount() == 1) {
            setIcon(image);
        } else {
            Icon icon = getIcon(edge, image, false);
            setIcon(icon);
        }

        return this;
    }


    /**
     * get icon for the given relation link
     */
    protected Icon getIcon(EdgeType edgeType, ImageIcon nodeImageIcon, boolean isClone) {
        Image nodeImage = null;

        if (isClone) {
            nodeImage = makeImageForRelLink(edgeType, true, nodeImageIcon);
        } else {
            nodeImage = makeImageForRelLink(edgeType, false, nodeImageIcon);
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

        if (edgeType == null) {
            result = result + "Node: " + treeNode.getModelTreeNode().getName();
        }
        else {
            String edgeTypeName = edgeType.getName();
            result = result + "Relation Type: " + edgeTypeName;
        }
        return result;
    }

    private Image makeImageForRelLink(EdgeType relLinkType, boolean isClone, ImageIcon nodeImageIcon) {
        Image relImage = OntoramaConfig.getEdgeDisplayInfo(relLinkType).getImage();
        Image nodeImage = makeCombinedIcon(isClone, relImage, nodeImageIcon);
        return nodeImage;
    }

    /**
     * combine relation link image and node image connected
     * by image drawing connecting line.
     */
    private Image makeCombinedIcon(boolean isClone, Image relImage, ImageIcon nodeImage) {

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
            g2.drawImage(nodeImage.getImage(), w, 0,
                    nodeImage.getIconWidth(), totalHeight,
                    nodeImage.getImageObserver());
        }
        return image;
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
        
        int x1 = width/3;
        int y1 = height / 2;
        int x2 = width - width/3;
        int y2 = y1;

        g2.setColor(_lineColor);
        g2.drawLine(x1, y1, x2, y2);

        return (new ImageIcon(image));
    }

}
