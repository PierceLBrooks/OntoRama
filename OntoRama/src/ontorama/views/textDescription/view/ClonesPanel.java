package ontorama.views.textDescription.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;

import ontorama.model.graph.Node;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.events.TreeNodeSelectedEvent;

import org.tockit.events.EventBroker;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */
public class ClonesPanel extends AbstractMultiValuesPanel {

    public ClonesPanel(String propName, EventBroker eventBroker) {
        super(propName, eventBroker);
    }

	/**
	 * overriten here because super class assumes we get graph nodes
	 * in the property values list, but in our case clones
	 * only happen in a tree and on tree nodes.
	 * @see ontorama.views.textDescription.view.AbstractPropertiesPanel#update(List)
	 */
	public void update(List propValuesList) {
		Iterator propValuesIterator = propValuesList.iterator();
		clear();
		// need updateUI, otherwise it seems that when a user clicks
		// on a clone button and clone is focused - we don't get a 'clone
		// button'
		_propValuePanel.updateUI();
		while (propValuesIterator.hasNext()) {
			TreeNode curNode = (TreeNode) propValuesIterator.next();
			JComponent component = createPropertyComponent(curNode);
			_propValuePanel.add(component);
		}
		_propValuePanel.update(_propValuePanel.getGraphics());
		repaint();
	}

	/**
	 * @todo hack, see createPropertyComponent(TreeNode) for more info
	 */
	protected JComponent createPropertyComponent(Node graphNode) {
		return null;
	}

	/**
	 * @todo hack because we needed a metod taking tree node as an argument
	 * not sure what is the best way to fix it, one possibility is to change
	 * super class to have different argument in the method. another possibility
	 * is to have this class not extending super class (this may break some
	 * things in DescriptionView because we will need to take out clones panel
	 * from the hashtable as hashtable expects to store AbstractPropertiesPanels
	 * as values.)
	 */
    protected JComponent createPropertyComponent(final TreeNode treeNode) {
        JButton button = new JButton();
        button.setText(treeNode.getName());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _eventBroker.processEvent(new TreeNodeSelectedEvent(treeNode, _eventBroker));
            }
        });
        return (JComponent) button;
    }

}

