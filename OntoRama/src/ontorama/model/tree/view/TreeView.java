package ontorama.model.tree.view;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeNode;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface TreeView {
	public void focus(TreeNode node);
	public void setTree(Tree tree);
	public void repaint();
}
