package ontorama.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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
import ontorama.conf.ImageMaker;
import ontorama.ui.events.GeneralQueryEvent;
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

    private ontorama.model.graph.Graph _graph;

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
    public void setGraph (ontorama.model.graph.Graph graph) {
        _graph = graph;
        _nodesList = graph.getUnconnectedNodesList();
        removeAllItems();
        addItem(_defaultHeadingString);
        setListSizeDependantProperties();
        sortList();
        Iterator it = _nodesList.iterator();
        while (it.hasNext()) {
            ontorama.model.graph.Node curNode = (ontorama.model.graph.Node) it.next();
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
                ontorama.model.graph.Node curNode = (ontorama.model.graph.Node) _nodesList.get(j);
                ontorama.model.graph.Node nextNode = (ontorama.model.graph.Node) _nodesList.get(j+1);

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
                ontorama.model.graph.NodeType nodeType = (ontorama.model.graph.NodeType) nodeTypesIterator.next();
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
                ontorama.model.graph.Node node = (ontorama.model.graph.Node) value;
                ontorama.model.graph.NodeType nodeType = node.getNodeType();
                setText(node.getName());
                // @todo hack: should consider adding the method into interface
                if (_graph instanceof ontorama.model.graph.GraphImpl) {
                    ontorama.model.graph.GraphImpl graphImpl = (ontorama.model.graph.GraphImpl) _graph;
                    int numOfDescendants = graphImpl.getNumOfDescendants(node);
                    setText(node.getName() + " (" + numOfDescendants + ")");
                }
                if (nodeType != null) {
                    ImageIcon image = (ImageIcon) _nodeTypeToImageMapping.get(nodeType);
                    setIcon(image);
                    setToolTipText(nodeType.getDisplayName());
                }
                else {
                    setIcon(_noImage);
                }
            }
            return this;
        }
    }

    private ImageIcon makeNodeIcon(int width, int height, Color color, Color outlineColor, ontorama.model.graph.NodeType nodeType) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);
        g2.drawRect(0, 0, width, height);

        g2.setColor(color);

        Shape displayShape;
        if(nodeType == null) {
            /// @todo this check for null is a hack. have to change all following
            // if's and else's to a meaninfull  flow.
            int ovalSize = width - (width * 12) / 100;
            int ovalX = 0;
            int ovalY = (height - ovalSize) / 2;
            displayShape = new Ellipse2D.Double(ovalX, ovalY, ovalSize, ovalSize);
        }
        else {
            displayShape = nodeType.getDisplayShape();
        }
		Rectangle2D bounds = displayShape.getBounds2D();

        double xOffset = -bounds.getX();
        double yOffset = -bounds.getY();
		double scale;		
        if(bounds.getWidth()/width > bounds.getHeight()/height) {
            scale = width/bounds.getWidth();
            yOffset += (bounds.getHeight() - scale * height)/2;
        } else {
            scale = height/bounds.getHeight();
            xOffset += (bounds.getWidth() - scale * width)/2;
        }
		
		g2.scale(scale,scale);
        g2.translate(xOffset, yOffset);
        g2.fill(displayShape);
        g2.setColor(outlineColor);
        g2.draw(displayShape);

        return (new ImageIcon(image));
    }



}
