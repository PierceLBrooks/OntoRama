package ontorama.view;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class ErrorPopupMessage extends JOptionPane {

    public ErrorPopupMessage(String message, Component parent) {
        //message = "Sorry, " + message;
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        optionPane.showMessageDialog(parent, message);

    }

    public static void main(String[] args) {
        ErrorPopupMessage dialog = new ErrorPopupMessage("Testing Error Popup", null);
//    dialog.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//              System.exit(0);
//            }
//        });
    }

}