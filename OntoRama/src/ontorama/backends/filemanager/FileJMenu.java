package ontorama.backends.filemanager;

import javax.swing.*;
import java.awt.*;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 9/10/2002
 * Time: 09:13:28
 * To change this template use Options | File Templates.
 */
public class FileJMenu extends JMenu {
    private FileBackend _fileBackend = null;

    private String _menuName = "File Bck";
    private String _menuItemOpen = "Open...";
    private String _menuItemSave = "Save...";

    public FileJMenu(FileBackend fb){
        super();

        _fileBackend = fb;

        setText(_menuName);

    }

}
