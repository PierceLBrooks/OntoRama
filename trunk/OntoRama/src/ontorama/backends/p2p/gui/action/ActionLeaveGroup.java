package ontorama.backends.p2p.gui.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 12:11:12
 * To change this template use Options | File Templates.
 */
public class ActionLeaveGroup extends AbstractAction {
         public ActionLeaveGroup(String name) {
             super(name);
         }
         public void actionPerformed(ActionEvent e) {
             System.out.println("...action leave group");
         }

}
