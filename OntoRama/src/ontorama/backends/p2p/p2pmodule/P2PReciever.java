package ontorama.backends.p2p.p2pmodule;

import java.util.Hashtable;
import java.util.List;

import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.ChangePanel;
import ontorama.backends.p2p.gui.PeersPanel;
import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.QueryResult;
import ontorama.ontotools.query.Query;


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
        List panels = backend.getPanels();
        activePeers = (PeersPanel) panels.get(0);
        changes = (ChangePanel) panels.get(1);
    }

    public void recievePropagateCommand(int TAG, String senderPeerID, String senderPeerName, String senderGroupID, String internalModel){
            switch (TAG){
                    case P2PReciever.TAGPROPAGATEINIT:
                    	System.err.println("P2PReciever.TAGPROPAGATEINIT");
                          this.recieveInit(senderPeerID,senderPeerID, senderGroupID,internalModel);
                          break;

                    case P2PReciever.TAGPROPAGATEDELETE:
		            	System.err.println("P2PReciever.TAGPROPAGATEDELETE");
                        //Add the change to the panel showing made changes
                        changes.addChange(internalModel, senderPeerName);
                        break;

                    case P2PReciever.TAGPROPAGATEUPDATE:
            			System.err.println("P2PReciever.TAGPROPAGATEUPDATE");                    
                        //Add the change to the panel showing made changes
                        changes.addChange(internalModel, senderPeerName);
                        break;

                    case P2PReciever.TAGPROPAGATEADD:
            			System.err.println("P2PReciever.TAGPROPAGATEADD");                    
                        //Add the change to the panel showing made changes
                        changes.addChange(internalModel, senderPeerName);
                        break;
                case P2PReciever.TAGPROPAGATELEAVEGROUP:
	            	System.err.println("P2PReciever.TAGPROPAGATELEAVEGROUP");
                    //Remove the peer from the group
                    activePeers.removePeer(senderPeerID,internalModel);
                    break;
                    case P2PReciever.TAGPROPAGATEJOINGROUP:
		            	System.err.println("P2PReciever.TAGPROPAGATEJOINGROUP");            
                         this.recieveJoinGroup(senderPeerID, senderPeerName, internalModel);
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
            QueryResult qr = P2PBackend.getQueryResult(result, new Query(backend.getSourcePackageName(), backend.getParser(), backend.getSourceUri()));
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
    private void recieveJoinGroup(String senderPeerID, String senderPeerName, String groupID){
       this.activePeers.addPeer(senderPeerID, senderPeerName, groupID);
    }


    //Help classes
	private void recieveInit(String senderPeerID, String senderPeerName, String senderGroupID,String internalModel){
    	try{
        	//Add the new host to the panel showing connected peers
			activePeers.addPeer(senderPeerID, senderPeerName, senderGroupID);

			//Parse the input recieved from the new peer
			//RdfDamlParser parser = new RdfDamlParser();
            /// @todo shouldn't have empty query here
            QueryResult qr = P2PBackend.getQueryResult(internalModel, new Query(backend.getSourcePackageName(), backend.getParser(), backend.getSourceUri()));
			backend.getP2PGraph().add(qr);
		} catch (GraphModificationException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		} catch (NoSuchRelationLinkException e) {
        	System.err.println("An error accured");
            e.printStackTrace();
		}
	}
}