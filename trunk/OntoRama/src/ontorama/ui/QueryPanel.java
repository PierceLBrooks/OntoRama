package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ontorama.OntoramaConfig;
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.model.ViewQueryEventHandler;
import ontorama.model.ViewQuery;
import ontorama.model.graph.view.GraphView;
import ontorama.model.graph.Node;
import ontorama.model.graph.Graph;
import ontorama.ontotools.query.Query;
import org.tockit.events.EventBroker;

/**
 * QueryPanel is responsible for building an interface for a query that
 * will be used to query Ontology Server (using GraphBuilder)
 *
 */
public class QueryPanel extends JPanel implements ActionListener, ViewQuery, GraphView {

    private int _depth = -1;

    private JTextField _queryField;
    private JButton _querySubmitButton;
    private JButton _queryStopButton;
    private JTextField _depthField;
    private JLabel _depthLabel = new JLabel("depth: ");

    private static final String _queryFieldToolTip = "Type query term here";
    private static final String _depthFieldToolTip = "Specify query depth (integer from 1 to 9). This feature is only available for Dynamic sources.";


    /**
     *
     */
    private JPanel _relationLinksPanel = new JPanel();

    /**
     * table of check boxes for relation links
     * keys - checkBoxes, values - relation type integers
     */
    private Hashtable _relationLinksCheckBoxes = new Hashtable();

    /**
     * relation links that user has chosen to display
     */
    private List _wantedRelationLinks = new LinkedList();

    /**
     *
     */
    private EventBroker _eventBroker;

    /**
     *
     */
    private OntoRamaApp _ontoRamaApp;

    /**
     * @todo  maybe OntoRamaApp shouldn't be a parameter in constructor
     *        (this is done for executing queries). Better way to do this is to follow
     *        Observer Pattern
     */
    public QueryPanel(OntoRamaApp ontoRamaApp, EventBroker eventBroker) {

        _eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);
        new ViewQueryEventHandler(eventBroker, this);

        _ontoRamaApp = ontoRamaApp;

        JPanel queryFieldPanel = new JPanel();

        _queryField = new JTextField(25);
        _queryField.setToolTipText(_queryFieldToolTip);
        _queryField.addActionListener(this);

        _depthField = new JTextField(1);
        _depthField.setToolTipText(_depthFieldToolTip);
        _depthField.addActionListener(this);
        _depthField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                if ((!Character.isDigit(ke.getKeyChar())) && (!Character.isLetter(ke.getKeyChar()))) {
                    return;
                }
                int depth = -1;
                try {
                    depth = (new Integer(_depthField.getText())).intValue();
                } catch (NumberFormatException nfe) {
                	/// @todo instead of calling error dialog on static - should use event broker for this
                    OntoRamaApp.showErrorDialog("Please use integers to specify depth");
                    _depthField.selectAll();
                }
                if (depth > 4) {
                    OntoRamaApp.showErrorDialog("Please choose smaller integers " +
                            "for depth setting. " +
                            "Large setting for this parameter may result in long load times");
                    _depthField.selectAll();
                }
                _depth = depth;
            }
        });

        _querySubmitButton = new JButton(new QueryAction());
        _queryStopButton = new JButton(_ontoRamaApp._stopQueryAction);

//		ImageIcon leftButtonIcon = new ImageIcon("images/right.gif");
//		JButton newButton = new JButton("Test button", leftButtonIcon);
//		newButton.setMnemonic(KeyEvent.VK_T);
//		newButton.setVerticalTextPosition(AbstractButton.BOTTOM);
//        newButton.setHorizontalTextPosition(AbstractButton.CENTER);
//        newButton.setLabel("test button label");
//		//newButton.setEnabled(false);

        queryFieldPanel.add(new JLabel("Search for: "));
        queryFieldPanel.add(_queryField);

        queryFieldPanel.add(_depthLabel);
        queryFieldPanel.add(_depthField);

        queryFieldPanel.add(_querySubmitButton);
        queryFieldPanel.add(_queryStopButton);
//        queryFieldPanel.add(newButton);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());

        buildRelationLinksQueryPanel();
        add(_relationLinksPanel, BorderLayout.NORTH);

        add(queryFieldPanel, BorderLayout.CENTER);
    }

    /**
     * implementation of action performed
     */
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == _queryField) {
            _querySubmitButton.doClick();
        }
        if (ae.getSource() == _depthField) {
            _querySubmitButton.doClick();
        }
    }

    /**
     *
     */
    public Query getQuery() {
        return buildNewQuery();
    }

    /**
     *
     */
    public void setQuery(Query query) {
        setQueryField(query.getQueryTypeName());
        setWantedRelationLinks(query.getRelationLinksList());
        if (query.getDepth() > 0) {
            setDepthField(query.getDepth());
        } else {
            _depthField.setText("");
        }
    }

    /**
     *
     */
    public void enableDepth() {
        _depthLabel.setEnabled(true);
        _depthField.setEnabled(true);

        _depthLabel.setVisible(true);
        _depthField.setVisible(true);
    }

    /**
     *
     */
    public void disableDepth() {
        _depthLabel.setEnabled(false);
        _depthField.setEnabled(false);

        _depthLabel.setVisible(false);
        _depthField.setVisible(false);
    }


    /**
     *
     */
    private String getQueryField() {
        return _queryField.getText();
    }

    /**
     *
     */
    public void setQueryField(String queryString) {
        _queryField.setText(queryString);
    }

    /**
     *
     */
    private int getDepthField() {
        return _depth;
    }

    /**
     *
     */
    private void setDepthField(int depth) {
        _depthField.setText(String.valueOf(depth));
    }

    /**
     *
     */
    private List getWantedRelationLinks() {
        List wantedRelationLinks = new LinkedList();
        Enumeration en = _relationLinksCheckBoxes.keys();
        while (en.hasMoreElements()) {
            JCheckBox key = (JCheckBox) en.nextElement();
            if (key.isSelected()) {
                ontorama.model.graph.EdgeType relLinkType = (ontorama.model.graph.EdgeType) _relationLinksCheckBoxes.get(key);
                wantedRelationLinks.add(relLinkType);
            }
        }
        return wantedRelationLinks;
    }

    /**
     *
     */
    private void setWantedRelationLinks(List wantedLinks) {
        Enumeration enum = _relationLinksCheckBoxes.keys();
        while (enum.hasMoreElements()) {
            JCheckBox curCheckBox = (JCheckBox) enum.nextElement();
            ontorama.model.graph.EdgeType correspondingRelLink = (ontorama.model.graph.EdgeType) _relationLinksCheckBoxes.get(curCheckBox);
            if (wantedLinks.contains(correspondingRelLink)) {
                curCheckBox.setSelected(true);
            } else {
                curCheckBox.setSelected(false);
            }
        }
    }

    /**
     *
     */
    private void buildRelationLinksQueryPanel() {
        Iterator it = OntoramaConfig.getEdgeTypesList().iterator();

        while (it.hasNext()) {
            ontorama.model.graph.EdgeType cur = (ontorama.model.graph.EdgeType) it.next();
            if (! OntoramaConfig.getEdgeDisplayInfo(cur).isDisplayInGraph()) {
                continue;
            }
            JCheckBox curCheckBox = new JCheckBox(cur.getName());
            ImageIcon displayIcon = OntoramaConfig.getEdgeDisplayInfo(cur).getDisplayIcon();
            JLabel displayIconLabel = new JLabel(displayIcon);
            curCheckBox.setSelected(true);
            curCheckBox.addItemListener(new CheckBoxListener());
            _relationLinksCheckBoxes.put(curCheckBox, cur);
            _relationLinksPanel.add(displayIconLabel);
            _relationLinksPanel.add(curCheckBox);
        }

    }


    /**
     * ItemListener
     */
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            _wantedRelationLinks = getWantedRelationLinks();
        }
    }

    /**
     * query Action
     */
    class QueryAction extends AbstractAction {

        private static final String ACTION_COMMAND_KEY_COPY = "execute-query-command";
        private static final String NAME_COPY = "Get";
        private static final String SHORT_DESCRIPTION_COPY = "Execute Query";
        private static final String LONG_DESCRIPTION_COPY = "Execute Query";

        /**
         *
         */
        public QueryAction() {
            putValue(Action.NAME, NAME_COPY);
            putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
            putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
            putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent parm1) {
            doQuery();
        }
    }


    /**
     *
     */
    private Query buildNewQuery() {
        Query query = new Query(getQueryField(), getWantedRelationLinks());
        query.setDepth(getDepthField());
        return query;
    }

    /**
     *
     */
    protected void doQuery() {
//        Query newQuery = buildNewQuery();
//        _ontoRamaApp.executeQuery(newQuery);
//        _ontoRamaApp.appendHistoryMenu(newQuery);
    }



    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     *
     */
    public void focus(Node node) {
        _queryField.setText(node.getName());
    }

    /**
     */
    public void query(String nodeName) {
//        _queryField.setText(nodeName);
        doQuery();
    }

    public void setGraph (Graph graph) {
    }

    public void repaint () {
        super.repaint();
    }


}
