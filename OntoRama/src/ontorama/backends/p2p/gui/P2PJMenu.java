/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 10, 2002
 * Time: 10:59:41 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.gui;

import java.awt.Frame;
import java.io.File;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.filechooser.FileFilter;

import ontorama.backends.filemanager.Util;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.action.ActionGroupSearch;
import ontorama.backends.p2p.gui.action.ActionShowJoinGroupDialog;
import ontorama.backends.p2p.gui.action.ActionShowLeaveGroupDialog;
import ontorama.backends.p2p.gui.action.ActionResetChangePanel;
import ontorama.ui.OntoRamaApp;

public class P2PJMenu extends JMenu {

    private P2PBackend _p2pBackend;
    private Frame _parentFrame;

    private static final String _menuName = "P2P";

    private Action _searchAction;
    private Action _joinGroupAction;
    private Action _leaveGroupAction;
    private Action _resetChangePanelAction;
    
    public P2PJMenu (P2PBackend p2pBackend) {
        super();
        _p2pBackend = p2pBackend;
    	System.out.println("p2p menu, p2p backend = " + _p2pBackend);
        _parentFrame = OntoRamaApp.getMainFrame();
        setText(_menuName);
        
        _searchAction = new ActionGroupSearch("Group search", p2pBackend);
        add(_searchAction);
        addSeparator();

        _joinGroupAction = new ActionShowJoinGroupDialog("Join Group", p2pBackend);
        add(_joinGroupAction);
        _leaveGroupAction = new ActionShowLeaveGroupDialog("Leave Group", _p2pBackend);
        add(_leaveGroupAction);
        addSeparator();

        _resetChangePanelAction = new ActionResetChangePanel("Reset Change Panel", _p2pBackend);
        add(_resetChangePanelAction);
        addSeparator();
           	
    	setActionsEnabled(false);
        
    }
   
    private void setActionsEnabled (boolean actionIsEnabled) {
//    	_searchAction.setEnabled(isEnabled);
//    	_joinGroupAction.setEnabled(isEnabled);
//    	_leaveGroupAction.setEnabled(isEnabled);
//    	_updatePanelAction.setEnabled(isEnabled);
//    	_resetChangePanelAction.setEnabled(isEnabled);
    }   
    
    private class P2PFileFilter extends FileFilter {
    	
    	private String p2pExtension;
    	
		/**
		 * Constructor for P2PFileFilter.
		 */
		public P2PFileFilter(String extension) {
			this.p2pExtension = extension;
		}

    	/**
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}

			/// @todo shouldn't have to reference stuff from the file backend.
			String extension = Util.getExtension(file);
			if (extension == null) {
				return false;
			}

			if (extension.equals(this.p2pExtension)) {
				return true;
			}
			return false;
		}

		/**
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			String descr = "OntoRama P2P files";
			return descr;
		}

	}

}
