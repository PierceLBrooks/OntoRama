package ontorama.backends.p2p.gui;

import javax.swing.*;
import java.awt.*;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:04:28
 * To change this template use Options | File Templates.
 */
public class P2PMainPanel extends JFrame {

    private static final String _title = "P2P";
    private static final String _peerPanelTitle = "Peers";
    private static final String _peerPanelToolTip = "Information about group you have goined and other peers can be found here";
    private static final String _changePanelTitle = "Updates from other peers";
    private static final String _changePanelToolTip = "View updates recieved from other peers";

    PeersPanel peerPanel;
    ChangePanel changePanel;


    public P2PMainPanel(Frame parent) {
        super(_title);

        peerPanel = new PeersPanel();
        changePanel = new ChangePanel();

        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.addTab(_changePanelTitle, null, changePanel, _changePanelToolTip);
        tabbedPanel.setSelectedComponent(changePanel);
        tabbedPanel.addTab(_peerPanelTitle, null, peerPanel, _peerPanelToolTip);

        Container contentPanel = getContentPane();
        contentPanel.add(tabbedPanel);
        pack();
        setLocationOnScreen(parent);
    }

    public void showP2PPanel (boolean show) {
        setVisible(show);
    }

    public PeersPanel getPeerPanel() {
        return peerPanel;
    }

    public ChangePanel getChangePanel() {
        return changePanel;
    }

    private void setLocationOnScreen (Frame parent) {
        Dimension parentSize = parent.getSize();
        Point parentLocation = parent.getLocation();

        Dimension p2pPanelSize = getSize();

        double x = parentLocation.getX() + parentSize.getWidth() - p2pPanelSize.getWidth();
        double y = parentLocation.getY() + parentSize.getHeight() - p2pPanelSize.getHeight();

        setLocation((int) x, (int) y);
    }


}