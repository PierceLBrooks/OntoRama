package ontorama.view.textDescription.view;

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
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.model.graph.view.GraphView;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.Node;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.ontotools.NoSuchRelationLinkException;
import org.tockit.events.EventBroker;

/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

public class DescriptionView extends JPanel implements GraphView {

    /**
     * this hashtable will hold labels of concept property names as keys
     * and labels of corresponding values for a node as values
     */
    //private Hashtable labels = new Hashtable();

    /**
     * Keys - name of property
     * Values - panel
     */
    Hashtable _nodePropertiesPanels = new Hashtable();

    /**
     *
     */
    private ClonesPanel _clonesPanel;

    String _clonesLabelName = "Clones";

    private String _fullUrlPropName = "Full Url ";

    /**
     * @todo  think of a way to not hardcode _parents
     *
     * @todo	shouldn't hardcode parent relation, see line
     * because if someone changed config file for supertype/subtype relation to
     * be some other integer - this won't work. there is also other scenarios for
     * failure. maybe should have some rules for constructing config.xml file
     * or some other way to tell which relation is supertype/subtype.
     *
     */
    private String _reverseRelationLinkName;
    private ontorama.model.graph.EdgeType _firstRelationLink = (ontorama.model.graph.EdgeType) OntoramaConfig.getEdgeTypesList().get(0);

    /**
     *
     */
    private ParentsPanel _parentsPanel;

    /**
     *
     */
    private NodePropertiesPanel _fullUrlPanel;

    /**
     *
     */
    private Dimension _propertyNameLabelsDimension;

    /**
     *
     */
    private EventBroker _eventBroker;

    private ontorama.model.graph.Graph _graph;

    /**
     *
     */
    public DescriptionView( EventBroker eventBroker) {

        _eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);

        initReverseRelation();

        initPropertiesPanels();
        System.out.println("1");
        _fullUrlPanel =
                new NodePropertiesPanel(_fullUrlPropName, new LinkedList());
        System.out.println("2");
        _clonesPanel = new ClonesPanel(_clonesLabelName, _eventBroker);
        System.out.println("3");
        _parentsPanel = new ParentsPanel(_reverseRelationLinkName, _eventBroker);
        System.out.println("4");

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
        Enumeration propPanelsEnum = _nodePropertiesPanels.keys();
        while (propPanelsEnum.hasMoreElements()) {
            String propName = (String) propPanelsEnum.nextElement();
            JPanel propPanel = (JPanel) _nodePropertiesPanels.get(propName);
            leftSubPanel.add(propPanel);
        }

        rightSubPanel.add(_clonesPanel);
        rightSubPanel.add(_parentsPanel);
        rightSubPanel.add(_fullUrlPanel);

        add(leftSubPanel);
        add(rightSubPanel);
    }


    /**
     *
     */
    private void initReverseRelation() {
        _reverseRelationLinkName = _firstRelationLink.getReverseEdgeName();
    }

    /**
     * initialise concept properties panels
     */
    private void initPropertiesPanels() {

        List edgeTypesToDisplay = new LinkedList();
        List edgeTypesList = OntoramaConfig.getEdgeTypesList();
        Iterator it = edgeTypesList.iterator();
        while (it.hasNext()) {
            ontorama.model.graph.EdgeType edgeType = (ontorama.model.graph.EdgeType) it.next();
            EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
            if (displayInfo.isDisplayInDescription()) {
                edgeTypesToDisplay.add(edgeType);
                NodePropertiesPanel propPanel =
                        new NodePropertiesPanel(displayInfo.getDisplayLabel(), new LinkedList());
                _nodePropertiesPanels.put(edgeType.getName(), propPanel);
                //_nodePropertiesPanels.put(displayInfo.getDisplayLabel(), propPanel);
            }
            if (displayInfo.isDisplayReverseEdgeInDescription()) {
                edgeTypesToDisplay.add(edgeType);
                NodePropertiesPanel propPanel =
                        new NodePropertiesPanel(displayInfo.getDisplayLabel(), new LinkedList());
                _nodePropertiesPanels.put(edgeType.getReverseEdgeName(), propPanel);
                //_nodePropertiesPanels.put(displayInfo.getDisplayLabel(), propPanel);
            }
        }
    }

    /**
     * find max label width
     */
    private int getMaxLabelWidth() {
        Iterator it = _nodePropertiesPanels.values().iterator();
        //Enumeration e = labels.keys();
        int length = 0;
        while (it.hasNext()) {
            NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
            //JLabel curLabel = curPanel.getPropNameLabel();
            //JLabel curLabel = (JLabel) e.nextElement();
            int width = curPanel.getPropNameLabelWidth();
            if (width > length) {
                length = width;
            }
        }
        int clonesWidth = _clonesPanel.getPropNameLabelWidth();
        if (clonesWidth > length) {
            length = clonesWidth;
        }
        int parentsWidth = _parentsPanel.getPropNameLabelWidth();
        if (parentsWidth > length) {
            length = parentsWidth;
        }
        int fullUrlWidth = _fullUrlPanel.getPropNameLabelWidth();
        if (fullUrlWidth > length) {
            length = fullUrlWidth;
        }
        return length;
    }

    /**
     *
     */
    private void setLabelSizesForNodePropertiesPanels() {
        Iterator it = _nodePropertiesPanels.values().iterator();
        while (it.hasNext()) {
            NodePropertiesPanel curPanel = (NodePropertiesPanel) it.next();
            curPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
        }
        _clonesPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
        _parentsPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
        _fullUrlPanel.setPropNameLabelWidth(_propertyNameLabelsDimension);
    }

    /**
     *
     */
    private Dimension calcLabelSize() {
        int padding = 5;
        int maxSize = getMaxLabelWidth() + padding;
        Iterator it = _nodePropertiesPanels.values().iterator();
        if (it.hasNext()) {
            NodePropertiesPanel panel = (NodePropertiesPanel) it.next();
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
            NodePropertiesPanel propPanel =
                    (NodePropertiesPanel) _nodePropertiesPanels.get(propertyName);
            propPanel.clear();
        }
        _clonesPanel.clear();
        _parentsPanel.clear();
    }

    /**
     *
     */
    public void enableDynamicFields() {
        _parentsPanel.setVisible(true);
    }

    /**
     *
     */
    public void disableDynamicFields() {
        _parentsPanel.setVisible(false);
    }

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     *
     */
    public void focus(ontorama.model.graph.Node node) {
        Enumeration e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String edgeName = (String) e.nextElement();
            try {
                NodePropertiesPanel propPanel = (NodePropertiesPanel) _nodePropertiesPanels.get(edgeName);
                List value = new LinkedList();
                ontorama.model.graph.EdgeType edgeType = OntoramaConfig.getEdgeType(edgeName);
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
                System.err.println("NoSuchRelationLinkException exception: " + exc);
                exc.printStackTrace();
            }
        }
        // deal with clones
        _clonesPanel.update(node.getClones().iterator());

        List fullUrlPropList = new LinkedList();
        fullUrlPropList.add(node.getIdentifier());
        _fullUrlPanel.update(fullUrlPropList);
        _parentsPanel.update(_graph.getInboundEdgeNodes(node, _firstRelationLink).iterator());
    }


    public void setGraph (ontorama.model.graph.Graph graph) {
        _graph = graph;
    }


    public void repaint () {
        super.repaint();
    }



}
