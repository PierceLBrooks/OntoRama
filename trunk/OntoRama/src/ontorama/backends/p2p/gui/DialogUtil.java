package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.awt.*;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 10:22:19
 * To change this template use Options | File Templates.
 */
public class DialogUtil{

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

    public static boolean textInputIsValid (JDialog dialog, String text, String promptName) {
        if (text.length() <= 0) {
            JOptionPane.showMessageDialog(dialog, "Please enter " + promptName + ".");
            return false;
        }
        return true;
    }



}
