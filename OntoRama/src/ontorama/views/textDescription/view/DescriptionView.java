package ontorama.views.textDescription.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ontorama.OntoramaConfig;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.model.graph.view.GraphView;
import ontorama.model.tree.events.NodeClonesRequestEvent;
import ontorama.model.graph.Graph;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.NoSuchRelationLinkException;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * Title:        OntoRama
 * Description:  Description view will display any additional
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
 * @author
 * @version 1.0
 */

public class DescriptionView extends JPanel implements GraphView {

    /**
     * Keys - name of edge type 
     * Values - panel
     */
    Hashtable _nodePropertiesPanels = new Hashtable();
    
    /**
     * list edge type names so we know the order in which to 
     * add them to the ui
     */
    List _edgeTypeNames = new LinkedList();

    /**
     * string that will appear on the label corresponding to clones
     */
    String _clonesLabelName = "Clones";
    
    /**
     * string that will appear on the label corresponding to node identifier
     */
    private String _fullUrlPropName = "Full Url ";

    /**
     * optimal dimension for all label names to fit in
     */
    private Dimension _propertyNameLabelsDimension;

    /**
     *
     */
    private EventBroker _eventBroker;
    
    /**
     * 
     */
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
		public void processEvent(Event event) {
			List clones = (List) event.getSubject();
			AbstractPropertiesPanel clonesPanel = (AbstractPropertiesPanel) _nodePropertiesPanels.get(_clonesLabelName);
			clonesPanel.update(clones);
		}
	}

    /**
     *
     */
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
        Iterator edgeTypeNamesIterator = _edgeTypeNames.iterator();
        int size = _nodePropertiesPanels.size();
        int halfSize = size/2;
        int count = 0;
        while (edgeTypeNamesIterator.hasNext()) {
        	String propName = (String) edgeTypeNamesIterator.next();
            JPanel propPanel = (JPanel) _nodePropertiesPanels.get(propName);
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
     * initialise concept properties panels
     */
    private void initPropertiesPanels() {

        List edgeTypesToDisplay = new LinkedList();
        List edgeTypesList = OntoramaConfig.getEdgeTypesList();
        Iterator it = edgeTypesList.iterator();
        while (it.hasNext()) {
            EdgeType edgeType = (EdgeType) it.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if ((displayInfo.isDisplayInDescription()) || (displayInfo.isDisplayReverseEdgeInDescription()) ) {
                edgeTypesToDisplay.add(edgeType);
                AbstractPropertiesPanel propPanel;
                if (displayInfo.isQueryOn()) {
                    propPanel = new MultiValuesPanel(displayInfo.getDisplayLabel(), _eventBroker);
                }
                else {
                    propPanel = new NodePropertiesPanel(displayInfo.getDisplayLabel(), new LinkedList());
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
    	_nodePropertiesPanels.put(_fullUrlPropName, new NodePropertiesPanel(_fullUrlPropName, new LinkedList()));
    	_edgeTypeNames.add(_fullUrlPropName);
    }

    /**
     * find max label width
     */
    private int getMaxLabelWidth() {
        Iterator it = _nodePropertiesPanels.values().iterator();
        int length = 0;
        while (it.hasNext()) {
            AbstractPropertiesPanel curPanel = (AbstractPropertiesPanel) it.next();
            int width = curPanel.getPropNameLabelWidth();
            if (width > length) {
                length = width;
            }
        }
        return length;
    }

    /**
     *
     */
    private void setLabelSizesForNodePropertiesPanels() {
        Iterator it = _nodePropertiesPanels.values().iterator();
        while (it.hasNext()) {
            AbstractPropertiesPanel curPanel = (AbstractPropertiesPanel) it.next();
            curPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
        }
    }

    /**
     *
     */
    private Dimension calcLabelSize() {
        int padding = 5;
        int maxSize = getMaxLabelWidth() + padding;
        Iterator it = _nodePropertiesPanels.values().iterator();
        if (it.hasNext()) {
            AbstractPropertiesPanel panel = (AbstractPropertiesPanel) it.next();
            return (new Dimension(maxSize, panel.getPropNameLabelHeight()));
        }
        return new Dimension(50, 20);
    }

    /**
     * clear description value panel from any leftover properties (left from previous example)
     */
    public void clear() {
        Enumeration e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            AbstractPropertiesPanel propPanel =
                    (AbstractPropertiesPanel) _nodePropertiesPanels.get(propertyName);
            propPanel.clear();
        }
    }

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     *
     */
    public void focus(Node node) {
        Enumeration e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String edgeName = (String) e.nextElement();
        	AbstractPropertiesPanel propPanel = (AbstractPropertiesPanel) _nodePropertiesPanels.get(edgeName);
        	List value = new LinkedList();
            try {
                EdgeType edgeType = OntoramaConfig.getEdgeType(edgeName);
                EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
                if (displayInfo.isDisplayInDescription()) {
                    value = _graph.getOutboundEdgeNodes(node, edgeType);
                }
                else if (displayInfo.isDisplayReverseEdgeInDescription()) {
                    value = _graph.getInboundEdgeNodes(node, edgeType);
                }
                propPanel.update(value);
            } catch (NoSuchRelationLinkException exc) {
                // this exception should have been caught when building the graph
                // we are displaying, so it should be safe to ignore it here
                // more then that - we are displaying 'fake' edges in this panel,
                // such as clones and full url. so instead of throwing exception - 
                // we know that we have to deal with these 'fake' edges.
                // @todo sounds like a hack to me (see comments above)
                if ( (edgeName.equals(_clonesLabelName)) || (edgeName.equals(_fullUrlPropName)) ) {
                	if (edgeName.equals(_clonesLabelName)) {
                		// NodeClonesRequestEventHandler takes care of this
                	}
                	else {
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