package ontorama.backends.p2p.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ontorama.backends.p2p.P2PBackend;

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
    private static final String _groupsPanelTitle = "Groups";
    private static final String _groupsPanelToolTip = "Join groups, create new groups";

	GroupsPanel groupsPanel;
    PeersPanel peerPanel;
    ChangePanel changePanel = new ChangePanel();
    SearchPanel searchPanel;


	/**
	 * @todo don't like passing backend as parameter here (needed so we can
	 * operate on groups in GroupsPanel). Better solution would be to handle
	 * this with event instead.
	 */
    public P2PMainPanel(P2PBackend p2pBackend) {
        super();
               
    	groupsPanel = new GroupsPanel(p2pBackend);
    	searchPanel = new SearchPanel(p2pBackend.getEventBroker());
    	peerPanel = new PeersPanel(p2pBackend);
        
        this.setLayout(new GridLayout(1,1));

        JTabbedPane tabbedPanel = new JTabbedPane();
        tabbedPanel.addTab(_changePanelTitle, null, changePanel, _changePanelToolTip);
        tabbedPanel.setSelectedComponent(changePanel);
        tabbedPanel.addTab(_peerPanelTitle, null, peerPanel, _peerPanelToolTip);
		tabbedPanel.addTab(_groupsPanelTitle, null, groupsPanel, _groupsPanelToolTip);
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

	public GroupsPanel getGroupsPanel() {
		return groupsPanel;
	}

}
