package ontorama.view;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.JButton;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 *  unascribed
 * @version 1.0
 */

public class OntoRamaToolBar extends JToolBar {

  /**
   *
   */
//  private JToolBar toolBar;

  /**
   *
   */
  public OntoRamaToolBar() {

    //toolBar = new JToolBar();
    super();

    JButton backButton = new JButton("Back");
    //toolBar.add(backButton);
    add(backButton);

    JButton forwardButton = new JButton("Forward");
    //toolBar.add(forwardButton);
    add(forwardButton);
  }

  /**
   *
   */
//  public JToolBar getToolBar () {
//    return this.toolBar;
//  }
}