
package ontorama.tree.view;

import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.ExpandVetoException;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.Icon;
import javax.swing.ImageIcon;
//import javax.swing.ActionMap;

import java.awt.Image;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.awt.AWTEvent;
import java.awt.Event;

import ontorama.ontologyConfig.*;

import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.Edge;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.model.OntoTreeBuilder;

import ontorama.util.Debug;
import ontorama.util.event.ViewEventListener;
import ontorama.util.event.ViewEventObserver;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

//public class OntoTreeView implements OntoNodeObserver, TreeSelectionListener,
//                                      TreeWillExpandListener,
//                                      KeyListener, MouseListener,
//									  ViewEventObserver {

public class OntoTreeView extends JScrollPane implements KeyListener, MouseListener,
                                    TreeWillExpandListener,
                                    TreeSelectionListener,
                                    ViewEventObserver  {

    private JScrollPane treeView;
    private JTree tree;
    //private OntoTreeNode focusedNode;

    private Debug debug = new Debug(false);

    private ViewEventListener viewListener;

    private boolean KEY_IS_PRESSED = false;
    private boolean MOUSE_IS_PRESSED = false;
    private int pressedKey = -1;
    private int pressedMouseButton = -1;
    private KeyEvent curKeyEvent = null;
    private MouseEvent curMouseEvent = null;
    private GraphNode curGraphNode = null;

    private boolean cancelExpand = true;
    private boolean cancelCollapse = true;

    /**
     * Constructor
     */
    public OntoTreeView(ViewEventListener viewListener)  {
        super();

        this.viewListener = viewListener;
        this.viewListener.addObserver(this);


        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
            // case if this lookAndFeel doesn't exist
        }
    }

    /**
     *
     */
    public void setGraph (Graph graph) {
        // build OntoTreeModel for this graph
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

//        System.out.println("edges num = " + Edge.edges.size());
//        OntoTreeNode ontoTreeNode = (OntoTreeNode) ontoTreeModel.getRoot();
//        System.out.println("root node = " + ontoTreeNode);
//        TreeNode treeNode = (TreeNode) ontoTreeNode;
//        System.out.println("root treeNode = " + treeNode);
//        if (Edge.edges.size() == 0) {
//          // we have only root node
//          this.tree = new JTree((OntoTreeNode) ontoTreeModel.getRoot());
//        }
//        else {
          this.tree = new JTree(ontoTreeModel);
//        }


        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.setEditable(false);
        this.tree.putClientProperty("JTree.lineStyle", "Angled");
       

        //Listen for when the selection changes.
        //this.tree.addTreeSelectionListener(this);

        this.tree.addTreeWillExpandListener(this);

        this.tree.addMouseListener(this);
        this.tree.addKeyListener(this);

        ToolTipManager.sharedInstance().registerComponent(this.tree);

        this.tree.setCellRenderer(new OntoTreeRenderer());

        // fold/unfold all tree nodes depending on GraphNode.getFolded() value

        Iterator it = ontoTreeModel.getOntoTreeIterator();
        while (it.hasNext()) {
          OntoTreeNode node = (OntoTreeNode) it.next();
          GraphNode curGraphNode = node.getGraphNode();
          TreePath path = node.getTreePath();
          if (! curGraphNode.getFoldedState()) {
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
     * @param   TreeSelectionEvent e
     */
    public void valueChanged(TreeSelectionEvent e) {
      OntoTreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();
        debug.message("TreeSelectionListener ******** node " + node);
        System.out.println("TreeSelectionListener ******** node " + node);
        return;
    }

    /**
     * Implementation of treeWillCollapse for TreeWillExpandListener interface
     * This is implemented for cases when user clicks on '+'/'-' in the tree view
     * to collapse/expand rather then just double clicking on nodes in order to
     * collapse/expand.
     * This implementation is not neccessary if mousePressed is modified to work
     * out where exactly user has clicked (at the moment we just care only about
     * mouse clicks directly on the node, all other coordinates are ignored.)
     */
    public void treeWillExpand(TreeExpansionEvent e)
                throws ExpandVetoException {
      debug.message("Tree-will-expand event detected " + e);
      //System.out.println("Tree-will-expand event detected ");

      TreePath path = e.getPath();
      if (path == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        System.out.println("treeWillExpand: path == null");
        return;
      }

      OntoTreeNode treeNode = (OntoTreeNode) path.getLastPathComponent();
      GraphNode graphNode = treeNode.getGraphNode();
      //System.out.println("\n\nthis.tree.isCollapsed(path) = " + this.tree.isCollapsed(path));
      //System.out.println("\n\n treeView is sending toggleEvent to ViewEventListener, node = " + graphNode.getName());
      //viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);

      //boolean cancelExpand = true;
//      System.out.println("treeWillExpand: cancelExpand = " + cancelExpand);
//      if (cancelExpand) {
//          //Cancel expansion.
//          debug.message("Tree expansion cancelled " + e);
//          System.out.println("Tree expansion cancelled ");
//          throw new ExpandVetoException(e);
//      }
    }

    /**
     * Implementation of treeWillExpand for TreeWillExpandListener interface
     * See comments for treeWillExpand
     */
    public void treeWillCollapse(TreeExpansionEvent e)
                    throws ExpandVetoException {
      debug.message("Tree-will-collapse event detected " + e);
      //System.out.println("Tree-will-collapse event detected ");

      TreePath path = e.getPath();
      if (path == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        System.out.println("treeWillCollapse: path == null");
        return;
      }
      OntoTreeNode treeNode = (OntoTreeNode) path.getLastPathComponent();
      GraphNode graphNode = treeNode.getGraphNode();
      System.out.println("\n\nthis.tree.isCollapsed(path) = " + this.tree.isCollapsed(path));
      System.out.println("\n\n treeView is sending toggleEvent to ViewEventListener, node = " + graphNode.getName());
      //viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
      //return;

      //boolean cancelCollapse = true;
//      System.out.println("treeWillCollapse: cancelCollapse = " + cancelCollapse);
//      if (cancelCollapse) {
//          //Cancel expansion.
//          debug.message("Tree collapse cancelled " + e);
//          System.out.println("Tree collapse cancelled ");
//          throw new ExpandVetoException(e);
//      }

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

      //debug.message("keyPressed, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));

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
              notifyMouseKeyEvent (e, this.curMouseEvent, this.curGraphNode);
              return;
      }
      //debug.message("keyReleased, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));
    }

    /**
     *
     */
    public void keyTyped(KeyEvent e) {
      debug.message("keyTyped, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));
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
      //System.out.println("mousePressed, selRow = " + selRow);
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        return;
      }
      //System.out.println("mousePressed, selPath = " + selPath);
      OntoTreeNode treeNode = (OntoTreeNode) selPath.getLastPathComponent();
      GraphNode graphNode = treeNode.getGraphNode();
      this.curGraphNode = graphNode;
      if(selRow != -1) {
         if(e.getClickCount() == 1) {
             debug.message("mousePressed, single click,  row=" + selRow);
             if (this.KEY_IS_PRESSED) {
                 //this.pressedMouseButton = e.getModifiers();
                 notifyMouseKeyEvent (this.curKeyEvent, e, graphNode);
             }
             else {
                viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_SINGLECLICK);
                //viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
             }
         }
//         else if(e.getClickCount() == 2) {
//             debug.message("mousePressed, double click,  row=" + selRow);
//             viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
//         }
      }
    }

    /**
     *
     */
    public void mouseClicked (MouseEvent e) {
      //debug.message("mouseClicked");
    }

    /**
     *
     */
    public void mouseEntered (MouseEvent e) {
      //debug.message("mouseEntered");
    }

    /**
     *
     */
    public void mouseExited (MouseEvent e) {
      //debug.message("mouseExited");
    }

    /**
     *
     */
    public void mouseReleased (MouseEvent e) {
      this.pressedMouseButton = -1;
      this.MOUSE_IS_PRESSED = false;
      this.curMouseEvent = null;
      this.curGraphNode = null;
      //debug.message("mouseReleased");
    }

    /**
     *
     */
    private void notifyMouseKeyEvent (KeyEvent keyEvent, MouseEvent mouseEvent, GraphNode graphNode) {
      int keyEventCode = keyEvent.getModifiers();
      int mouseEventCode = mouseEvent.getModifiers();
      System.out.println("notifyMouseKeyEvent, Event.META_MASK = " + Event.META_MASK + ", mouseEventCode = " + mouseEventCode);
      //if ((keyEventCode == InputEvent.CTRL_MASK) && (mouseEventCode == InputEvent.BUTTON1_MASK)) {
      if (keyEventCode == InputEvent.CTRL_MASK)  {
              this.viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_SINGLECLICK_KEY_CTRL);
      }
    }

    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     * @todo	shouldn't need to check if treeNode == null. This is a hack! this should be fixed in graphBuilder
     */
    public void focus ( GraphNode node) {
      OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
      //if (treeNode == null) {
      //        return;
      //}
      TreePath path = treeNode.getTreePath();
      this.tree.setSelectionPath(path);
      this.tree.scrollPathToVisible(path);
    }

    /**
     *
     */
    public void toggleFold ( GraphNode node) {
      /*
      this.tree.repaint();

      OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
      //if (treeNode == null) {
      //        return;
      //}
      TreePath path = treeNode.getTreePath();
      System.out.println(".....path = " + path);
//      int row = this.tree.getRowForPath(path);
//      boolean isExpanded = this.tree.isExpanded(path);
      boolean isCollapsed = this.tree.isCollapsed(path);
//      System.out.println("this.tree.isExpanded = " + isExpanded);
      System.out.println(".....this.tree.isCollapsed = " + isCollapsed);

      boolean isFolded = node.getFoldedState();
      System.out.println("node.getFoldedState() = " + node.getFoldedState());
      //if (isCollapsed) {
      if (isFolded) {
        expandPath(path);
      }
      else {
        collapsePath(path);
      }
      this.tree.scrollPathToVisible(path);

        this.tree.repaint();
      */
    }

    /**
     *
     */
    private void expandPath (TreePath path) {
//      System.out.println(".....expanding");
      //this.tree.expandPath(path);
      cancelExpand = false;
      cancelCollapse = true;
      this.tree.fireTreeExpanded(path);
      //this.tree.expandRow(row);
    }

    /**
     *
     */
    private void collapsePath (TreePath path) {
//        System.out.println(".....collapsing");
        //this.tree.collapsePath(path);
        cancelExpand = true;
        cancelCollapse = false;
        this.tree.fireTreeCollapsed(path);
        //this.tree.collapseRow(row);
    }

    /**
     *
     */
    public void query ( GraphNode node) {
      //System.out.println("******* treeView got QUERY for node " + node.getName());
    }

}

