package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;


public class AboutOntoRamaDialog extends JDialog {
	private final static String licenseFileLoc = "LICENSE.txt";

    private Color backgroundColor = Color.white;

    public AboutOntoRamaDialog(Component owner) {
        System.out.println("AboutOntoRamaDialog");

        ImageMapping.loadImages();

        JPanel mainPanel = new JPanel();
		mainPanel.setBackground(backgroundColor);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        Dimension d = new Dimension(20, 20);
        mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel headingLabel = new JLabel("OntoRama");
        headingLabel.setFont(new java.awt.Font("Dialog", 1, 26));
        headingLabel.setForeground(Color.blue);
        headingLabel.setBackground(backgroundColor);
        headingLabel.setText("OntoRama");
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(headingLabel);
		mainPanel.add(Box.createRigidArea(d));

        JLabel label2 = new JLabel("Brought to you by  DSTC and KVO");
        label2.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label2);
		mainPanel.add(Box.createRigidArea(d));

        JPanel imagesPanel = new JPanel();
        BoxLayout imagesLayout = new BoxLayout(imagesPanel, BoxLayout.X_AXIS);
        imagesPanel.setLayout(imagesLayout);
        imagesPanel.setBackground(backgroundColor);
        imagesPanel.add(new JLabel((Icon) ImageMapping.dstcLogoImage));
        Dimension d2 = new Dimension(80, 0);
        imagesPanel.add(Box.createRigidArea(d2));
        imagesPanel.add(new JLabel((Icon) ImageMapping.kvoLogoImage));
        imagesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(imagesPanel);
		mainPanel.add(Box.createRigidArea(d));

        JLabel copyrightLabel = new JLabel("Copyright (c) 1999-2003 DSTC");
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(copyrightLabel);
		mainPanel.add(Box.createRigidArea(d));

        try {
			JLabel licenseLabel = new JLabel("License: ");
			licenseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			String license = readLicense(licenseFileLoc);
			JTextArea licenseTextArea = new JTextArea(license, 30, 70);
			licenseTextArea.setEditable(false);
			JScrollPane licenseScrollPane = new JScrollPane(licenseTextArea);
			mainPanel.add(licenseLabel);
			mainPanel.add(licenseScrollPane);
			mainPanel.add(Box.createRigidArea(d));			
        }
        catch (IOException e) {
        	/// @todo should do something better here with the exception
        	System.err.println("Couldn't read license: " + e.getMessage());
        	e.printStackTrace();
        }

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(okButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setTitle("About OntoRama");
        setBackground(Color.white);
        //setSize(400,400);
        //setLocationRelativeTo(owner);

        pack();
        show();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //close();
            }
        });

    }

    private String readLicense(String licenseFileLoc) throws IOException {
    	InputStream licenseStream = this.getClass().getClassLoader().getResourceAsStream(licenseFileLoc);
    	BufferedReader br = new BufferedReader(new InputStreamReader(licenseStream));
		String line = br.readLine();
		StringBuffer license = new StringBuffer();
		while (line != null) {
			license.append(line);
			license.append("\n");
			line = br.readLine();
		}
		return license.toString();
	}

	/**
     *
     */
    public void close() {
        dispose();
    }
}