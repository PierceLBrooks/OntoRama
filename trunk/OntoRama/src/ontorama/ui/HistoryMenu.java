package ontorama.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.*;

import ontorama.OntoramaConfig;
import ontorama.ui.action.BackHistoryAction;
import ontorama.ui.action.ForwardHistoryAction;
import ontorama.ui.events.DisplayHistoryItemEvent;
import ontorama.model.QueryStartEvent;
import ontorama.conf.examplesConfig.OntoramaExample;
import ontorama.ontotools.query.Query;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.events.Event;
import org.tockit.events.LoggingEventListener;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class HistoryMenu extends JMenu {

    /**
     *
     */
    private JMenuItem _historyBackMenuItem;
    private JMenuItem _historyForwardMenuItem;

    /**
     * keys - menuItems
     * values - corresponging history elements
     */
    private static Hashtable _menuItemHistoryMapping;

    /**
     *
     */
    private int _maxHistoryItems = 20;

    /**
     * hold all history menu items for operating
     * back and forward buttons
     */
    private static LinkedList _historyItems;

    /**
     * event broker capable of processing queries
     */
    private EventBroker _eventBroker;

    public Action _backAction;
    public Action _forwardAction;

    private class DisplayHistoryItemEventHandler implements EventBrokerListener {
        public void processEvent (Event event) {
            JMenuItem menuItem = (JMenuItem) event.getSubject();
            JCheckBoxMenuItem historyItem = (JCheckBoxMenuItem) menuItem;
            HistoryElement historyElement = (HistoryElement) _menuItemHistoryMapping.get(historyItem);
            // get corresponding example
            OntoramaExample example = historyElement.getExample();
            OntoramaConfig.setCurrentExample(example);
            // get graph for this query and load it into app
            _eventBroker.processEvent(new QueryStartEvent(historyElement.getQuery()));
            //setSelectedMenuItem(historyItem);
            enableBackForwardButtons();
        }
    }


    /**
     */
    public HistoryMenu(EventBroker eventBroker) {
        super("History");
        _eventBroker = eventBroker;
        _menuItemHistoryMapping = new Hashtable();
        _historyItems = new LinkedList();
        _backAction = new BackHistoryAction(_eventBroker);
        _forwardAction = new ForwardHistoryAction(_eventBroker);

        _eventBroker.subscribe(new DisplayHistoryItemEventHandler(), DisplayHistoryItemEvent.class, JMenuItem.class);
        new LoggingEventListener(_eventBroker,DisplayHistoryItemEvent.class,Object.class,System.out);

        setMnemonic(KeyEvent.VK_H);
        buildHistoryMenu();
    }

    public Action getBackAction () {
        return _backAction;
    }

    public Action getForwardAction () {
        return _forwardAction;
    }

    /**
     * create History Menu
     */
    private void buildHistoryMenu() {

        // create back and forward buttons
        _historyBackMenuItem = add(_backAction);
        _historyForwardMenuItem = add(_forwardAction);

        // set shortcut keys
        _historyBackMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
        _historyForwardMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));

        // set enabled/disabled
        enableBackForwardButtons();

        addSeparator();
    }

    /**
     * Append history menu with new example
     *
     * Assumptions:	we are assuming that each example would correspond to
     * a query with ALL relation links.
     */
    public void appendHistory(String termName, OntoramaExample example) {
        Query query = new Query(termName, OntoramaConfig.getEdgeTypesList());
        appendHistory(query, example);
    }


    /**
     * Append history menu with new example
     *
     */
    public void appendHistory(Query query, OntoramaExample example) {

        String historyItemLabelName = query.getQueryTypeName() + " (" + example.getName() + ") ";

        String historyItemToolTipText = historyItemLabelName;
        if (query.getDepth() > -1) {
            historyItemToolTipText = historyItemToolTipText + ", depth = " + query.getDepth();
        }
        historyItemToolTipText = historyItemToolTipText + ", rel links = " + query.getRelationLinksList();


        if ((_historyItems.size() > 0) && (_historyItems.size() > _maxHistoryItems)) {
            JCheckBoxMenuItem firstMenuItem = (JCheckBoxMenuItem) _historyItems.getFirst();
            _historyItems.removeFirst();
            _menuItemHistoryMapping.remove(firstMenuItem);
            remove(firstMenuItem);
        }

        HistoryElement historyElement = new HistoryElement(historyItemLabelName, query, example);

        JCheckBoxMenuItem historyItem = new JCheckBoxMenuItem(historyItemLabelName);
        historyItem.setToolTipText(historyItemToolTipText);
        setSelectedMenuItem(historyItem);
        _menuItemHistoryMapping.put(historyItem, historyElement);
        _historyItems.add(historyItem);
        historyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem historyItem = (JCheckBoxMenuItem) e.getSource();
                _eventBroker.processEvent(new DisplayHistoryItemEvent(historyItem));
            }
        });
        add(historyItem);
        enableBackForwardButtons();
    }

    /**
     *
     */
    public static JCheckBoxMenuItem getMenuItem(int index) {
        return (JCheckBoxMenuItem) _historyItems.get(index);
    }

    /**
     * Return currently selected menu item from History menu.
     * @return  selected menu item,
     *          if there is no item selected - return null
     * NOTE: Assuming that there is only one item selected at any time,
     *        if there is more then one items selected - return
     *        the first one.
     */
    private static JCheckBoxMenuItem getSelectedHistoryMenuItem() {
        Enumeration e = _menuItemHistoryMapping.keys();
        while (e.hasMoreElements()) {
            JCheckBoxMenuItem cur = (JCheckBoxMenuItem) e.nextElement();
            if (cur.isSelected()) {
                return cur;
            }
        }
        return null;
    }

    /**
     *
     */
    public static int getIndexOfSelectedHistoryMenuItem() {
        JCheckBoxMenuItem curSelectedItem = getSelectedHistoryMenuItem();
        if (curSelectedItem == null) {
            return (-1);
        }
        return (_historyItems.indexOf(curSelectedItem));
    }

    /**
     *
     */
    protected void enableBackForwardButtons() {
        int curSelectedHistoryIndex = getIndexOfSelectedHistoryMenuItem();
        if (curSelectedHistoryIndex <= 0) {
            _backAction.setEnabled(false);
        } else {
            _backAction.setEnabled(true);
        }
        if (curSelectedHistoryIndex >= (_menuItemHistoryMapping.size() - 1)) {
            _forwardAction.setEnabled(false);
        } else {
            _forwardAction.setEnabled(true);
        }
    }

    /**
     * will only work for menu items that are JCheckBoxMenuItem's
     */
    protected static void setSelectedMenuItem(JCheckBoxMenuItem selectItem) {
        // first deselect previously selected item
        Enumeration enum = _menuItemHistoryMapping.keys();
        while (enum.hasMoreElements()) {
            JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) enum.nextElement();
            curItem.setSelected(false);
        }

        // select given item
        selectItem.setSelected(true);
    }
}