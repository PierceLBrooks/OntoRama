package ontorama.hyper.view.simple;



import ontorama.hyper.canvas.CanvasManager;
import ontorama.hyper.model.HyperNode;
import ontorama.model.Graph;
import ontorama.model.GraphNode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedList;


public class SimpleHyperView  extends CanvasManager {


    /**
     * The spring length is the desired length between the nodes..
     */
    private static double springLength = 100;

    /**
     * Loads new ontology with top concept.
     */
    public void setGraph( Graph graph ) {
        //Add HyperNodes to hashtabel stored in CanvasManager
        hypernodes = new Hashtable();
        canvasItems.clear();
        Iterator it = graph.iterator();
        while( it.hasNext() ) {
            GraphNode gn = (GraphNode)it.next();
            HyperNode hn = new HyperNode( gn.getName() );
            gn.addObserver( hn.getNodeObserver() );
            hypernodes.put( gn, hn );
        }
        GraphNode root = graph.getRootNode();
        if( root == null ) {
            System.out.println("Root = null");
            return;
        }
        // 6.283 is the number of radians in a circle
        basicLayout(root, 6.283, 0);
        //Add observers to HyperNodes
        it = hypernodes.values().iterator();
        while( it.hasNext() ) {
            HyperNode node = (HyperNode)it.next();
            HyperNodeView hnv = new HyperNodeView( node );
            node.addHyperObserver( hnv );
            canvasItems.add( hnv );
        }
        repaint();
    }

    /**
     * Try to give the ontology a basic layout.
     * The spring and electrical algorthms shall they do the rest.
     */
    private void basicLayout(GraphNode root, double rads, double startAngle) {
        int numOfChildren = root.getNumberOfChildren();
        if( numOfChildren == 0 ) {
            return;
        }
        double angle = rads/ numOfChildren;
        double x = 0, y = 0, radius = 0, count = 1;
        Iterator it = root.getChildrenIterator();
        while( it.hasNext() ) {
            GraphNode node = (GraphNode)it.next();
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

    public void paintComponent(Graphics g) {
        Graphics2D g2d = ( Graphics2D )g;
        java.awt.Paint oldPaint = g2d.getPaint();
        g2d.setColor(new Color( 222, 222, 222 ));
        g2d.fillRect(0, 0, getWidth(), getHeight() );
        int width = this.getSize().width;
        int height = getSize().height;
        g2d.translate( width/2, height/2 );
        g2d.setColor(new Color( 244, 244, 244 ));
        //double sphereRadius = NodeView.getSphereRadius();
        //double sphereSize = 2 * sphereRadius;
        // set the current scalling factor
        //if( width < height ) {
        //    canvasScale = width/sphereSize;
        //} else {
        //    canvasScale = height/sphereSize;
        //}
        //g2d.scale( canvasScale, canvasScale );
        //g2d.fill( new Ellipse2D.Double( -sphereRadius, -sphereRadius, sphereRadius*2, sphereRadius*2) );
        // do not continue. No nodes to paint.
        //if( root == null ) {
        //    return;
        //}
        drawNodes( g2d );
        //g2d.setPaint(oldPaint);
        //g2d.setColor( Color.black );
        //g2d.drawString(statusBar, -width/4, height/4);
    }
}