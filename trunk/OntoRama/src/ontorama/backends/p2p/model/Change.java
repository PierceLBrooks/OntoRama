package ontorama.backends.p2p.model;

import ontorama.backends.p2p.PeerItemReference;


/**
 * Model a change received from other peers.
 * or a change we want to send to other peers.
 * 
 * @author nataliya
 * Created on 7/03/2003
 */
public abstract class Change {
	protected PeerItemReference _peer;
	protected String _action;
	protected String _initiatorUri;

	
	public String getAction() {
		return _action;
	}

	public String getInitiatorUri() {
		return _initiatorUri;
	}

	public PeerItemReference getPeer() {
		return _peer;
	}
	
	public void setPeer(PeerItemReference peer) {
		_peer = peer;
	}

}
