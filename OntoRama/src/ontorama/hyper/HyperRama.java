package ontorama.hyper;

/**
 * This driver program for HyperRama.
 */

import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.model.Graph;
import ontorama.model.GraphNode;
import ontorama.model.GraphBuilder;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class HyperRama extends JFrame{

    public HyperRama() {
        this.addWindowListener(new WindowAdapter()	{
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        Container container = this.getContentPane();
        SimpleHyperView shv = new SimpleHyperView();
        container.add( shv );
        //get graph
        GraphBuilder reader = new GraphBuilder();
        Graph graph = reader.getGraph();
        shv.setGraph( graph );
        this.setSize( 500, 500 );
        this.setVisible( true );
    }

    /**
     * Test driver for HyperRama
     */
    public static void main( String args[] ) {
        new HyperRama();
    }
}