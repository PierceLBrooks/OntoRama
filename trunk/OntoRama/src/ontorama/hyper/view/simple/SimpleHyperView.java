package ontorama.hyper.view.simple;



import ontorama.hyper.canvas.CanvasManager;
import ontorama.hyper.model.HyperNode;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.Edge;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;


public class SimpleHyperView  extends CanvasManager {

    /**
     * The spring length is the desired length between the nodes..
     */
    private static double springLength = 150;

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
        //temporary map HyperNodeViews to GraphNode to build LineViews
        hypernodeviews = new Hashtable();
        canvasItems.clear();
        //GraphNode root = graph.getRootNode();
        GraphNode root = graph.getEdgeRootNode();
        System.out.println("root = " + root);
        if( root == null ) {
            System.out.println("Root = null");
            return;
        }
        makeHyperNodes(root);
        System.out.println("SimpleHyperView, hypernodes size = " + hypernodes.size());

        // 6.283 is the number of radians in a circle
        basicLayout(root, 6.283, 0);

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
            //System.out.println("hyper node = " + hn.getName() + ", x = " + x + ", y = " + y);
            basicLayout( node, angle, ang );
        }
    }

    public void update(Graphics g) {
        paintComponent(g);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = ( Graphics2D )g;
        //java.awt.Paint oldPaint = g2d.getPaint();
        g2d.setColor(new Color( 222, 222, 222 ));
        g2d.fillRect(0, 0, getWidth(), getHeight() );
        int width = this.getSize().width;
        int height = getSize().height;
        g2d.translate( width/2, height/2 );
        double sphereRadius = HyperNodeView.getSphereRadius();
        double sphereSize = 2 * sphereRadius;
        if( width < height ) {
            canvasScale = width/sphereSize;
        } else {
            canvasScale = height/sphereSize;
        }
        // set the current scalling factor
        g2d.scale( canvasScale, canvasScale );
        g2d.setColor(new Color( 244, 244, 244 ));
        g2d.fill( new Ellipse2D.Double( -sphereRadius, -sphereRadius, sphereRadius*2, sphereRadius*2) );
        long start = System.currentTimeMillis();
        drawNodes( g2d );
        long laps = ( System.currentTimeMillis() - start);
//        System.out.println("laps time = " + laps + " ms");
        //g2d.setPaint(oldPaint);
    }
}