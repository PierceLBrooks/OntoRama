package ontorama.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import ontorama.OntoramaConfig;
import ontorama.controller.GeneralQueryEvent;
import ontorama.model.Graph;
import ontorama.model.GraphImpl;
import ontorama.model.Node;
import ontorama.model.NodeType;
import ontorama.ontologyConfig.ImageMaker;
import ontorama.webkbtools.query.Query;
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
                Object selectedObject = cb.getSelectedItem();
                if (selectedObject instanceof String) {
                    return;
                }
                Node selectedNode = (Node) selectedObject;
                System.out.println("\n\nsending new QueryEvent");
                _eventBroker.processEvent(new GeneralQueryEvent(new Query(selectedNode.getName())));
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

        private Hashtable _nodeTypeToImageMapping = new Hashtable();
        private ImageIcon _defaultImage;
        private ImageIcon _noImage;

        public ListRenderer() {
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);

            /// @todo copied this from ontotree renderer. need a unified way to get these
            // images from all viewers.
            int iconW = ImageMaker.getWidth();
            int iconH = ImageMaker.getHeight();

            Iterator nodeTypesIterator = OntoramaConfig.getNodeTypesList().iterator();
            while (nodeTypesIterator.hasNext()) {
                NodeType nodeType = (NodeType) nodeTypesIterator.next();
                Color color = OntoramaConfig.getNodeTypeDisplayInfo(nodeType).getColor();
                ImageIcon image = makeNodeIcon(iconW/2, iconH, color, Color.black, nodeType);
                _nodeTypeToImageMapping.put(nodeType, image);
            }
            /// @todo hack shouldn't pass null
            //_defaultImage = makeNodeIcon(iconW/2, iconH, Color.white, Color.black, null);
            _noImage = makeNodeIcon(iconW/2, iconH, getBackground(), getBackground(), null);
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
                    ImageIcon image = (ImageIcon) _nodeTypeToImageMapping.get(nodeType);
                    setIcon(image);
                    setToolTipText(nodeType.getNodeType());
                }
                else {
                    setIcon(_noImage);
                }
            }
            return this;
        }
    }

    private ImageIcon makeNodeIcon(int width, int height, Color color, Color outlineColor, NodeType nodeType) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();

        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);
        g2.drawRect(0, 0, width, height);

        int ovalSize = width - (width * 12) / 100;
        int ovalX = 0;
        int ovalY = (height - ovalSize) / 2;
        g2.setColor(color);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (nodeType == null) {
            /// @todo this check for null is a hack. have to change all following
            // if's and else's to a meaninfull  flow.
            Ellipse2D circle = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
            g2.fill(circle);
            g2.setColor(outlineColor);
            g2.draw(circle);
        }
        else {
            // @todo shouldn't hardcode string 'concept' here.
            if (nodeType.getNodeType().equals("concept")) {
                Ellipse2D circle = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
                g2.fill(circle);
                g2.setColor(outlineColor);
                g2.draw(circle);
            }
            else {
                int x[] = {0, 0, width};
                int y[] = {0, height -1, height -1};
                Polygon polygon = new Polygon(x, y, 3);
                g2.fill(polygon);
                g2.setColor(outlineColor);
                g2.draw(polygon);
            }
        }

//        Ellipse2D circle = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
//        g2.fill(circle);
//        g2.setColor(outlineColor);
//        g2.draw(circle);

        return (new ImageIcon(image));
    }



}
