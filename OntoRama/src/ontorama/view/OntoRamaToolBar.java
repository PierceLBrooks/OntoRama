package ontorama.view;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.JButton;
import javax.swing.Action;

import ontorama.view.action.*;

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
   * @todo  should not pass reference to OntoRamaApp here
   */
  public OntoRamaToolBar(OntoRamaApp mainApp) {

    super();

//    Action backAction = new BackHistoryAction();
    JButton backButton = add(mainApp._backAction);

    add(backButton);

//    Action forwardAction = new ForwardHistoryAction();
    JButton forwardButton = add(mainApp._forwardAction);
    add(forwardButton);
  }

}