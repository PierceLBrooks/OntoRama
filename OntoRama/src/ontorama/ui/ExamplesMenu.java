package ontorama.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.examplesConfig.OntoramaExample;

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

    /**
     * currently selected item.
     */
    private JCheckBoxMenuItem curSelectedExampleMenuItem;

    /**
     * @todo  shouldn't have this reference in this class
     */
    private OntoRamaApp _mainApp;

    /**
     *
     * @todo  shouldn't have to get reference to mainApp
     */
    public ExamplesMenu(OntoRamaApp mainApp) {
        super("Examples");
        _mainApp = mainApp;
        _examplesList = OntoramaConfig.getExamplesList();
        _menuItemExampleMapping = new Hashtable();
        _submenusMapping = new Hashtable();

        setMnemonic(KeyEvent.VK_E);

        buildExamplesMenuItems();
    }

    /**
     *
     */
    public void displayExample(JCheckBoxMenuItem menuItem) {
        //System.out.println("displayExample for " + menuItem);

        // get corresponding example
        OntoramaExample example = (OntoramaExample) _menuItemExampleMapping.get(menuItem);

        //boolean querySuccessfull = _ontoramaMenu.executeQuery(example.getRoot(), example, menuItem);
        boolean querySuccessfull = _mainApp.executeQueryForGivenExample(example.getRoot(), example);
        if (!querySuccessfull) {
            return;
        }
        // append history
        _mainApp.appendHistoryForGivenExample(example.getRoot(), example);

        // select corresponding example menu item
        setSelectedExampleMenuItem(example);
    }

    /**
     *
     */
    protected void setSelectedExampleMenuItem(OntoramaExample example) {
        Enumeration enum = _menuItemExampleMapping.keys();
        while (enum.hasMoreElements()) {
            JCheckBoxMenuItem exampleMenuItem = (JCheckBoxMenuItem) enum.nextElement();
            OntoramaExample curExample = (OntoramaExample) _menuItemExampleMapping.get(exampleMenuItem);
            exampleMenuItem.setSelected(false);
            if (curExample.equals(example)) {
                exampleMenuItem.setSelected(true);
            }
        }
    }

    /**
     *
     * @todo  fixed commented out
     */
    private void buildExamplesMenuItems() {

        Iterator examplesIterator = _examplesList.iterator();
        while (examplesIterator.hasNext()) {
            OntoramaExample curExample = (OntoramaExample) examplesIterator.next();
            //System.out.println("curExample = " + curExample.getName());
            JCheckBoxMenuItem curItem = new JCheckBoxMenuItem(curExample.getName(), false);
            _menuItemExampleMapping.put(curItem, curExample);

            curItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem sourceItem = (JCheckBoxMenuItem) e.getSource();
                    //System.out.println("action: " + sourceItem.getLabel());
                    displayExample(sourceItem);
                }
            });

            String subfolderName = curExample.getMenuSubfolderName();
            //System.out.println("subfolderName = " + subfolderName);

            if (subfolderName != null) {
                JMenu subMenu = findExamplesSubMenu(subfolderName);
                //System.out.println("adding item " + curItem.getName() + " to submenu " + subMenu.getName());
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


    /**
     *
     */
    private JCheckBoxMenuItem findExampleMenuItem(OntoramaExample example) {
        Enumeration enum = _menuItemExampleMapping.keys();
        JCheckBoxMenuItem correspondingExampleMenuItem = null;
        while (enum.hasMoreElements()) {
            JCheckBoxMenuItem curItem = (JCheckBoxMenuItem) enum.nextElement();
            OntoramaExample curExample = (OntoramaExample) _menuItemExampleMapping.get(curItem);
            if (curExample.equals(example)) {
                correspondingExampleMenuItem = curItem;
                break;
            }
        }
        return correspondingExampleMenuItem;
    }
}