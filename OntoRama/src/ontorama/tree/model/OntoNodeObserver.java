
package ontorama.tree.model;

import javax.swing.tree.TreePath;

/**
 * NodeObserver is part of the MVC parten for updating
 * nodes in the current view.
 */


public interface OntoNodeObserver {

	/**
	 * Method called to update observer.
     * In this case - update OntoTreeView by focusing on TreePath
	 */
	public void updateOntoTree( TreePath path );

}