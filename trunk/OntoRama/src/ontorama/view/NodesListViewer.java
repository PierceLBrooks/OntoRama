package ontorama.view;

import java.util.List;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import ontorama.model.GraphNode;

/**
 * @author nataliya
 *
 * Display selectable list of nodes
 */
public class NodesListViewer extends JFrame {

	/**
	 * array of nodes to display
	 */
	private GraphNode[] _nodes;

	/**
	 *
	 */
	public JList _nodesList;

	/**
	 *
	 */
	public NodesListViewer (List nodes) {
		Vector nodesVector = new Vector((Collection) nodes);
		_nodesList = new JList(nodesVector);
		_nodesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(_nodesList);

//		_nodesList.addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged (ListSelectionEvent e) {
//				GraphNode selectedNode = (GraphNode) _nodesList.getSelectedValue();
//				System.out.println("selected node = " + selectedNode.getName());
//			}
//		});

		getContentPane().add(scrollPane);

		pack();
	}

//	/**
//	 *
//	 */
//	public void setNodesList (List nodes) {
//	}


	/**
	 *
	 */
	public void showList (boolean setVisibleFlag) {
		setVisible(setVisibleFlag);
	}

        /**
         *
         */
        public JList getList () {
          return _nodesList;
        }

}

