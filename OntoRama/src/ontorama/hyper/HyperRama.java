package ontorama.hyper;

/**
 * This class is the viewer for HyperRama.  It is designed to be part of a larger
 * app for displaying ontologys
 */

import ontorama.hyper.model.HyperNode;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphBuilder;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Container;
import java.util.Iterator;
import java.util.Hashtable;

public class HyperRama extends JComponent {




    /**
     * Loads new ontology with top concept.
     */
    public void setGraph( Graph graph ) {
        //Add HyperNodes to hashtabel
        Hashtable hypernodes = new Hashtable();
        Iterator it = graph.iterator();
        while( it.hasNext() ) {
            GraphNode gn = (GraphNode)it.next();
            HyperNode hn = new HyperNode( gn.getName() );
            gn.addObserver( hn.getNodeObserver() );
            hypernodes.put( gn, hn );
        }

        it = hypernodes.values().iterator();
        while( it.hasNext() ) {
            HyperNode hn = (HyperNode)it.next();
            System.out.println( hn );
        }
    }


    /**
     * Test driver for HyperRama
     */
    public static void main( String args[] ) {
        JFrame testApp = new JFrame();
        HyperRama hr = new HyperRama();
        Container container = testApp.getContentPane();
        //get graph
        GraphBuilder reader = new GraphBuilder();
        Graph graph = reader.getGraph();
        hr.setGraph( graph );
        container.add( hr );
        testApp.setSize( 500, 500 );
        testApp.setVisible( true );
    }
}