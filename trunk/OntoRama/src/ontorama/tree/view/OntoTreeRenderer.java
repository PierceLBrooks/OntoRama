package ontorama.tree.view;

import java.util.Iterator;

import java.awt.image.*;
import java.awt.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import junit.runner.ReloadingTestSuiteLoader;
import javax.swing.ToolTipManager;
import javax.swing.JScrollPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.Color;
import java.awt.Component;

import ontorama.ontologyConfig.ImageMaker;

import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.model.OntoTreeModel;

import ontorama.ontologyConfig.RelationLinkDetails;

/**
 * @todo    clean up methods, remove unneeded variables
 */
public class OntoTreeRenderer extends DefaultTreeCellRenderer {
	//ImageIcon tutorialIcon;
	Color color1 = Color.gray;
	Color color2 = Color.pink;

	/**
	 *
	 */
	public OntoTreeRenderer() {
		//tutorialIcon = new ImageIcon("images/middle.gif");
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
		RelationLinkDetails relLinkDetails = getRelLinkDetails(value);

		//setBackgroundNonSelectionColor(isChild(relLinkDetails));
		//setBackgroundSelectionColor(isChild(relLinkDetails));

		setIcon(getIcon(relLinkDetails));
		setToolTipText(getToolTipText(value,relLinkDetails));
		return this;
	}

	/**
	 *
	 */
	private RelationLinkDetails getRelLinkDetails (Object value) {
		OntoTreeNode treeNode = (OntoTreeNode) value;
		int relLink = treeNode.getRelLink();
		RelationLinkDetails relLinkDetails = ontorama.OntoramaConfig.getRelationLinkDetails(relLink);
		return relLinkDetails;
	}

	/**
	 *
	 */
	protected Color isChild (RelationLinkDetails relLinkDetails) {
		Color color = relLinkDetails.getDisplayColor();
		return color;

	}

	/**
	 *
	 */
	protected Icon getIcon (RelationLinkDetails relLinkDetails) {
		Image relImage = relLinkDetails.getDisplayImage();
		ImageIcon relImageIcon = new ImageIcon(relImage);
		ImageObserver relImageObserver = relImageIcon.getImageObserver();
		
		
		Image nodeImage = makeNodeImage(Color.blue, relImage, relImageObserver);
		
		
		//Icon icon = new ImageIcon(relImage);
		Icon icon = new ImageIcon(nodeImage);

		return icon;
	}

	/**
	 *
	 */
	protected String getToolTipText (Object value, RelationLinkDetails relLinkDetails) {
		OntoTreeNode treeNode = (OntoTreeNode) value;
		String relLinkName = relLinkDetails.getLinkName();
		String result = "";
		//result = "Concept: " + treeNode.getGraphNode().getName();
		//result = result + "\n";
		result = result + "Relation Type: " + relLinkName;
		return result;
	}
	
	/**
	 * 
	 */
    public Image makeNodeImage (Color color, Image relImage,
    							ImageObserver relImageObserver) {
    	int relImageWidth = ImageMaker.getWidth();
    	int relImageHeight = ImageMaker.getHeight();   	
    	
        Image image = new BufferedImage(relImageWidth*2,relImageHeight,BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();
		
        g.setColor(Color.white);
        g.fillRect(0, 0, relImageWidth*2, relImageHeight*2);
        g.drawRect(0, 0, relImageWidth*2, relImageHeight*2);

		g.drawImage(relImage, 0, 0, relImageWidth, relImageHeight, relImageObserver);


    	int ovalSize = relImageWidth/2;
    	int ovalX = relImageWidth + relImageWidth/2;
    	int ovalY = 0;
        g.setColor(Color.blue);
        g.fillOval(ovalX, ovalY, ovalSize, ovalSize);
        g.setColor(Color.black);
        g.drawOval(ovalX, ovalY, ovalSize+1, ovalSize+1);


		int x1 = relImageWidth;
		int y1 = relImageHeight/2;
		int x2 = relImageWidth + ovalSize;
		int y2 = y1;
		
		g.setColor(Color.black);
		g.drawLine(x1,y1,x2,y2);        	
		
        return image;
    }
	
}
