package ontorama.view;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Action;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;

import ontorama.webkbtools.query.Query;

import ontorama.view.action.*;

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
   *
   */
  private static OntoRamaApp _mainApp;

  /**
   * @todo  shouldn't pass reference to ontoramaMenu and mainApp
   */
  public HistoryMenu( OntoRamaApp mainApp) {
    super("History");
    _mainApp = mainApp;
    _menuItemHistoryMapping = new Hashtable();
    _historyItems = new LinkedList();

    setMnemonic(KeyEvent.VK_H);


    buildHistoryMenu();
  }

  /**
   * create History Menu
   */
  private void buildHistoryMenu () {

    // create back and forward buttons
    _historyBackMenuItem = add(_mainApp._backAction);
    _historyForwardMenuItem = add(_mainApp._forwardAction);

    // set shortcut keys
    _historyBackMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
    _historyForwardMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));

    // set enabled/disabled
    enableBackForwardButtons();

    addSeparator();


	System.out.println("\n\n\nappend history, example = " +  OntoramaConfig.getCurrentExample());
    appendHistory(OntoramaConfig.getCurrentExample().getRoot(), OntoramaConfig.getCurrentExample());
  }
  
  /**
   * Append history menu with new example
   * 
   * Assumptions:	we are assuming that each example would correspond to 
   * a query with ALL relation links.
   */
  public void appendHistory (String termName, OntoramaExample example) {
    Query query = new Query(termName, OntoramaConfig.getRelationLinksList());
    appendHistory(query, example);
  }
  

  /**
   * Append history menu with new example
   * 
   */
  public void appendHistory (Query query, OntoramaExample example) {

    //int historyItemsCount = this.menuItemHistoryMapping.size();
    Enumeration historyItemsEnum =  _menuItemHistoryMapping.keys();

    //String historyItemLabelName = query.getQueryTypeName() + " (" + example.getName() + ") ";
    String historyItemLabelName = query.getQueryTypeName() + " (" + example.getName() + ") ";
    historyItemLabelName = historyItemLabelName + ", depth = " + query.getDepth();
    historyItemLabelName = historyItemLabelName + ", rel links = " + query.getRelationLinksList();
    

    if ((_historyItems.size() > 0) && (_historyItems.size() > _maxHistoryItems)) {
      // need to remove first item
      JCheckBoxMenuItem firstMenuItem = (JCheckBoxMenuItem) _historyItems.getFirst();
      System.out.println("first menu item = " + firstMenuItem.getText());
      _historyItems.removeFirst();
      _menuItemHistoryMapping.remove(firstMenuItem);
      remove(firstMenuItem);
    }
    
    //System.out.println("example = " + example);
    HistoryElement historyElement = new HistoryElement(historyItemLabelName, query, example);

    JCheckBoxMenuItem historyItem = new JCheckBoxMenuItem(historyItemLabelName);
    setSelectedMenuItem( historyItem);
    _menuItemHistoryMapping.put(historyItem , historyElement);
    _historyItems.add(historyItem);
    historyItem.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        JCheckBoxMenuItem historyItem = (JCheckBoxMenuItem) e.getSource();
        displayHistoryItem(historyItem);
      }
    });
    add(historyItem);
    enableBackForwardButtons();
  }

  /**
   *
   */
  public static JCheckBoxMenuItem getMenuItem (int index) {
    return (JCheckBoxMenuItem) _historyItems.get(index);
  }


  /**
   *
   */
  public static void displayHistoryItem (JCheckBoxMenuItem historyItem) {
  	
  	//System.out.println("displayHistoryItem for historyItem = " + historyItem.getText());

    HistoryElement historyElement = (HistoryElement) _menuItemHistoryMapping.get(historyItem);

    // get corresponding example
    OntoramaExample example = historyElement.getExample();
    
//    // get corresponding query
//    Query query = historyElement.getQuery();

    //JCheckBoxMenuItem correspondingExampleMenuItem = _ontoramaMenu.findExampleMenuItem(example);

    boolean querySuccessfull = _mainApp.executeQueryForHistoryElement(historyElement);
    if (!querySuccessfull) {
      return;
    }

    // select corresponding example
    _mainApp.setSelectedExampleMenuItem(example);
    setSelectedMenuItem(historyItem);
    enableBackForwardButtons();
  }

  /**
   * Return currently selected menu item from History menu.
   * @return  selected menu item,
   *          if there is no item selected - return null
   * NOTE: Assuming that there is only one item selected at any time,
   *        if there is more then one items selected - return
   *        the first one.
   */
  private static JCheckBoxMenuItem getSelectedHistoryMenuItem () {
    Enumeration e = _menuItemHistoryMapping.keys();
    while (e.hasMoreElements()) {
      JCheckBoxMenuItem cur = (JCheckBoxMenuItem) e.nextElement();
      if (cur.isSelected()) {
        //System.out.println("getSelectedHistoryMenuItem returning " + cur);
        return cur;
      }
    }
    return null;
  }

  /**
   *
   */
  public static int getIndexOfSelectedHistoryMenuItem () {
    JCheckBoxMenuItem curSelectedItem = getSelectedHistoryMenuItem();
    //System.out.println(" curSelectedItem = " + curSelectedItem);
    if (curSelectedItem == null) {
      return (-1);
    }
    System.out.println("getIndexOfSelectedHistoryMenuItem, returning: " + _historyItems.indexOf(curSelectedItem));
    return (_historyItems.indexOf(curSelectedItem));
  }

  /**
   *
   */
  protected static void enableBackForwardButtons () {
    int curSelectedHistoryIndex = getIndexOfSelectedHistoryMenuItem();
    int maxHistoryItem = _menuItemHistoryMapping.size() - 1;
    System.out.println("***enableBackForwardButtons, curSelectedHistoryIndex = " + curSelectedHistoryIndex + ", maxHistoryItem = " + maxHistoryItem);
    if (curSelectedHistoryIndex <= 0) {
      //this.historyBackMenuItem.setEnabled(false);
      _mainApp._backAction.setEnabled(false);
    }
    else {
      //this.historyBackMenuItem.setEnabled(true);
      _mainApp._backAction.setEnabled(true);
    }
    if (curSelectedHistoryIndex >= (_menuItemHistoryMapping.size()-1)) {
      //this.historyForwardMenuItem.setEnabled(false);
      _mainApp._forwardAction.setEnabled(false);
    }
    else {
      //this.historyForwardMenuItem.setEnabled(true);
      _mainApp._forwardAction.setEnabled(true);
    }
  }

  /**
   * will only work for menu items that are JCheckBoxMenuItem's
   */
  protected static void setSelectedMenuItem ( JCheckBoxMenuItem selectItem) {
  	
  	//System.out.println("setSelectedMenuItem for item = " + selectItem.getText());
  	
    // first deselect previously selected item
    Enumeration enum = _menuItemHistoryMapping.keys();
    while (enum.hasMoreElements()) {
      JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) enum.nextElement();
      curItem.setSelected(false);
    }

    // select given item
    selectItem.setSelected(true);
  }


// this method appears to cause a bug: history menu items are selected 
// incorrectly when user browses to some nodes (not root node). first history
// item corresponding to the given example is selected. this is not always what
// we want. should compare by example and query details
// 
//  /**
//   *
//   */
//   protected void setSelectedHistoryMenuItem (OntoramaExample example) {
//    Enumeration enum = _menuItemHistoryMapping.keys();
//    while (enum.hasMoreElements()) {
//      JCheckBoxMenuItem historyMenuItem = (JCheckBoxMenuItem) enum.nextElement();
//      HistoryElement historyElement = (HistoryElement) _menuItemHistoryMapping.get(historyMenuItem);
//      OntoramaExample curExample = historyElement.getExample();
//      if (curExample.equals(example)) {
//        setSelectedMenuItem(historyMenuItem);
//        return;
//      }
//    }
//   }
}