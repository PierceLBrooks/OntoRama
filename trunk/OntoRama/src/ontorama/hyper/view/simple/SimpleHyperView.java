package ontorama.hyper.view.simple;



import ontorama.hyper.canvas.CanvasManager;
import ontorama.hyper.model.HyperNode;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.Edge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;



public class SimpleHyperView  extends CanvasManager {

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
    private static double springLength = 150;

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

    public SimpleHyperView() {
        this.addMouseListener( this );
        this.addMouseMotionListener( this );
        this.setDoubleBuffered( true );
        this.setOpaque( true );
    }

    /**
     * Loads new ontology with top concept.
     */
    public void setGraph( Graph graph ) {
        //Add HyperNodes to hashtabel stored in CanvasManager
        hypernodes = new Hashtable();
        //Map HyperNodeViews to GraphNode to build LineViews
        hypernodeviews = new Hashtable();
        canvasItems.clear();
        root = graph.getRootNode();
        System.out.println("root = " + root);
        if( root == null ) {
            System.out.println("Root = null");
            return;
        }
        makeHyperNodes(root);
        //System.out.println("SimpleHyperView, hypernodes size = " + hypernodes.size());

        // (Math.PI * 2) is the number of radians in a circle
        //basicLayout(root, Math.PI * 2, 0);

        System.out.println("Running radial layout...");
        radialLayout(root, Math.PI * 2, 0);
//        //if( runSpringForceAlgorithms == true ) {
        long start = System.currentTimeMillis();
//        layoutNodes( 100 );
        long end = System.currentTimeMillis();
//        //}
//        System.out.println("Time taken: " + ( (end - start)) + "ms");


//        System.out.println("Running radial layout...");
//        radialLayout(root, Math.PI * 2, 0);
//        System.out.println("Running layoutNodes2( 100 )...");
//        start = System.currentTimeMillis();
//        layoutNodes2( 100 );
//        end = System.currentTimeMillis();
//        System.out.println("Time taken: " + ( (end - start)) + "ms");
        //add lines to canvas manager.
        addLinesToHyperNodeViews( hypernodeviews, root );

        //Add HyperNodeViews to canvas manager.
        Iterator it = hypernodeviews.values().iterator();
        while( it.hasNext() ) {
            HyperNodeView hnv = (HyperNodeView)it.next();
            canvasItems.add( hnv );
        }
        //Add HyperNodeViews labels canvas manager.
        it = hypernodeviews.values().iterator();
        while( it.hasNext() ) {
            HyperNodeView hnv = (HyperNodeView)it.next();
            canvasItems.add( new LabelView( hnv ) );
        }
        repaint();
    }

    /**
     * Method to save the current hyper view to a svg file.
     */
    public void saveCanvasToFile( String filename ) {
        // Get a DOMImplementation
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);
        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize( new Dimension( this.getWidth(), this.getHeight() ) );
        paintComponent(svgGenerator);
        boolean useCSS = true; // we want to use CSS style attribute
        try {
            OutputStream file = new FileOutputStream(testFileOutputPath+filename+".svg");
            Writer out = new OutputStreamWriter(file, "UTF-8");
            svgGenerator.stream(out, useCSS);
            out.close();
        }
        catch(SVGGraphics2DIOException svge) {}
        catch(IOException ioe) {}
    }
    /**
     * Temperary method to test spring and force algorthms
     */
    public void testSpringAndForceAlgorthms( double springLength, double stiffness, double electric_charge ) {
        this.springLength = springLength;
        this.STIFFNESS = stiffness;
        this.ELECTRIC_CHARGE = electric_charge;
        int iteration = 50;
        // 6.283 is the number of radians in a circle
        basicLayout(root, 6.283, 0);
        System.out.println("Start spring and force algorthm: " + iteration + " iterations");
        long start = System.currentTimeMillis();
        int numOfItorations = layoutNodes( iteration );
        long end = System.currentTimeMillis();
        System.out.println("finished spring and force algorthm: " + numOfItorations + " iterations");
        long timeTaken = ( end - start )/1000;
        System.out.println("Time taken: " + timeTaken + "s");
        try {
            Writer out = new FileWriter(testFileOutputPath+"HyperTestLayouting.txt", true);
            BufferedWriter bufferedOut = new BufferedWriter( out );
            bufferedOut.write( root.getName() );
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
        }
        catch( IOException ioe ) {
            System.out.println("IOException: " + ioe.getMessage());
        }
        this.paintComponent( this.getGraphics() );
    }

    /**
     *
     */
     private void makeHyperNodes (GraphNode top) {
        HyperNode hn = new HyperNode (top);
        hn.addFocusChangedObserver(this);
        HyperNodeView hnv = new HyperNodeView (hn);
        hypernodes.put(top,hn);
        hypernodeviews.put(top,hnv);
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
    private void addLinesToHyperNodeViews( Hashtable hypernodeviews, GraphNode root ) {
        List queue = new LinkedList();
        queue.add( root );
        while( !queue.isEmpty() ) {
            GraphNode curGraphNode = (GraphNode)queue.remove(0);
            HyperNodeView curHyperNodeView = (HyperNodeView)hypernodeviews.get( curGraphNode );
            //System.out.println("curHyperNodeView = " + curHyperNodeView);

            Iterator outboundEdges = Edge.getOutboundEdges(curGraphNode);
            while (outboundEdges.hasNext()) {
                Edge edge = (Edge) outboundEdges.next();
                int edgeType = edge.getType();
                GraphNode outboundGraphNode = edge.getToNode();
                HyperNodeView outboundHyperNodeView = (HyperNodeView) hypernodeviews.get(outboundGraphNode);
                //System.out.println("---" + edgeType + "---outboundHyperNodeView = " + outboundHyperNodeView);
                canvasItems.add(new HyperEdgeView(curHyperNodeView, outboundHyperNodeView,edgeType) );
                queue.add( outboundGraphNode );
            }
        }
    }

    /**
     * Try to give the ontology a basic layout.
     * The spring and electrical algorthms shall they do the rest.
     */
    private void basicLayout(GraphNode root, double rads, double startAngle) {
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        int numOfOutboundNodes = Edge.getIteratorSize(outboundNodesIterator);
        int numOfInboundNodes = Edge.getIteratorSize(Edge.getInboundEdgeNodes(root));
        if (numOfOutboundNodes == 0) {
            return;
        }
        double angle = rads/numOfOutboundNodes;
        double x = 0, y = 0, radius = 0, count = 1;
        outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        while (outboundNodesIterator.hasNext()) {
            GraphNode node = (GraphNode) outboundNodesIterator.next();
            double ang = (angle * count) + startAngle - rads/2;
            count = count + 1;
            radius = springLength * ( node.getDepth() );
            x = Math.cos(ang) * radius;
            y = Math.sin(ang) * radius;
            HyperNode hn = (HyperNode)hypernodes.get(node);
            if( hn == null) {
                return;
            }
            hn.setLocation( x, y);
            basicLayout( node, angle, ang );
        }
    }

    /**
     * Try to give the ontology a radial layout.
     * The spring and electrical algorthms shall they do the rest.
     */
    private void radialLayout(GraphNode root, double wedge, double startAngle) {
        double rootNodeLeafTotal = getLeafNodeTotal( root );
//        System.out.println("GraphNode root node is: " + root.getName());
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        double numOfOutboundNodes = Edge.getIteratorSize(outboundNodesIterator);
        if (numOfOutboundNodes < 1) {
            return;
        }
        double x = 0, y = 0, radius = 0, count = 1;
        outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        double sumOfSlices = 0;
        while (outboundNodesIterator.hasNext()) {
            GraphNode curNode = (GraphNode) outboundNodesIterator.next();
            double curNodeLeafTotal = getLeafNodeTotal( curNode );
            double slice = wedge * (curNodeLeafTotal / rootNodeLeafTotal);
            if( slice == 0 ) {
                slice = .1;//(wedge/numOfOutboundNodes);
            }
//            if( (sumOfSlices + slice ) >= wedge ) {
//                slice = 0;
//            }
            double drawAngle = (sumOfSlices + ( slice / 2 ) )  + startAngle;
//            System.out.println(curNode.getName() + " has " + curNodeLeafTotal + " curNodeLeafTotal");
//            System.out.println(curNode.getName() + " has " + (slice*(180/Math.PI)) + " slice");
//            System.out.println(curNode.getName() + " has " + (sumOfSlices*(180/Math.PI)) + " sumOfSlices");
//            System.out.println(curNode.getName() + " has " + (wedge*(180/Math.PI)) + " wedge");
//            System.out.println(curNode.getName() + " has " + (startAngle*(180/Math.PI)) + " startAngle");
//            System.out.println(curNode.getName() + " has " + (drawAngle*(180/Math.PI)) + " drawAngle");
            sumOfSlices = sumOfSlices + slice;
            count++;
            radius = springLength  * curNode.getDepth();
            x = Math.cos(drawAngle) * radius;
            y = Math.sin(drawAngle) * radius;
            HyperNode hn = (HyperNode)hypernodes.get(curNode);
            hn.setLocation( x, y);
            radialLayout( curNode, slice, ( drawAngle - (slice/2) ) );
        }
    }



    /**
     * This method counts the number of leaves on a sub branch.
     */
    private int getLeafNodeTotal( GraphNode root ) {
        int sumOfLeafNodes = 0;
        Iterator outboundNodesIterator = Edge.getOutboundEdgeNodes(root);
        while( outboundNodesIterator.hasNext() ) {
            GraphNode cur = ( GraphNode )outboundNodesIterator.next();
            //is this node a feaf node
            int numOfchildren = Edge.getIteratorSize( Edge.getOutboundEdgeNodes(cur) );
            if( numOfchildren == 0 ) {
                sumOfLeafNodes++;
            } else {
             sumOfLeafNodes = sumOfLeafNodes + getLeafNodeTotal( cur );
            }
        }
        return sumOfLeafNodes;
    }

   /**
     * Use a spring algorithm to layout nodes.
     */
    public int layoutNodes( int iteration) {
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
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                queue.add( node );
            }
            while( !queue.isEmpty() ) {
                GraphNode cur = (GraphNode)queue.remove( 0 );
                sumOfAverageMoves += adjustPosition( cur );
                count++;
                it = Edge.getOutboundEdgeNodes(cur);
                while( it.hasNext() ) {
                    GraphNode node = (GraphNode)it.next();
                    queue.add( node );
                }
            }
            lastMinMove = minNodeMove;
            minNodeMove = sumOfAverageMoves/count;
            minMoveDiff = lastMinMove - minNodeMove;
            minMoveDiff = Math.abs(minMoveDiff);
            System.out.print(".");
//            System.out.println("* * * minMoveDiff: " + minMoveDiff);
            numOfItorations++;
        }while( numOfItorations < iteration  && minMoveDiff > .001 );
        System.out.println("Iterated " + numOfItorations + " times");
        return numOfItorations;
    }

    /**
     * Adjust the position of the node using spring algorithm.
     */
    public double  adjustPosition( GraphNode cur ) {
        // calculate spring forces for edges to parents
        double sumOfMoves = 0;
        int count = 0;
        double xMove = 0;
        double yMove = 0;
        double curX = 0;
        double curY = 0;
        Iterator it = Edge.getInboundEdgeNodes(cur);
        while( it.hasNext() ) {
            GraphNode parent = (GraphNode)it.next();
            HyperNode curHyperNodeParent = (HyperNode)hypernodes.get( parent );
            HyperNode curHyperNode = (HyperNode)hypernodes.get( cur );
            double vectorLength = curHyperNode.distance( curHyperNodeParent );
            if(vectorLength > 0.00001) { // don't try to calculate spring if length is zero
                double springlength = springLength;
                //double springlength = springLength / Math.sqrt(parent.getDepth() + 1);
                double force = STIFFNESS * ( springlength - vectorLength ) / vectorLength;
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeParent.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeParent.getY());
                curHyperNode.setLocation( xMove, yMove);
                sumOfMoves = sumOfMoves + ( Math.abs(xMove - curX) + Math.abs(yMove - curY) ) / 2;
                count++;
            }
            else {
                curHyperNode.setLocation( curHyperNode.getX() + (Math.random() - 0.5),
                                          curHyperNode.getY() + (Math.random() - 0.5) );
            }
        }
        // calculate the electrical (repulsory) forces
        List queue = new LinkedList();
        queue.add( root );
        mainWhile: while(!queue.isEmpty()) {
            GraphNode other = (GraphNode) queue.remove(0);
            //it = other.getChildrenIterator();
            it = Edge.getOutboundEdgeNodes( other );
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                queue.add( node );
            }
            if(other == cur) {
                continue;
            }
            //it = cur.getParents();
            it = Edge.getInboundEdgeNodes(cur);
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                if(node == other) {
                    continue mainWhile;
                }
            }
            HyperNode curHyperNodeOther = (HyperNode)hypernodes.get( other );
            HyperNode curHyperNode = (HyperNode)hypernodes.get( cur );
            double vectorLength = curHyperNode.distance( curHyperNodeOther );
            if(vectorLength > 0.00001) { // don't try to calculate spring if length is zero0.00001
                int levelDiff = Math.abs( cur.getDepth() - other.getDepth() + 1 );
                //double force = levelDiff * levelDiff * ELECTRIC_CHARGE / (vectorLength * vectorLength * vectorLength); // two for the force, one for normalization
                double force = (ELECTRIC_CHARGE)/ (vectorLength * vectorLength);
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeOther.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeOther.getY());
                curHyperNode.setLocation( xMove, yMove );
                sumOfMoves = sumOfMoves + ( Math.abs(xMove - curX) + Math.abs(yMove - curY) ) / 2;
                count++;
            }
            else {
                curHyperNode.setLocation(curHyperNode.getX() + (Math.random() - 0.5),
                                curHyperNode.getY() + (Math.random() - 0.5) );
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
    public int layoutNodes2( int iteration) {
        List finishedNodes = new LinkedList();
        List queue = new LinkedList();
        int numOfItorations = 0;
        double averageMove;
        boolean loopAgain;
        System.out.println("Starting spring and force algorthms: ");
        do {
            loopAgain = false;
            Iterator it = Edge.getOutboundEdgeNodes(root);
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                queue.add( node );
            }
            while( !queue.isEmpty() ) {
                GraphNode cur = (GraphNode)queue.remove( 0 );
                if( !finishedNodes.contains( cur ) ) {
                    averageMove = adjustPosition2( cur );
                    loopAgain = true;
                    if( averageMove < ELECTRIC_CHARGE*STIFFNESS*( 1 - (1/springLength)) ) {
                        finishedNodes.add( cur );
                    }
                }
                it = Edge.getOutboundEdgeNodes(cur);
                while( it.hasNext() ) {
                    GraphNode node = (GraphNode)it.next();
                    queue.add( node );
                }
            }
            System.out.print(".");
//            System.out.println("* * * minMoveDiff: " + minMoveDiff);
            numOfItorations++;
        }while( numOfItorations < iteration && loopAgain == true);
        System.out.println("Iterated " + numOfItorations + " times");
        return numOfItorations;
    }

    /**
     * Adjust the position of the node using spring algorithm.
     */
    public double  adjustPosition2( GraphNode cur ) {
        // calculate spring forces for edges to parents
        double sumOfMoves = 0;
        int count = 0;
        double xMove = 0;
        double yMove = 0;
        double curX = 0;
        double curY = 0;
        Iterator it = Edge.getInboundEdgeNodes(cur);
        while( it.hasNext() ) {
            GraphNode parent = (GraphNode)it.next();
            HyperNode curHyperNodeParent = (HyperNode)hypernodes.get( parent );
            HyperNode curHyperNode = (HyperNode)hypernodes.get( cur );
            double vectorLength = curHyperNode.distance( curHyperNodeParent );
            if(vectorLength > 0.00001) { // don't try to calculate spring if length is zero
                double springlength = springLength;
                //double springlength = springLength / Math.sqrt(parent.getDepth() + 1);
                double force = STIFFNESS * ( springlength - vectorLength ) / vectorLength;
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeParent.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeParent.getY());
                curHyperNode.setLocation( xMove, yMove);
                sumOfMoves = sumOfMoves + ( Math.abs(xMove - curX) + Math.abs(yMove - curY) ) / 2;
                count++;
            }
            else {
                curHyperNode.setLocation( curHyperNode.getX() + (Math.random() - 0.5),
                                          curHyperNode.getY() + (Math.random() - 0.5) );
            }
        }
        // calculate the electrical (repulsory) forces
        List queue = new LinkedList();
        queue.add( root );
        mainWhile: while(!queue.isEmpty()) {
            GraphNode other = (GraphNode) queue.remove(0);
            //it = other.getChildrenIterator();
            it = Edge.getOutboundEdgeNodes( other );
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                queue.add( node );
            }
            if(other == cur) {
                continue;
            }
            //it = cur.getParents();
            it = Edge.getInboundEdgeNodes(cur);
            while( it.hasNext() ) {
                GraphNode node = (GraphNode)it.next();
                if(node == other) {
                    continue mainWhile;
                }
            }
            HyperNode curHyperNodeOther = (HyperNode)hypernodes.get( other );
            HyperNode curHyperNode = (HyperNode)hypernodes.get( cur );
            double vectorLength = curHyperNode.distance( curHyperNodeOther );
            if(vectorLength > 0.00001) { // don't try to calculate spring if length is zero0.00001
                int levelDiff = Math.abs( cur.getDepth() - other.getDepth() + 1 );
                //double force = levelDiff * levelDiff * ELECTRIC_CHARGE / (vectorLength * vectorLength * vectorLength); // two for the force, one for normalization
                double force = (ELECTRIC_CHARGE)/ (vectorLength * vectorLength);
                curX = curHyperNode.getX();
                curY = curHyperNode.getY();
                xMove = curHyperNode.getX() + force * (curHyperNode.getX() - curHyperNodeOther.getX());
                yMove = curHyperNode.getY() + force * (curHyperNode.getY() - curHyperNodeOther.getY());
                curHyperNode.setLocation( xMove, yMove );
                sumOfMoves = sumOfMoves + ( Math.abs(xMove - curX) + Math.abs(yMove - curY) ) / 2;
                count++;
            }
            else {
                curHyperNode.setLocation(curHyperNode.getX() + (Math.random() - 0.5),
                                curHyperNode.getY() + (Math.random() - 0.5) );
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
        Graphics2D g2d = ( Graphics2D )g;
        g2d.setColor(new Color( 222, 222, 222 ));
        g2d.fillRect(0, 0, getWidth(), getHeight() );
        int width = this.getSize().width;
        int height = this.getSize().height;
        g2d.translate( width/2, height/2 );
        double sphereRadius = HyperNodeView.getSphereRadius();
        double sphereSize = 2 * sphereRadius;
        if( width < height ) {
            canvasScale = width/sphereSize;
        } else {
            canvasScale = height/sphereSize;
        }
        // set the current scalling factor
        if( canvasScale != 0) {
            g2d.scale( canvasScale, canvasScale );
        }
        g2d.setColor(new Color( 244, 244, 244 ));
        g2d.fill( new Ellipse2D.Double( -sphereRadius, -sphereRadius, sphereRadius*2, sphereRadius*2) );
        drawNodes( g2d );
    }
}