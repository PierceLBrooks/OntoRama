package ontorama.backends.p2p.p2pprotocol;


/**
 * This class handles the group exception when the global group or a subgroup 
 * cannot be created
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class GroupExceptionInit extends GroupException {

	/**
	* The constructor 
	* 
	* @param e the exception
	* @param descr a description of the error
	*/ 	
	public GroupExceptionInit(Exception e,String descr) {
		super(e,descr);	
	}

	/**
	* The constructor 
	* 
	* @param descr a description of the error
	*/ 	
	public GroupExceptionInit(String descr) {
		super(descr);	
	}

}
