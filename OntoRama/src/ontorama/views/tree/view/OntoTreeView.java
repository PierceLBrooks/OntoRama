package ontorama.views.tree.view;

import java.awt.Event;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.events.TreeNodeSelectedEvent;
import ontorama.model.tree.controller.TreeViewFocusEventHandler;
import ontorama.model.tree.view.TreeView;
import ontorama.util.Debug;
import ontorama.views.tree.model.OntoTreeModel;
import ontorama.views.tree.model.OntoTreeNode;
import ontorama.views.tree.model.OntoTreeBuilder;
import org.tockit.events.EventBroker;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class OntoTreeView extends JScrollPane implements KeyListener, MouseListener,
        TreeSelectionListener, TreeView {

    private JTree _treeView;
    //private OntoTreeNode focusedNode;

    private Debug debug = new Debug(false);

    private EventBroker _eventBroker;

    private Tree _tree;

    private boolean KEY_IS_PRESSED = false;
    private boolean MOUSE_IS_PRESSED = false;
    private int pressedKey = -1;
    private int pressedMouseButton = -1;
    private KeyEvent curKeyEvent = null;
    private MouseEvent curMouseEvent = null;

    /**
     * Constructor
     */
    public OntoTreeView( EventBroker eventBroker) {
        super();

        _eventBroker = eventBroker;
        new TreeViewFocusEventHandler(_eventBroker, this);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        } catch (Exception e) {
            // case if this lookAndFeel doesn't exist
        }
    }

    /**
     *
     */
    public void setTree(Tree tree) {
        _tree = tree;
        OntoTreeModel ontoTreeModel = new OntoTreeModel(_tree);

        _treeView = new JTree(ontoTreeModel);

        _treeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _treeView.setEditable(false);
        _treeView.putClientProperty("JTree.lineStyle", "Angled");

        _treeView.addMouseListener(this);
        _treeView.addKeyListener(this);

        ToolTipManager.sharedInstance().registerComponent(this._treeView);

        _treeView.setCellRenderer(new OntoTreeRenderer());

        unfoldTree(ontoTreeModel);

        setViewportView(_treeView);

        _treeView.putClientProperty("JTree.lineStyle", "Angled");
        _treeView.setScrollsOnExpand(true);

        repaint();
        _treeView.repaint();
    }

    private void unfoldTree(OntoTreeModel ontoTreeModel) {
        List q = new LinkedList();
        OntoTreeNode rootNode = (OntoTreeNode) ontoTreeModel.getRoot();
        q.add(rootNode);

        while (! q.isEmpty()) {
            OntoTreeNode curNode = (OntoTreeNode) q.remove(0);
            Enumeration children = curNode.children();
            while (children.hasMoreElements())  {
                OntoTreeNode curChild = (OntoTreeNode) children.nextElement();
                q.add(curChild);
            }
            TreePath path = curNode.getTreePath();
            _treeView.expandPath(path);
        }
    }

    /**
     * Implementation of valueChanged needed for interface
     * implementation of TreeSelectionListener.
     * Overriten here so we can control single mouse clicks behaviour ourselves.
     * @param   e - TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        OntoTreeNode node = (OntoTreeNode) _treeView.getLastSelectedPathComponent();
        debug.message("TreeSelectionListener ******** node " + node);
        System.out.println("TreeSelectionListener ******** node " + node);
        return;
    }

    /**
     *
     */
    public void keyPressed(KeyEvent e) {
        this.KEY_IS_PRESSED = true;
        this.pressedKey = e.getModifiers();
        this.curKeyEvent = e;
        /*
      if (e.getModifiers() == InputEvent.ALT_GRAPH_MASK) {
        debug.message("keyEvent ALT_GRAPH_MASK");
      }
      if (e.getModifiers() == InputEvent.ALT_MASK) {
        debug.message("keyEvent ALT_MASK ");
      }
      if (e.getModifiers() == InputEvent.CTRL_MASK ) {
        debug.message("keyEvent CTRL_MASK ");
      }
      if (e.getModifiers() == InputEvent.META_MASK ) {
        debug.message("keyEvent META_MASK ");
      }
      if (e.getModifiers() == InputEvent.SHIFT_MASK ) {
        debug.message("keyEvent SHIFT_MASK ");
      }
	  */

    }

    /**
     *
     */
    public void keyReleased(KeyEvent e) {
        this.pressedKey = -1;
        this.KEY_IS_PRESSED = false;
        this.curKeyEvent = null;
        System.out.println("... key event = " + e.getModifiers());
        if (this.KEY_IS_PRESSED) {
            notifyMouseKeyEvent(e, this.curMouseEvent);
            
            return;
        }
    }

    /**
     *
     */
    public void keyTyped(KeyEvent e) {
        debug.message("keyTyped, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + KeyEvent.getKeyText(e.getKeyCode()));
    }

    /**
     *
     */
    public void mousePressed(MouseEvent e) {
        this.curMouseEvent = e;
        this.MOUSE_IS_PRESSED = true;
        this.pressedMouseButton = e.getModifiers();

        //System.out.println("... mouse event = " + e.getModifiers());
        /*
        if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
          debug.message("BUTTON1_MASK");
          System.out.println("BUTTON1_MASK");
          //this.pressedMouseButton = InputEvent.BUTTON1_MASK;
        }
        if (e.getModifiers() == InputEvent.BUTTON2_MASK) {
          debug.message("BUTTON2_MASK");
          System.out.println("BUTTON2_MASK");
          //this.pressedMouseButton = InputEvent.BUTTON2_MASK;
        }
        if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
          debug.message("BUTTON3_MASK ");
          System.out.println("BUTTON3_MASK ");
          //this.pressedMouseButton = InputEvent.BUTTON3_MASK;
        }
        if (e.getModifiers() == InputEvent.META_MASK ) {
          debug.message("META_MASK ");
          System.out.println("META_MASK ");
          //this.pressedMouseButton = InputEvent.META_MASK;
        }
        */

        int selRow = _treeView.getRowForLocation(e.getX(), e.getY());
        if (selRow == -1) {
        	return;
        }
        if (e.getClickCount() == 1) {
            debug.message("mousePressed, single click,  row=" + selRow);
            if (this.KEY_IS_PRESSED) {
                notifyMouseKeyEvent(this.curKeyEvent, e);
            } else {
				TreeNode modelNode = getModelNodeFromMouseEvent(e);
				if (modelNode == null ) return;
                _eventBroker.processEvent(new TreeNodeSelectedEvent(modelNode));
            }
        }
    }

    /**
     *
     */
    public void mouseClicked(MouseEvent e) {
        //debug.message("mouseClicked");
    }

    /**
     *
     */
    public void mouseEntered(MouseEvent e) {
        //debug.message("mouseEntered");
    }

    /**
     *
     */
    public void mouseExited(MouseEvent e) {
        //debug.message("mouseExited");
    }

    /**
     *
     */
    public void mouseReleased(MouseEvent e) {
        this.pressedMouseButton = -1;
        this.MOUSE_IS_PRESSED = false;
        this.curMouseEvent = null;
        //this.curGraphNode = null;
        //debug.message("mouseReleased");
    }

    /**
     *
     */
    private void notifyMouseKeyEvent(KeyEvent keyEvent, MouseEvent mouseEvent) {
		ontorama.model.tree.TreeNode modelNode = getModelNodeFromMouseEvent(mouseEvent);
		if (modelNode == null ) return;
    	
        int keyEventCode = keyEvent.getModifiers();
        int mouseEventCode = mouseEvent.getModifiers();
        System.out.println("notifyMouseKeyEvent, Event.META_MASK = " + Event.META_MASK + ", mouseEventCode = " + mouseEventCode);
        //if ((keyEventCode == InputEvent.CTRL_MASK) && (mouseEventCode == InputEvent.BUTTON1_MASK)) {
        if (keyEventCode == InputEvent.CTRL_MASK) {
//            _eventBroker.processEvent(new QueryEvent(graphNode));
        }
    }
    
    
    /**
     * 
     */
    private ontorama.model.tree.TreeNode getModelNodeFromMouseEvent (MouseEvent mouseEvent) {
        TreePath selPath = _treeView.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (selPath == null) {
            // mouse clicked not on a node, but somewhere else in the tree ui
            return null;
        }
		return  getModelNodeFromTreePath(selPath);
    }
   
    

	/**
	 * 
	 */
	private ontorama.model.tree.TreeNode getModelNodeFromTreePath(TreePath selPath) {
		OntoTreeNode treeNode = (OntoTreeNode) selPath.getLastPathComponent();
		ontorama.model.tree.TreeNode modelNode = treeNode.getModelTreeNode();
		return modelNode;
	}

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     */
    public void focus(TreeNode node) {
        OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
        TreePath path = treeNode.getTreePath();
        _treeView.setSelectionPath(path);
        _treeView.scrollPathToVisible(path);
    }


    public void repaint() {
        super.repaint();
    }


}

