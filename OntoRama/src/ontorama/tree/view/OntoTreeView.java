package ontorama.tree.view;

import java.awt.Event;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ontorama.controller.NodeSelectedEvent;
import ontorama.controller.QueryEvent;
import ontorama.model.graph.controller.GraphViewFocusEventHandler;
import ontorama.model.graph.view.GraphView;
import ontorama.model.Graph;
import ontorama.model.Node;
import ontorama.tree.model.OntoTreeBuilder;
import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.util.Debug;
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
        TreeSelectionListener, GraphView {

    private JTree tree;
    //private OntoTreeNode focusedNode;

    private Debug debug = new Debug(false);

    private EventBroker eventBroker;

    private Graph graph;

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

        this.eventBroker = eventBroker;
        new GraphViewFocusEventHandler(eventBroker, this);

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
    public void setGraph(Graph graph) {
        this.graph = graph;
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        this.tree = new JTree(ontoTreeModel);

        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.setEditable(false);
        this.tree.putClientProperty("JTree.lineStyle", "Angled");

        this.tree.addMouseListener(this);
        this.tree.addKeyListener(this);

        ToolTipManager.sharedInstance().registerComponent(this.tree);

        this.tree.setCellRenderer(new OntoTreeRenderer(this.graph));

        Iterator it = ontoTreeModel.getOntoTreeIterator();
        while (it.hasNext()) {
            OntoTreeNode node = (OntoTreeNode) it.next();
            Node curGraphNode = node.getGraphNode();
            TreePath path = node.getTreePath();
            if (!curGraphNode.getFoldedState()) {
                this.tree.expandPath(path);
            }
        }

        setViewportView(tree);

        tree.putClientProperty("JTree.lineStyle", "Angled");
        this.tree.setScrollsOnExpand(true);

        repaint();
        this.tree.repaint();
    }

    /**
     * Implementation of valueChanged needed for interface
     * implementation of TreeSelectionListener.
     * Overriten here so we can control single mouse clicks behaviour ourselves.
     * @param   e - TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
        OntoTreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();
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

        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        if (selRow == -1) {
        	return;
        }
        if (e.getClickCount() == 1) {
            debug.message("mousePressed, single click,  row=" + selRow);
            if (this.KEY_IS_PRESSED) {
                notifyMouseKeyEvent(this.curKeyEvent, e);
            } else {
				Node graphNode = getGraphNodeFromMouseEvent(e);
				if (graphNode == null ) return;                	
                eventBroker.processEvent(new NodeSelectedEvent(graphNode));
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
		Node graphNode = getGraphNodeFromMouseEvent(mouseEvent);
		if (graphNode == null ) return;
    	
        int keyEventCode = keyEvent.getModifiers();
        int mouseEventCode = mouseEvent.getModifiers();
        System.out.println("notifyMouseKeyEvent, Event.META_MASK = " + Event.META_MASK + ", mouseEventCode = " + mouseEventCode);
        //if ((keyEventCode == InputEvent.CTRL_MASK) && (mouseEventCode == InputEvent.BUTTON1_MASK)) {
        if (keyEventCode == InputEvent.CTRL_MASK) {
            eventBroker.processEvent(new QueryEvent(graphNode));
        }
    }
    
    
    /**
     * 
     */
    private Node getGraphNodeFromMouseEvent (MouseEvent mouseEvent) {
        TreePath selPath = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (selPath == null) {
            // mouse clicked not on a node, but somewhere else in the tree view
            return null;
        }
		return  getGraphNodeFromTreePath(selPath);
    }
   
    

	/**
	 * 
	 */
	private Node getGraphNodeFromTreePath(TreePath selPath) {
		OntoTreeNode treeNode = (OntoTreeNode) selPath.getLastPathComponent();
		Node graphNode = treeNode.getGraphNode();
		return graphNode;
	}

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     */
    public void focus(Node node) {
        OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
        TreePath path = treeNode.getTreePath();
        this.tree.setSelectionPath(path);
        this.tree.scrollPathToVisible(path);
    }


    public void repaint() {
        super.repaint();
    }


}

