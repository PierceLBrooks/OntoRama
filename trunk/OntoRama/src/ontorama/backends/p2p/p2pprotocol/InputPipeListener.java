package ontorama.backends.p2p.p2pprotocol;

import net.jxta.endpoint.Message;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import ontorama.backends.p2p.p2pmodule.P2PRecieverInterface;

/**
* InputpipeDiscoveryListener is a listener for messages from other peers
* recieved on a inputpipe.
* 
* @author Henrika 
* @author Johang
* @version P2P-OntoRama 1.0.0
* 
* <b>Copyright:</b>Copyright (c) 2002
* <br>
* <b>Company:</b>DSTC
*
*/

public class InputPipeListener implements PipeMsgListener {
	private P2PRecieverInterface reciever = null;
	private CommunicationProtocolJxta commProt = null;

	
	/** 
	* The constructor 
	*
	* @param obj the communication object that called the method
	* @param recieverObject the object that should recieve incoming messages
	* 
	* @version P2P-OntoRama 1.0.0
	*/
	public InputPipeListener (CommunicationProtocolJxta commProt, P2PRecieverInterface recieverObject) {
		this.reciever = recieverObject;		
		this.commProt = commProt;
	}

	/** 
	* This method is invoked by JXTA when someting arrives on a inputpipe.
	*
	* @param event contains information about the message recieved
	* 
	* @version P2P-OntoRama 1.0.0
	*/
    public void pipeMsgEvent(PipeMsgEvent event) {
        Message message = null;
               
		message = event.getMessage();
		
		String var = message.getString("TAG");
        String senderPeerIDStr = message.getString("SenderPeerID");

        //check to see if the message is sent for us
        if ((senderPeerIDStr != null) && var != null) {
            System.err.println("We have recieved a message with TAG, senderPeerName = " + message.getString("SenderPeerName") + ", senderPeerID:" + var + "," + senderPeerIDStr + " Body:" + message.getString("Body"));

            //Only process messages that this peer has not sent
            if (!(senderPeerIDStr.equals(
				this.commProt.getGlobalPG().getPeerID().toString()))) {


                switch(new Integer(var).intValue()) {
                    case CommunicationProtocolJxta.TAGPROPAGATE :
                        this.getP2PReciever().recievePropagateCommand(
                                                        new Integer(message.getString("propType")).intValue(),
                                                        message.getString("SenderPeerID"),
                                                        message.getString("SenderPeerName"),
                                                        message.getString("GroupID"),
                                                        message.getString("Body"));
                        break;
                    case CommunicationProtocolJxta.TAGLOGOUT :
                        this.getP2PReciever().recieveLogoutCommand(message.getString("SenderPeerID"));
                        break;
                    case CommunicationProtocolJxta.TAGSEARCH :

                        this.getP2PReciever().recieveSearchRequest(message.getString("SenderPipeID"),
                                                                   message.getString("Body"));
                        break;
                    case CommunicationProtocolJxta.TAGSEARCHRESPONSE :
                        this.recieveSearchResponse(message);
                        break;
                    case CommunicationProtocolJxta.TAGFLUSHPEER :
                        this.recieveFlushPeerAdvertisement(message.getString("GroupID"),
                                                            message.getString("PeerID"));
                        break;
                        }
            }
        }
    }
        



	/** 
	* This method adds responses recieved on queries to the global response object.
	*
	* @param response the response with was recieved
	* 
	* @version P2P-OntoRama 1.0.0
	*/
    private void recieveSearchResponse(Message response){
        String responseText = null;  
        String responsePeerID = null;
                    
        responsePeerID = response.getString("SenderPeerID");
        responseText = response.getString("Body");            
        this.commProt.getSearchResult().add(new SearchResultElement(responsePeerID, responseText)); 
    }


	/** 
	* This method is called to flush PeerAdvertisements from a given group. 
	*
	* @param groupID the id of the peergroup in that the flush should be done
	* @param peerID the id of the peer (peer advertisement) that should be flushed
	*  
	* @version P2P-OntoRama 1.0.0
	*/	
	public void recieveFlushPeerAdvertisement(String groupID, String peerID) {
	    try {
			this.commProt.recieveFlushPeerAdvertisement(groupID,peerID);
		} catch (GroupExceptionThread e) {
			//TODO throw something
		} catch (GroupExceptionFlush e) {
			//TODO throw something
		}

	}
	
	/** 
	* Returns the reciever object
	*  
	* @return P2PReciever the reciever object
	* 
	* @version P2P-OntoRama 1.0.0
	*/	
	private P2PRecieverInterface getP2PReciever() {
		return this.reciever;
	}
}

