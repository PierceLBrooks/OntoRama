package ontorama.views.hyper.view;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ontorama.OntoramaConfig;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.NodeType;
import ontorama.model.tree.Tree;
import ontorama.model.tree.TreeEdge;
import ontorama.model.tree.TreeNode;
import ontorama.model.tree.controller.TreeViewFocusEventHandler;
import ontorama.model.tree.view.TreeView;
import ontorama.views.hyper.controller.DraggedEventHandler;
import ontorama.views.hyper.controller.NodeActivatedEventHandler;
import ontorama.views.hyper.controller.NodeContextMenuHandler;
import ontorama.views.hyper.controller.NodePointedEventHandler;
import ontorama.views.hyper.controller.NodeSelectedEventTransformer;
import ontorama.views.hyper.controller.RotateEventHandler;
import ontorama.views.hyper.controller.SphereMouseMovedEventHandler;
import ontorama.views.hyper.model.HyperNode;
import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemMouseMovementEvent;
import org.tockit.events.EventBroker;


public class SimpleHyperView extends Canvas implements TreeView {

    /**
     * Hold the mapping of HyperNode to GraphNodes
     */
    protected Hashtable hypernodes = new Hashtable();

    /**
     * Holds the mapping of HyperNodeView to GraphNodes
     */
    protected Hashtable hypernodeviews = new Hashtable();

    /**
     * Hold the top concept (_root node) for current query.
     */
    private TreeNode _root = null;

    private Tree _tree;

    /**
     * Store the HyperNode that has focus.
     */
    protected HyperNode focusNode = null;

    /**
     * Stores the hyperNodeView that is having its
     * EdgeImpl highlighted back to the _root node.
     */
    private HyperNodeView currentHighlightedView = null;

    /**
     * Stores the LabelView that is selected.
     */
    protected static LabelView labelView = null;
    private static SphereView sphereView = null;

    /**
     * The time when we did the last animation step.
     */
    protected long animationTime = 0;

    /**
     * Holds the remaining length of the animation.
     *
     * If negative animation we don't animate at the moment.
     */
    protected long lengthOfAnimation = -1;

    /**
     * Holds the current canvas scale.
     */
    protected double canvasScale = 1;


    /**
     * The spring length is the desired length between the nodes..
     */
    private static double springLength = 100;

    /**
     * Stiffness factor for spring alogrithm
     */
    private double STIFFNESS = .2;

    /**
     * Determines strength of repulsion betweeen two nodes
     */
    private double ELECTRIC_CHARGE = 10;
    
    public Projection projection = null;

    /**
     *
     */
    public SimpleHyperView(EventBroker eventBroker, Projection projection) {
        super(eventBroker);
        this.projection = projection;
        new NodeSelectedEventTransformer(eventBroker);
        new TreeViewFocusEventHandler(eventBroker, this);
//        new NodeActivatedEventHandler(this, eventBroker);
//        //new NodeDraggedEventHandler(this, eventBroker);
//        new NodePointedEventHandler(this, eventBroker);
//        if (OntoramaConfig.EDIT_ENABLED) {
//        	new NodeContextMenuHandler(this, eventBroker);
//        }
//        new SphereMouseMovedEventHandler(this, eventBroker);
//    	new DraggedEventHandler(this, eventBroker);
//    	new RotateEventHandler(this, eventBroker);
        SimpleHyperView.sphereView = new SphereView(((SphericalProjection)projection).getSphereRadius());
    }

	/**
	 * Loads new ontology with top concept.
	 */
	public void setTree(Tree tree) {
		// reset canvas variables
		resetCanvas();
		_tree = tree;
		_root = _tree.getRootNode();

		makeHyperNodes(_root);
		NodePlacementDetails rootNode = new NodePlacementDetails();
		rootNode.node = _root;
		rootNode.numOfLeaves = getLeafNodeTotal(_root);
		weightedRadialLayout(rootNode, 0);
		System.out.println("Running radial layout...");
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		end = System.currentTimeMillis();
		System.out.println("Time taken: " + ((end - start)) + "ms");

		addCanvasItem(SimpleHyperView.sphereView);
		addLinesToHyperNodeViews(hypernodeviews, _root);
		addHyperNodeViews();
		addLabelViews();

		setLeafNodes();

		repaint();
	}


    public Tree getTree() {
        return _tree;
    }

    /**
     *
     */
    public void focus(TreeNode treeNode) {
        if ((focusNode != null) && (treeNode.equals(focusNode.getTreeNode())) ) {
            return;
        }

        focusNode = (HyperNode) this.hypernodes.get(treeNode);
        double distance = Math.sqrt(focusNode.getX() * focusNode.getX() +
                focusNode.getY() * focusNode.getY());
        if(distance == 0) {
            return;
        }

        // set focused node label to selected
        HyperNodeView hyperNodeView  = (HyperNodeView) this.hypernodeviews.get(treeNode);
        testIfVisibleOrFolded(hyperNodeView);
        setLabelSelected(hyperNodeView);

        animationTime = System.currentTimeMillis();
        lengthOfAnimation = (long) (distance * 1.5);
        animate();
    }

    /**
     *
     */
    public void toggleFold(TreeNode node) {
        HyperNodeView focusedHyperNodeView = (HyperNodeView) this.hypernodeviews.get(node);
        if (focusedHyperNodeView == null) {
            return;
        }
        if (focusedHyperNodeView.isLeaf() == true) {
            return;
        }
        boolean foldedState = focusedHyperNodeView.getFolded();
        drawFolded(foldedState, node);
        focusedHyperNodeView.setFolded(!foldedState);
        repaint();
    }

    /**
     * Method to fold and unfold HyperNodeViews.
     */
    private void drawFolded(boolean foldedState, TreeNode node) {
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            TreeEdge curEdge = (TreeEdge) it.next();
            TreeNode cur = curEdge.getToNode();
            HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(cur);
            if (hyperNodeView != null) {
                hyperNodeView.setVisible(foldedState);
                if (!hyperNodeView.getFolded()) {
                    this.drawFolded(foldedState, cur);
                }
            }
        }
    }

    /**
     * Unfold nodes back to _root node.
     */
    private void unfoldNodes(HyperNodeView hyperNodeView) {
        TreeNode node = hyperNodeView.getTreeNode();
        TreeNode parent = node.getParent();
        HyperNodeView curHyperNode = (HyperNodeView) hypernodeviews.get(parent);
        if (!curHyperNode.getVisible()) {
            unfoldNodes(curHyperNode);
        }
        if (curHyperNode.getFolded()) {
            drawFolded(true, parent);
            curHyperNode.setFolded(false);
        }
        repaint();
    }

    /**
     * When node gets focus.
     * Test if node is visible, if not find folded node and unfold.
     * If node is folded, unfold.
     */
    private void testIfVisibleOrFolded(HyperNodeView hyperNodeView) {
        if (hyperNodeView == null) {
            return;
        }
        if (!hyperNodeView.getVisible()) {
            System.out.println(hyperNodeView.getName() + " is not visible");
            unfoldNodes(hyperNodeView);
        }
    }


    /**
     * This method set the flag in HyperViewNode to indicate if a
     * node if a leaf node.
     */
    private void setLeafNodes() {
        Iterator it = hypernodeviews.values().iterator();
        int numOfLeaves = 0;
        while (it.hasNext()) {
            HyperNodeView hnv = (HyperNodeView) it.next();
            TreeNode treeNode = hnv.getTreeNode();
            numOfLeaves = treeNode.getChildren().size();
            if (numOfLeaves == 0) {
                hnv.setNodeAsLeafNode();
            }
        }
    }


    /**
     *
     */
    private void makeHyperNodes(TreeNode node) {
        HyperNode hn = new HyperNode(node);
        NodeType nodeType = node.getGraphNode().getNodeType();
        // @todo hack for unknown node type
        if (nodeType == null) {
            Iterator it = OntoramaConfig.getNodeTypesList().iterator();
            while (it.hasNext()) {
                NodeType cur = (NodeType) it.next();
                if (cur.getNodeType().equals("unknown")) {
                    nodeType = cur;
                }
            }
        }
        hn.addFocusChangedObserver(this);
        HyperNodeView hnv = new HyperNodeView(hn, nodeType, projection);
        hypernodes.put(node, hn);
        hypernodeviews.put(node, hnv);
        Iterator childrenEdges = node.getChildren().iterator();
        while (childrenEdges.hasNext()) {
            TreeEdge edge = (TreeEdge) childrenEdges.next();
            TreeNode gn = edge.getToNode();
            makeHyperNodes(gn);
        }
    }


    /**
     * Try to give the ontology a basic layout.
     * The spring and electrical algorthms shall they do the rest.
     */
    private void radialLayout(TreeNode root, double rads, double startAngle) {
        List childrenEdgesList = root.getChildren();
        Iterator childrenEdges = childrenEdgesList.iterator();
        int numOfOutboundNodes = childrenEdgesList.size();
        if (numOfOutboundNodes == 0) {
            return;
        }
        double angle = rads / numOfOutboundNodes;
        double x = 0, y = 0, radius = 0, count = 1;
        while (childrenEdges.hasNext()) {
            TreeEdge edge = (TreeEdge) childrenEdges.next();
            TreeNode node = edge.getToNode();
            double ang = (angle * count) + startAngle - rads / 2;
            count = count + 1;
            radius = springLength * (node.getDepth());
            x = Math.cos(ang) * radius;
            y = Math.sin(ang) * radius;
            HyperNode hn = (HyperNode) hypernodes.get(node);
            if (hn == null) {
                return;
            }
            hn.setLocation(x, y);
            radialLayout(node, angle, ang);
        }
    }

    /**
     * Inner class to store graph node radial layouting info.
     */
    private class NodePlacementDetails {
        public TreeNode node = null;
        public double numOfLeaves = 0;
        // (Math.PI * 2) is the number of radians in a circle
        public double wedge = Math.PI * 2;
        public double weight = 1;// used to calc node radius from center
    }

    /**
     * Create a new array of NodePlacementDetails.
     *
     * This array will be used to store graph node radial
     * layouting info.
     * List can be ordered by the number of leaf nodes.
     */
    private NodePlacementDetails[] getNewNodeList(int size) {
        NodePlacementDetails[] nodeList = new NodePlacementDetails[size];
        for (int i = 0; i < size; i++) {
            nodeList[i] = new NodePlacementDetails();
        }
        return nodeList;
    }

    /**
     * Order NodePlacementDetails by number of leaf nodes.
     *
     * Using a simple bubble sort for now.
     */
    private void sortNodeListAscending(NodePlacementDetails[] nodeList) {
        NodePlacementDetails temp;
        for (int i = 0; i < (nodeList.length - 1); i++) {
            for (int j = i + 1; j < nodeList.length; j++) {
                if (nodeList[i].numOfLeaves < nodeList[j].numOfLeaves) {
                    temp = nodeList[i];
                    nodeList[i] = nodeList[j];
                    nodeList[j] = temp;
                }
            }
        }
    }

    /**
     * Method node in the order they are to be layed out
     * in the euclidean plane.
     */
    private NodePlacementDetails[] orderNodes(NodePlacementDetails[] nodeList) {
        NodePlacementDetails[] sortedNodeList = this.getNewNodeList(nodeList.length);
        // copy nodes into new list
        for (int i = 0; i < sortedNodeList.length; i++) {
            sortedNodeList[i] = nodeList[i];
        }
        // From the center place alternate large and small nodes.
        // Algorithm treats all arrays as even, this is okay as odd
        // size arrays last node is the smallest and would be positioned
        // at the end anyway.
        int index = 0;
        int arrayLength = nodeList.length - (nodeList.length % 2);//length of even array
        int center = (arrayLength / 2); // find index center
        int i = 0; // starts at largest
        int j = arrayLength - 1; // starts at smallest
        int offset = 0; //where to place node from center
        // required as one large node is placed in the center on first loop
        boolean firstLoop = true;
        while (index < arrayLength) {
            //place largest nodes
            sortedNodeList[center + (offset * -1)] = nodeList[i++];// place to the left
            index++;
            if (index >= arrayLength) {
                break;
            }
            if (firstLoop == true) {
                firstLoop = false;
            } else {
                sortedNodeList[center + offset] = nodeList[i++];// place to the right
                index++;
                if (index >= arrayLength) {
                    break;
                }
            }
            offset = offset + 1;
            // place smallest nodes
            sortedNodeList[center + (offset * -1)] = nodeList[j--];// place to the left
            index++;
            if (index >= arrayLength) {
                break;
            }
            sortedNodeList[center + offset] = nodeList[j--];// place to the right
            index++;
            offset = offset + 1;
        }
        return sortedNodeList;
    }

    /**
     * Try to give the ontology a radial layout that allocates a
     * percentage of the wedge space to subtrees based on the number of
     * leaf node each subtree has to the total number of leaves on the tree.
     */
    private void weightedRadialLayout(NodePlacementDetails rootNode, double startAngle) {
        double angle = startAngle;
        // Position node in the euclidean plane.
        // Calculate node radius from the center.
        double radius = springLength * rootNode.node.getDepth();
        if (radius != 0) { // Not _root node
            // Not sure if I like this effect, but shall leave it here for now
//            System.out.println("Radius before: " + radius + " numOfLeaves " + rootNode.numOfLeaves);
//            radius = radius - ((springLength * rootNode.weight));
//            System.out.println("Radius after: " + radius);
        }
        double drawAngle = startAngle + rootNode.wedge / 2;
        double x = Math.cos(drawAngle) * radius;
        double y = Math.sin(drawAngle) * radius;
        HyperNode hn = (HyperNode) hypernodes.get(rootNode.node);
        hn.setLocation(x, y);
        
        List childrenEdgesList = (rootNode.node).getChildren();

        double numOfOutboundNodes = childrenEdgesList.size();
        if (numOfOutboundNodes < 1) {
            return;
        }
        NodePlacementDetails[] nodeList = getNewNodeList((int) numOfOutboundNodes);
        Iterator childrenEdgesIterator = childrenEdgesList.iterator();
        int count = 0;

        //get graph node and their leaf count
        while (childrenEdgesIterator.hasNext()) {
            TreeEdge edge = (TreeEdge) childrenEdgesIterator.next();
            TreeNode cur = edge.getToNode();
            double numOfLeaves = getLeafNodeTotal(cur);
            nodeList[count].node = cur;
            nodeList[count].numOfLeaves = numOfLeaves;
            count = count + 1;
        }

        sortNodeListAscending(nodeList);// sort list in ascending orde
        // calculate each nodes wedge space.
        // node with no children will be at the end of the list.
        // they shall get a equal share of what ever wedge space is left
        // after processing nodes with children.
        double usedSpace = rootNode.wedge;
        for (int i = 0; i < nodeList.length; i++) {
            if (nodeList[i].numOfLeaves > 0) {
                nodeList[i].weight = nodeList[i].numOfLeaves / rootNode.numOfLeaves;
                nodeList[i].wedge = nodeList[i].weight * rootNode.wedge;
                usedSpace = usedSpace - nodeList[i].wedge;
            } else {// give what space is left to nodes with no children.
                double wedge = usedSpace / (nodeList.length - i);
                for (int j = i; j < nodeList.length; j++) {
                    nodeList[j].wedge = wedge;
                    nodeList[j].weight = wedge;// try this for now
                }
                break;
            }
        }
        // try now to space node out based on the number of leaves each node has
        nodeList = this.orderNodes(nodeList);
        // Recursively call all children, and allocat them their wedge space.
        for (int i = 0; i < nodeList.length; i++) {
            weightedRadialLayout(nodeList[i], angle);
            angle = angle + nodeList[i].wedge;
        }
    }


    /**
     * This method counts the number of leaves on a sub branch.
     */
    private int getLeafNodeTotal(TreeNode root) {
        int sumOfLeafNodes = 0;
        Iterator childrenEdgesIterator = root.getChildren().iterator();
        while (childrenEdgesIterator.hasNext()) {
            TreeEdge edge = (TreeEdge) childrenEdgesIterator.next();
            TreeNode cur = edge.getToNode();
            int numOfchildren = cur.getChildren().size();
            if (numOfchildren == 0) {
                sumOfLeafNodes++;
            } else {
                sumOfLeafNodes = sumOfLeafNodes + getLeafNodeTotal(cur);
            }
        }
        return sumOfLeafNodes;
    }

    /**
     * Use a spring algorithm to layout nodes.
     */
    public int layoutNodes(int iteration) {
        List queue = new LinkedList();
        int numOfItorations = 0;
        double minNodeMove = 0;
        double lastMinMove = 1000;
        double minMoveDiff;
        int count;
        double sumOfAverageMoves;
        System.out.println("Starting spring and force algorthms: ");
        do { //for(int i = 0; i < iteration && maxNodeMove ; i++) {
            count = 0;
            sumOfAverageMoves = 0;
            Iterator it = _root.getChildren().iterator();
            while (it.hasNext()) {
                TreeEdge edge = (TreeEdge) it.next();
                TreeNode node = edge.getToNode();
                queue.add(node);
            }
            while (!queue.isEmpty()) {
                TreeNode cur = (TreeNode) queue.remove(0);
                sumOfAverageMoves += adjustPosition(cur);
                count++;
                it = cur.getChildren().iterator();
                while (it.hasNext()) {
                    TreeEdge edge = (TreeEdge) it.next();
                    TreeNode node = edge.getToNode();
                    queue.add(node);
                }
            }
            lastMinMove = minNodeMove;
            minNodeMove = sumOfAverageMoves / count;
            minMoveDiff = lastMinMove - minNodeMove;
            minMoveDiff = Math.abs(minMoveDiff);
            System.out.print(".");
            numOfItorations++;
        } while (numOfItorations < iteration && minMoveDiff > .001);
        return numOfItorations;
    }

    /**
     * Adjust the position of the node using spring algorithm.
     */
    public double adjustPosition(TreeNode cur) {
        // calculate spring forces for _graphEdges to parents
        double sumOfMoves = 0;
        int count = 0;
        double xMove = 0;
        double yMove = 0;
        double curX = 0;
        double curY = 0;
        TreeNode parent = cur.getParent();
        HyperNode curHyperNodeParent = (HyperNode) hypernodes.get(parent);
        HyperNode curHyperNode = (HyperNode) hypernodes.get(cur);
        double vectorLength = curHyperNode.distance(curHyperNodeParent);
        if (vectorLength > 0.00001) { // don't try to calculate spring if length is zero
            double springlength = springLength;
            //double springlength = springLength / Math.sqrt(parent.getDepth() + 1);
            double force = STIFFNESS * (springlength - vectorLength) / vectorLength;
            curX = curHyperNode.getX();
            curY = curHyperNode.getY();
            xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeParent.getX());
            yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeParent.getY());
            curHyperNode.setLocation(xMove, yMove);
            sumOfMoves = sumOfMoves + (Math.abs(xMove - curX) + Math.abs(yMove - curY)) / 2;
            count++;
        } else {
            curHyperNode.setLocation(curHyperNode.getX() + (Math.random() - 0.5),
                    curHyperNode.getY() + (Math.random() - 0.5));
        }
        // calculate the electrical (repulsory) forces
        List queue = new LinkedList();
        queue.add(_root);
        mainWhile: while (!queue.isEmpty()) {
            TreeNode other = (TreeNode) queue.remove(0);
            Iterator it = other.getChildren().iterator();
            while (it.hasNext()) {
                TreeEdge edge = (TreeEdge) it.next();
                TreeNode node = edge.getToNode();
                queue.add(node);
            }
            if (other == cur) {
                continue;
            }
            TreeNode node = cur.getParent();
            if (node == other) {
                continue mainWhile;
            }
            HyperNode hyperNodeOther = (HyperNode) hypernodes.get(other);
            HyperNode hyperNode = (HyperNode) hypernodes.get(cur);
            double vectorLength1 = hyperNode.distance(hyperNodeOther);
            if (vectorLength1 > 0.00001) { // don't try to calculate spring if length is zero0.00001
                //int levelDiff = Math.abs(cur.getDepth() - other.getDepth() + 1);
                //double force = levelDiff * levelDiff * ELECTRIC_CHARGE / (vectorLength * vectorLength * vectorLength); // two for the force, one for normalization
                double force = (ELECTRIC_CHARGE) / (vectorLength1 * vectorLength1);
                curX = hyperNode.getX();
                curY = hyperNode.getY();
                xMove = hyperNode.getX() + force * (hyperNode.getX() - hyperNodeOther.getX());
                yMove = hyperNode.getY() + force * (hyperNode.getY() - hyperNodeOther.getY());
            	hyperNode.setLocation(xMove, yMove);
                sumOfMoves = sumOfMoves + (Math.abs(xMove - curX) + Math.abs(yMove - curY)) / 2;
                count++;
            } else {
            	hyperNode.setLocation(hyperNode.getX() + (Math.random() - 0.5),
            	hyperNode.getY() + (Math.random() - 0.5));
            }
        }
        double averageMove = sumOfMoves / count;
        return averageMove;
    }



    public void update(Graphics g) {
        paintComponent(g);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(222, 222, 222));
        g2d.fillRect(0, 0, getWidth(), getHeight());
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = this.getSize().width;
        int height = this.getSize().height;
        g2d.translate(width / 2, height / 2);

        double sphereRadius = ((SphericalProjection)projection).getSphereRadius();


        double sphereSize = 2 * sphereRadius;
        if (width < height) {
            canvasScale = width / sphereSize;
        } else {
            canvasScale = height / sphereSize;
        }
        // set the current scalling factor
        if (canvasScale != 0) {
            g2d.scale(canvasScale, canvasScale);
        }

        paintCanvas(g2d);

        Rectangle2D bounds = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        setScreenTransform(this.scaleToFit(g2d, bounds));

        if (focusNode == null) {
            return;
        }

        if (animationTime != 0) {
            animate();
        }
        else {
            if (focusNode.hasClones()) {
                TreeNode treeNode = focusNode.getTreeNode();
                HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(treeNode);
                if (treeNode.getClones().size() != 0) {
                    Iterator it = treeNode.getClones().iterator();
                    while (it.hasNext()) {
                        TreeNode cloneNode = (TreeNode) it.next();
                        unfoldNodes((HyperNodeView) hypernodeviews.get(cloneNode));
                    }
                    hyperNodeView.showClones(g2d, hypernodeviews);
                }
            }
        }
    }


    /**
     * Rotate node about the center (0, 0) by angle passed.
     */
    protected void rotateNodes(double angle) {
        Iterator it = this.hypernodes.values().iterator();
        while (it.hasNext()) {
            HyperNode hn = (HyperNode) it.next();
            hn.rotate(angle);
        }
    }


    /**
     * Move all the nodes by an offset x and y.
     */
    public void moveCanvasItems(double x, double y) {
        Iterator it = this.hypernodes.values().iterator();
        while (it.hasNext()) {
            HyperNode hn = (HyperNode) it.next();
            hn.move(x, y);
        }
    }

    /**
     */
    protected void animate() {

        long newTime = System.currentTimeMillis();
        long elapsedTime = newTime - animationTime;
        double animDist = elapsedTime / (double) lengthOfAnimation;
        lengthOfAnimation -= elapsedTime;
        animationTime = newTime;
        if (animDist > 1) {
            animDist = 1;
            animationTime = 0;
        }

        moveCanvasItems(focusNode.getX() * animDist, focusNode.getY() * animDist);

        repaint();
    }

    /**
     * Method called when a new graph is loaded.
     *
     * Reset all global variables
     */
    protected void resetCanvas() {
        super.clearCanvas();
        this.hypernodes.clear();
        this.hypernodeviews.clear();
        this.focusNode = null;
        this.currentHighlightedView = null;
    	SimpleHyperView.labelView = null;
        this.canvasScale = 1;

    }

    /**
     * Method called to highlight _graphEdges back to the _root node.
     */
    private void highlightEdge(TreeNode node) {
    	TreeNode parent = node.getParent();
        HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(parent);
        if (hyperNodeView != null) {
            hyperNodeView.setHighlightEdge(true);
            this.highlightEdge(parent);
        }
    }


    /**
     * When a hyperNode has focus, its label is placed last in the
     * canvasItems list ( so as to be drawn last), and is told
     * that it has focus.
     */
    protected void setLabelSelected(HyperNodeView selectedNodeView) {
        // find the LabelView for this HyperNodeView.
        ListIterator it = this.canvasItems.listIterator(this.canvasItems.size());
        while (it.hasPrevious()) {
            CanvasItem canvasItem = (CanvasItem) it.previous();
            if (canvasItem instanceof LabelView) {
                if (((LabelView) canvasItem).hasHyperNodeView(selectedNodeView) == true) {
                	SimpleHyperView.labelView = (LabelView) canvasItem;
                    break;
                }
            }
        }
        if (labelView != null) {
            canvasItems.remove(SimpleHyperView.labelView);
            canvasItems.add(SimpleHyperView.labelView);
        }
    }

    /**
     * Return the selected LabelView.
     */
    public static LabelView getSelectedLabelView() {
        return SimpleHyperView.labelView;
    }

    /**
     * Add lines to join HyperNodeViews.
     *
     */
    private void addLinesToHyperNodeViews(Hashtable hypernodeviews, TreeNode root) {
        List queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode curTreeNode = (TreeNode) queue.remove(0);
            HyperNodeView curHyperNodeView = (HyperNodeView) hypernodeviews.get(curTreeNode);
            Iterator childrenEdges = curTreeNode.getChildren().iterator();
            while (childrenEdges.hasNext()) {
                TreeEdge edge = (TreeEdge) childrenEdges.next();
                EdgeType edgeType = edge.getEdgeType();
                TreeNode childNode = edge.getToNode();
                HyperNodeView outboundHyperNodeView = (HyperNodeView) hypernodeviews.get(childNode);
                addCanvasItem(new HyperEdgeView(curHyperNodeView, outboundHyperNodeView, edgeType, projection));
                queue.add(childNode);
            }
        }
    }

    /**
     *
     */
    private void addHyperNodeViews() {
        Iterator it = hypernodeviews.values().iterator();
        while (it.hasNext()) {
            HyperNodeView hnv = (HyperNodeView) it.next();
            addCanvasItem(hnv);
        }
    }

    /**
     *  Add HyperNodeViews labels canvas manager.
     */
    private void addLabelViews() {
        Iterator it = hypernodeviews.values().iterator();
        while (it.hasNext()) {
            HyperNodeView hnv = (HyperNodeView) it.next();
            addCanvasItem(new LabelView(hnv));
        }
    }
    /**
     *
     * @param nodeView
     * @param draggedEvent
     */
    public void dragNode(HyperNodeView nodeView, CanvasItemDraggedEvent draggedEvent) {
        drag(draggedEvent);
    }

	public void drag(CanvasItemDraggedEvent draggedEvent) {
		SimpleHyperView.labelView = null;
		this.focusNode = null;
		this.currentHighlightedView = null;

		double x = draggedEvent.getCanvasToPosition().getX();
		double y = draggedEvent.getCanvasToPosition().getY();
		double lpx = draggedEvent.getCanvasFromPosition().getX();
		double lpy = draggedEvent.getCanvasFromPosition().getY();

		double xDif = (lpx - x);
		double yDif = (lpy - y);

		moveCanvasItems(xDif, yDif);
		repaint();
	}

	public void rotate(CanvasItemDraggedEvent draggedEvent) {
		SimpleHyperView.labelView = null;
		this.focusNode = null;
		this.currentHighlightedView = null;

		double x = draggedEvent.getCanvasToPosition().getX();
		double y = draggedEvent.getCanvasToPosition().getY();
		double lpx = draggedEvent.getCanvasFromPosition().getX();
		double lpy = draggedEvent.getCanvasFromPosition().getY();

		// calculate angle of rotation
		double angle = Math.atan2(lpx, lpy) - Math.atan2(x, y);
		this.rotateNodes(angle);

		repaint();
	}


    /**
     * Highlight all _graphEdges on the path from the _root to a closest node for current mouse event
     */
    public void highlightPathToRoot(CanvasItemMouseMovementEvent pointedEvent) {
        Iterator it = canvasItems.iterator();
        double minDist = this.getWidth();
        double dist = 0;
        HyperNodeView closestNode = null;
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof HyperNodeView) {
                HyperNodeView curNodeView = (HyperNodeView) cur;

                double curX = pointedEvent.getCanvasPosition().getX() - curNodeView.getProjectedX();
                double curY = pointedEvent.getCanvasPosition().getY() - curNodeView.getProjectedY();
                curX = curX * (1 / canvasScale);
                curY = curY * (1 / canvasScale);
                dist = Math.sqrt(curX * curX + curY * curY);

                if (dist < minDist) {
                    minDist = dist;
                    closestNode = curNodeView;
                }
            }
        }
        if (closestNode == null) {
            return;
        }
        highlightNodePathToRoot(closestNode);
    }

    public void highlightNodePathToRoot(HyperNodeView node) {
        if (!node.equals(currentHighlightedView)) {
            currentHighlightedView = node;
            node.setHighlightEdge(true);
            this.highlightEdge(node.getTreeNode());
            repaint();
        }
    }
}
