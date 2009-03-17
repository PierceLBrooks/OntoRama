package ontorama.backends.examplesmanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import ontorama.backends.examplesmanager.ExamplesBackend;
import ontorama.backends.examplesmanager.OntoramaExample;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 */

@SuppressWarnings("serial")
public class ExamplesMenu extends JMenu {

    /**
     * list of OntoramaExamples
     */
    private List<OntoramaExample> _examplesList;

    private Map<JRadioButtonMenuItem, OntoramaExample> _menuItemExampleMapping;

    /**
     * keys - name of submenu
     * values - submenu itself
     */
    private Map<String, JMenu> _submenusMapping;

   
    private ExamplesBackend _backend;
    
    private ButtonGroup _buttonGroup;


    public ExamplesMenu(ExamplesBackend backend) {
        super("Examples");
        _backend = backend;
        _examplesList = _backend.getExamplesList();
        _menuItemExampleMapping = new HashMap<JRadioButtonMenuItem, OntoramaExample>();
        _submenusMapping = new HashMap<String, JMenu>();
        setMnemonic(KeyEvent.VK_E);
        buildExamplesMenuItems();
    }
       
    public void displayExample(JRadioButtonMenuItem menuItem) {
        // get corresponding example
        OntoramaExample example = _menuItemExampleMapping.get(menuItem);
        
        _backend.processQueryFromExampleMenu(example);

        // select corresponding example menu item
        setSelectedExampleMenuItem(example);
    }

    public void setSelectedExampleMenuItem(OntoramaExample example) {
    	for (Entry<JRadioButtonMenuItem, OntoramaExample> entry : _menuItemExampleMapping.entrySet()) {
			JRadioButtonMenuItem exampleMenuItem = entry.getKey();
            OntoramaExample curExample = entry.getValue();
            if (curExample.equals(example)) {
                exampleMenuItem.setSelected(true);
            }
        }
    }

    private void buildExamplesMenuItems() {
    	_buttonGroup = new ButtonGroup();

    	for (OntoramaExample curExample : _examplesList) {
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

        JMenu subMenu = _submenusMapping.get(submenuName);
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