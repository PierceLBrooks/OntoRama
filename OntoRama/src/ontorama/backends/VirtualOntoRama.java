package ontorama.backends;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ontorama.backends.filemanager.FileBackend;
import ontorama.backends.filemanager.FileMenu;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.P2PMenu;
import ontorama.backends.p2p.p2pmodule.ChangePanel;
import ontorama.backends.p2p.p2pmodule.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;
import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

public class VirtualOntoRama {
	private P2PBackend p2pBackend = null;
    private OntoRamaBackend ontoRamaBackend = null;
	private FileBackend fileBackend = null;
	private Vector backends = null;
	private Hashtable allNodes = null;
		
    public VirtualOntoRama(){
        ontoRamaBackend = new OntoRamaBackend(this);
        p2pBackend = new P2PBackend(ontoRamaBackend); 
		this.allNodes = new Hashtable();
        backends = new Vector();
		backends.add(p2pBackend);		
        fileBackend = new FileBackend();
		backends.add(fileBackend);
		
        char charIn;
       
        while (true) { 
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("");
            System.out.println("************************");
            System.out.println("OntoRama");
			System.out.println("************************");
          
            System.out.println(""); 
            System.out.println("a => Show P2P Menu (on menu bar)");    
            System.out.println("b => Show File Manager Menu (on menu bar)");   
            System.out.println("c => Show Popup Menu");     
            System.out.println("d => Show Peers Panel");  
            System.out.println("e => Show Change Panel"); 
            System.out.println("f => Show ontology"); 
            System.out.println("g => Search"); 
            System.out.println(""); 
            System.out.println("h => Exit OntoRama"); 

            System.out.print(">");
			charIn = readChar();      
            switch(charIn) {
            	case 'a' :  
                        ((P2PMenu) p2pBackend.getMenu()).showMenu();
                        break;
            	case 'b' :  
                        ((FileMenu) fileBackend.getMenu()).showMenu();
                        break;
                case 'c' :  
                        this.getPopupMenu();
                        break;
                case 'd' :  
                        PeersPanel pp = (PeersPanel)p2pBackend.getPanels().get(0);
                        pp.show();
                        break;
                case 'e' :  
                        ChangePanel chp = (ChangePanel)p2pBackend.getPanels().get(1);
                        chp.show();
                        break;
                case 'f' :  
                        System.out.println("My Ontology");
                        System.out.println(this.p2pBackend.showGraph());
                        break;                            
                case 'g' :  
						System.out.print("Search for (URI):");
						String lineIn = readLine();
						if (lineIn.length() == 0) {
							System.out.println("The string was empty...");
						} else {
							List relationLinks = (List) new LinkedList();
							relationLinks.add(new Integer(1));
							relationLinks.add(new Integer(2));
							relationLinks.add(new Integer(4));
							relationLinks.add(new Integer(7));
	                        ExtendedGraph extGraph = p2pBackend.search(new Query(lineIn,relationLinks));
	                        String ontology = extGraph.toRDFString();
	                        System.err.println("Result of the search:");
	                        System.err.println(ontology);
						}
                        break;
                case 'h' :  
						System.out.println("Bye...");
						System.exit(0);
            }
        }
    }
     
    private static String readLine(){
        try {
            BufferedReader stdin = 
                new BufferedReader(new InputStreamReader(System.in));
            return stdin.readLine();
        } catch (Exception e) {
            // if there is a failure, print an error
            e.printStackTrace();
        }
            return "";
        }
        
     
    private static void printSearchGroupResult(String tag, Vector obj) {
        SearchGroupResultElement searchGroupResultElement = null;
        System.out.println("Found following matching " + tag + "s");
        Enumeration tmpEnumernation = obj.elements();
            while (tmpEnumernation.hasMoreElements()) {
            searchGroupResultElement = (SearchGroupResultElement)tmpEnumernation.nextElement();
            System.out.println(tag + ":" + 
                searchGroupResultElement.getName() 
                + " (ID:" + searchGroupResultElement.getID() + ")");
                System.out.println("Description:" +
                searchGroupResultElement.getDescription());
        }
    }
    
        private static char readChar() {
        char retVal;
        try {
            // read stdin into the byte array.
            retVal = (char) System.in.read();
            System.in.read();
            System.in.read();
        } catch (Exception e) {
            // if there is a failure, print an error
            e.printStackTrace();
            retVal = 'w';
        }
        return retVal;
    }
  
     public static void main(String args[]){
            try {
                System.setErr(new PrintStream(new FileOutputStream("./err_log.txt")));
                //	System.setOut(new PrintStream(new FileOutputStream("./out_log.txt")));
                VirtualOntoRama ontoRama = new VirtualOntoRama();
            } catch (FileNotFoundException e) {
           }
           
     }


	private void getPopupMenu() {
		char charIn;

		System.out.println("----------------------");
		System.out.println("Popup menu");
        System.out.println("a => Assert Concept");     
        System.out.println("b => Assert Relation");
        System.out.println("");
        System.out.println("c => Reject Relation"); 
        System.out.println("");
        System.out.println("d => Assert Property");
        System.out.println("");
        System.out.println("f => Hide menu"); 
        System.out.print(">");

		charIn = readChar();      
		String fromURI;
		String toURI;
		GraphNode toNode;
		String fullName;
		String property;
		String propertyValue;
		int relationType;
		String relNamespace;
		LinkedList list = new LinkedList();
		String propertyNamespace;
		String defaultNamespace = "http://www.w3.org/TR/1999/PR-rdf-schema-19990303#";
        switch(charIn) {
			case 'a' :  
				System.out.println("Assert Concept");
				
				fromURI = this.getInput("Give from URI","");
				fullName = this.getInput("URI for the new node","");
				toNode = new GraphNode(fullName,fullName);
				this.allNodes.put(toNode.getFullName(),toNode);
				relationType = new Integer(this.getInput("The relation type [1/2/4/7]","")).intValue();
				relNamespace = this.getInput("Name space for the relation",defaultNamespace);

				p2pBackend.assertConcept((GraphNode) this.allNodes.get(fromURI), toNode, relationType,relNamespace);
				fileBackend.assertConcept((GraphNode) this.allNodes.get(fromURI), toNode, relationType,relNamespace);
				break;

			case 'd' :  
				System.out.println("Assert Property");
				toURI = this.getInput("URI to add property to","");
				property = this.getInput("Property name","");
				propertyNamespace = this.getInput("Namespace for " + property,"http://purl.org/metadata/dublin_core#");
				propertyValue = this.getInput(property + " value","");
				list = new LinkedList();
				list.add(propertyValue);
				try {
					if (this.allNodes.containsKey(toURI)) {
						toNode = (GraphNode) this.allNodes.get(toURI);
						toNode.setProperty(property,propertyNamespace,list);
						p2pBackend.updateConcept(toNode);	
						fileBackend.updateConcept(toNode);
					} else {
						System.err.println("The node URI could not be found");	
					}
				} catch (NoSuchRelationLinkException e) {
					System.out.println("No such property could be found");					
				}
				break;
			case 'b' :  
				System.out.println("Assert Relation");
				
				fromURI = this.getInput("Give from URI","");
				toURI = this.getInput("Give to URI","");				
				relationType = new Integer(this.getInput("The relation type [1/2/4/7]","")).intValue();
				relNamespace = this.getInput("Name space for the relation",defaultNamespace);
			
				p2pBackend.assertRelation((GraphNode) this.allNodes.get(fromURI), (GraphNode) this.allNodes.get(toURI),relationType,relNamespace);
				fileBackend.assertRelation((GraphNode) this.allNodes.get(fromURI), (GraphNode) this.allNodes.get(toURI),relationType,relNamespace);
				break;
			case 'c' :  
				System.out.println("Reject Relation");
				
				fromURI = this.getInput("Give from URI","");
				toURI = this.getInput("Give to URI","");				
				relationType = new Integer(this.getInput("The relation type [1/2/4/7]","")).intValue();
				relNamespace = this.getInput("Name space for the relation",defaultNamespace);

				p2pBackend.rejectRelation((GraphNode) this.allNodes.get(fromURI), (GraphNode) this.allNodes.get(toURI),relationType, relNamespace);
				fileBackend.rejectRelation((GraphNode) this.allNodes.get(fromURI), (GraphNode) this.allNodes.get(toURI),relationType, relNamespace);
				break;
			case 'f' :  
				break;
        }
	}		
	
	private String getInput(String text,String defaultText) {
		String retVal = null;
		if (defaultText.length() == 0) {
			System.out.print(text + ":");
			String lineIn = readLine();
			if (lineIn.length() == 0) {
				System.out.println("The string was empty...");
			} else {
				retVal = lineIn;
			}		 	
		} else {
			System.out.print(text + " (for default: " + defaultText + ", leave empty):");
			String lineIn = readLine();			
			if (lineIn.length() == 0) {
				retVal = defaultText;
			} else {
				retVal = lineIn;
			}
		}
		return retVal;
	}
	
	public Vector getBackends() {
		return backends;	
	}
}