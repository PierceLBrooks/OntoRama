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
import ontorama.model.QueryStartEvent;
import ontorama.ontotools.query.Query;
import ontorama.conf.examplesConfig.OntoramaExample;
import org.tockit.events.EventBroker;

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
     * event broker capable of processing query events.
     */
    private EventBroker _eventBroker;


    /**
     *
     */
    public ExamplesMenu(EventBroker eventBroker) {
        super("Examples");
        _eventBroker = eventBroker;
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

        // reset details in OntoramaConfig
        OntoramaConfig.setCurrentExample(example);

        // create a new query
        Query query = new Query(example.getRoot(), OntoramaConfig.getEdgeTypesList());

        // get graph for this query and load it into app
        _eventBroker.processEvent(new QueryStartEvent(query));

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
     */
    private void buildExamplesMenuItems() {

        Iterator examplesIterator = _examplesList.iterator();
        while (examplesIterator.hasNext()) {
            OntoramaExample curExample = (OntoramaExample) examplesIterator.next();
            JCheckBoxMenuItem curItem = new JCheckBoxMenuItem(curExample.getName(), false);
            _menuItemExampleMapping.put(curItem, curExample);

            curItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JCheckBoxMenuItem sourceItem = (JCheckBoxMenuItem) e.getSource();
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