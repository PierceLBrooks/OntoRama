package ontorama.hyper.view.simple;


import ontorama.controller.NodeSelectedEvent;
import ontorama.graph.controller.GraphViewFocusEventHandler;
import ontorama.graph.view.GraphView;
import ontorama.hyper.canvas.CanvasManager;
import ontorama.hyper.model.HyperNode;
import ontorama.model.Edge;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class SimpleHyperView extends CanvasManager implements GraphView {

    private class NodeSelectedEventTransformer implements EventListener {
        private EventBroker eventBroker;

        public NodeSelectedEventTransformer(EventBroker eventBroker, Class eventType) {
            this.eventBroker = eventBroker;
            eventBroker.subscribe(this, eventType, HyperNodeView.class);
        }

        public void processEvent(Event e) {
            HyperNodeView nodeView = (HyperNodeView) e.getSubject();
            eventBroker.processEvent(new NodeSelectedEvent(nodeView.getGraphNode()));
        }
    }

    private class NodeActivatedEventHandler implements EventListener {
        public NodeActivatedEventHandler(EventBroker eventBroker, Class eventType) {
            eventBroker.subscribe(this, eventType, HyperNodeView.class);
        }

        public void processEvent(Event e) {
            HyperNodeView nodeView = (HyperNodeView) e.getSubject();
            toggleFold(nodeView.getGraphNode());
        }
    }


    /**
     * Temp flag to turn off spring and force algorithms.
     */
    private boolean runSpringForceAlgorithms = true;


    /**
     * Hold the top concept (root node) for current query.
     */
    private GraphNode root = null;

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

    /**
     * Path for test output files.
     */
    private String testFileOutputPath = "benchmark_out/";

    public SimpleHyperView( EventBroker eventBroker) {
        super(eventBroker);
        new NodeSelectedEventTransformer(eventBroker, CanvasItemSelectedEvent.class);
        new GraphViewFocusEventHandler(eventBroker, this);
        //new NodeActivatedEventHandler(eventBroker, CanvasItemActivatedEvent.class);
    }

    public void focus(GraphNode node) {
        System.out.println();
        focusChanged(node);
        System.out.println();
    }

    public void toggleFold(GraphNode node) {
        HyperNodeView focusedHyperNodeView = (HyperNodeView) this.hypernodeviews.get(node);
        if (focusedHyperNodeView == null) {
            return;
        }
        if (focusedHyperNodeView.isLeaf() == true) {
            return;
        }
        boolean foldedState = focusedHyperNodeView.getFolded();
        setFolded(foldedState, node);
        node.setFoldState(!foldedState);
        repaint();
    }

    /**
     * Method to fold and unfold HyperNodeViews.
     */
    private void setFolded(boolean foldedState, GraphNode node) {
        System.out.println("Setting folded: " + node.getName());
        Iterator it = Edge.getOutboundEdgeNodes(node);
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            HyperNodeView hyperNodeView = (HyperNodeView) hypernodeviews.get(cur);
            if (hyperNodeView != null) {
                hyperNodeView.setVisible(foldedState);
                if (!hyperNodeView.getFolded()) {
                    this.setFolded(foldedState, cur);
                }
            }
        }
    }

    /**
     * Unfold nodes back to root node.
     */
    private void unfoldNodes(HyperNodeView hyperNodeView) {
        Iterator it = Edge.getInboundEdgeNodes(hyperNodeView.getGraphNode());
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            HyperNodeView curHyperNode = (HyperNodeView) hypernodeviews.get(cur);
            if (!curHyperNode.getVisible()) {
                unfoldNodes(curHyperNode);
            }
            if (curHyperNode.getFolded()) {
                setFolded(true, cur);
                curHyperNode.setFolded(false);
            }
        }
    }

    /**
     * When node gets focus.
     * Test if node is visible, if not find folded node and unfold.
     * If node is folded, unfold.
     */
    private void testIfVisibleOrFolded(HyperNodeView hyperNodeView) {
        // test if visible, if not find folded node.
        if (hyperNodeView == null) {
            return;
        }
        System.out.println("testIfVisibleOrFolded: hyperNodeView = " + hyperNodeView);
        if (!hyperNodeView.getVisible()) {
            System.out.println(hyperNodeView.getName() + " is not visible");
            unfoldNodes(hyperNodeView);
        }
    }

    /**
     * FocusChanged called by FocusListen to
     * emit a change in which node has focus.
     * The node that has focus is centered.
     */
    public void focusChanged(GraphNode graphNode) {
        focusNode = (HyperNode) this.hypernodes.get(graphNode);
        // set focused node label to selected
        testIfVisibleOrFolded((HyperNodeView) this.hypernodeviews.get(graphNode));
        setLabelSelected((HyperNodeView) (hypernodeviews.get(graphNode)));
        //place the label last in the list so that it gets drawn last.
        // calculate the length of the animation as a function of the distance
        // in the euclidian space (before hyperbolic projection)
        double distance = Math.sqrt(focusNode.getX() * focusNode.getX() +
                focusNode.getY() * focusNode.getY());
        lengthOfAnimation = (long) (distance * 1.5);
        animationTime = System.currentTimeMillis();
        repaint();
    }

    /**
     * Loads new ontology with top concept.
     */
    public void setGraph(Graph graph) {
        // reset canvas variables
        this.resetCanvas();
        this.root = graph.getRootNode();
        if (root == null) {
            System.out.println("Root = null");
            return;
        }
        makeHyperNodes(root);
        //System.out.println("SimpleHyperView, hypernodes size = " + hypernodes.size());
        NodePlacementDetails rootNode = new NodePlacementDetails();
        rootNode.node = root;
        rootNode.numOfLeaves = getLeafNodeTotal(root);
        weightedRadialLayout(rootNode, 0);
        // (Math.PI * 2) is the number of radians in a circle
//        radialLayout(root, Math.PI * 2, 0);
        System.out.println("Running radial layout...");
//        //if( runSpringForceAlgorithms == true ) {
        long start = System.currentTimeMillis();
//        layoutNodes( 200 );
        long end = System.currentTimeMillis();
//        //}
//        System.out.println("Time taken: " + ( (end - start)) + "ms");


//        System.out.println("Running radial layout...");
//        radialLayout(root, Math.PI * 2, 0);
//        System.out.println("Running layoutNodes2( 100 )...");
//        start = System.currentTimeMillis();
//        layoutNodes2( 200 );
        end = System.currentTimeMillis();
        System.out.println("Time taken: " + ((end - start)) + "ms");
        //add lines to canvas manager.
        addLinesToHyperNodeViews(hypernodeviews, root);

        //Add HyperNodeViews to canvas manager.
        Iterator it = hypernodeviews.values().iterator();
        while (it.hasNext()) {
            HyperNodeView hnv = (HyperNodeView) it.next();
            addCanvasItem(hnv);
        }
        //Add HyperNodeViews labels canvas manager.
        it = hypernodeviews.values().iterator();
        while (it.hasNext()) {
            HyperNodeView hnv = (HyperNodeView) it.next();
            addCanvasItem(new LabelView(hnv));
        }
        setLeafNodes();
        repaint();
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
            numOfLeaves = Edge.getIteratorSize(Edge.getOutboundEdges(hnv.getGraphNode()));
            if (numOfLeaves == 0) {
                hnv.setNodeAsLeafNode();
            }
        }
    }


    /**
     * Temperary method to test spring and force algorthms
     */
    public void testSpringAndForceAlgorthms(double springLength, double stiffness, double electric_charge) {
        this.springLength = springLength;
        this.STIFFNESS = stiffness;
        this.ELECTRIC_CHARGE = electric_charge;
        int iteration = 50;
        // 6.283 is the number of radians in a circle
        radialLayout(root, 6.283, 0);
        System.out.println("Start spring and force algorthm: " + iteration + " iterations");
        long start = System.currentTimeMillis();
        int numOfItorations = layoutNodes(iteration);
        long end = System.currentTimeMillis();
        System.out.println("finished spring and force algorthm: " + numOfItorations + " iterations");
        long timeTaken = (end - start) / 1000;
        System.out.println("Time taken: " + timeTaken + "s");
        try {
            Writer out = new FileWriter(testFileOutputPath + "HyperTestLayouting.txt", true);
            BufferedWriter bufferedOut = new BufferedWriter(out);
            bufferedOut.write(root.getName());
            bufferedOut.write("\t");
            bufferedOut.write(String.valueOf(springLength));
            bufferedOut.write("\t");
            bufferedOut.write(String.valueOf(stiffness));
            bufferedOut.write("\t");
            bufferedOut.write(String.valueOf(electric_charge));
            bufferedOut.write("\t");
            bufferedOut.write(String.valueOf(timeTaken));
            bufferedOut.write("\t");
            bufferedOut.write(numOfItorations + " of " + iteration);
            bufferedOut.newLine();
            bufferedOut.close();
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
        this.paintComponent(this.getGraphics());
    }

    /**
     *
     */
    private void makeHyperNodes(GraphNode top) {
        HyperNode hn = new HyperNode(top);
        hn.addFocusChangedObserver(this);
        HyperNodeView hnv = new HyperNodeView(hn);
        hypernodes.put(top, hn);
        hypernodeviews.put(top, hnv);
        Iterator outboundNodes = Edge.getOutboundEdgeNodes(top);
        while (outboundNodes.hasNext()) {
            GraphNode gn = (GraphNode) outboundNodes.next();
            makeHyperNodes(gn);
        }
    }

    /**
     * Add lines to join HyperNodeViews.
     *
     * ///TODO lines should eventually represent the binary relationship
     * between nodes.
     */
    private void addLinesToHyperNodeViews(Hashtable hypernodeviews, GraphNode root) {
        List queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()) {
            GraphNode curGraphNode = (GraphNode) queue.remove(0);
            HyperNodeView curHyperNodeView = (HyperNodeView) hypernodeviews.get(curGraphNode);
            //System.out.println("curHyperNodeView = " + curHyperNodeView);

            Iterator outboundEdges = Edge.getOutboundEdges(curGraphNode);
            while (outboundEdges.hasNext()) {
                Edge edge = (Edge) outboundEdges.next();
                int edgeType = edge.getType();
                GraphNode outboundGraphNode = edge.getToNode();
                HyperNodeView outboundHyperNodeView = (HyperNodeView) hypernodeviews.get(outboundGraphNode);
                //System.out.println("---" + edgeType + "---outboundHyperNodeView = " + outboundHyperNodeView);
                addCanvasItem(new HyperEdgeView(curHyperNodeView, outboundHyperNodeView, edgeType));
                queue.add(outboundGraphNode);
            }
        }
    }

    /**
     * Try to give the ontology a basic layout.
     * The spring and electrical algorthms shall they do the rest.
     */
    private void radialLayout(GraphNode root, double rads, double startAngle) {
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        int numOfOutboundNodes = Edge.getIteratorSize(outboundNodesIterator);
        int numOfInboundNodes = Edge.getIteratorSize(Edge.getInboundEdgeNodes(root));
        if (numOfOutboundNodes == 0) {
            return;
        }
        double angle = rads / numOfOutboundNodes;
        double x = 0, y = 0, radius = 0, count = 1;
        outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        while (outboundNodesIterator.hasNext()) {
            GraphNode node = (GraphNode) outboundNodesIterator.next();
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
        public GraphNode node = null;
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
//        for( int i = 0; i < nodeList.length; i++ ) {
//            System.out.println("NumberOfLeaves = " + nodeList[i].numOfLeaves);
//        }
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
//        System.out.println("center = " + center + " arrayLength " + arrayLength + " nodeList.length " + nodeList.length);
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
//        System.out.println("finnished sorting");
//        for( int x = 0; x < sortedNodeList.length; x++ ) {
//            System.out.println("NumberOfLeaves = " + sortedNodeList[x].numOfLeaves);
//            System.out.println("Node = " + sortedNodeList[x].node.getName() + " Wedge = " + (180d/Math.PI * sortedNodeList[x].wedge));
//        }
        return sortedNodeList;
    }

    /**
     * Try to give the ontology a radial layout that allocates a
     * percentage of the wedge space to subtrees based on the number of
     * leaf node each subtree has to the total number of leaves on the tree.
     */
    private void weightedRadialLayout(NodePlacementDetails rootNode, double startAngle) {
//        double rootNodeLeafTotal = getLeafNodeTotal( root );
        double angle = startAngle;
        // Position node in the euclidean plane.
        // Calculate node radius from the center.
        double radius = springLength * rootNode.node.getDepth();
        if (radius != 0) { // Not root node
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
//        System.out.println("GraphNode root node is: " + root.getName());
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(rootNode.node);
        double numOfOutboundNodes = Edge.getIteratorSize(outboundNodesIterator);
        if (numOfOutboundNodes < 1) {
            return;
        }
        NodePlacementDetails[] nodeList = getNewNodeList((int) numOfOutboundNodes);
        outboundNodesIterator = Edge.getOutboundEdgeNodes(rootNode.node);
        int count = 0;
        //get graph node and their leaf count
        while (outboundNodesIterator.hasNext()) {
            GraphNode cur = (GraphNode) outboundNodesIterator.next();
            double numOfLeaves = getLeafNodeTotal(cur);
            nodeList[count].node = cur;
            nodeList[count].numOfLeaves = numOfLeaves;
            count = count + 1;
        }
        this.sortNodeListAscending(nodeList);// sort list in ascending orde
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
    private int getLeafNodeTotal(GraphNode root) {
        int sumOfLeafNodes = 0;
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        while (outboundNodesIterator.hasNext()) {
            GraphNode cur = (GraphNode) outboundNodesIterator.next();
            //is this node a feaf node
            int numOfchildren = Edge.getIteratorSize(Edge.getOutboundEdgeNodes(cur));
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
            Iterator it = Edge.getOutboundEdgeNodes(root);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                queue.add(node);
            }
            while (!queue.isEmpty()) {
                GraphNode cur = (GraphNode) queue.remove(0);
                sumOfAverageMoves += adjustPosition(cur);
                count++;
                it = Edge.getOutboundEdgeNodes(cur);
                while (it.hasNext()) {
                    GraphNode node = (GraphNode) it.next();
                    queue.add(node);
                }
            }
            lastMinMove = minNodeMove;
            minNodeMove = sumOfAverageMoves / count;
            minMoveDiff = lastMinMove - minNodeMove;
            minMoveDiff = Math.abs(minMoveDiff);
            System.out.print(".");
//            System.out.println("* * * minMoveDiff: " + minMoveDiff);
            numOfItorations++;
        } while (numOfItorations < iteration && minMoveDiff > .001);
        System.out.println("Iterated " + numOfItorations + " times");
        return numOfItorations;
    }

    /**
     * Adjust the position of the node using spring algorithm.
     */
    public double adjustPosition(GraphNode cur) {
        // calculate spring forces for edges to parents
        double sumOfMoves = 0;
        int count = 0;
        double xMove = 0;
        double yMove = 0;
        double curX = 0;
        double curY = 0;
        Iterator it = Edge.getInboundEdgeNodes(cur);
        while (it.hasNext()) {
            GraphNode parent = (GraphNode) it.next();
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
        }
        // calculate the electrical (repulsory) forces
        List queue = new LinkedList();
        queue.add(root);
        mainWhile: while (!queue.isEmpty()) {
            GraphNode other = (GraphNode) queue.remove(0);
            //it = other.getChildrenIterator();
            it = Edge.getOutboundEdgeNodes(other);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                queue.add(node);
            }
            if (other == cur) {
                continue;
            }
            //it = cur.getParents();
            it = Edge.getInboundEdgeNodes(cur);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                if (node == other) {
                    continue mainWhile;
                }
            }
            HyperNode curHyperNodeOther = (HyperNode) hypernodes.get(other);
            HyperNode curHyperNode = (HyperNode) hypernodes.get(cur);
            double vectorLength = curHyperNode.distance(curHyperNodeOther);
            if (vectorLength > 0.00001) { // don't try to calculate spring if length is zero0.00001
                int levelDiff = Math.abs(cur.getDepth() - other.getDepth() + 1);
                //double force = levelDiff * levelDiff * ELECTRIC_CHARGE / (vectorLength * vectorLength * vectorLength); // two for the force, one for normalization
                double force = (ELECTRIC_CHARGE) / (vectorLength * vectorLength);
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeOther.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeOther.getY());
                curHyperNode.setLocation(xMove, yMove);
                sumOfMoves = sumOfMoves + (Math.abs(xMove - curX) + Math.abs(yMove - curY)) / 2;
                count++;
            } else {
                curHyperNode.setLocation(curHyperNode.getX() + (Math.random() - 0.5),
                        curHyperNode.getY() + (Math.random() - 0.5));
            }
        }
        double averageMove = sumOfMoves / count;
//        System.out.println("averageMove: " + averageMove);
//        if( averageMove < minNodeMove && averageMove > .008) {
//            minNodeMove = averageMove;
//        }
        return averageMove;
    }

    /**
     * Use a spring algorithm to layout nodes.
     */
    public int layoutNodes2(int iteration) {
        List finishedNodes = new LinkedList();
        List queue = new LinkedList();
        int numOfItorations = 0;
        double averageMove;
        boolean loopAgain;
        System.out.println("Starting spring and force algorthms: ");
        do {
            loopAgain = false;
            Iterator it = Edge.getOutboundEdgeNodes(root);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                queue.add(node);
            }
            while (!queue.isEmpty()) {
                GraphNode cur = (GraphNode) queue.remove(0);
                if (!finishedNodes.contains(cur)) {
                    averageMove = adjustPosition2(cur);
                    loopAgain = true;
                    if (averageMove < (ELECTRIC_CHARGE * STIFFNESS * (1 - (1 / springLength)) / 10)) {
                        finishedNodes.add(cur);
                    }
                }
                it = Edge.getOutboundEdgeNodes(cur);
                while (it.hasNext()) {
                    GraphNode node = (GraphNode) it.next();
                    queue.add(node);
                }
            }
            System.out.print(".");
//            System.out.println("* * * minMoveDiff: " + minMoveDiff);
            numOfItorations++;
        } while (numOfItorations < iteration && loopAgain == true);
        System.out.println("Iterated " + numOfItorations + " times");
        return numOfItorations;
    }

    /**
     * Adjust the position of the node using spring algorithm.
     */
    public double adjustPosition2(GraphNode cur) {
        // calculate spring forces for edges to parents
        double sumOfMoves = 0;
        int count = 0;
        double xMove = 0;
        double yMove = 0;
        double curX = 0;
        double curY = 0;
        Iterator it = Edge.getInboundEdgeNodes(cur);
        while (it.hasNext()) {
            GraphNode parent = (GraphNode) it.next();
            HyperNode curHyperNodeParent = (HyperNode) hypernodes.get(parent);
            HyperNode curHyperNode = (HyperNode) hypernodes.get(cur);
            double vectorLength = curHyperNode.distance(curHyperNodeParent);
            if (vectorLength > 0.00001) { // don't try to calculate spring if length is zero
                //double springlength = springLength;
                double springlength = springLength / Math.sqrt(parent.getDepth() + 1);
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
        }
        // calculate the electrical (repulsory) forces
        List queue = new LinkedList();
        queue.add(root);
        mainWhile: while (!queue.isEmpty()) {
            GraphNode other = (GraphNode) queue.remove(0);
            //it = other.getChildrenIterator();
            it = Edge.getOutboundEdgeNodes(other);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                queue.add(node);
            }
            if (other == cur) {
                continue;
            }
            //it = cur.getParents();
            it = Edge.getInboundEdgeNodes(cur);
            while (it.hasNext()) {
                GraphNode node = (GraphNode) it.next();
                if (node == other) {
                    continue mainWhile;
                }
            }
            HyperNode curHyperNodeOther = (HyperNode) hypernodes.get(other);
            HyperNode curHyperNode = (HyperNode) hypernodes.get(cur);
            double vectorLength = curHyperNode.distance(curHyperNodeOther);
            if (vectorLength > 0.00001) { // don't try to calculate spring if length is zero0.00001
                int levelDiff = Math.abs(cur.getDepth() - other.getDepth() + 1);
                double force = levelDiff * levelDiff * ELECTRIC_CHARGE / (vectorLength * vectorLength * vectorLength); // two for the force, one for normalization
                //double force = (ELECTRIC_CHARGE)/ (vectorLength * vectorLength);
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeOther.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeOther.getY());
                curHyperNode.setLocation(xMove, yMove);
                sumOfMoves = sumOfMoves + (Math.abs(xMove - curX) + Math.abs(yMove - curY)) / 2;
                count++;
            } else {
                curHyperNode.setLocation(curHyperNode.getX() + (Math.random() - 0.5),
                        curHyperNode.getY() + (Math.random() - 0.5));
            }
        }
        double averageMove = sumOfMoves / count;
//        System.out.println("averageMove: " + averageMove);
//        if( averageMove < minNodeMove && averageMove > .008) {
//            minNodeMove = averageMove;
//        }
        return averageMove;
    }

    public void update(Graphics g) {
        paintComponent(g);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(222, 222, 222));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        int width = this.getSize().width;
        int height = this.getSize().height;
        g2d.translate(width / 2, height / 2);
        double sphereRadius = HyperNodeView.getSphereRadius();
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
        g2d.setColor(new Color(244, 244, 244));
        g2d.fill(new Ellipse2D.Double(-sphereRadius, -sphereRadius, sphereRadius * 2, sphereRadius * 2));
        drawNodes(g2d);
    }

    public void query(GraphNode node) {
    }
}
