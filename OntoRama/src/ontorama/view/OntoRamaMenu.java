package ontorama.view;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;


import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.util.event.ViewEventListener;
import ontorama.webkbtools.query.Query;


/**
 * Title:        OntoRama
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      DSTC
 * @author
 * @version 1.0
 */

public class OntoRamaMenu {

  private JMenuBar menuBar;
  private JMenu fileMenu;
  private JMenu examplesMenu;
  private JMenu historyMenu;

  private OntoRamaApp mainApp;

  /**
   * list of OntoramaExamples
   */
  private List examplesList;

  /**
   * keys - menu items
   * values - examples
   */
  private Hashtable menuItemExampleMapping;

  /**
   * keys - name of submenu
   * values - submenu itself
   */
  private Hashtable submenusMapping;

  /**
   * currently selected item.
   */
  private JCheckBoxMenuItem curSelectedExampleMenuItem;

  /**
   * keys - menuItems
   * values - corresponging history elements
   */
  private Hashtable menuItemHistoryMapping;


  /**
   *
   */
  private int maxHistoryItems = 10;

  /**
   *
   */
  ViewEventListener viewEventListener;

  /**
   *
   */
  private JMenuItem historyBackMenuItem;
  private JMenuItem historyForwardMenuItem;
  private boolean backButtonIsEnabled = false;
  private boolean forwardButtonIsEnabled = false;


  /**
   * hold all history menu items for operating
   * back and forward buttons
   */
  private LinkedList historyItems;

  /**
   *
   */
  public OntoRamaMenu (OntoRamaApp mainApp, ViewEventListener viewEventListener) {
    this.mainApp = mainApp;
    this.viewEventListener = viewEventListener;

    this.examplesList = OntoramaConfig.getExamplesList();
    this.menuItemExampleMapping = new Hashtable();
    this.submenusMapping = new Hashtable();
    this.menuItemHistoryMapping = new Hashtable();
    this.historyItems = new LinkedList();

    this.menuBar = new JMenuBar();

    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic(KeyEvent.VK_F);
    buildFileMenu();

    this.examplesMenu = new JMenu("Examples");
    this.examplesMenu.setMnemonic(KeyEvent.VK_E);
    buildExamplesMenuItems();

    this.historyMenu = new JMenu("History");
    this.historyMenu.setMnemonic(KeyEvent.VK_H);
    buildHistoryMenu();

    this.menuBar.add(this.fileMenu);
    this.menuBar.add(this.examplesMenu);
    this.menuBar.add(this.historyMenu);
  }

  /**
   *
   */
  public JMenuBar getMenuBar () {
    return this.menuBar;
  }

  /**
   * create File menu
   */
  private void buildFileMenu () {
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    exitMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        sendCloseMainApp();
      }
    });


    this.fileMenu.add(exitMenuItem);
  }

  /**
   * create History Menu
   */
  private void buildHistoryMenu () {

    // create back and forward buttons
    this.historyBackMenuItem = new JMenuItem("Back");
    this.historyForwardMenuItem = new JMenuItem("Forward");

    // set shortcut keys
    this.historyBackMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
    this.historyForwardMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK));

    // add listeners
    this.historyBackMenuItem.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e) {
        historyBackAction();
      }
    });
    this.historyForwardMenuItem.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent e) {
        historyForwardAction();
      }
    });

    // set enabled/disabled
    enableBackForwardButtons();

    // add  back and forward buttons to the history menu
    this.historyMenu.add(this.historyBackMenuItem);
    this.historyMenu.add(this.historyForwardMenuItem);
    this.historyMenu.addSeparator();

    appendHistory(OntoramaConfig.getCurrentExample().getRoot(), OntoramaConfig.getCurrentExample());

  }

  /**
   *
   */
  private void buildExamplesMenuItems () {

    Iterator examplesIterator = examplesList.iterator();
    while (examplesIterator.hasNext()) {
      OntoramaExample curExample = (OntoramaExample) examplesIterator.next();
      //System.out.println("curExample = " + curExample.getName());
      JCheckBoxMenuItem curItem = new JCheckBoxMenuItem(curExample.getName(), false);
      if (curExample.isLoadFirst()) {
        setSelectedMenuItem(menuItemExampleMapping, curItem);
      }
      this.menuItemExampleMapping.put(curItem,curExample);

      curItem.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          JCheckBoxMenuItem sourceItem = (JCheckBoxMenuItem) e.getSource();
          //System.out.println("action: " + sourceItem.getLabel());
          displayExample(sourceItem);
        }
      });

      String subfolderName = curExample.getMenuSubfolderName();
      //System.out.println("subfolderName = " + subfolderName);

      if (subfolderName != null) {
        JMenu subMenu = findExamplesSubMenu(subfolderName);
        //System.out.println("adding item " + curItem.getName() + " to submenu " + subMenu.getName());
        subMenu.add(curItem);
      }
      else {
        this.examplesMenu.add(curItem);
      }
    }

  }

  /**
   * Find submenu for given name. (check if such submenu already exists,
   * if so - return it. otherwise - create new and return the new one).
   */
  private JMenu findExamplesSubMenu (String submenuName) {

    JMenu subMenu = (JMenu) submenusMapping.get(submenuName);
    if (subMenu == null) {
      subMenu = makeNewExamplesSubMenu(submenuName);
    }
    return subMenu;
  }

  /**
   * create new submenu in Examples menu
   */
  private JMenu makeNewExamplesSubMenu (String submenuName) {
    JMenu newSubmenu = new JMenu(submenuName);
    this.examplesMenu.addSeparator();
    this.examplesMenu.add(newSubmenu);

    this.submenusMapping.put(submenuName,newSubmenu);

    return newSubmenu;
  }

  /**
   * will only work for menu items that are JCheckBoxMenuItem's
   */
  private void setSelectedMenuItem (Hashtable itemsTable, JCheckBoxMenuItem selectItem) {
    // first deselect previously selected item
    Enumeration enum = itemsTable.keys();
    while (enum.hasMoreElements()) {
      JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) enum.nextElement();
      curItem.setSelected(false);
    }

    // select given item
    selectItem.setSelected(true);
  }

  /**
   *
   */
  private JCheckBoxMenuItem findFirstHistoryItem () {

    Enumeration historyItemsEnum =  this.menuItemHistoryMapping.keys();
    if (historyItemsEnum.hasMoreElements()) {
      return ((JCheckBoxMenuItem) historyItemsEnum.nextElement());
    }
    return null;
  }



  /**
   *
   */
  public void appendHistory (String termName, OntoramaExample example) {

    int historyItemsCount = this.menuItemHistoryMapping.size();
    Enumeration historyItemsEnum =  this.menuItemHistoryMapping.keys();

    String historyItemLabelName = termName + " (" + example.getName() + ") ";

    if ((historyItemsCount > 0) && (historyItemsCount > maxHistoryItems)) {
      // need to remove first item
      JCheckBoxMenuItem firstMenuItem = findFirstHistoryItem();
      System.out.println("first menu item = " + firstMenuItem.getText());
      this.menuItemHistoryMapping.remove(firstMenuItem);
      this.historyItems.removeFirst();
      historyMenu.remove(firstMenuItem);
    }

    HistoryElement historyElement = new HistoryElement(historyItemLabelName, termName, example);

    JCheckBoxMenuItem historyItem = new JCheckBoxMenuItem(historyItemLabelName);
    setSelectedMenuItem(menuItemHistoryMapping, historyItem);
    this.menuItemHistoryMapping.put(historyItem , historyElement);
    this.historyItems.add(historyItem);
    historyItem.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        JCheckBoxMenuItem historyItem = (JCheckBoxMenuItem) e.getSource();
        displayHistoryItem(historyItem);
      }
    });
    this.historyMenu.add(historyItem);

  }

  /**
   *
   */
  public void displayExample(JCheckBoxMenuItem menuItem) {
    //System.out.println("displayExample for " + menuItem);

    // get corresponding example
    OntoramaExample example = (OntoramaExample) menuItemExampleMapping.get(menuItem);

    boolean querySuccessfull = executeQuery(example.getRoot(), example, menuItem);
    if (!querySuccessfull) {
      return;
    }
    appendHistory(example.getRoot(),example);
  }


  /**
   *
   */
  public void displayHistoryItem (JCheckBoxMenuItem historyItem) {
    //System.out.println("displayHistoryItem for " + historyItem);
    HistoryElement historyElement = (HistoryElement) this.menuItemHistoryMapping.get(historyItem);

    // get corresponding example
    OntoramaExample example = historyElement.getExample();

    // find corresponding example and select it
    Enumeration enum = this.menuItemExampleMapping.keys();
    JCheckBoxMenuItem correspondingExampleMenuItem = null;
    while (enum.hasMoreElements()) {
      JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) enum.nextElement();
      OntoramaExample curExample = (OntoramaExample) this.menuItemExampleMapping.get(curItem);
      if (curExample.equals(example)) {
        correspondingExampleMenuItem = curItem;
        break;
      }
    }

    boolean querySuccessfull = executeQuery(historyElement.getTermName(), example, correspondingExampleMenuItem);
    if (!querySuccessfull) {
      return;
    }
    setSelectedMenuItem(menuItemHistoryMapping, historyItem);
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
  private JCheckBoxMenuItem getSelectedHistoryMenuItem () {
    Enumeration e = this.menuItemHistoryMapping.keys();
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
  private int getIndexOfSelectedHistoryMenuItem () {
    JCheckBoxMenuItem curSelectedItem = getSelectedHistoryMenuItem();
    //System.out.println(" curSelectedItem = " + curSelectedItem);
    if (curSelectedItem == null) {
      return (-1);
    }
    System.out.println("getIndexOfSelectedHistoryMenuItem, returning: " + historyItems.indexOf(curSelectedItem));
    return (historyItems.indexOf(curSelectedItem));
  }

  /**
   *
   */
  private void enableBackForwardButtons () {
    int curSelectedHistoryIndex = getIndexOfSelectedHistoryMenuItem();
    int maxHistoryItem = this.menuItemHistoryMapping.size() - 1;
    System.out.println("***enableBackForwardButtons, curSelectedHistoryIndex = " + curSelectedHistoryIndex + ", maxHistoryItem = " + maxHistoryItem);
    if (curSelectedHistoryIndex <= 0) {
      this.historyBackMenuItem.setEnabled(false);
    }
    else {
      this.historyBackMenuItem.setEnabled(true);
    }
    if (curSelectedHistoryIndex >= (this.menuItemHistoryMapping.size()-1)) {
      this.historyForwardMenuItem.setEnabled(false);
    }
    else {
      this.historyForwardMenuItem.setEnabled(true);
    }
  }

  /**
   *
   */
  public void historyBackAction () {
    int indexOfCur = getIndexOfSelectedHistoryMenuItem();
    /*
    if (indexOfCur <= 0) {
      System.out.println("BEEP");
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    */
    int backInd = indexOfCur - 1;
    JCheckBoxMenuItem backItem = (JCheckBoxMenuItem) historyItems.get(backInd);
    System.out.println("historyBackAction, displaying item at ind = " + backInd + ", item : " + backItem.getText());
    displayHistoryItem(backItem);
  }

  /**
   *
   */
  public void historyForwardAction () {
    int indexOfCur = getIndexOfSelectedHistoryMenuItem();
    /*
    if (indexOfCur >= (this.menuItemHistoryMapping.size()-1)) {
      System.out.println("BEEP");
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    */
    JCheckBoxMenuItem forwardItem = (JCheckBoxMenuItem) historyItems.get(indexOfCur + 1);
    displayHistoryItem(forwardItem);
  }

  /**
   *
   */
  private boolean executeQuery (String termName, OntoramaExample example,
                        JCheckBoxMenuItem correspondingExampleMenuItem) {

    // reset details in OntoramaConfig
    OntoramaConfig.setCurrentExample(example);

    // create a new query
    Query query = new Query(termName);

    // get graph for this query and load it into app
    if (this.mainApp.executeQuery(query)) {
      // indicate that this example is currently displayed one
      setSelectedMenuItem(menuItemExampleMapping, correspondingExampleMenuItem);
      enableBackForwardButtons();
      return true;
    }
    return false;
  }

  /**
   * close main application
   */
  private void sendCloseMainApp () {
    this.mainApp.closeMainApp();
  }
}