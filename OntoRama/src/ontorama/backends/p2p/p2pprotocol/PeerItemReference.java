package ontorama.backends.p2p.p2pprotocol;

/**
 * @author nataliya
 * Created on 6/03/2003
 */
public class PeerItemReference {
	// @todo this class here is only so we can keep track of peer names in relation 
	// peer id's. ItemReference already does that - we should use it. For this to
	// happen we need to figure out if it is possible to either pass ItemReference id's 
	// from whoever gets that info from the network or if it is possible 
	// to pass ID's instead of strings.

	private String peerId;
	private String peerName;

	public PeerItemReference (String peerId, String peerName) {
		this.peerId = peerId;
		this.peerName = peerName;
	}
    	
	public String getPeerId() {
		return peerId;
	}

	public String getPeerName() {
		return peerName;
	}


}
