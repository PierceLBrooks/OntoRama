package ontorama.views.textDescription.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ontorama.OntoramaConfig;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.model.tree.events.NodeClonesRequestEvent;
import ontorama.model.graph.Graph;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.GraphView;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * Description view will display any additional
 * information for a focused node that is not displayed in 
 * the graph/tree views, such as synonyms, creator, clones, etc.
 * Currently there are a couple ways to specify what should 
 * be displayed in the description panel:
 * - edge types have an attribute displayInDescriptionWindow,
 * if this attribute is set to be true - nodes related
 * to focused node via edges of this type will be displayed
 * in the description window.
 * - some other properties can be hard coded into this class,
 * we done so for clones and full url's for now.
 *  
 * Copyright:  Copyright (c) 2001 Company:      DSTC
 */
@SuppressWarnings("serial")
public class DescriptionView extends JPanel implements GraphView {

    /**
     * Keys - name of edge type 
     * Values - panel
     */
    Hashtable<String, AbstractPropertiesPanel> _nodePropertiesPanels = new Hashtable<String, AbstractPropertiesPanel>();
    
    /**
     * list edge type names so we know the order in which to 
     * add them to the ui
     */
    List<String> _edgeTypeNames = new ArrayList<String>();

    /**
     * string that will appear on the label corresponding to clones
     */
    String _clonesLabelName = "Clones";
    
    /**
     * string that will appear on the label corresponding to node identifier
     */
    private String _fullUrlPropName = "Full Url ";
    
    private String _descriptionLabelName = "Description";

    /**
     * optimal dimension for all label names to fit in
     */
    private Dimension _propertyNameLabelsDimension;

    private EventBroker _eventBroker;
    
    private Graph _graph;
    
    private class NodeClonesRequestEventHandler implements EventBrokerListener {
    	
		/**
		 * Constructor for NodeClonesRequestEventHandler.
		 */
		public NodeClonesRequestEventHandler(EventBroker eventBroker) {
			eventBroker.subscribe(this, NodeClonesRequestEvent.class, Object.class);
		}

    	/**
		 * @see org.tockit.events.EventBrokerListener#processEvent(org.tockit.events.Event)
		 */
		@SuppressWarnings("unchecked")
		public void processEvent(Event event) {
			List<Object> clones = (List<Object>) event.getSubject();
			AbstractPropertiesPanel clonesPanel = _nodePropertiesPanels.get(_clonesLabelName);
			clonesPanel.update(clones);
		}
	}

    public DescriptionView( EventBroker eventBroker) {

        _eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);
        new NodeClonesRequestEventHandler(eventBroker);
        
        initPropertiesPanels();

        _propertyNameLabelsDimension = calcLabelSize();
        setLabelSizesForNodePropertiesPanels();

        setLayout(new GridLayout(1, 2));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));


        JPanel leftSubPanel = new JPanel();
        leftSubPanel.setLayout(new BoxLayout(leftSubPanel, BoxLayout.Y_AXIS));
        leftSubPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel rightSubPanel = new JPanel();
        rightSubPanel.setLayout(new BoxLayout(rightSubPanel, BoxLayout.Y_AXIS));
        rightSubPanel.setBorder(BorderFactory.createEtchedBorder());

        // add panels to the ui
        Iterator<String> edgeTypeNamesIterator = _edgeTypeNames.iterator();
        int size = _nodePropertiesPanels.size();
        int halfSize = size/2;
        int count = 0;
        while (edgeTypeNamesIterator.hasNext()) {
        	String propName = edgeTypeNamesIterator.next();
            JPanel propPanel = _nodePropertiesPanels.get(propName);
            if (count < halfSize) {
            	leftSubPanel.add(propPanel);
            }
            else {
            	rightSubPanel.add(propPanel);
            }
            count++;
        }
        add(leftSubPanel);
        add(rightSubPanel);
    }


    /**
     * Initialize concept properties panels
     */
    private void initPropertiesPanels() {

        List<EdgeType> edgeTypesToDisplay = new ArrayList<EdgeType>();
        List<EdgeType> edgeTypesList = OntoramaConfig.getEdgeTypesList();
        Iterator<EdgeType> it = edgeTypesList.iterator();
        while (it.hasNext()) {
            EdgeType edgeType = it.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if ((displayInfo.isDisplayInDescription()) || (displayInfo.isDisplayReverseEdgeInDescription()) ) {
                edgeTypesToDisplay.add(edgeType);
                AbstractPropertiesPanel propPanel;
                if (displayInfo.isQueryOn()) {
                    propPanel = new MultiValuesPanel(displayInfo.getDisplayLabel(), _eventBroker);
                }
                else {
                    propPanel = new NodePropertiesPanel(displayInfo.getDisplayLabel(), new ArrayList<Object>());
                }
                if (displayInfo.isDisplayInDescription()) {
                    _nodePropertiesPanels.put(edgeType.getName(), propPanel);
                    _edgeTypeNames.add(edgeType.getName());
                }
                if (displayInfo.isDisplayReverseEdgeInDescription()) {
                    _nodePropertiesPanels.put(edgeType.getReverseEdgeName(), propPanel);
                    _edgeTypeNames.add(edgeType.getReverseEdgeName());
                }
            }
        }
        _nodePropertiesPanels.put(_clonesLabelName, new ClonesPanel(_clonesLabelName, _eventBroker));
        _edgeTypeNames.add(_clonesLabelName);
		_nodePropertiesPanels.put(_descriptionLabelName, new NodePropertiesPanel(_descriptionLabelName, new ArrayList<Object>()));
		_edgeTypeNames.add(_descriptionLabelName);
    	_nodePropertiesPanels.put(_fullUrlPropName, new NodePropertiesPanel(_fullUrlPropName, new ArrayList<Object>()));
    	_edgeTypeNames.add(_fullUrlPropName);
    }

    /**
     * find max label width
     */
    private int getMaxLabelWidth() {
        Iterator<AbstractPropertiesPanel> it = _nodePropertiesPanels.values().iterator();
        int length = 0;
        while (it.hasNext()) {
            AbstractPropertiesPanel curPanel = it.next();
            int width = curPanel.getPropNameLabelWidth();
            if (width > length) {
                length = width;
            }
        }
        return length;
    }

    private void setLabelSizesForNodePropertiesPanels() {
        Iterator<AbstractPropertiesPanel> it = _nodePropertiesPanels.values().iterator();
        while (it.hasNext()) {
            AbstractPropertiesPanel curPanel = it.next();
            curPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
        }
    }

    private Dimension calcLabelSize() {
        int padding = 5;
        int maxSize = getMaxLabelWidth() + padding;
        Iterator<AbstractPropertiesPanel> it = _nodePropertiesPanels.values().iterator();
        if (it.hasNext()) {
            AbstractPropertiesPanel panel = it.next();
            return (new Dimension(maxSize, panel.getPropNameLabelHeight()));
        }
        return new Dimension(50, 20);
    }

    /**
     * clear description value panel from any leftover properties (left from previous example)
     */
    public void clear() {
        Enumeration<String> e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String propertyName = e.nextElement();
            AbstractPropertiesPanel propPanel =
                    _nodePropertiesPanels.get(propertyName);
            propPanel.clear();
        }
    }

    //////////////////////////ViewEventObserver interface implementation////////////////

    public void focus(Node node) {
        Enumeration<String> e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String edgeName = e.nextElement();
        	AbstractPropertiesPanel propPanel = _nodePropertiesPanels.get(edgeName);
        	List<Object> value = new ArrayList<Object>();
            try {
                EdgeType edgeType = OntoramaConfig.getEdgeType(edgeName);
                EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
                if (displayInfo.isDisplayInDescription()) {
                    value = _graph.getOutboundEdgeNodesByType(node, edgeType);
                }
                else if (displayInfo.isDisplayReverseEdgeInDescription()) {
                    value = _graph.getInboundEdgeNodesByType(node, edgeType);
                }
                propPanel.update(value);
            } catch (NoSuchRelationLinkException exc) {
                // this exception should have been caught when building the graph
                // we are displaying, so it should be safe to ignore it here
                // more then that - we are displaying 'fake' edges in this panel,
                // such as clones and full url. so instead of throwing exception - 
                // we know that we have to deal with these 'fake' edges.
                if ( (edgeName.equals(_clonesLabelName)) || (edgeName.equals(_fullUrlPropName))
                						|| (edgeName.equals(_descriptionLabelName)) ) {
                	if (edgeName.equals(_descriptionLabelName)) {
						AbstractPropertiesPanel descrPanel = _nodePropertiesPanels.get(_descriptionLabelName);
						value.add(node.getDescription());
						descrPanel.update(value);
                	}
                	else if (!edgeName.equals(_clonesLabelName)){
						// NodeClonesRequestEventHandler takes care of case if we are 
						// processing clones.
                		value.add(node.getIdentifier());
                		propPanel.update(value);
                	}
                }
                else {
                	System.err.println("NoSuchRelationLinkException exception: " + exc);
                	exc.printStackTrace();
                }
            }
        }
    }


    public void setGraph (Graph graph) {
        _graph = graph;
    }


    public void repaint () {
        super.repaint();
    }
}
