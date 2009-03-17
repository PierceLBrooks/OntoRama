package ontorama.views.textDescription.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ontorama.model.tree.TreeNode;
import ontorama.model.tree.events.TreeNodeSelectedEvent;

import org.tockit.events.EventBroker;

/**
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 */
@SuppressWarnings("serial")
public class ClonesPanel extends AbstractPropertiesPanel {
	private JPanel _propValuePanel = new JPanel();
	private EventBroker _eventBroker;

    public ClonesPanel(String propName, EventBroker eventBroker) {
		_eventBroker = eventBroker;
		_propNameLabel.setText(propName);

		layoutFirstComponent();

		// add second panel
		_propValuePanel.setLayout(new BoxLayout(_propValuePanel, BoxLayout.X_AXIS));
		_propValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(_propValuePanel);
    }

	/**
	 * Overridden here because super class assumes we get graph nodes
	 * in the property values list, but in our case clones
	 * only happen in a tree and on tree nodes.
	 * @see ontorama.views.textDescription.view.AbstractPropertiesPanel#update(List)
	 */
	public void update(List<Object> propValuesList) {
		Iterator<Object> propValuesIterator = propValuesList.iterator();
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
    
	public void clear() {
		_propValuePanel.removeAll();
	}
}
