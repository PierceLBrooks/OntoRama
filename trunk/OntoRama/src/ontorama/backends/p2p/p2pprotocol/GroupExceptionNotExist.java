package ontorama.backends.p2p.p2pprotocol;


/**
 * This class handles the group exception if the group does not exist
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class GroupExceptionNotExist extends GroupException {

	/**
	* The constructor 
	* 
	* @param descr a description of the error
	*/ 	
	public GroupExceptionNotExist(String descr) {
		super(descr);	
	}

	/**
	* The constructor 
	* 
	* @param e the exception
	* @param descr a description of the error
	*/ 	
	public GroupExceptionNotExist(Exception e,String descr) {
		super(e,descr);	
	}

}
