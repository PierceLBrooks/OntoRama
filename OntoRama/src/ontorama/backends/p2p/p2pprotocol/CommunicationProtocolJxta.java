package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.protocol.PipeAdvertisement;
import ontorama.backends.p2p.P2PGlobals;
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

	private CommunicationGroup communicationGroup;
	private CommunicationInit communicationInit;
	private CommunicationSender communicationSender;

	
	//The serach result from a query (on tontologies)
	private Vector searchResult;
	
	//The pipe advertisement
	private Hashtable outputPropagatePipe;

	//The pipe
	private Hashtable inputPropagatePipe;

	//The pipe
	private Hashtable inputPipeAdvertisement;

	/**
	 * The constructor for the class.
	 * 
	 * @exception GroupExceptionInit
	 * @version P2P-OntoRama 1.0.0
	 */
	public CommunicationProtocolJxta (P2PRecieverInterface recieverObject) throws GroupExceptionInit {

		inputPipeAdvertisement = new Hashtable();
		outputPropagatePipe = new Hashtable();
		inputPropagatePipe = new Hashtable();

		communicationGroup = new CommunicationGroup(this);
		communicationInit = new CommunicationInit(this, recieverObject);
		communicationSender = new CommunicationSender(this);	
		
		try {
			communicationInit.initJxtaTopGroup();
		} catch (GroupExceptionInit e) {
			e.printStackTrace();
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
	}

	/**
	* Sends a Logout Command to every Peer in the network that response to 
	* the PeerDiscovery. 
	* 
	* @exception  GroupExceptionThread
    * @exception   GroupExceptionFlush
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendLogoutCommand() throws GroupExceptionThread, GroupExceptionFlush {
		try {
			communicationSender.sendMessage(0,
											null,
											getPeerID().toString(),
											null,
											P2PGlobals.TAGLOGOUT,
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
	* @exception IOException
    * @exception GroupExceptionThread
	* @version P2P-OntoRama 1.0.0
	*/
	public Vector sendSearch(String query) throws IOException, GroupExceptionThread {
		Vector searchResult = null;
		try {
			searchResult = sendSearchRequest(query);
		} catch (GroupExceptionThread e) {
			throw (GroupExceptionThread) e.fillInStackTrace();		}
		return searchResult;
	}


	/**
	* Sends a response to a question
	* 
	* @param recieverPipeAdvID the peer that made the question
	* @param body what will be sent as a response
	* @exception GroupExceptionThread
	*
	* @version P2P-OntoRama 1.0.0
	*/ 
    public void sendSearchResponse(String recieverPipeAdvID, String body) throws GroupExceptionThread {
       try {
		communicationSender.sendMessage(0,
		   									recieverPipeAdvID, 
		   									null, 
		   									null, 
		   									P2PGlobals.TAGSEARCHRESPONSE, 
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
	* @exception  GroupExceptionThread
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public void sendPropagate(int TAG, String recieverID, String internalModel) throws GroupExceptionThread {
		try {
			communicationSender.sendMessage(TAG,
											recieverID,
											getPeerID().toString(),
											null,
											P2PGlobals.TAGPROPAGATE, 
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
	 * @exception GroupException
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
	 * @exception GroupExceptionNotAllowed
     * @exception GroupException
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
	 * @exception GroupException
     * @exception IOException
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
	 * @return a vector of GroupReferenceElement
	 * @exception  IOException
     * @exception  GroupExceptionThread
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
	* @return a vector of GroupReferenceElement
	* @exception  IOException
    * @exception  GroupExceptionThread
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
    * @exception GroupExceptionThread
	*
	* @version P2P-OntoRama 1.0.0
	*/
    private Vector sendSearchRequest(String query) throws GroupExceptionThread{
		return communicationSender.sendSearchRequest(query);
    }
	
	/** 
	* This method is called to flush (on every peer) this peers 
	* PeerAdvertisements from a given group. 
	*
	* @param groupIDasString the id of the peergroup in that the flush should be done
	* @exception GroupExceptionFlush
    * @exception  GroupExceptionThread
	* 
	* @version P2P-OntoRama 1.0.0
	*
	*/	
	public void callFlushPeerAdvertisementFrom(String groupIDasString) throws GroupExceptionFlush, GroupExceptionThread{
		try {
			communicationSender.flushPeerAdvertisement(groupIDasString,"");
			//Flush the peer advertisement remotely
			PeerGroup pg = communicationGroup.getPeerGroup(groupIDasString);
			String peerIDasString = pg.getPeerAdvertisement().getID().toString();

			System.err.println("CommProt::callFlush(" + pg.toString() +";" + peerIDasString);

			communicationSender.sendMessage(0,
											null,
											peerIDasString,
											groupIDasString,
											P2PGlobals.TAGFLUSHPEER,
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
	* @param peerIDasString
	* @exception GroupExceptionFlush
    * @exception GroupExceptionThread
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
    * Returns all the groups the peer belongs to.   
    * 
    * @return a vector of group IDs
    *
    * @version P2P-OntoRama 1.0.0
    */
    public Vector getMemberOfGroups(){
        return new Vector(this.communicationGroup.memberOfGroupsByValues());   
    }
    
    
	protected PeerGroup getPeerGroup(String groupIDasString) {
		return this.communicationGroup.getPeerGroup(groupIDasString);
	}
	
	/**
	* Gets the PeerID of this peer
	* @return a peer id
	* @version P2P-OntoRama 1.0.0
	*/
	public PeerID getPeerID() {
		return getGlobalPG().getPeerID();
	}
	
	/**
	* Gets the global PeerGroup. The global PeerGroup is the group every peer belongs to.
	* @return the globa peergroup
	* @version P2P-OntoRama 1.0.0
	*/
	public PeerGroup getGlobalPG() {
		return communicationInit.getGlobalPG();
	}

	/**
	* Gets the search result 
	* @return a vector of SearchResultElement
	* @version P2P-OntoRama 1.0.0
	*/
	protected Vector getSearchResult() {
		return searchResult;
	}

	/**
	* Sets the search result
	* @param obj vector of SearchResultElement that should be set
	* @version P2P-OntoRama 1.0.0
	*/
	protected void clearSearchResult() {
		searchResult = new Vector();
	}
	
	/**
	* Sets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @param obj the pipe advertisement to set
	* @version P2P-OntoRama 1.0.0
	*/
	protected void addInputPipeAdvertisement(PeerGroupID groupID,PipeAdvertisement obj)  {
		System.out.println("\nCommunication::setInputPipeAdvertisement for group id " + groupID + " putting " + obj);
		inputPipeAdvertisement.put(groupID,obj);
	}
	

	/**
	* Gets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @return the pipe advertisement
	* @version P2P-OntoRama 1.0.0
	*/
	protected PipeAdvertisement getInputPipeAdvertisement(PeerGroupID groupID)  {
		System.out.println("Communication::getInputPipeAdvertisement hashtable size = " + inputPipeAdvertisement.size());
		System.out.println("Communication::getInputPipeAdvertisement for group id " + groupID +
						" returning " + (PipeAdvertisement) inputPipeAdvertisement.get(groupID));
		return (PipeAdvertisement) inputPipeAdvertisement.get(groupID);
	}


	/**
	* Sets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @param obj the pipe advertisement to set
	* @version P2P-OntoRama 1.0.0
	*/
	protected void addOutputPropagatePipe(PeerGroupID groupID,OutputPipe obj)  {
		outputPropagatePipe.put(groupID,obj);
	}

	/**
	* Gets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @return the pipe advertisement
	* @version P2P-OntoRama 1.0.0
	*/
	protected OutputPipe getOutputPropagatePipe(PeerGroupID groupID)  {
		return (OutputPipe) outputPropagatePipe.get(groupID);
	}

	/**
	* Sets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @param obj the pipe advertisement to set
	* @version P2P-OntoRama 1.0.0
	*/
	protected void addInputPropagatePipe(PeerGroupID groupID,OutputPipe obj)  {
		inputPropagatePipe.put(groupID,obj);
	}

	/**
	* Gets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
	* from other peers.
	* @return the pipe advertisement
	* @version P2P-OntoRama 1.0.0
	*/
	protected InputPipe getInputPropagatePipe(PeerGroupID groupID)  {
		return (InputPipe) inputPropagatePipe.get(groupID);
	}
	
       
	/* (non-Javadoc)
	 * @see ontorama.backends.p2p.p2pprotocol.CommunicationProtocol#sendSearchAllPeers()
	 */
	public Vector sendSearchAllPeers() throws GroupExceptionThread, IOException {
		return this.communicationGroup.peerDiscoveryForGlobalGroup();
	}

}
