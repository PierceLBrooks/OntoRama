package ontorama.backends.p2p.p2pmodule;

import ontorama.backends.p2p.p2pprotocol.PeerItemReference;

/**
 * This interface handles the P2P communication between OntoRama and 
 * the P2P protocol. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */

public interface P2PRecieverInterface {
	
	public void recievePropagateCommand(int TAG, PeerItemReference senderPeer, String senderGroupID, String internalModel);
	
	public void recieveLogoutCommand(String senderPeerID);
	
	public void recieveSearchRequest(String peerID, String query);

    public void recieveSearchResponse(String senderPeerID,String result);

}
