package ontorama.view;

import ontorama.controller.QueryEvent;
import ontorama.model.GraphNode;
import org.tockit.events.EventBroker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author nataliya
 *
 * Display selectable list of nodes
 */
public class NodesListViewer extends JComboBox {

    /**
     * list of currently displayed nodes
     */
    private List _nodesList = new LinkedList();

    /**
     * event broker
     */
    private EventBroker _eventBroker;

    /**
     * default heading/explanation string that should
     * always be first in the pull-down list.
     */
    private String _defaultHeadingString = "Unconnected Nodes List";

    /**
     * Viewer to display list of unconnected nodes for current graph
     */
    public NodesListViewer(EventBroker eventBroker)  {
        super();
        _eventBroker = eventBroker;
        _nodesList = new LinkedList();

        addItem(_defaultHeadingString);
        setSelectedIndex(0);
        setListViewSize();

        addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String selectedNodeName = (String) cb.getSelectedItem();
                nodeIsSelectedAction(selectedNodeName);
            }
        });
        setListSizeDependantProperties();
    }

    /**
     * set new list of nodes
     */
    public void setNodesList (List nodes) {
        _nodesList = nodes;

        removeAllItems();
        addItem(_defaultHeadingString);

        setListSizeDependantProperties();

        Iterator it = sortList().iterator();
        while (it.hasNext()) {
            String curName = (String) it.next();
            addItem(curName);
        }
    }

    /**
     *
     */
    private void setListSizeDependantProperties() {
        if (_nodesList.size() == 0) {
            setEnabled(false);
            setToolTipText("No unconnected nodes found in this ontology");
        }
        else {
            setEnabled(true);
            setToolTipText("Click here to see list of nodes not reachable from root node via relation links");
        }
    }

    /**
     *
     */
    private void setListViewSize () {
        Font font = getFont();
        FontMetrics fontMetrics = getFontMetrics(font);
        int strWidth = fontMetrics.stringWidth(_defaultHeadingString);
        int padding = 5;
        Dimension d = new Dimension(strWidth + padding*2, fontMetrics.getHeight() + padding*2);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    /**
     * things we need to do when one of list items is selected.
     * @param selectedNodeName
     */
    private void nodeIsSelectedAction(String selectedNodeName) {
        if (selectedNodeName == _defaultHeadingString) {
            return;
        }

        GraphNode selectedNode = null;
        Iterator it = _nodesList.iterator();
        while (it.hasNext()) {
            GraphNode node = (GraphNode) it.next();
            if (node.getName().equals(selectedNodeName)) {
                selectedNode = node;
            }
        }
        _eventBroker.processEvent(new QueryEvent(selectedNode));
    }

    /**
     * sort list of nodes in ascending alphabetic order
     * (algorithm is taken from the website http://www.bus.utexas.edu/~plummer/sorting.ppt )
     * @return sorted list of nodes
     */
    private List sortList()  {
        int j;
        List nodeNamesList = new LinkedList();
        boolean atLeastOneSwap=true;
        while(atLeastOneSwap) {
            atLeastOneSwap = false;
            for(j=0; j < (_nodesList.size()-1); ++j) {
                GraphNode curNode = (GraphNode) _nodesList.get(j);
                GraphNode nextNode = (GraphNode) _nodesList.get(j+1);

                String curNodeName = curNode.getName();
                String nextNodeName = nextNode.getName();

                if(curNodeName.compareToIgnoreCase(nextNodeName) >  0 ) {    // ascending sort
                    _nodesList.set(j, nextNode);
                    _nodesList.set(j+1, curNode);
                    atLeastOneSwap = true;
                }
            }
        }

        Iterator it = _nodesList.iterator();
        while (it.hasNext()) {
            GraphNode node = (GraphNode) it.next();
            nodeNamesList.add(node.getName());
        }
        return nodeNamesList;
    }
}
