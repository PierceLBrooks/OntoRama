package ontorama.backends.examplesmanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import ontorama.backends.examplesmanager.ExamplesBackend;
import ontorama.backends.examplesmanager.OntoramaExample;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class ExamplesMenu extends JMenu {

    /**
     * list of OntoramaExamples
     */
    private List _examplesList;

    /**
     * keys - menu items
     * values - examples
     */
    private Hashtable _menuItemExampleMapping;

    /**
     * keys - name of submenu
     * values - submenu itself
     */
    private Hashtable _submenusMapping;

   
    private ExamplesBackend _backend;
    
    private ButtonGroup _buttonGroup;


    /**
     *
     */
    public ExamplesMenu(ExamplesBackend backend) {
        super("Examples");
        _backend = backend;
        _examplesList = _backend.getExamplesList();
        _menuItemExampleMapping = new Hashtable();
        _submenusMapping = new Hashtable();
        setMnemonic(KeyEvent.VK_E);
        buildExamplesMenuItems();
    }
       
    /**
     *
     */
    public void displayExample(JRadioButtonMenuItem menuItem) {
        //System.out.println("displayExample for " + menuItem);

        // get corresponding example
        OntoramaExample example = (OntoramaExample) _menuItemExampleMapping.get(menuItem);
        
        _backend.processQueryFromExampleMenu(example);

        // select corresponding example menu item
        setSelectedExampleMenuItem(example);
    }

    /**
     *
     */
    public void setSelectedExampleMenuItem(OntoramaExample example) {
        Enumeration enumeration = _menuItemExampleMapping.keys();
        while (enumeration.hasMoreElements()) {
            JRadioButtonMenuItem exampleMenuItem = (JRadioButtonMenuItem) enumeration.nextElement();
            OntoramaExample curExample = (OntoramaExample) _menuItemExampleMapping.get(exampleMenuItem);
            if (curExample.equals(example)) {
                exampleMenuItem.setSelected(true);
            }
        }
    }

    /**
     *
     */
    private void buildExamplesMenuItems() {
    	_buttonGroup = new ButtonGroup();

        Iterator examplesIterator = _examplesList.iterator();
        while (examplesIterator.hasNext()) {
            OntoramaExample curExample = (OntoramaExample) examplesIterator.next();
            JRadioButtonMenuItem curItem = new JRadioButtonMenuItem(curExample.getName(), false);
            _menuItemExampleMapping.put(curItem, curExample);
            _buttonGroup.add(curItem);

            curItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JRadioButtonMenuItem sourceItem = (JRadioButtonMenuItem) e.getSource();
                    displayExample(sourceItem);
                }
            });

            String subfolderName = curExample.getMenuSubfolderName();

            if (subfolderName != null) {
                JMenu subMenu = findExamplesSubMenu(subfolderName);
                subMenu.add(curItem);
            } else {
                add(curItem);
            }
        }

    }


    /**
     * Find submenu for given name. (check if such submenu already exists,
     * if so - return it. otherwise - create new and return the new one).
     */
    private JMenu findExamplesSubMenu(String submenuName) {

        JMenu subMenu = (JMenu) _submenusMapping.get(submenuName);
        if (subMenu == null) {
            subMenu = makeNewExamplesSubMenu(submenuName);
        }
        return subMenu;
    }

    /**
     * create new submenu in Examples menu
     */
    private JMenu makeNewExamplesSubMenu(String submenuName) {
        JMenu newSubmenu = new JMenu(submenuName);
        addSeparator();
        add(newSubmenu);

        _submenusMapping.put(submenuName, newSubmenu);

        return newSubmenu;
    }
}