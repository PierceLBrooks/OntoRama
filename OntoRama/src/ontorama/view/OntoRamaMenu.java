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
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;


import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.view.action.*;
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

public class OntoRamaMenu extends JMenuBar {

//  private JMenuBar menuBar;
  private JMenu fileMenu;
  //private JMenu examplesMenu;
  private ExamplesMenu examplesMenu;
  private HistoryMenu historyMenu;
  //private JMenu historyMenu;
  private JMenu helpMenu;

  /**
   * @todo  implement Action events for all action events and remove
   * this reference to mainApp.
   */
  private OntoRamaApp mainApp;

  /**
   *
   */
  //private JToolBar toolBar;
  //private OntoRamaToolBar toolBar;



  /**
   *
   */
  public OntoRamaMenu (OntoRamaApp mainApp) {
    super();
    this.mainApp = mainApp;

//    this.menuBar = new JMenuBar();

    this.fileMenu = new JMenu("File");
    this.fileMenu.setMnemonic(KeyEvent.VK_F);
    this.fileMenu.add(mainApp._exitAction);

    this.examplesMenu = new ExamplesMenu(this);

    this.historyMenu = new HistoryMenu(this, this.mainApp);

    this.helpMenu = new JMenu("Help");
    this.helpMenu.add(mainApp._aboutAction);

    add(this.fileMenu);
    add(this.examplesMenu);
    add(this.historyMenu);
    add(this.helpMenu);
  }


  /**
   * @todo  fix commented out
   */
//  protected boolean executeQuery (String termName, OntoramaExample example,
//                        JCheckBoxMenuItem correspondingExampleMenuItem) {
  protected boolean executeQuery (String termName, OntoramaExample example) {

    // reset details in OntoramaConfig
    OntoramaConfig.setCurrentExample(example);

    // create a new query
    Query query = new Query(termName);

    // get graph for this query and load it into app
    if (this.mainApp.executeQuery(query)) {
      // indicate that this example is currently displayed one
      //historyMenu.setSelectedMenuItem(menuItemExampleMapping, correspondingExampleMenuItem);
      //historyMenu.enableBackForwardButtons();
      return true;
    }
    return false;
  }


  /**
   *
   */
  protected void appendHistory (String termName, OntoramaExample example) {
    historyMenu.appendHistory(termName, example);
  }

  /**
   *
   */
  protected void setSelectedHistoryMenuItem (OntoramaExample example) {
    historyMenu.setSelectedHistoryMenuItem(example);
  }

  /**
   *
   */
  protected void setSelectedExampleMenuItem (OntoramaExample example) {
    examplesMenu.setSelectedExampleMenuItem(example);
  }

}