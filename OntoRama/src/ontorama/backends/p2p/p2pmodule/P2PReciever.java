package ontorama.backends.p2p.p2pmodule;

import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.PeersPanel;
import ontorama.backends.p2p.gui.ChangePanel;
import ontorama.model.util.GraphModificationException;
import ontorama.webkbtools.query.parser.ParserResult;
import ontorama.webkbtools.query.parser.rdf.RdfDamlParser;
import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;


/**
 *This Class implements the functionality for handeling input from the network  
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>        Copyright (c) 2002<br>
 * <b>Company:</b>          DSTC<br>
 * 
 * TODO handle the exception better
 * TODO change to correct spelling of method names, e.g. reciever
 */

public class P2PReciever implements P2PRecieverInterface{
    public final static int TAGPROPAGATEADD = 1;    
    public final static int TAGPROPAGATEDELETE = 2;
    public final static int TAGPROPAGATEUPDATE = 3;     
    public final static int TAGPROPAGATEINIT = 4;
    public final static int TAGPROPAGATEJOINGROUP = 5;

    P2PBackend backend = null;
//    PeersPanel activePeers = null;
//    ChangePanel changes = null;
    PeersPanel activePeers = null;
    ChangePanel changes = null;
    Hashtable idMapping = null;
  
    public P2PReciever(P2PBackend backend){
        this.backend = backend;
        idMapping = new Hashtable();
        //Get the panel used to status of peers
        List panels = (List) backend.getPanels();
        activePeers = (PeersPanel) panels.get(0);
        changes = (ChangePanel) panels.get(1);
    }

    
    public void recievePropagateCommand(int TAG, String senderPeerID, String senderPeerName, String senderGroupID, String internalModel){
            switch (TAG){
                    case P2PReciever.TAGPROPAGATEINIT:
                          this.receiveInit(senderPeerID,senderPeerID, senderGroupID,internalModel);
                          break;

                    case P2PReciever.TAGPROPAGATEDELETE:
                        //Add the change to the panel showing made changes
                        //Todo the string tobe shown could include what has been deleted
                        changes.addChange(internalModel, senderPeerName);
                        break;     
                             
                    case P2PReciever.TAGPROPAGATEUPDATE:
                        //Add the change to the panel showing made changes
                        //Todo the string tobe shown could include what has been deleted
                        changes.addChange(internalModel, senderPeerName);    
                        break;
                                            
                    case P2PReciever.TAGPROPAGATEADD:
                        //Add the change to the panel showing made changes
                        //Todo the string tobe shown could include what has been deleted
                        changes.addChange(internalModel, senderPeerName); 
                        break;
                    case P2PReciever.TAGPROPAGATEJOINGROUP: 
                         this.recieveJoinGroup(senderPeerID, senderPeerName, internalModel);
                    break;
            }   
    }
    
    public void recieveLogoutCommand(String senderPeerID){
            //Remove the peer from the panel showing connected peers
            activePeers.removePeer(senderPeerID);
    
    }
    
	public void recieveSearchRequest(String senderPeerID, String query){
		System.out.println("Received a search request, query:" + query);
		backend.searchRequest(senderPeerID, query);
           
    }

    public void recieveSearchResponse(String senderPeerID,String result){
        try {
            //Parse the input recieved from the new peer   
            Reader intModel = new StringReader(result);
            RdfDamlParser parser = new RdfDamlParser();
            ParserResult parserResult = parser.getResult(intModel);
			backend.getP2PGraph().add(parserResult);

        } catch (ParserException e) {
            System.err.println("Error in receiveSearchResponse");
            e.printStackTrace();
		} catch (GraphModificationException e) {
            System.err.println("Error in receiveSearchResponse");
            e.printStackTrace();
		} catch (NoSuchRelationLinkException e) {
            System.err.println("Error in receiveSearchResponse");
            e.printStackTrace();
        }
    
    }

    private void recieveJoinGroup(String senderPeerID, String senderPeerName, String groupID){
       //Add the new peer to the panel showing peers and groups
       idMapping.put(senderPeerID, senderPeerName);
       this.activePeers.addPeer(senderPeerID, senderPeerName, groupID);
    }


    //Help classes
	private void receiveInit(String senderPeerID, String senderPeerName, String senderGroupID,String internalModel){
    	try{ 
        	//Add the new host to the panel showing connected peers
            this.idMapping.put(senderPeerID, senderPeerName);
			activePeers.addPeer(senderPeerID, senderPeerName, senderGroupID);

			//Parse the input recieved from the new peer                     
			Reader intModel = new StringReader(internalModel);
			RdfDamlParser parser = new RdfDamlParser();
			ParserResult parserResult = parser.getResult(intModel);
			backend.getP2PGraph().add(parserResult);
			
		} catch (ParserException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		} catch (GraphModificationException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		} catch (NoSuchRelationLinkException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		}  
	}
}