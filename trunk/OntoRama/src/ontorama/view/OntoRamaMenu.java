package ontorama.view;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

//import java.awt.MenuBar;
//import java.awt.Menu;
//import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
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
  public OntoRamaMenu (OntoRamaApp mainApp) {
    this.mainApp = mainApp;
    this.examplesList = OntoramaConfig.getExamplesList();
    this.menuItemExampleMapping = new Hashtable();
    this.submenusMapping = new Hashtable();
    this.menuItemHistoryMapping = new Hashtable();

    this.menuBar = new JMenuBar();

    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic(KeyEvent.VK_F);
    buildFileMenu();

    this.examplesMenu = new JMenu("Examples");
    this.examplesMenu.setMnemonic(KeyEvent.VK_E);
    buildExamplesMenuItems();

    this.historyMenu = new JMenu("History");
    this.historyMenu.setMnemonic(KeyEvent.VK_H);
    appendHistory(OntoramaConfig.getCurrentExample().getRoot(), OntoramaConfig.getCurrentExample());

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
   *
   */
  private void buildExamplesMenuItems () {

    Iterator examplesIterator = examplesList.iterator();
    while (examplesIterator.hasNext()) {
      OntoramaExample curExample = (OntoramaExample) examplesIterator.next();
      //System.out.println("curExample = " + curExample.getName());
      JCheckBoxMenuItem curItem = new JCheckBoxMenuItem(curExample.getName(), false);
      if (curExample.isLoadFirst()) {
        setSelectedExampleMenuItem(curItem);
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
   *
   */
  private void setSelectedExampleMenuItem (JCheckBoxMenuItem selectItem) {
    // first deselect previously selected item
    Enumeration examplesEnum = menuItemExampleMapping.keys();
    while (examplesEnum.hasMoreElements()) {
      JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) examplesEnum.nextElement();
      curItem.setSelected(false);
    }

    // select given item
    selectItem.setSelected(true);
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
   *
   */
  public void appendHistory (String termName, OntoramaExample example) {

    String historyItemLabelName = termName + " (" + example.getName() + ") ";

    HistoryElement historyElement = new HistoryElement(historyItemLabelName, termName, example);

    JMenuItem historyItem = new JMenuItem(historyItemLabelName);
    this.menuItemHistoryMapping.put(historyItem , historyElement);
    historyItem.addActionListener(new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        JMenuItem historyItem = (JMenuItem) e.getSource();
        displayHistoryItem(historyItem);
      }
    });
    this.historyMenu.add(historyItem);
  }

  /**
   *
   */
  public void displayExample(JCheckBoxMenuItem menuItem) {
    System.out.println("displayExample for " + menuItem);

    // get corresponding example
    OntoramaExample example = (OntoramaExample) menuItemExampleMapping.get(menuItem);

    executeQuery(example.getRoot(), example, menuItem);

  }


  /**
   *
   */
  public void displayHistoryItem (JMenuItem historyItem) {
    System.out.println("displayHistoryItem for " + historyItem);
    HistoryElement historyElement = (HistoryElement) this.menuItemHistoryMapping.get(historyItem);
    // get corresponding example
    OntoramaExample example = historyElement.getExample();

    // indicate that this example is currently displayed one
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
    executeQuery(historyElement.getTermName(), example, correspondingExampleMenuItem);
  }

  /**
   *
   */
  private void executeQuery (String termName, OntoramaExample example,
                        JCheckBoxMenuItem correspondingExampleMenuItem) {
    // indicate that this example is currently displayed one
    setSelectedExampleMenuItem(correspondingExampleMenuItem);

    // reset details in OntoramaConfig
    OntoramaConfig.setCurrentExample(example);

    // create a new query
    Query query = new Query(termName);

    // get graph for this query and load it into app
    this.mainApp.executeQuery(query);

  }

  /**
   * close main application
   */
  private void sendCloseMainApp () {
    this.mainApp.closeMainApp();
  }
}