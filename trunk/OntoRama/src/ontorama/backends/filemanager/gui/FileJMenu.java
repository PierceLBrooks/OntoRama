package ontorama.backends.filemanager.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;

import ontorama.backends.filemanager.FileBackend;
import ontorama.ui.OntoRamaApp;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 9/10/2002
 * Time: 09:13:28
 * To change this template use Options | File Templates.
 */
public class FileJMenu extends JMenu {
    private FileBackend _fileBackend = null;

    private String _menuName = "File";
    private String _menuItemOpen = "Open...";
    private String _menuItemSave = "Save...";

    private JFileChooser _fileChooser;

    public FileJMenu(FileBackend fb){
        super();

        _fileBackend = fb;
        _fileChooser = new JFileChooser();
        _fileChooser.addChoosableFileFilter(new FileBackendFileFilter(_fileBackend));

        setText(_menuName);

        Action openAction = new AbstractAction(_menuItemOpen) {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        };

        Action saveAction = new AbstractAction(_menuItemSave) {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        };

        add(openAction);
        add(saveAction);
    }

    private void openFile () {
        int returnValue = _fileChooser.showOpenDialog(OntoRamaApp.getMainFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            _fileBackend.loadFile(_fileChooser.getSelectedFile());
        }
    }

    private void saveFile () {
        int returnValue = _fileChooser.showSaveDialog(OntoRamaApp.getMainFrame());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            _fileBackend.saveFile(_fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

}
