
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
import javax.swing.ActionMap;

import java.awt.Image;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.awt.AWTEvent;

import ontorama.ontologyConfig.*;

import ontorama.model.Graph;
import ontorama.model.GraphNode;

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

public class OntoTreeView implements KeyListener, MouseListener,
                                    TreeWillExpandListener,
                                    TreeSelectionListener,
                                    ViewEventObserver {

    private JScrollPane treeView;
    private JTree tree;
    private OntoTreeNode focusedNode;

    private Debug debug = new Debug(false);

    private ViewEventListener viewListener;

    private boolean KEY_IS_PRESSED = false;
    private boolean MOUSE_IS_PRESSED = false;
    private int pressedKey = -1;
    private int pressedMouseButton = -1;
    private KeyEvent curKeyEvent = null;
    private MouseEvent curMouseEvent = null;
    private GraphNode curGraphNode = null;

    /**
     * Constructor
     */
    public OntoTreeView(Graph graph, ViewEventListener viewListener)  {
        this.viewListener = viewListener;
        this.viewListener.addObserver(this);

        // build OntoTreeModel for this graph
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
            // case if this lookAndFeel doesn't exist
        }

        this.tree = new JTree(ontoTreeModel);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.setEditable(false);

        //Listen for when the selection changes.
        //this.tree.addTreeSelectionListener(this);

        this.tree.addTreeWillExpandListener(this);


        this.tree.addMouseListener(this);
        this.tree.addKeyListener(this);
        ActionMap actionMap = this.tree.getActionMap();
        //actionMap.put();
        System.out.println("ActionMap keys= " + actionMap.keys());


        ToolTipManager.sharedInstance().registerComponent(this.tree);

        this.tree.setCellRenderer(new OntoTreeRenderer());

        this.treeView = new JScrollPane(tree);
        tree.putClientProperty("JTree.lineStyle", "Angled");

//        System.out.println("InputEvent.ALT_GRAPH_MASK = " + InputEvent.ALT_GRAPH_MASK);
//        System.out.println("InputEvent.ALT_MASK = " + InputEvent.ALT_MASK);
//        System.out.println("InputEvent.BUTTON1_MASK = " + InputEvent.BUTTON1_MASK);
//        System.out.println("InputEvent.BUTTON2_MASK = " + InputEvent.BUTTON2_MASK);
//        System.out.println("InputEvent.BUTTON3_MASK = " + InputEvent.BUTTON3_MASK);
//        System.out.println("InputEvent.CTRL_MASK = " + InputEvent.CTRL_MASK);
//        System.out.println("InputEvent.META_MASK = " + InputEvent.META_MASK);
//        System.out.println("InputEvent.SHIFT_MASK = " + InputEvent.SHIFT_MASK);
//        System.out.println("MouseEvent.BUTTON1_MASK = " + MouseEvent.BUTTON1_MASK);
//        System.out.println("MouseEvent.MOUSE_PRESSED = " + MouseEvent.MOUSE_PRESSED);
//        System.out.println("MouseEvent.MOUSE_CLICKED = " + MouseEvent.MOUSE_CLICKED);
//        System.out.println("MouseEvent.MOUSE_RELEASED = " + MouseEvent.MOUSE_RELEASED);
//        System.out.println("AWTEvent.MOUSE_EVENT_MASK = " + AWTEvent.MOUSE_EVENT_MASK);
//        System.out.println("AWTEvent.MOUSE_MOTION_EVENT_MASK = " + AWTEvent.MOUSE_MOTION_EVENT_MASK);
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
     * Return TreeView as a JComponent
     * @param   -
     * @return  treeView
     */
    public JComponent getTreeViewPanel () {
        return (JComponent) this.treeView;
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
      debug.message("Tree-will-expand event detected" + e);
      System.out.println("Tree-will-expand event detected" + e);

      TreePath path = e.getPath();
      if (path == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        System.out.println("treeWillExpand: path == null");
        return;
      }
      OntoTreeNode treeNode = (OntoTreeNode) path.getLastPathComponent();
      GraphNode graphNode = treeNode.getGraphNode();
      viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
      return;

      //boolean cancelExpand = true;
      //if (cancelExpand) {
      //    //Cancel expansion.
      //    debug.message("Tree expansion cancelled" + e);
      //    System.out.println("Tree expansion cancelled" + e);
      //    throw new ExpandVetoException(e);
      //}
    }

    /**
     * Implementation of treeWillExpand for TreeWillExpandListener interface
     * See comments for treeWillExpand
     */
    public void treeWillCollapse(TreeExpansionEvent e)
                    throws ExpandVetoException {
      debug.message("Tree-will-collapse event detected" + e);
      System.out.println("Tree-will-collapse event detected" + e);

      TreePath path = e.getPath();
      if (path == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        System.out.println("treeWillCollapse: path == null");
        return;
      }
      OntoTreeNode treeNode = (OntoTreeNode) path.getLastPathComponent();
      GraphNode graphNode = treeNode.getGraphNode();

      viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
      return;

      //boolean cancelCollapse = true;
      //if (cancelCollapse) {
      //    //Cancel expansion.
      //    debug.message("Tree collapse cancelled" + e);
      //    System.out.println("Tree collapse cancelled" + e);
      //    throw new ExpandVetoException(e);
      //}

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

      System.out.println("... mouse event = " + e.getModifiers());
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
      System.out.println("mousePressed, selRow = " + selRow);
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath == null) {
        // mouse clicked not on a node, but somewhere else in the tree view
        return;
      }
      System.out.println("mousePressed, selPath = " + selPath);
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
             }
         }
         else if(e.getClickCount() == 2) {
             debug.message("mousePressed, double click,  row=" + selRow);
             viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_DOUBLECLICK);
         }
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

    private void notifyMouseKeyEvent (KeyEvent keyEvent, MouseEvent mouseEvent, GraphNode graphNode) {
      //System.out.println("notifyMouseKeyEvent, this.pressedKey = " + this.pressedKey + ", this.pressedMouseButton = " + this.pressedMouseButton);
      System.out.println("notifyMouseKeyEvent, InputEvent.CTRL_MASK = " + InputEvent.CTRL_MASK + ", InputEvent.BUTTON1_MASK = " + InputEvent.BUTTON1_MASK);
      int keyEventCode = keyEvent.getModifiers();
      int mouseEventCode = mouseEvent.getModifiers();
      int foo1 = ((InputEvent) keyEvent).getModifiers();
      int foo2 = ((InputEvent) mouseEvent).getModifiers();
      System.out.println("notifyMouseKeyEvent, keyEventCode = " + keyEventCode + ", mouseEventCode = " + mouseEventCode);
      System.out.println("notifyMouseKeyEvent, foo1 = " + foo1 + ", foo2 = " + foo2);
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
      System.out.println();
      System.out.println("******* treeView got focus for node " + node.getName());

      OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
      if (treeNode == null) {
              return;
      }
      TreePath path = treeNode.getTreePath();
      this.tree.setSelectionPath(path);

      //treeNode.setFocus();
      System.out.println();
    }

    /**
     *
     */
    public void toggleFold ( GraphNode node) {
      System.out.println("******* treeView got toggleFold for node " + node.getName());

      OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
      if (treeNode == null) {
              return;
      }
      TreePath path = treeNode.getTreePath();
      int row = this.tree.getRowForPath(path);
      System.out.println("this.tree.isExpanded(row) = " + this.tree.isExpanded(row));

      if (this.tree.isExpanded(row)) {
              System.out.println("expanding");
              this.tree.expandRow(row);
      }
      else {
              System.out.println("collapsing");
              this.tree.collapseRow(row);

      }

    }

    /**
     *
     */
    public void query ( GraphNode node) {
      System.out.println("******* treeView got QUERY for node " + node.getName());
    }

}

