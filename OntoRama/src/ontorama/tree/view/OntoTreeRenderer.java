package ontorama.tree.view;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ToolTipManager;
import javax.swing.JScrollPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.Color;
import java.awt.Component;

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
		Image image = relLinkDetails.getDisplayImage();
		Icon icon = new ImageIcon(image);

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
}
