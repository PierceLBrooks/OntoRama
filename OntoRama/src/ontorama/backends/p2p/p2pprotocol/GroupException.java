package ontorama.backends.p2p.p2pprotocol;

import net.jxta.exception.PeerGroupException;

/**
 * This class handles the group exception and extends JXTA PeerGroupException
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class GroupException extends PeerGroupException {
	private Exception e = null;

	/**
	* The constructor 
	* 
	* @param e the exception
	* @param msg a description of the error
	*/ 	
	public GroupException(Exception e, String msg) {
			super(msg);
			this.e = e;
	}

	/**
	* The constructor 
	* 
	* @param msg a description of the error
	*/ 	
	public GroupException(String msg) {
			super(msg);
	}


	/**
	* Gets the exception
	* 
	* @return Exception the exception object for this exception
	*/
	public Exception getException() {
		return this.e;
	}
	
}
