package ontorama.view;

import ontorama.model.GraphNode;
import ontorama.controller.QueryEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.Vector;

import org.tockit.events.EventBroker;

/**
 * @author nataliya
 *
 * Display selectable list of nodes
 */
public class NodesListViewer extends JScrollPane {

    /**
     *
     */
    private JList _nodesList;

    /**
     *
     */
    private EventBroker _eventBroker;

    /**
     *
     */
    public NodesListViewer(EventBroker eventBroker, List nodes)  {
        _eventBroker = eventBroker;
        setNodesList(nodes);
    }


    /**
     *
     */
    public void setNodesList (List nodes) {
        Vector nodesVector = new Vector(nodes);
        _nodesList = new JList(nodesVector);
        _nodesList.setCellRenderer(new NodeListCellRenderer());
        _nodesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _nodesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                GraphNode selectedNode =
                        (GraphNode) _nodesList.getSelectedValue();
                //System.out.println("selected node = " + selectedNode);
                _eventBroker.processEvent(new QueryEvent(selectedNode));
            }
        });
        setViewportView(_nodesList);
        repaint();
    }

    /**
     *
     */
    public void showList(boolean setVisibleFlag) {
        setVisible(setVisibleFlag);
    }


    class NodeListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            GraphNode node = (GraphNode) value;
            String s = node.getName();
//            s = s + "   (" + node.getBranchNodesNum() + ")";
            setText(s);
            setToolTipText("number of all children: ???? not implemented yet..");
            return this;
        }
    }

}
