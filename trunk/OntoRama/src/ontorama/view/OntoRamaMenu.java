package ontorama.view;

import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;

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

  /**
   *
   */


  /**
   *
   */
  public OntoRamaMenu (OntoRamaApp mainApp) {
    this.mainApp = mainApp;

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
    MenuItem item1 = new MenuItem("example1");
    item1.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        MenuItem sourceItem = (MenuItem) e.getSource();
        System.out.println("action: " + sourceItem.getLabel());
        displayExample(sourceItem);
      }
    });
    this.examplesMenu.add(item1);

  }

  /**
   *
   */
  public void displayExample(MenuItem menuItem) {
    System.out.println("displayExample for " + menuItem);
    Query query = new Query("node1");
    this.mainApp.executeQueryForNewGraph(this.mainApp.getGraphFromQuery(query));
  }
}