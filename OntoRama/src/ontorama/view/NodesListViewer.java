package ontorama.view;

import ontorama.model.GraphNode;
import ontorama.controller.QueryEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;

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

        Vector nodesVector = new Vector(sortList(nodes));
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

    /**
     * sort list of nodes in ascending alphabetic order
     * (algorithm is taken from the website http://www.bus.utexas.edu/~plummer/sorting.ppt )
     * @param nodesList list of nodes to sort
     * @return sorted list of nodes
     */
    private List sortList(List nodesList)  {
        int j;
        boolean atLeastOneSwap=true;
        while(atLeastOneSwap) {
            atLeastOneSwap = false;
            for(j=0; j < (nodesList.size()-1); ++j) {
                GraphNode curNode = (GraphNode) nodesList.get(j);
                GraphNode nextNode = (GraphNode) nodesList.get(j+1);

                String curNodeName = curNode.getName();
                String nextNodeName = nextNode.getName();

                if(curNodeName.compareToIgnoreCase(nextNodeName) >  0 ) {    // ascending sort
                    nodesList.set(j, nextNode);
                    nodesList.set(j+1, curNode);
                    atLeastOneSwap = true;
                }
            }
        }
        return nodesList;
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
