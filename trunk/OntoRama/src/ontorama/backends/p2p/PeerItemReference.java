package ontorama.backends.p2p;

/**
 * @author nataliya
 * Created on 6/03/2003
 */
public class PeerItemReference {
	private String peerId;
	private String peerName;

	public PeerItemReference (String peerId, String peerName) {
		this.peerId = peerId;
		this.peerName = peerName;
	}
    	
	public String getID() {
		return peerId;
	}

	public String getName() {
		return peerName;
	}


}
