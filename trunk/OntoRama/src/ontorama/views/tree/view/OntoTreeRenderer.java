package ontorama.views.tree.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import ontorama.OntoramaConfig;
import ontorama.conf.ImageMaker;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.model.tree.TreeEdge;
import ontorama.model.tree.TreeNode;
import ontorama.views.tree.model.OntoTreeNode;

/**
 * Renderer for TreeView
 */
public class OntoTreeRenderer extends DefaultTreeCellRenderer {

    /**
     * node and line colors
     */
    private final static Color _cloneNodeColor = Color.red;
    private final static Color _unknownNodeColor = Color.white;
    private final static Color _lineColor = Color.gray;

    /**
     *
     */
    private ImageIcon _cloneNodeImageIcon;
    private ImageIcon _unknownNodeImageIcon;

    /**
     * icon drawing line between relation link
     * icon and node icon
     */
    private ImageIcon _lineIcon;

    /**
     * Renderer for OntoTree View
     * @todo shouldn't have to pass graph to the renderer. doing this only to be able to display
     * relation type signatures. Possible solutions:
     * - introduce RelationNodes
     * - ?
     */
    public OntoTreeRenderer() {

        int iconW = ImageMaker.getWidth();
        int iconH = ImageMaker.getHeight();

        _lineIcon = makeLineIcon(iconW / 2, iconH);

        //initRelationLinkImages();
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
        // @todo hack for unknown node type
        if (nodeType == null) {
        	nodeType = OntoramaConfig.UNKNOWN_TYPE;
        }

        String nodeTextStr = treeNode.getModelTreeNode().getName();

        if (nodeType == OntoramaConfig.RELATION_TYPE) {
            String sign1 = null;
            String sign2 = null;
            Iterator it = treeNode.getModelTreeNode().getChildren().iterator();
            while (it.hasNext()) {
            	TreeNode curNode = (TreeNode) it.next();
                TreeEdge curEdge = treeNode.getModelTreeNode().getEdge(curNode);
                EdgeType edgeType = curEdge.getEdgeType();
                // @todo again hardcoding relation name - if config.xml file changes - this won't work.
                // probably need RelationNode to fix this.
                if (edgeType.getName().equals("relSignature1")) {
                    sign1 = curEdge.getToNode().getName();
                }
                if (edgeType.getName().equals("relSignature2")) {
                    sign2 = curEdge.getToNode().getName();
                }
            }
            nodeTextStr = nodeTextStr + " (";
            if (sign1 == null ) {
                nodeTextStr = nodeTextStr + "*";
            }
            else {
                nodeTextStr = nodeTextStr + sign1;
            }
            nodeTextStr = nodeTextStr + ", ";
            if (sign2 == null ) {
                nodeTextStr = nodeTextStr + "*";
            }
            else {
                nodeTextStr = nodeTextStr + sign2;
            }
            nodeTextStr = nodeTextStr + ")";
        }
        setText(nodeTextStr);

        setToolTipText(getToolTipText(value, edge));

        /// @todo should always have nodeType != null, when editing graph - it should be able
        // to figure out node type.
        if (nodeType == null) {
            nodeType = OntoramaConfig.UNKNOWN_TYPE;
        }

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
            //nodeImage = (Image) _relLinksImages.get(edgeType);
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
        ontorama.model.tree.TreeNode node = treeNode.getModelTreeNode();
        NodeType nodeType = node.getNodeType();

        if (edgeType == null) {
            result = result + "Node: " + treeNode.getModelTreeNode().getName();
        }
        else {
            String edgeTypeName = edgeType.getName();
            result = result + "Relation Type: " + edgeTypeName;
        }
        return result;
    }

    /**
     *
     */
    private Image makeImageForRelLink(ontorama.model.graph.EdgeType relLinkType, boolean isClone, ImageIcon nodeImageIcon) {
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
