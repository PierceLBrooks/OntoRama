package ontorama.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

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
    	JOptionPane.showMessageDialog(parent, message);

    }

//    public static void main(String[] args) {
//        new ErrorPopupMessage("Testing Error Popup", null);
//    }

}