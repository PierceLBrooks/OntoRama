package ontorama.backends.p2p.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 11/10/2002
 * Time: 13:04:28
 * To change this template use Options | File Templates.
 */
public class P2PMainPanel extends JPanel {

    private static final String _peerPanelTitle = "Peers";
    private static final String _peerPanelToolTip = "Information about group you have goined and other peers can be found here";
    private static final String _changePanelTitle = "Updates from other peers";
    private static final String _changePanelToolTip = "View updates recieved from other peers";

    PeersPanel peerPanel;
    ChangePanel changePanel;
    SearchPanel searchPanel;


    public P2PMainPanel() {
        super();
        this.setLayout(new GridLayout(1,1));

        peerPanel = new PeersPanel();
        changePanel = new ChangePanel();
        searchPanel = new SearchPanel();

        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.addTab(_changePanelTitle, null, changePanel, _changePanelToolTip);
        tabbedPanel.setSelectedComponent(changePanel);
        tabbedPanel.addTab(_peerPanelTitle, null, peerPanel, _peerPanelToolTip);
        tabbedPanel.addTab("Changes", null, changePanel, "See changes");
        tabbedPanel.addTab("Search", null, searchPanel, "Search for a term");

		add(tabbedPanel);
    }

    public PeersPanel getPeerPanel() {
        return peerPanel;
    }

    public ChangePanel getChangePanel() {
        return changePanel;
    }


}
