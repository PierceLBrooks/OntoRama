
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
//import javax.swing.event.TreeExpansionListener;
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

import ontorama.ontologyConfig.*;

import ontorama.model.Graph;
import ontorama.model.GraphNode;

import ontorama.tree.model.OntoTreeModel;
import ontorama.tree.model.OntoTreeNode;
import ontorama.tree.model.OntoTreeBuilder;
import ontorama.tree.model.OntoNodeObserver;

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

public class OntoTreeView implements OntoNodeObserver, TreeSelectionListener,
                                      TreeWillExpandListener,
                                      KeyListener, MouseListener,
									  ViewEventObserver {


    private JScrollPane treeView;
    private JTree tree;
    private OntoTreeNode focusedNode;

    private Debug debug = new Debug(false);
	
	private ViewEventListener viewListener;


    /**
     * Constructor
     */
    public OntoTreeView(Graph graph, ViewEventListener viewListener)  {
		this.viewListener = viewListener;
		this.viewListener.addObserver(this);

        // build OntoTreeModel for this graph
        OntoTreeModel ontoTreeModel = new OntoTreeModel(graph);

        // sort out observers
        addOntoObservers(ontoTreeModel);

        try {
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
            // case if this lookAndFeel doesn't exist
        }

        //final JTree tree = new JTree(ontoTreeModel);
        this.tree = new JTree(ontoTreeModel);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.tree.setEditable(false);

        //Listen for when the selection changes.
        this.tree.addTreeSelectionListener(this);

        this.tree.addTreeWillExpandListener(this);


        this.tree.addMouseListener(this);
        this.tree.addKeyListener(this);
//        ActionMap actionMap = this.tree.getActionMap();
//        actionMap.put();
//        System.out.println("ActionMap keys= " + actionMap.keys());


        ToolTipManager.sharedInstance().registerComponent(this.tree);

        this.tree.setCellRenderer(new MyRenderer());

        this.treeView = new JScrollPane(tree);
        tree.putClientProperty("JTree.lineStyle", "Angled");
    }

    /**
     * Implementation of valueChanged needed for interface
     * implementation of TreeSelectionListener
     * @param   TreeSelectionEvent e
     */
    public void valueChanged(TreeSelectionEvent e) {
      OntoTreeNode node = (OntoTreeNode) tree.getLastSelectedPathComponent();
      //if ( !node.equals(focusedNode)) {
        debug.message("TreeSelectionListener ******** node " + node);
        //node.setFocus();
      //}
    }

    /**
     * Update tree view by focusing on given object
     * @param   TreePath path
     */
    public void updateOntoTree (TreePath path) {
        this.tree.makeVisible(path);
        //System.out.println("---OntoTreeView, method updateOntoTree for path" + path);
        focusedNode = (OntoTreeNode) path.getLastPathComponent();
        this.tree.addSelectionPath(path);
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
     * Add this model as an observer to all OntoTreeNodes
     * @param OntoTreeModel ontoTreeModel
     */
    private void addOntoObservers (OntoTreeModel ontoTreeModel) {
        Iterator it = ontoTreeModel.getOntoTreeIterator();
        while (it.hasNext()) {
            OntoTreeNode ontoTreeNode = (OntoTreeNode) it.next();
            ontoTreeNode.addOntoObserver(this);
        }
    }

    /**
     * Implementation of treeWillCollapse for TreeWillExpandListener interface
     */
    public void treeWillExpand(TreeExpansionEvent e)
                throws ExpandVetoException {
        debug.message("Tree-will-expand event detected" + e);

        boolean cancelExpand = false;
        if (cancelExpand) {
            //Cancel expansion.
            debug.message("Tree expansion cancelled" + e);
            throw new ExpandVetoException(e);
        }
    }

    /**
     * Implementation of treeWillExpand for TreeWillExpandListener interface
     */
    public void treeWillCollapse(TreeExpansionEvent e) {
        debug.message("Tree-will-collapse event detected" + e);
    }

//    /**
//     * Required by TreeExpansionListener interface.
//     */
//    public void treeExpanded(TreeExpansionEvent e) {
//        debug.message("Tree-expanded event detected" + e);
//    }
//
//    /**
//     * Required by TreeExpansionListener interface.
//     */
//    public void treeCollapsed(TreeExpansionEvent e) {
//        debug.message("Tree-collapsed event detected" + e);
//    }


    public void keyPressed(KeyEvent e) {
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

      //debug.message("keyPressed, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));

    }
    public void keyReleased(KeyEvent e) {
      //debug.message("keyReleased, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));
    }
    public void keyTyped(KeyEvent e) {
      debug.message("keyTyped, key char " + e.getKeyChar() + ", key code" + e.getKeyCode() + ", keyText = " + e.getKeyText(e.getKeyCode()));
    }


    public void mousePressed(MouseEvent e) {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	  if (selPath == null) {
		  // mouse clicked not on a node, but somewhere else in the tree view
		  return;
	  }
	  OntoTreeNode treeNode = (OntoTreeNode) selPath.getLastPathComponent();
	  GraphNode graphNode = treeNode.getGraphNode();
      if(selRow != -1) {
         if(e.getClickCount() == 1) {
             debug.message("mousePressed, single click,  row=" + selRow);
			 viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_SINGLECLICK);
         }
         else if(e.getClickCount() == 2) {
             debug.message("mousePressed, double click,  row=" + selRow);
         }
      }
    }
	
    public void mouseClicked (MouseEvent e) {
      int selRow = tree.getRowForLocation(e.getX(), e.getY());
      TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
      if (selPath == null) { return; }
      OntoTreeNode selNode = (OntoTreeNode) selPath.getLastPathComponent();
      debug.message("mouseClicked,  row=" + selRow + ", node = " + selNode.getGraphNode().getName());

      if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
        debug.message("BUTTON1_MASK");
      }
      if (e.getModifiers() == InputEvent.BUTTON2_MASK) {
        debug.message("BUTTON2_MASK");
      }
      if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
        debug.message("BUTTON3_MASK ");
      }
      if (e.getModifiers() == InputEvent.META_MASK ) {
        debug.message("META_MASK ");
      }
    }
    public void mouseEntered (MouseEvent e) {
      //debug.message("mouseEntered");
    }
    public void mouseExited (MouseEvent e) {
      //debug.message("mouseExited");
    }
    public void mouseReleased (MouseEvent e) {
      //debug.message("mouseReleased");
    }


    /**
     * @todo    Renderer should be in a standalone class
     * @todo    clean up methods, remove unneeded variables
     */
    private class MyRenderer extends DefaultTreeCellRenderer {
        //ImageIcon tutorialIcon;
        Color color1 = Color.gray;
        Color color2 = Color.pink;

        /**
         *
         */
        public MyRenderer() {
            //tutorialIcon = new ImageIcon("images/middle.gif");
        }

        /**
         *
         */
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            RelationLinkDetails relLinkDetails = getRelLinkDetails(value);

            //setBackgroundNonSelectionColor(isChild(relLinkDetails));
            //setBackgroundSelectionColor(isChild(relLinkDetails));

            setIcon(getIcon(relLinkDetails));
            setToolTipText(getToolTipText(value,relLinkDetails));
            return this;
        }

        /**
         *
         */
        private RelationLinkDetails getRelLinkDetails (Object value) {
            OntoTreeNode treeNode = (OntoTreeNode) value;
            int relLink = treeNode.getRelLink();
            RelationLinkDetails relLinkDetails = ontorama.OntoramaConfig.getRelationLinkDetails(relLink);
            return relLinkDetails;
        }

        /**
         *
         */
        protected Color isChild (RelationLinkDetails relLinkDetails) {
            Color color = relLinkDetails.getDisplayColor();
            return color;

        }

        /**
         *
         */
        protected Icon getIcon (RelationLinkDetails relLinkDetails) {
            Image image = relLinkDetails.getDisplayImage();
            Icon icon = new ImageIcon(image);

            return icon;
        }

        /**
         *
         */
        protected String getToolTipText (Object value, RelationLinkDetails relLinkDetails) {
            OntoTreeNode treeNode = (OntoTreeNode) value;
            String relLinkName = relLinkDetails.getLinkName();
            String result = "";
            //result = "Concept: " + treeNode.getGraphNode().getName();
            //result = result + "\n";
            result = result + "Relation Type: " + relLinkName;
            return result;
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
	}
	
	/**
	 *
	 */
	public void query ( GraphNode node) {
	}
			
}

