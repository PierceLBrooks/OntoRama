package ontorama.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import ontorama.ui.action.BackHistoryAction;
import ontorama.ui.action.ForwardHistoryAction;
import ontorama.ui.events.DisplayHistoryItemEvent;
import ontorama.ui.events.HistoryQueryStartEvent;
import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
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
    
    private ButtonGroup _buttonGroup;
    
    private Backend _backend = OntoramaConfig.getBackend();

    private class DisplayHistoryItemEventHandler implements EventBrokerListener {
        public void processEvent (Event event) {
            JMenuItem menuItem = (JMenuItem) event.getSubject();
        	JRadioButtonMenuItem historyItem = (JRadioButtonMenuItem) menuItem;
            HistoryElement historyElement = (HistoryElement) _menuItemHistoryMapping.get(historyItem);
            // get graph for this query and load it into app
            _eventBroker.processEvent(new HistoryQueryStartEvent(historyElement));
            //enableBackForwardButtons();
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
        _buttonGroup = new ButtonGroup();

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
     * Update history menu: if given query is a query not stored in history
     * items list - append history list. Otherwise - jump to the history
     * item correponding to this query.
     */
    public void updateHistory(Query query) {
    	JRadioButtonMenuItem historyItem;
		if ( (historyItem=findIfQueryAlreadyExists(query)) != null) {
			//setSelectedMenuItem(historyItem);
			historyItem.setSelected(true);
		}
		else {
			appendHistory(query);			
		}
        enableBackForwardButtons();
    }

	/**
	 * Append history with given query
	 */
	public void appendHistory(Query query) {
		System.out.println("HistoryMenu::appendHistory for query " + query);
		
		String historyItemLabelName = query.getQueryTypeName();	
		String historyItemToolTipText = historyItemLabelName;
		if (query.getQueryTypeName().length() <= 0) {
		}
		else {
		}
	
		if (query.getDepth() > -1) {
		    historyItemToolTipText = historyItemToolTipText + ", depth = " + query.getDepth();
		}
		historyItemToolTipText = historyItemToolTipText + ", rel links = " + query.getRelationLinksList();
			
		if ((_historyItems.size() > 0) && (_historyItems.size() > _maxHistoryItems)) {
		    JRadioButtonMenuItem firstMenuItem = (JRadioButtonMenuItem) _historyItems.getFirst();
		    _historyItems.removeFirst();
		    _menuItemHistoryMapping.remove(firstMenuItem);
		    remove(firstMenuItem);
		}
		
		HistoryElement historyElement = new HistoryElement(historyItemLabelName, query, _backend.getQueryEngine());
		
		JRadioButtonMenuItem historyItem = new JRadioButtonMenuItem(historyItemLabelName);
		_buttonGroup.add(historyItem);
		historyItem.setToolTipText(historyItemToolTipText);
		historyItem.setSelected(true);
		_menuItemHistoryMapping.put(historyItem, historyElement);
		_historyItems.add(historyItem);
		historyItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        JRadioButtonMenuItem historyItem = (JRadioButtonMenuItem) e.getSource();
		        _eventBroker.processEvent(new DisplayHistoryItemEvent(historyItem));
		    }
		});
		add(historyItem);
	}

	public JRadioButtonMenuItem findIfQueryAlreadyExists(Query query) {
		Iterator it = _historyItems.iterator();
		while (it.hasNext()) {
			JRadioButtonMenuItem historyItem = (JRadioButtonMenuItem) it.next();
			HistoryElement historyElement = (HistoryElement) _menuItemHistoryMapping.get(historyItem);
			Query historyQuery = historyElement.getQuery();
			if (query.equals(historyQuery)) {
				return historyItem;
			}
		}
		return null;
	}

    /**
     *
     */
    public static JRadioButtonMenuItem getMenuItem(int index) {
        return (JRadioButtonMenuItem) _historyItems.get(index);
    }

    /**
     * Return currently selected menu item from History menu.
     * @return  selected menu item,
     *          if there is no item selected - return null
     * NOTE: Assuming that there is only one item selected at any time,
     *        if there is more then one items selected - return
     *        the first one.
     */
    private static JRadioButtonMenuItem getSelectedHistoryMenuItem() {
        Enumeration e = _menuItemHistoryMapping.keys();
        while (e.hasMoreElements()) {
            JRadioButtonMenuItem cur = (JRadioButtonMenuItem) e.nextElement();
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
        JRadioButtonMenuItem curSelectedItem = getSelectedHistoryMenuItem();
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
}