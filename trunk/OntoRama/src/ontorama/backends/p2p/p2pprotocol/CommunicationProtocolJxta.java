package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.peergroup.PeerGroup;
import ontorama.backends.p2p.p2pmodule.P2PRecieverInterface;

/**
 * This class handles the P2P communication and implements the 
 * interface CommunicationProtocol. It is implemented with JXTA, www.jxta.org, 
 * from Sun Microsystem. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */

public class CommunicationProtocolJxta implements CommunicationProtocol {
	private CommunicationGroup communicationGroup = null;
	private CommunicationInit communicationInit = null;
	private CommunicationSender communicationSender = null;

	//Object to use for receiving messages
	P2PRecieverInterface reciever = null;	
	
	/**
	 * The constructor for the class.
	 * 
	 * @exception
	 * @version P2P-OntoRama 1.0.0
	 */
	public CommunicationProtocolJxta (P2PRecieverInterface recieverObject) throws GroupExceptionInit {
		communicationGroup = new CommunicationGroup(this);
		communicationInit = new CommunicationInit(this);
		communicationSender = new CommunicationSender(this);
		
		this.reciever = recieverObject;
		try {
			communicationInit.initJxtaTopGroup();

            communicationInit.startInputPipeEndpoint(communicationGroup.getGlobalPG());

		} catch (GroupExceptionInit e) {
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
	}

	/**
	* Sends a Logout Command to every Peer in the network that response to 
	* the PeerDiscovery. 
	* 
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendLogoutCommand() throws GroupExceptionThread, GroupExceptionFlush {
		try {
			communicationSender.sendMessage(0,
											null,
											communicationSender.getPeerIDasString(),
											null,
											Communication.TAGLOGOUT,
											"");
			//Takes care of the cache problem at remote peers
			//otherwise other peers still thinks this peer is online
			communicationSender.callFlushPeerAdvertisementOnLogout();

		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();
		} catch (GroupExceptionFlush e) {
			throw (GroupExceptionFlush) e.fillInStackTrace();
		}
	}


	/**
	* Sends a search request to other peers
	* 
	* @param query the query to send
	* @return a vector of SearchResultElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public Vector sendSearch(String query) throws IOException, GroupExceptionThread {
		Vector searchResult = null;
		try {
			searchResult = sendSearchRequest(query);
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();		}
		return searchResult;
	}


	/**
	* Sends a response to a question
	* 
	* @param recieverPeerID the peer that made the question
	* @param body what will be sent as a response
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/ 
    public void sendSearchResponse(String recieverPipeAdvID, String body) throws GroupExceptionThread {
       try {
		communicationSender.sendMessage(0,
		   									recieverPipeAdvID, 
		   									null, 
		   									null, 
		   									Communication.TAGSEARCHRESPONSE, 
		   									body);
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();	
		} 
    }


	/**
	* Propagate information to every peer that response to the PeerDiscovery. 
	* 
	* @param TAG the tag that identify the type of message to be sent
	* @param recieverID the peer id of the reciever (null if to every peer)
	* @param internalModel the message to send
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendPropagate(int TAG, String recieverID, String internalModel) throws GroupExceptionThread {
		try {
			communicationSender.sendMessage(TAG,
											recieverID,
											communicationSender.getPeerIDasString(),
											null,
											Communication.TAGPROPAGATE, 
											internalModel);
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();		
		} 			
	}

	/**
	 * Create a group
	 * 
	 * @param name name of the group
	 * @param descr description of the group
	 * 
	 * @return the created PeerGroup
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public PeerGroup sendCreateGroup(String name,String descr) throws GroupException {
		try {
			return this.communicationGroup.createGroup(name,descr);
		} catch (GroupException e) {
			throw (GroupException) e.fillInStackTrace();
		}
	}


	/** 
	 * Join the group that has given groupID.
	 * 
	 * @param groupIDasString group to join
	 * 
	 * @return name of the group if joined sucessfully
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public String sendJoinGroup(String groupIDasString) throws GroupExceptionNotAllowed, GroupException {
		PeerGroup pg;
		try {
			pg = this.communicationGroup.joinGroup(groupIDasString);
			if (pg != null) {
				//Changed
                //communicationInit.startInputPipeEndpoint(pg);
                communicationInit.startPropagatePipeEndpoint(pg);
                return pg.getPeerGroupName();
			}
		} catch (GroupExceptionNotAllowed e) {
			throw (GroupExceptionNotAllowed) e.fillInStackTrace();			
		} catch (GroupException e) {
			throw (GroupException) e.fillInStackTrace();
		}
		
		return null;
	}


	/**
	 * Leave a group with PeerGroupID groupID
	 * 
	 * @param groupIDasString peer group id of the group to leave
	 * 
	 * @return true if leaved group sucessfully
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public boolean sendLeaveGroup(String groupIDasString) throws GroupException, IOException {
		try {
			return this.communicationGroup.leaveGroup(groupIDasString);
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();

		} catch (GroupException e) {
			throw (GroupException) e.fillInStackTrace();
		}
	}

	/**
	 * Search for PeerGroups, can choose to search for:
	 * group name, group descr or both
	 * 
	 * @param searchAttrib null/SEARCHGROUPNAME/SEARCHGROUPDESCR
	 * @param searchString null/string and or wildcards (e.g. ?,*)
	 * 
	 * @return a vector of SearchGroupResultElement
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public Vector sendSearchGroup(String searchAttrib, 
								  String searchString) throws IOException, GroupExceptionThread {
		Vector retVal = null;
		try {
			retVal = this.communicationGroup.searchGroup(searchAttrib,searchString);
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();	
		}
		return retVal;
	}
	

	/**
	* Is called to do searches for at other peers. The methods sends out a 
	* search request and then waits for 20 seconds to get responses.
	* 
	* @param groupIDasString the peer group to perform a peer discovery in
	* @return a vector of SearchGroupResultElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public Vector peerDiscovery (String groupIDasString) throws IOException, GroupExceptionThread {
		Vector retVal = null;
		try {
			retVal= this.communicationGroup.peerDiscovery(groupIDasString);
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();			
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();				
		}	
		return retVal;
	}	


	/**
	* Is called to search for information
	* 
	* @param query a string containing the query 
	* @return a vector of SearchResultElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
    private Vector sendSearchRequest(String query) throws IOException, GroupExceptionThread{
		return communicationSender.sendSearchRequest(query);
    }
	
	/** 
	* This method is called to flush (on every peer) this peers 
	* PeerAdvertisements from a given group. 
	*
	* @param groupIDasString the id of the peergroup in that the flush should be done
	* @exception 
	* 
	* @version P2P-OntoRama 1.0.0
	*
	*/	
	public void callFlushPeerAdvertisementFrom(String groupIDasString) throws GroupExceptionFlush, GroupExceptionThread{
		try {
			communicationSender.flushPeerAdvertisement(groupIDasString,"");
			//Flush the peer advertisement remotely
			PeerGroup pg = communicationSender.getPeerGroup(groupIDasString);
			String peerIDasString = pg.getPeerAdvertisement().getID().toString();

			System.err.println("CommProt::callFlush(" + pg.toString() +";" + peerIDasString);

			communicationSender.sendMessage(0,
											null,
											peerIDasString,
											groupIDasString,
											Communication.TAGFLUSHPEER,
											"");
		} catch (GroupExceptionFlush e) {
			throw (GroupExceptionFlush) e.fillInStackTrace();	
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();
		}
		
	}


	/** 
	* This method is called on an incoming flush request
	*
	* @param groupIDasString the id of the peergroup in that the flush should be done
	* @param peerID 
	* @exception 
	* 
	* @version P2P-OntoRama 1.0.0
	*
	*/	
	public void recieveFlushPeerAdvertisement(String groupIDasString,String peerIDasString) throws GroupExceptionFlush, GroupExceptionThread{
		System.err.println("CommProt::recievedFLushPeerAdvertisement" + groupIDasString + ";" + peerIDasString);
		try {
			communicationSender.flushPeerAdvertisement(groupIDasString,peerIDasString);
		} catch (GroupExceptionFlush e) {
			throw (GroupExceptionFlush) e.fillInStackTrace();	
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();
		}
		
	}
	
	/**
	* Gets the reciever object
	* 
	* @return P2PReciever the the reciever object
	*
	* @version P2P-OntoRama 1.0.0
	*/	
	public P2PRecieverInterface getRecieverObject() {
		return this.reciever;
	}

 /**
    * Returns all the groups the peer belongs to.   
    * 
    * @return a vector of group IDs
    *
    * @version P2P-OntoRama 1.0.0
    */
    public Vector getMemberOfGroups(){
        Vector groups = null;
        Hashtable temptable = this.communicationGroup.getMemberOfGroups();
        groups = new Vector(temptable.values());         
        
        return groups;
    }
}
