
package ontorama.view;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.BorderFactory;
import javax.swing.border.*;


public class AboutOntoRamaDialog extends JDialog {

  private Color backgroundColor = Color.white;

  public AboutOntoRamaDialog(Component owner) {
    System.out.println("AboutOntoRamaDialog");

    ImageMapping.loadImages();

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
    Dimension d = new Dimension(20,20);
    mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


    JLabel headingLabel = new JLabel ("OntoRama");
    headingLabel.setFont(new java.awt.Font("Dialog", 1, 26));
    headingLabel.setForeground(Color.blue);
    headingLabel.setBackground(backgroundColor);
    headingLabel.setText("OntoRama");
    headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel label2 = new JLabel ("Brought to you by  DSTC and KVO");
    label2.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel imagesPanel = new JPanel();
    BoxLayout imagesLayout = new BoxLayout(imagesPanel, BoxLayout.X_AXIS);
    imagesPanel.setLayout(imagesLayout);
    imagesPanel.setBackground(backgroundColor);
    imagesPanel.add(new JLabel ( (Icon) ImageMapping.dstcLogoImage));
    Dimension d2 = new Dimension (80, 0);
    imagesPanel.add(Box.createRigidArea(d2));
    imagesPanel.add(new JLabel ( (Icon) ImageMapping.kvoLogoImage));
    imagesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel copyrightLabel = new JLabel("Copyright (c) 1999-2002 DSTC");
    copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed (ActionEvent e) {
        close();
      }
    });
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(backgroundColor);
    buttonPanel.add(okButton);





    mainPanel.setBackground(backgroundColor);

    mainPanel.add(headingLabel);
    mainPanel.add(Box.createRigidArea(d));
    mainPanel.add(label2);
    mainPanel.add(Box.createRigidArea(d));
    mainPanel.add(imagesPanel);
    mainPanel.add(Box.createRigidArea(d));
    mainPanel.add(copyrightLabel);
    mainPanel.add(Box.createRigidArea(d));


    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    setTitle("About OntoRama");
    setBackground(Color.white);
    //setSize(400,400);
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