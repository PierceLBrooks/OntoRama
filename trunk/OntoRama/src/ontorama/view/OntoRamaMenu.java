package ontorama.view;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;

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

  private MenuBar menuBar;
  private Menu examplesMenu;
  private Menu historyMenu;

  private OntoRamaApp mainApp;

  private List examplesList;

  private Hashtable menuItemExampleMapping;

  /**
   * keys - name of submenu
   * values - submenu itself
   */
  private Hashtable submenusMapping;

  /**
   *
   */


  /**
   *
   */
  public OntoRamaMenu (OntoRamaApp mainApp) {
    this.mainApp = mainApp;
    this.examplesList = OntoramaConfig.getExamplesList();

    this.menuItemExampleMapping = new Hashtable();

    this.submenusMapping = new Hashtable();

    this.menuBar = new MenuBar();

    this.examplesMenu = new Menu("Examples");
    buildExamplesMenuItems();

    this.historyMenu = new Menu("History");

    this.menuBar.add(this.examplesMenu);
    this.menuBar.add(this.historyMenu);
  }

  /**
   *
   */
  public MenuBar getMenuBar () {
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
      MenuItem curItem = new MenuItem(curExample.getName());
      this.menuItemExampleMapping.put(curItem,curExample);

      curItem.addActionListener(new ActionListener() {
        public void actionPerformed (ActionEvent e) {
          MenuItem sourceItem = (MenuItem) e.getSource();
          //System.out.println("action: " + sourceItem.getLabel());
          displayExample(sourceItem);
        }
      });

      String subfolderName = curExample.getMenuSubfolderName();
      //System.out.println("subfolderName = " + subfolderName);

      if (subfolderName != null) {
        Menu subMenu = findExamplesSubMenu(subfolderName);
        //System.out.println("adding item " + curItem.getName() + " to submenu " + subMenu.getName());
        subMenu.add(curItem);
      }
      else {
        this.examplesMenu.add(curItem);
      }
    }

  }

  /**
   *
   */
  public void displayExample(MenuItem menuItem) {
    System.out.println("displayExample for " + menuItem);

    OntoramaExample example = (OntoramaExample) menuItemExampleMapping.get(menuItem);

    OntoramaConfig.setNewExampleDetails(example);

    // create a new query
    Query query = new Query(example.getRoot());
    // get graph for this query and load it into app
    this.mainApp.executeQueryForNewGraph(this.mainApp.getGraphFromQuery(query));
  }

  /**
   *
   */
  private Menu findExamplesSubMenu (String submenuName) {

    Menu subMenu = (Menu) submenusMapping.get(submenuName);
    if (subMenu == null) {
      subMenu = makeNewExamplesSubMenu(submenuName);
    }

    return subMenu;
  }

  /**
   * create new submenu in Examples menu
   */
  private Menu makeNewExamplesSubMenu (String submenuName) {
        Menu newSubmenu = new Menu(submenuName);
        this.examplesMenu.addSeparator();
        this.examplesMenu.add(newSubmenu);

        this.submenusMapping.put(submenuName,newSubmenu);

        return newSubmenu;
  }
}