package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.StructuredDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.impl.membership.NullMembershipService.NullAuthenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 * This class handles the P2P communication which is related to group functionality.
 *  
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class CommunicationGroup extends Communication {
	private CommunicationProtocolJxta commProt = null;
	private Hashtable createdGroups = null;

	public CommunicationGroup(CommunicationProtocolJxta commProt) {
		this.commProt = commProt;
		this.createdGroups = new Hashtable();
	}


	/**
	 * Creates a new group within the global group.
	 * 
	 * @param name name of the group
	 * @param descr description of the group
	 * 
	 * @return the created peer group
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public PeerGroup createGroup(String name,String descr) throws GroupException {
		PeerGroup pg = null;
		ModuleImplAdvertisement implAdv = null;

		//Get the ModuleImplAdvertisement
		try {
			implAdv = Communication.getGlobalPG().getAllPurposePeerGroupImplAdvertisement();

			//Create group
			pg = Communication.getGlobalPG().newGroup(null,implAdv, name,descr);

			this.createdGroups.put(pg.getPeerGroupID(), pg);
			
			System.out.println("\ncreated group PeerGroup pg = " + pg + ", group name = " + pg.getPeerGroupName());
		} catch (PeerGroupException e) {
			throw new GroupException(e,"Could not create a group");
		} catch (Exception e) {
			throw new GroupException(e,"Could not get the all purpose peer group advertisement");
		}
		return pg; 			
	}



	/** 
	 * Join the group that has given groupID.
	 * 
	 * @param groupID The ID of the group to join
	 * 
	 * @return the peer group that was joined
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public PeerGroup joinGroup(String groupIDasString) throws GroupExceptionNotExist, GroupExceptionNotAllowed {
		System.out.println("CommunicationGroup::joinGroup, groupIDasString = " + groupIDasString);
		PeerGroupID groupID = this.getPeerGroupID(groupIDasString);
		PeerGroup pg = null;
		//Get PeerGroup
		System.out.println("CommunicationGroup::joinGroup, groupID = " + groupID);
        try {
			//if (this.getMemberOfGroups().containsKey(groupID)) {
			if (this.memberOfGroupsContains(groupID)) {            
                return null;
            } else {
                pg = (PeerGroup) this.createdGroups.get(groupID);
                if (null == pg) {         
                  pg = Communication.getGlobalPG().newGroup(groupID);
                } 
                this.joinGroup(pg);
            }
		} catch (GroupExceptionNotAllowed e) {
			throw (GroupExceptionNotAllowed) e.fillInStackTrace();
		} catch (PeerGroupException e) {
			//No group was found	
			throw new GroupExceptionNotExist(e,"No PeerGroup with ID:" + groupID + " was found");
		}
		
		return pg; 			
	}


	/** 
	 * Join the group that has given groupID.
	 * 
	 * @param groupID The ID of the group to join
	 * 
	 * @return the peer group that was joined
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	private PeerGroup joinGroup(PeerGroup pg) throws GroupExceptionNotAllowed, GroupException {
		NullAuthenticator auth = null;
		StructuredDocument indentityInfo = null;

		//Get membershipservice
		MembershipService member = pg.getMembershipService();

		//Get my credential
		AuthenticationCredential authCred = new AuthenticationCredential(
												pg,null,indentityInfo);


		//Apply
		try {
			auth = (NullAuthenticator) member.apply(authCred);

			//If auth ok Then Join
			if (auth.isReadyForJoin()) {
				member.join(auth);

				//If joined, then update the memberOfGroups
				//this.getMemberOfGroups().put(pg.getPeerGroupID(), pg);
				this.addToMemberOfGroups(pg);
				
				System.out.println("CommunicationGroup::joinGroup(pg): joined group " + pg.getPeerGroupName() + ", id = " + pg.getPeerGroupID());
	           				
			} else {
				throw new GroupExceptionNotAllowed("Was not allowed to join the Peer Group");
			}
		} catch (ProtocolNotSupportedException e) {
			throw new GroupException(e, "Could not apply since the protocol is not supported");
		} catch (GroupExceptionNotAllowed e) {
			throw (GroupExceptionNotAllowed) e.fillInStackTrace();
		} catch (PeerGroupException e) {
			throw new GroupExceptionNotAllowed(e,"Was not allowed to joing the Peer Group");
		}
		return pg; 			
	}

	/**
	 * Leave a group with a specified  groupID
	 * 
	 * @param groupID The ID of the group to leave
	 * 
	 * @return true if leaved group sucessfully
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public boolean leaveGroup(String groupIDasString) throws GroupException, IOException{
		PeerGroupID groupID = this.getPeerGroupID(groupIDasString);
		PeerGroup pg = null;
		MembershipService member = null;
		DiscoveryService discServ = null;		
		PipeAdvertisement pipeAdv =null;

		//Get PeerGroup
		try {
			pg = Communication.getGlobalPG().newGroup(groupID);
		} catch (PeerGroupException e) {
			throw new GroupException(e, "The group does not exist");
		}


		discServ = pg.getDiscoveryService();
		
		//Get membershipservice
		member = pg.getMembershipService();

		//Resign from the membership
		try {
			//TODO handle the cache problem on leaving group when a peer discovery is performed
			//could be solved bu flush or reset the lifetime for peer advertisement
			//Takes care of cache problems on remote peers
			//otherwise other peers still thinks this peer is in the group (on peer discovery)
//			commProt.callFlushPeerAdvertisementFrom(groupID.toString());

			
			//JXTA method for resigning from a group
			member.resign();


			//Flushs the advertisment from the parent group (in this case the Global Group)
			DiscoveryService discServGlobal = Communication.getGlobalPG().getDiscoveryService();
			discServGlobal.flushAdvertisements(groupIDasString, DiscoveryService.GROUP);

			//if left group, then update memberOfGroups

			//this.removeElementFromMembersOfGroup(groupIDasString);
			removeFromMemberOfGroups(groupIDasString);

			//this.getMemberOfGroups().remove(groupIDasString);
			
			//remove the inputpipe by flushing it from local cache
			pipeAdv = this.getInputPipeAdvertisement(pg.getPeerGroupID());
			System.out.println("CommunicationGroup::leaveGroup, discServ = " + discServ + ", pipeAdv = " 
									+ pipeAdv);
			if (pipeAdv == null) {
				/// @todo not sure if this check should be here (if we ever could have null pipeAdv
				/// or if this should never happen)
				ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error leaving group", 
										"Ooops...\nCouldn't find pipe advertisement in order to flush advertisement :(");
			}
			discServ.flushAdvertisements(pipeAdv.getPipeID().toString(), DiscoveryService.ADV);
							
		} catch (PeerGroupException e) {
				throw new GroupException(e,"Could not leave the peer group");
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();
		}
		return true;
	}

	/**
	 * Search for PeerGroups, can choose to search for:
	 * group name, group descr or both
	 * 
	 * @param searchAttrib: null/SEARCHGROUPNAME/SEARCHGROUPDESCR
	 * @param searchString: null/string and or wildcards (e.g. ?,*)
	 * 
	 * @return vector of GroupReferenceElement
	 * @exception 
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public Vector searchGroup(String searchAttrib, 
								String searchString) 
								throws GroupExceptionThread, IOException {
									
		Vector result = new Vector();
		DiscoveryService discServ = Communication.getGlobalPG().getDiscoveryService();
		PeerGroupAdvertisement pgAdv = null;
		Enumeration enum = null;
				
		//Send a request to other peers
		discServ.getRemoteAdvertisements(null,
									DiscoveryService.GROUP,
										searchAttrib,
										searchString,
										20);

		discServ.getRemoteAdvertisements(null,
									DiscoveryService.GROUP,
										null,
										null,
										10);
										
		//Wait for the adv to come in
		try {
			Thread.sleep(3*1000);

			//Collect adv from local cache
			enum = discServ.getLocalAdvertisements(DiscoveryService.GROUP,
														searchAttrib,
														searchString);
		} catch (InterruptedException e) {
			throw new GroupExceptionThread(e, "The thread was interrupted while searching for groups");
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();
		}
		
		System.out.println("returned from group search");
													
		while (enum.hasMoreElements()) {
			//found at least one adv
			//populate SearchGroupResult from incoming advertisments
			pgAdv = (PeerGroupAdvertisement) enum.nextElement();
			GroupReferenceElement groupRes = new GroupReferenceElement (
												pgAdv.getPeerGroupID(),
												pgAdv.getName(), pgAdv.getDescription());
			System.out.println("search groups returned: group name = " + groupRes.getName() + ", group id = " + pgAdv.getPeerGroupID());
			result.add(groupRes);				
		}

		return result;
	}
	
	/**
	* Is called to do searches for at other peers. The methods sends out a search request and 
	* then waits for a certain amount of time for responses from other peers.
	* 
	* @param query a string containing the query 
	* @return a vector of GroupReferenceElement
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public Vector peerDiscovery (String groupIDasString) 
								throws IOException, GroupExceptionThread {
		
		PeerAdvertisement peerAdv = null;
		PeerGroup pg = null;
		DiscoveryService discServ = Communication.getGlobalPG().getDiscoveryService();		
        Enumeration enum1 = null;
		Enumeration enum = null;
       		
		//Get the group the the peer answering the question belongs to. 
		pg = getPeerGroup(groupIDasString);
		
		//Get the correct discoveryService (from the correct group)
		DiscoveryService discServ1 = pg.getDiscoveryService();
			
		//Send a request to other peers
		discServ1.getRemoteAdvertisements(null,
									DiscoveryService.PEER,
										null,null,
										10);
		try {
			Thread.sleep(3*1000);

			enum1 = discServ1.getLocalAdvertisements(DiscoveryService.PEER,
													null,null);
		} catch (IOException e) {
			throw (IOException) e.fillInStackTrace();
		} catch (InterruptedException e) {
			throw new GroupExceptionThread(e, "The thread was interrupted while doing a peer discovery");
		}
										
				
					
		//Get all peer advertisements that are stored in the local cahe
		Vector searchGroupResult = new Vector();	
		System.out.println("\tCommunicationGroup::peerDiscovery retuning");				
		while (enum1.hasMoreElements()) {
			//Add the peer information to the searchGroupResult
			peerAdv = (PeerAdvertisement) enum1.nextElement();
			System.out.println("\t.peerAdv.getName() = " + peerAdv.getName()
								+ ", peerAdv.getPeerID() = " 
								+ peerAdv.getPeerID()
								+ ", group = " + peerAdv.getPeerGroupID());
			searchGroupResult.add(new GroupReferenceElement(peerAdv.getPeerID(),
								  									peerAdv.getName(),
								  									peerAdv.getDescription()));
		}
		return searchGroupResult;
	}
	

//	private void removeElementFromMembersOfGroup(String groupIDasString) {
//		Enumeration enum = this.getMemberOfGroups().elements();
//		
//		PeerGroup pg = null;
//		int i = 0;
//		
//		while (enum.hasMoreElements()) {
//			pg = (PeerGroup) enum.nextElement();
//			if (pg.getPeerGroupID().toString().equals(groupIDasString)) {
//				this.getMemberOfGroups().remove(pg);
//			} 
//			i++;
//		}
//	}

//	private void printMembers() {
//		Enumeration enum = this.getMemberOfGroups().elements();
//		PeerGroup pg = null;
//		
//		System.err.println("This are the members in mermbersOfGroup");
//		
//		while (enum.hasMoreElements()) {
//			pg = (PeerGroup) enum.nextElement();
//			System.err.println("PG:" + pg.getPeerGroupName());	
//		}
//	}
}
