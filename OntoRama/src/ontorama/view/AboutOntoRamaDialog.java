
package ontorama.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
//import java.awt.*;
import java.awt.event.*;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.Box;

public class AboutOntoRamaDialog extends JDialog {

  //Frame owner;

  //public AboutOntoRamaDialog(Frame owner) {
  public AboutOntoRamaDialog(Component owner) {
    //this.owner = owner;
    System.out.println("AboutOntoRamaDialog");

    JLabel label1 = new JLabel ("OntoRama");
    JLabel label2 = new JLabel ("Version: 0.0");
    JLabel label3 = new JLabel ("Brought to you by DSTC and KVO       ");

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        close();
      }
    });
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton, BorderLayout.WEST);



    getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
    Dimension d = new Dimension(10,20);
    getContentPane().add(Box.createRigidArea(d));


    getContentPane().add(label1);
    getContentPane().add(label2);
    getContentPane().add(label3);
    getContentPane().add(buttonPanel);

    setTitle("About OntoRama");
    setBackground(Color.white);
    setSize(200,200);
    setLocationRelativeTo(owner);

    pack();
    show();

    addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //close();
        }
    });

  }

  /**
   *
   */
  public void close () {
    dispose();

  }
}