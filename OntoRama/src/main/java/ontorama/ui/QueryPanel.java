package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ontorama.OntoramaConfig;
import ontorama.conf.EdgeTypeDisplayInfo;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Graph;
import ontorama.model.graph.GraphView;
import ontorama.model.graph.Node;
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.ontotools.query.Query;
import ontorama.ui.action.QueryAction;
import ontorama.ui.action.StopQueryAction;

import org.tockit.events.EventBroker;

/**
 * QueryPanel is responsible for building an interface for a query that
 * will be used to query an ontology server (using GraphBuilder)
 *
 */
@SuppressWarnings("serial")
public class QueryPanel extends JPanel implements ActionListener, GraphView {

    private int _depth = -1;

	private final JLabel _queryLabel = new JLabel("Search for: ");
    private final JTextField _queryField;
    private final JButton _querySubmitButton;
    private final JButton _queryStopButton;
    private final JTextField _depthField;
    private final JLabel _depthLabel = new JLabel("depth: ");

    private static final String _queryFieldToolTip = "Type query term here";
    private static final String _depthFieldToolTip = "Specify query depth (integer from 1 to 9). This feature is only available for Dynamic sources.";


    private final JPanel _relationLinksPanel = new JPanel();

    /**
     * table of check boxes for relation links
     */
    private final Map<JCheckBox, EdgeType> _relationLinksCheckBoxes = new HashMap<JCheckBox, EdgeType>();

    /**
     * relation links that user has chosen to display
     */
    private List<EdgeType> _wantedRelationLinks = new ArrayList<EdgeType>();

    private final EventBroker _eventBroker;

    public StopQueryAction _stopQueryAction;
    private final QueryAction _queryAction;

    public QueryPanel(EventBroker eventBroker) {
        _eventBroker = eventBroker;
        _stopQueryAction = new StopQueryAction(_eventBroker);
        _queryAction = new QueryAction(_eventBroker, this);
        
        new GraphViewFocusEventHandler(_eventBroker, this);

        JPanel queryFieldPanel = new JPanel();

        _queryField = new JTextField(25);
        _queryField.setToolTipText(_queryFieldToolTip);
        _queryField.addActionListener(this);

        _depthField = new JTextField(1);
        _depthField.setToolTipText(_depthFieldToolTip);
        _depthField.addActionListener(this);
        _depthField.addKeyListener(new KeyAdapter() {
            @Override
			public void keyReleased(KeyEvent ke) {
                if ((!Character.isDigit(ke.getKeyChar())) && (!Character.isLetter(ke.getKeyChar()))) {
                    return;
                }
                int depth = -1;
                try {
                    depth = (new Integer(_depthField.getText())).intValue();
                } catch (NumberFormatException nfe) {
                	ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "Please use integers to specify depth");
                    _depthField.selectAll();
                }
                if (depth > 4) {
                	ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", "Please choose smaller integers " +
                            "for depth setting. " +
                            "Large setting for this parameter may result in long load times");
                    _depthField.selectAll();
                }
                _depth = depth;
            }
        });

        _querySubmitButton = new JButton(_queryAction);
        _queryStopButton = new JButton(_stopQueryAction);

        queryFieldPanel.add(_queryLabel);
        queryFieldPanel.add(_queryField);

        queryFieldPanel.add(_depthLabel);
        queryFieldPanel.add(_depthField);

        queryFieldPanel.add(_querySubmitButton);
        queryFieldPanel.add(_queryStopButton);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());

        buildRelationLinksQueryPanel();
        add(_relationLinksPanel, BorderLayout.NORTH);

        add(queryFieldPanel, BorderLayout.CENTER);

		enableStopQueryAction(false);        
        enableQueryActions(false);
    }

    public Action getStopQueryAction () {
        return _stopQueryAction;
    }

    public void enableStopQueryAction (boolean isEnabled) {
        _stopQueryAction.setEnabled(isEnabled);
    }
    
    public void enableQueryActions (boolean isEnabled) {
    	_queryAction.setEnabled(isEnabled);
    	_queryLabel.setEnabled(isEnabled);
    	_queryField.setEnabled(isEnabled);
    	_depthField.setEnabled(isEnabled);
    	_depthLabel.setEnabled(isEnabled);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == _queryField) {
            _querySubmitButton.doClick();
        }
        if (ae.getSource() == _depthField) {
            _querySubmitButton.doClick();
        }
    }

    public void setQuery(Query query) {
        setQueryField(query.getQueryTypeName());
        List<EdgeType> wantedLinks = query.getRelationLinksList();
        if (wantedLinks.size() == 0) {
        	wantedLinks = OntoramaConfig.getEdgeTypesList();
        }
        setWantedRelationLinks(wantedLinks);
        if (query.getDepth() > 0) {
            setDepthField(query.getDepth());
        } else {
            _depthField.setText("");
        }
    }

    public String getQueryField() {
        return _queryField.getText();
    }

    public void setQueryField(String queryString) {
        _queryField.setText(queryString);
    }

    public int getDepthField() {
        return _depth;
    }

    private void setDepthField(int depth) {
        _depthField.setText(String.valueOf(depth));
    }

    public List<EdgeType> getWantedRelationLinks() {
        _wantedRelationLinks = updateWantedRelationLinks();
        
		List<EdgeType> edgeTypesToQuery = new ArrayList<EdgeType>();

		List<EdgeType> allEdgeTypes = OntoramaConfig.getEdgeTypesList();
		Iterator<EdgeType> it = allEdgeTypes.iterator();
		while (it.hasNext()) {
			EdgeType edgeType = it.next();
			if (_wantedRelationLinks.contains(edgeType)) {
				edgeTypesToQuery.add(edgeType);
				continue;
			}
			EdgeTypeDisplayInfo displayInfo = OntoramaConfig.getEdgeDisplayInfo(edgeType);
			if (displayInfo.isDisplayInDescription()) {
				edgeTypesToQuery.add(edgeType);
				continue;
			}
		}
        
        
        return edgeTypesToQuery;
    }


    private void setWantedRelationLinks(List<EdgeType> wantedLinks) {
    	for(JCheckBox curCheckBox: _relationLinksCheckBoxes.keySet()) {
            EdgeType correspondingRelLink = _relationLinksCheckBoxes.get(curCheckBox);
            if (wantedLinks.contains(correspondingRelLink)) {
                curCheckBox.setSelected(true);
            } else {
                curCheckBox.setSelected(false);
            }
        }
    }

    private void buildRelationLinksQueryPanel() {
        Iterator<EdgeType> it = OntoramaConfig.getEdgeTypesList().iterator();

        while (it.hasNext()) {
            EdgeType cur = it.next();
            if (! OntoramaConfig.getEdgeDisplayInfo(cur).isDisplayInGraph()) {
                continue;
            }
            JCheckBox curCheckBox = new JCheckBox(cur.getName());
            ImageIcon displayIcon = OntoramaConfig.getEdgeDisplayInfo(cur).getDisplayIcon();
            JLabel displayIconLabel = new JLabel(displayIcon);
            curCheckBox.setSelected(true);
            curCheckBox.addItemListener(new ItemListener () {
				public void itemStateChanged(ItemEvent e) {
					_wantedRelationLinks = updateWantedRelationLinks();
				}
			});
            _relationLinksCheckBoxes.put(curCheckBox, cur);
            _relationLinksPanel.add(displayIconLabel);
            _relationLinksPanel.add(curCheckBox);
        }

    }

    private List<EdgeType> updateWantedRelationLinks() {
        List<EdgeType> wantedRelationLinks = new ArrayList<EdgeType>();
        for(JCheckBox key : _relationLinksCheckBoxes.keySet()) {
            if (key.isSelected()) {
                EdgeType relLinkType = _relationLinksCheckBoxes.get(key);
                wantedRelationLinks.add(relLinkType);
            }
        }
        return wantedRelationLinks;
    }


    public void focus(Node node) {
        _queryField.setText(node.getName());
    }

    public void setGraph(Graph graph) {
    	// TODO shouldn't we do something here?
    }
}
