package ontorama.backends.filemanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

import ontorama.OntoramaConfig;
import ontorama.backends.Peer2PeerBackend;
import ontorama.backends.filemanager.gui.FileJMenu;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.model.graph.GraphModificationException;
import ontorama.model.graph.Graph;
import ontorama.model.graph.NoTypeFoundInResultSetException;
import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;
import ontorama.ui.events.GeneralQueryEvent;
import ontorama.model.graph.events.GraphLoadedEvent;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.writer.ModelWriter;
import ontorama.ontotools.writer.ModelWriterException;
import ontorama.ontotools.writer.rdf.RdfModelWriter;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileBackend implements Peer2PeerBackend{
    private P2PGraph graph = null;
    private ontorama.model.graph.Graph ontoramaGraph = null;
    private List panels = null;
    private EventBroker eventBroker;
    private String filename;

    private class GraphLoadedEventHandler implements EventBrokerListener {
        EventBroker eventBroker;
        public GraphLoadedEventHandler(EventBroker eventBroker)  {
            this.eventBroker = eventBroker;
            eventBroker.subscribe(this, GraphLoadedEvent.class, ontorama.model.graph.Graph.class);
        }

        public void processEvent(Event event) {
            ontoramaGraph = (Graph) event.getSubject();
            System.out.println("\n\nloaded graph = " + ontoramaGraph);
            /// @todo total hack, need to work out workflows
            // totally faked query and query result.
            QueryResult queryResult = new QueryResult(new Query(), ontoramaGraph.getNodesList(), ontoramaGraph.getEdgesList());
            try {
                System.out.println("FileBackend: creating new p2p graph");
                graph = new P2PGraphImpl(queryResult);
            }
            catch (NoSuchRelationLinkException e) {
                /// @todo deal with the exceptions
                e.printStackTrace();
            }
            catch (NoTypeFoundInResultSetException e) {
                /// @todo deal with the exceptions
                e.printStackTrace();
            }
        }
    }

    public FileBackend(){
        this.graph = new P2PGraphImpl();
        //We don't have any panels to this backend
        this.panels = new LinkedList();
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
        new GraphLoadedEventHandler(this.eventBroker);
    }
    
   

    public P2PGraph search(Query query){
        ///@todo temporarily commented out
        //return this.graph.search(query);
        return new P2PGraphImpl();
    }

    public void assertEdge(P2PEdge edge, URI asserter) throws GraphModificationException, NoSuchRelationLinkException{
         try {
			this.graph.assertEdge(edge, asserter);
		} catch (GraphModificationException e) {
				throw e;
		} catch (NoSuchRelationLinkException e) {
				throw e;
		}
    }


    public void assertNode(P2PNode node, URI asserter) throws GraphModificationException{
              try {
				this.graph.assertNode(node,asserter);
			} catch (GraphModificationException e) {
				throw e;
			}
    }

    public void rejectEdge(P2PEdge edge, URI rejecter) throws GraphModificationException, NoSuchRelationLinkException{
        try {
			this.graph.rejectEdge(edge,rejecter);
		} catch (GraphModificationException e) {
			throw e;
		} catch (NoSuchRelationLinkException e) {
			throw e;
		}
    }

    public void rejectNode(P2PNode node, URI rejecter) throws GraphModificationException{
        try {
			this.graph.rejectNode(node,rejecter);
		} catch (GraphModificationException e) {
			throw e;
		}
    }

    public List getPanels(){
        return this.panels;
    }

    public JMenu getMenu() {
        JMenu menu = new FileJMenu(this);
        return menu;
    }

    public void loadFile(String filename){
        System.out.println("Loading file = " + filename);
        this.filename = filename;

       /// @todo these values shouldn't be hardcoded here, but set in config file.
       OntoramaConfig.ontologyRoot = null;
       OntoramaConfig.sourceUri = filename;
       OntoramaConfig.sourcePackageName = "ontorama.ontotools.source.FileSource";
       OntoramaConfig.parserPackageName = "ontorama.ontotools.parser.rdf.RdfDamlParser";
       GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
       eventBroker.processEvent(queryEvent);
       OntoramaConfig.activateBackend(this);
    }

    public void saveFile(String filename){
        try {
            System.out.println("Saving file = " + filename);
            File file = new File(filename);
            FileWriter writer = new FileWriter(file);

//            ModelWriter modelWriter = new RdfP2PWriter();
//            System.out.println("writing graph = " + graph);
//            modelWriter.write(this.graph, writer);

            ModelWriter modelWriter = new RdfModelWriter();
            System.out.println("writing graph = " + ontoramaGraph);
            modelWriter.write(this.ontoramaGraph, writer);

            writer.close();

        } catch (IOException e) {
             System.err.println("Error when writing file");
             e.printStackTrace();
        } catch (ModelWriterException e) {
            e.printStackTrace();
            System.err.println("ModelWriterException:  " + e.getMessage());
        }
    }

    public void showPanels(boolean show) {
    }

	/**
	 * @see ontorama.backends.Backend#createNode(java.lang.String, java.lang.String)
	 */
	public Node createNode(String name, String fullName) {
		return new NodeImpl(name, fullName);
	}

}
