package ontorama.textDescription.view;

import ontorama.OntoramaConfig;
import ontorama.graph.controller.GraphViewFocusEventHandler;
import ontorama.graph.view.GraphView;
import ontorama.model.Edge;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphImpl;
import ontorama.ontologyConfig.RelationLinkDetails;
import ontorama.webkbtools.util.NoSuchPropertyException;
import org.tockit.events.EventBroker;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

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

    /**
     * @todo  think of a way to not hardcode clones
     */
    String _clonesLabelName = "Clones";

    /**
     * @todo  think of a way to not hardcode _fullUrlPropName
     */
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
    private RelationLinkDetails _firstRelationLink = OntoramaConfig.getRelationLinkDetails()[1];

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

    private Graph _graph;

    /**
     *
     */
    public DescriptionView( EventBroker eventBroker) {

        _eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);

        initReverseRelation();

        initPropertiesPanels();
        _fullUrlPanel =
                new NodePropertiesPanel(_fullUrlPropName, new LinkedList());
        _clonesPanel = new ClonesPanel(_clonesLabelName, _eventBroker);
        _parentsPanel = new ParentsPanel(_reverseRelationLinkName, _eventBroker);

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

        // add panels to the view
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
        _reverseRelationLinkName = _firstRelationLink.getReversedLinkName();
    }

    /**
     * initialise concept properties panels
     */
    private void initPropertiesPanels() {
        Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
        while (e.hasMoreElements()) {
            String propName = (String) e.nextElement();
            NodePropertiesPanel propPanel =
                    new NodePropertiesPanel(propName, new LinkedList());
            _nodePropertiesPanels.put(propName, propPanel);
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
//			JLabel curLabel = curPanel.getPropNameLabel();
//			curLabel.setSize(_propertyNameLabelsDimension);
//			curLabel.setMinimumSize(_propertyNameLabelsDimension);
//			curLabel.setMaximumSize(_propertyNameLabelsDimension);
//			curLabel.setPreferredSize(_propertyNameLabelsDimension);
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
    public void focus(GraphNode node) {
        //System.out.println("description view: focus()");
        Enumeration e = _nodePropertiesPanels.keys();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            try {
                NodePropertiesPanel propPanel =
                        (NodePropertiesPanel) _nodePropertiesPanels.get(
                                propertyName);
                propPanel.update(node.getProperty(propertyName));
            } catch (NoSuchPropertyException exc) {
                // this exception should have been caught when building the graph
                // we are displaying, so it should be safe to ignore it here
                System.err.println("NoSuchPropertyException exception: " + exc);
            }
        }
        // deal with clones
        _clonesPanel.update(node.getClones());

        List fullUrlPropList = new LinkedList();
        fullUrlPropList.add(node.getFullName());
        _fullUrlPanel.update(fullUrlPropList);
        _parentsPanel.update(_graph.getInboundEdgeNodes(node, _firstRelationLink));
    }


    public void setGraph (Graph graph) {
        _graph = graph;
    }


    public void repaint () {
        super.repaint();
    }



}
