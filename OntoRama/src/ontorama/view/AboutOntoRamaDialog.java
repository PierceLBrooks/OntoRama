
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
import javax.swing.Icon;
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

    ImageMapping.loadImages();

    JLabel label1 = new JLabel ("OntoRama");
    JLabel label2 = new JLabel ("Version: 0.0");

    //JPanel panel3 = new JPanel ();
    //panel3.add(ImageMapping.dstcLogoImage.getImage());

    JLabel label3 = new JLabel ("Brought to you by      ");
    JLabel label4 = new JLabel ("DSTC ", (Icon) ImageMapping.dstcLogoImage, JLabel.CENTER);
    JLabel label5 = new JLabel (" and ");
    JLabel label6 = new JLabel ("KVO ", (Icon) ImageMapping.kvoLogoImage, JLabel.CENTER);
    //panel3.add(label3);

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
    getContentPane().add(label4);
    getContentPane().add(label5);
    getContentPane().add(label6);
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