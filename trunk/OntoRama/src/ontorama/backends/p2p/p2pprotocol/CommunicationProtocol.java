package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Vector;

import net.jxta.peergroup.PeerGroup;

/**
 * This interface handles the P2P communication. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public interface CommunicationProtocol {
            

	/**
	* Sends a Logout Command to every Peer in the network that response to 
	* the PeerDiscovery. 
	* 
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendLogoutCommand() throws GroupExceptionThread, GroupExceptionFlush;
	
	/**
	* Propagate information to every peer that 
	* response to the PeerDiscovery. 
	* 
	* @param TAG the kind of propagate to send
	* @param recieverID the peer id of the reciever (null if the message shall be sent to every peer)
	* @param internalModel the changes that has been done
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendPropagate(int TAG, String recieverID, String internalModel) throws GroupExceptionThread ;


	/**
	* Is called to do searches for at other peers. The methods sends out a search request and 
	* then waits for 20 seconds to get responses
	* 
	* @param query a string containing the query 
	* @return a vector of searchResultElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
    public Vector sendSearch(String query) throws IOException, GroupExceptionThread ;


	/**
	 * Search for PeerGroups, can choose to search for:
	 * group name, group descr or both
	 * 
	 * @param searchAttrib null/SEARCHGROUPNAME/SEARCHGROUPDESCR
	 * @param searchString null/string and or wildcards (e.g. ?,*)
	 * 
	 * @return a vector of GroupReferenceElement
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
    public Vector sendSearchGroup(String searchAttrib, String searchString) throws IOException, GroupExceptionThread ;


	/**
	 * Create a group
	 * 
	 * @param name name of the group
	 * @param descr description of the group
	 * @return PeerGroup the created PeerGroup
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public PeerGroup sendCreateGroup(String name, String descr) throws GroupException;
	


	/** 
	 * Join the group that has given groupID.
	 * 
	 * @param groupID group to join
	 * @return group name if joined sucessfully
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public String sendJoinGroup(String groupID) throws GroupExceptionNotAllowed, GroupException ;
	


	/**
	 * Leave a group with PeerGroupID groupID
	 * 
	 * @param groupID PeerGroupID of the group to leave
	 * 
	 * @return true if leaved group sucessfully
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public boolean sendLeaveGroup(String groupID) throws GroupException, IOException ;

	
	/**
	* Sends a response to a question
	* 
	* @param recieverPeerID the peer that made the request
	* @param body what will be sent as a response
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/ 	
	public void sendSearchResponse(String recieverPeerID, String body) throws GroupExceptionThread ;

	/**
	* Is called to do searches for at other peers. The methods sends out a search request and 
	* then waits for 20 seconds to get responses
	* 
	* @param groupIDasString a string with the peer group id for the group to send a peer discovery in
	* @return a vector of GroupReferenceElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public Vector peerDiscovery (String groupIDasString) throws IOException, GroupExceptionThread;

    /**
    * Returns all the groups the peer belongs to.   
    * 
    * @return a vector of group IDs
    *
    * @version P2P-OntoRama 1.0.0
    */
    public Vector getMemberOfGroups();
    

	/**
	 * send search for all peers we can find (not necesserily beloning to any groups)
	 * @todo not sure if this belongs here at all
	 * @author nataliya
	 */
	public Vector sendSearchAllPeers () throws GroupExceptionThread, IOException;
}
