package ontorama.views.tree.view;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.TreeView;
import ontorama.model.tree.events.TreeNodeSelectedEvent;
import ontorama.model.tree.controller.TreeViewFocusEventHandler;
import ontorama.ui.events.QueryNodeEvent;
import ontorama.views.tree.model.OntoTreeModel;
import ontorama.views.tree.model.OntoTreeNode;
import ontorama.views.tree.model.OntoTreeBuilder;
import org.tockit.events.EventBroker;


@SuppressWarnings("serial")
public class OntoTreeView extends JScrollPane implements KeyListener, MouseListener,
        TreeSelectionListener, TreeView {

    private JTree _treeView;

    private EventBroker _eventBroker;

    private Tree _tree;

    public OntoTreeView( EventBroker eventBroker) {
        super();

        _eventBroker = eventBroker;
        new TreeViewFocusEventHandler(_eventBroker, this);
    }

    public void setTree(Tree tree) {
        _tree = tree;
        OntoTreeModel ontoTreeModel = new OntoTreeModel(_tree);

        _treeView = new JTree(ontoTreeModel);

        _treeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _treeView.setEditable(false);
        _treeView.addMouseListener(this);
        _treeView.addKeyListener(this);
        ToolTipManager.sharedInstance().registerComponent(this._treeView);
        _treeView.setCellRenderer(new OntoTreeRenderer());
        unfoldTree(ontoTreeModel);
        setViewportView(_treeView);
        _treeView.putClientProperty("JTree.lineStyle", "Angled");
        _treeView.setScrollsOnExpand(true);
        _treeView.repaint();
    }

    private void unfoldTree(OntoTreeModel ontoTreeModel) {
        List<OntoTreeNode> q = new ArrayList<OntoTreeNode>();
        OntoTreeNode rootNode = (OntoTreeNode) ontoTreeModel.getRoot();
        q.add(rootNode);

        while (! q.isEmpty()) {
            OntoTreeNode curNode = q.remove(0);
            Enumeration<OntoTreeNode> children = curNode.children();
            while (children.hasMoreElements())  {
                OntoTreeNode curChild = children.nextElement();
                q.add(curChild);
            }
            TreePath path = curNode.getTreePath();
            _treeView.expandPath(path);
        }
    }

    /**
     * Implementation of valueChanged needed for interface
     * implementation of TreeSelectionListener.
     * Overwritten here so we can control single mouse clicks behavior ourselves.
     * @param   e - TreeSelectionEvent
     */
    public void valueChanged(TreeSelectionEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        processInputEvent(e);
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int selRow = _treeView.getRowForLocation(e.getX(), e.getY());
        if (selRow == -1) {
        	return;
        }
        if (e.getClickCount() == 1) {
            processInputEvent(e);
        }
    }

    private void processInputEvent(InputEvent e) {
        TreeNode modelNode = getModelNodeFromInputEvent(e);
        if (modelNode == null ) return;
        int onmask = InputEvent.BUTTON1_DOWN_MASK;
        if ( e.getModifiersEx() == onmask) {
            _eventBroker.processEvent(new TreeNodeSelectedEvent(modelNode, _eventBroker));
            return;
        }
        onmask = InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK;
        if ( e.getModifiersEx() == onmask) {
            System.out.println("query for modelNode = " + modelNode);
            _eventBroker.processEvent(new QueryNodeEvent(modelNode));
            return;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }
    
    private TreeNode getModelNodeFromInputEvent (InputEvent event) {
        if (event instanceof KeyEvent) {
            OntoTreeNode treeNode = (OntoTreeNode) _treeView.getLastSelectedPathComponent();
            if (treeNode == null) {
                return null;
            }
            TreeNode modelNode = treeNode.getModelTreeNode();
            return modelNode;
        }
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            TreePath selPath = _treeView.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (selPath == null) {
                // mouse clicked not on a node, but somewhere else in the tree view
                return null;
            }
            return  getModelNodeFromTreePath(selPath);
        }
        return null;
    }
   
	private TreeNode getModelNodeFromTreePath(TreePath selPath) {
		OntoTreeNode treeNode = (OntoTreeNode) selPath.getLastPathComponent();
		TreeNode modelNode = treeNode.getModelTreeNode();
		return modelNode;
	}

    //////////////////////////ViewEventObserver interface implementation////////////////

    public void focus(TreeNode node) {
        OntoTreeNode treeNode = (OntoTreeNode) OntoTreeBuilder.getTreeNode(node);
        TreePath path = treeNode.getTreePath();
        _treeView.setSelectionPath(path);
        _treeView.scrollPathToVisible(path);
    }
}
