package ontorama.backends.p2p.p2pmodule;

import java.util.Hashtable;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.P2PBackendImpl;
import ontorama.backends.p2p.PeerItemReference;
import ontorama.backends.p2p.gui.ChangePanel;
import ontorama.backends.p2p.gui.P2PMainPanel;
import ontorama.backends.p2p.gui.PeersPanel;
import ontorama.backends.p2p.model.Change;
import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.query.Query;

/**
 * This Class implements the functionality for handeling input from the network
 *
 * @author henrika
 * @author johang
 *
 * @version P2P-OntoRama 1.0.0
 *
 * <b>Copyright:</b>        Copyright (c) 2002<br>
 * <b>Company:</b>          DSTC<br>
 *
 * @todo handle the exception better
 */
public class P2PReciever implements P2PRecieverInterface{
    public final static int TAGPROPAGATEADD = 1;
    public final static int TAGPROPAGATEDELETE = 2;
    public final static int TAGPROPAGATEUPDATE = 3;
    public final static int TAGPROPAGATEINIT = 4;
    public final static int TAGPROPAGATEJOINGROUP = 5;
    public final static int TAGPROPAGATELEAVEGROUP = 6;

    P2PBackend backend = null;
    PeersPanel activePeers = null;
    ChangePanel changes = null;
    Hashtable idMapping = null;

    public P2PReciever(P2PBackend backend){
        this.backend = backend;
        //Get the panel used to status of peers
    	P2PMainPanel panel = (P2PMainPanel) backend.getPanel();
    	activePeers = panel.getPeerPanel();
    	changes = panel.getChangePanel();
    }

//    public void recievePropagateCommand(int TAG, String senderPeerID, String senderPeerName, String senderGroupID, String internalModel){
	public void recievePropagateCommand(int TAG, PeerItemReference senderPeer, String senderGroupID, String internalModel){
            switch (TAG){
                    case P2PReciever.TAGPROPAGATEINIT:
                    	System.err.println("\nP2PReciever.TAGPROPAGATEINIT");
                          this.recieveInit(senderPeer, senderGroupID,internalModel);
                          break;

                    case P2PReciever.TAGPROPAGATEDELETE:
		            	System.err.println("\nP2PReciever.TAGPROPAGATEDELETE");
                        //Add the change to the panel showing made changes
                        processModelChange(internalModel, senderPeer);
                        break;

                    case P2PReciever.TAGPROPAGATEUPDATE:
            			System.err.println("\nP2PReciever.TAGPROPAGATEUPDATE");                    
                        //Add the change to the panel showing made changes
                        processModelChange(internalModel, senderPeer);
                        break;

                    case P2PReciever.TAGPROPAGATEADD:
            			System.err.println("\nP2PReciever.TAGPROPAGATEADD");                    
                        //Add the change to the panel showing made changes
                        processModelChange(internalModel, senderPeer);
                        break;
                case P2PReciever.TAGPROPAGATELEAVEGROUP:
	            	System.err.println("\nP2PReciever.TAGPROPAGATELEAVEGROUP");
                    //Remove the peer from the group
                    activePeers.removePeer(senderPeer.getID(),internalModel);
                    break;
                    case P2PReciever.TAGPROPAGATEJOINGROUP:
		            	System.err.println("\nP2PReciever.TAGPROPAGATEJOINGROUP");            
                         this.recieveJoinGroup(senderPeer, internalModel);
                    break;
            }
    }

    public void recieveLogoutCommand(String senderPeerID){
            //Remove the peer from the panel showing connected peers
            activePeers.removePeerFromAllGroups(senderPeerID);
    }

	public void recieveSearchRequest(String senderPeerID, String query){
		System.out.println("P2PReciever::Recieved a search request, query:" + query);
        System.out.println("P2PReciever::Passing this search request to backend:" + backend);
		backend.searchRequest(senderPeerID, query);

    }

    public void recieveSearchResponse(String senderPeerID,String result){
        try {
            //Parse the input recieved from the new peer
            //RdfDamlParser parser = new RdfDamlParser();
            /// @todo shouldn't have empty query here
            QueryResult qr = P2PBackendImpl.getQueryResult(result, new Query());
			backend.getP2PGraph().add(qr);
		} catch (GraphModificationException e) {
            System.err.println("Error in recieveSearchResponse");
            e.printStackTrace();
		} catch (NoSuchRelationLinkException e) {
            System.err.println("Error in recieveSearchResponse");
            e.printStackTrace();
        }

    }
    
    /**
     * Add the new peer to the panel showing peers and groups
     * @param senderPeerID
     * @param senderPeerName
     * @param groupID
     */
//    private void recieveJoinGroup(String senderPeerID, String senderPeerName, String groupID){
	private void recieveJoinGroup(PeerItemReference peer, String groupID){
       this.activePeers.addPeer(peer, groupID);
    }


    //Help classes
//	private void recieveInit(String senderPeerID, String senderPeerName, String senderGroupID,String internalModel){
	private void recieveInit(PeerItemReference peer, String senderGroupID,String internalModel){
    	try{
        	//Add the new host to the panel showing connected peers
			activePeers.addPeer(peer, senderGroupID);

			//Parse the input recieved from the new peer
			//RdfDamlParser parser = new RdfDamlParser();
            /// @todo shouldn't have empty query here
            QueryResult qr = P2PBackendImpl.getQueryResult(internalModel, new Query());
			backend.getP2PGraph().add(qr);
		} catch (GraphModificationException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		} catch (NoSuchRelationLinkException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		}
	}
	
	private void processModelChange (String modelChange, PeerItemReference senderPeer) {
		
		try {
			Change change = XmlMessageProcessor.parseXmlMessage(modelChange);
			System.out.println("\n\nrecieved change type " + change.getAction());

			change.setPeer(senderPeer);
			
			//Add the change to the panel showing made changes		
			changes.addChange(change);
		}
		catch (XmlMessageParserException e) {
			// @todo not sure what to do with exception here.
			e.printStackTrace();
		}
		
	}
}