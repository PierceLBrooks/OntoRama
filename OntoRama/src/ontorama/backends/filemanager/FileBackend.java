package ontorama.backends.filemanager;

import java.io.*;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import ontorama.backends.Backend;
import ontorama.backends.filemanager.gui.FileJMenu;
import ontorama.backends.p2p.model.P2PEdge;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.backends.p2p.model.P2PNode;
import ontorama.model.util.GraphModificationException;
import ontorama.model.events.GraphChangedEvent;
import ontorama.model.Graph;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.writer.ModelWriter;
import ontorama.webkbtools.writer.ModelWriterException;
import ontorama.webkbtools.writer.rdf.RdfP2PWriter;
import ontorama.controller.QueryEvent;
import ontorama.controller.GeneralQueryEvent;
import ontorama.OntoramaConfig;

import javax.swing.*;

import org.tockit.events.EventBroker;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileBackend implements Backend{
    private P2PGraph graph = null;
    private List panels = null;
    private EventBroker eventBroker;

    public FileBackend(){
        System.out.println("file backend constructor");
            this.graph = new P2PGraphImpl();
            //We don't have any panels to this backend
            this.panels = new LinkedList();
    }

    public void setEventBroker(EventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    public P2PGraph search(Query query){
            return this.graph.search(query);
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

    public JMenu getJMenu() {
        JMenu menu = new FileJMenu(this);
        return menu;
    }

    public void loadFile(String filename){
            System.out.println("Loading file = " + filename);
           /// @todo these values shouldn't be hardcoded here, but set in config file.
           OntoramaConfig.ontologyRoot = null;
           OntoramaConfig.sourceUri = filename;
           OntoramaConfig.sourcePackageName = "ontorama.webkbtools.inputsource.FileSource";
           OntoramaConfig.parserPackageName = "ontorama.webkbtools.query.parser.rdf.RdfDamlParser";
           GeneralQueryEvent queryEvent = new GeneralQueryEvent(new Query());
           System.out.println("querEvent = " + queryEvent);
           eventBroker.processEvent(queryEvent);
    }

    public void saveFile(String filename){
        try {
            System.out.println("Saving file = " + filename);
            File file = new File(filename);
            FileWriter writer = new FileWriter(file);

            ModelWriter modelWriter = new RdfP2PWriter();
            Writer _writer = new StringWriter();
            modelWriter.write(this.graph, _writer);

            String rdfOutput = _writer.toString();
            writer.write(rdfOutput);
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

}
