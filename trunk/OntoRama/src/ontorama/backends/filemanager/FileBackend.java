package ontorama.backends.filemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import ontorama.backends.Backend;
import ontorama.backends.ExtendedGraph;
import ontorama.backends.GraphNode;
import ontorama.backends.Menu;
import ontorama.backends.RdfDamlParser;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.ParserException;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FileBackend implements Backend{
    private ExtendedGraph graph = null;
    private List panels = null;
    
    public FileBackend(){
            this.graph = new ExtendedGraph(); 
            //We don't have any panels to this backend
            this.panels = new LinkedList();  
    }
       
    public ExtendedGraph search(Query query){
            return this.graph.search(query);
    }
    
    public void assertRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation){
         this.graph.addEdge(fromNode.getFullName(), toNode.getFullName(), edgeType,nameSpaceForRelation);
    } 
    
    
    public void assertConcept(GraphNode fromNode, GraphNode node, int edgeType,String nameSpaceForRelation){
              this.graph.addNode(node);
              this.graph.addEdge(fromNode.getFullName(), node.getFullName(), edgeType, nameSpaceForRelation);  
    }
    
    public void rejectRelation(GraphNode fromNode, GraphNode toNode, int edgeType,String nameSpaceForRelation){
        this.graph.rejectEdge(fromNode.getFullName(), toNode.getFullName(), edgeType,nameSpaceForRelation);
    }
    
    public void updateConcept(GraphNode node){
         this.graph.updateNode(node);
    }
    
    public List getPanels(){
        return this.panels;
    }
    public Menu getMenu(){
        return new FileMenu(this);
    }
    
    public void loadFile(String filename){
    
       try {
            System.out.println("Loading file = " + filename);
            Reader reader = new FileReader(filename);
            RdfDamlParser parser = new RdfDamlParser();
            parser.parse(reader, this.graph, "own");
           
        } catch (FileNotFoundException e) {
            System.out.println("The file was not found");
            System.err.println("Error the file was not found");
        } catch (ParserException e) {
            System.err.println("Error");
            e.printStackTrace();
        } catch (NoSuchPropertyException e) {
            System.err.println("Error");
            e.printStackTrace();
        }
            
    }
    public void saveFile(String filename){
        try {
            System.out.println("Saving file = " + filename);
            File file = new File(filename);
            FileWriter writer = new FileWriter(file);
            String rdfOutput = this.graph.toRDFString();     
            writer.write(rdfOutput);
            writer.close();
            
        } catch (IOException e) {
             System.err.println("Error when writing file");
             e.printStackTrace();
        }
    }

}
