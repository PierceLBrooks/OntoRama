package ontorama.backends.filemanager;

import ontorama.OntoramaConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 9/10/2002
 * Time: 09:13:28
 * To change this template use Options | File Templates.
 */
public class FileJMenu extends JMenu {
    private FileBackend _fileBackend = null;

    private String _menuName = "File Backend";
    private String _menuItemOpen = "Open...";
    private String _menuItemSave = "Save...";

    private JFileChooser _fileChooser;

    public FileJMenu(FileBackend fb){
        super();

        _fileBackend = fb;
        _fileChooser = new JFileChooser();

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
        int returnValue = _fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            _fileBackend.loadFile(_fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void saveFile () {
        int returnValue = _fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            _fileBackend.saveFile(_fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

}
