package ontorama.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.conf.NodeTypeDisplayInfo;
import ontorama.ui.events.GeneralQueryEvent;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphImpl;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeType;
import ontorama.ontotools.query.Query;
import org.tockit.events.EventBroker;

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

    private Graph _graph;

    /**
     * event broker
     */
    private EventBroker _eventBroker;

    /**
     * default heading/explanation string that should
     * always be first in the pull-down list.
     */
    private String _defaultHeadingString = "Component List";

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
                Object selectedObject = cb.getSelectedItem();
                if (selectedObject instanceof String) {
                    return;
                }
                ontorama.model.graph.Node selectedNode = (ontorama.model.graph.Node) selectedObject;
                System.out.println("\n\nsending new QueryNodeEvent");
                Backend backend = OntoramaConfig.getBackend();
                Query newQuery = new Query(selectedNode.getName(),backend.getSourcePackageName(), backend.getParser(), backend.getSourceUri());
                _eventBroker.processEvent(new GeneralQueryEvent(newQuery));
            }
        });
        setListSizeDependantProperties();
        ListRenderer renderer = new ListRenderer();
        setRenderer(renderer);
    }

    /**
     * set new list of nodes
     */
    public void setGraph (Graph graph) {
        _graph = graph;
        _nodesList = graph.getUnconnectedNodesList();
        removeAllItems();
        addItem(_defaultHeadingString);
        setListSizeDependantProperties();
        sortList();
        Iterator it = _nodesList.iterator();
        while (it.hasNext()) {
            Node curNode = (Node) it.next();
            addItem(curNode);
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
        int paddingW = 30;
        int paddingH = 5;
        Dimension d = new Dimension(strWidth + paddingW*2, fontMetrics.getHeight() + paddingH*2);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    /**
     * sort list of nodes in ascending alphabetic order
     * (algorithm is taken from the website http://www.bus.utexas.edu/~plummer/sorting.ppt )
     */
    private void sortList()  {
        int j;
        boolean atLeastOneSwap=true;
        while(atLeastOneSwap) {
            atLeastOneSwap = false;
            for(j=0; j < (_nodesList.size()-1); ++j) {
                Node curNode = (Node) _nodesList.get(j);
                Node nextNode = (Node) _nodesList.get(j+1);

                String curNodeName = curNode.getName();
                String nextNodeName = nextNode.getName();

                if(curNodeName.compareToIgnoreCase(nextNodeName) >  0 ) {    // ascending sort
                    _nodesList.set(j, nextNode);
                    _nodesList.set(j+1, curNode);
                    atLeastOneSwap = true;
                }
            }
        }
    }

    class ListRenderer extends JLabel implements ListCellRenderer {

        private ImageIcon _defaultImage;
        private ImageIcon _noImage;

        public ListRenderer() {
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }


        public Component getListCellRendererComponent(
                                        JList list,
                                        Object value,
                                        int index,
                                        boolean isSelected,
                                        boolean cellHasFocus) {


            if (value instanceof String ) {
                String valueStr = (String) value;
                setText(valueStr);
                setToolTipText(valueStr);
                setIcon(_noImage);
                return this;
            }
            else {
                Node node = (Node) value;
                NodeType nodeType = node.getNodeType();
                setText(node.getName());
                // @todo hack: should consider adding the method into interface
                if (_graph instanceof GraphImpl) {
                    GraphImpl graphImpl = (GraphImpl) _graph;
                    int numOfDescendants = graphImpl.getNumOfDescendants(node);
                    setText(node.getName() + " (" + numOfDescendants + ")");
                }
                if (nodeType != null) {
                	NodeTypeDisplayInfo displayInfo = OntoramaConfig.getNodeTypeDisplayInfo(nodeType);
                    setIcon(new ImageIcon(displayInfo.getImage()));
                    setToolTipText(displayInfo.getName());
                }
                else {
                    setIcon(_noImage);
                }
            }
            return this;
        }
    }
}
