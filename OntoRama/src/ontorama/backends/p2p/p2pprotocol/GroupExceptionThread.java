package ontorama.backends.p2p.p2pprotocol;

/**
 * This class handles the group exception thread
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class GroupExceptionThread extends GroupException{

	/**
	* The constructor 
	* 
	* @param e the exception
	* @param descr a description of the error
	*/ 	
	public GroupExceptionThread(Exception e,String descr) {
		super(e,descr);	
	}

	/**
	* The constructor 
	* 
	* @param descr a description of the error
	*/ 	
	public GroupExceptionThread(String descr) {
		super(descr);	
	}
}
