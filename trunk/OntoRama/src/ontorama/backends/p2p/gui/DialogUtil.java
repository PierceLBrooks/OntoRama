package ontorama.backends.p2p.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 10:22:19
 * To change this template use Options | File Templates.
 */
public class DialogUtil{
	
	public static final String newGroupNameLabel = "Name";
	public static final String newGroupDescriptionLabel = "Description";

    public static void centerDialog(Frame parent, JDialog dialog) {
        Point parentLocation = parent.getLocation();
        double locX = parentLocation.getX();
        double locY = parentLocation.getY();

        Dimension parentSize = parent.getSize();
        double parentWidth = parentSize.getWidth();
        double parentHeight = parentSize.getHeight();

        double x = locX + (parentWidth - dialog.getSize().getWidth())/2;
        double y = locY + (parentHeight - dialog.getSize().getHeight())/2;
        dialog.setLocation((int) x, (int) y);
    }

    public static JPanel buildButtonsPanel(final JButton okButton, JButton cancelButton) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(okButton);
        return buttonPanel;
    }

    public static boolean textInputIsValid (Component parent, String text, String promptName) {
        if (text.length() <= 0) {
            JOptionPane.showMessageDialog(parent, "Please enter " + promptName + ".");
            return false;
        }
        return true;
    }
    
    public static JTextField createNewGroupNameTextField() {
    	JTextField field = new JTextField(20);
    	field.setToolTipText("Type name of a group you want to create");
    	return field;
    }
    
    public static JTextField createNewGroupDescriptionTextField() {
    	JTextField field = new JTextField(40);
    	field.setToolTipText("Type description for this group");
    	return field;
    }



}
