package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Collection;
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
public class CommunicationGroup  {

	// Keeps track of which group this peer belongs to
	// keys - PeerGroupID, values - PeerGroup
	private Hashtable memberOfGroups;
	
	private CommunicationProtocolJxta commProt;
	
	private Hashtable createdGroups;

	public CommunicationGroup(CommunicationProtocolJxta commProt) {
		this.commProt = commProt;
		this.memberOfGroups = new Hashtable();
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
			implAdv = this.commProt.getGlobalPG().getAllPurposePeerGroupImplAdvertisement();

			//Create group
			pg = this.commProt.getGlobalPG().newGroup(null,implAdv, name,descr);

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
		PeerGroupID groupID = getPeerGroupID(groupIDasString);
		PeerGroup pg = null;
		//Get PeerGroup
		System.out.println("CommunicationGroup::joinGroup, groupID = " + groupID);
        try {
			if (memberOfGroupsContains(groupID)) {            
                return null;
            } else {
                pg = (PeerGroup) this.createdGroups.get(groupID);
                if (null == pg) {         
                  pg = this.commProt.getGlobalPG().newGroup(groupID);
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
				addToMemberOfGroups(pg);
				
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
		PeerGroupID groupID = getPeerGroupID(groupIDasString);
		PeerGroup pg = null;
		MembershipService member = null;
		DiscoveryService discServ = null;		
		PipeAdvertisement pipeAdv =null;

		//Get PeerGroup
		try {
			pg = this.commProt.getGlobalPG().newGroup(groupID);
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
			DiscoveryService discServGlobal = this.commProt.getGlobalPG().getDiscoveryService();
			discServGlobal.flushAdvertisements(groupIDasString, DiscoveryService.GROUP);

			//if left group, then update memberOfGroups
			removeFromMemberOfGroups(groupIDasString);
		
			//remove the inputpipe by flushing it from local cache
			pipeAdv = this.commProt.getInputPipeAdvertisement(pg.getPeerGroupID());
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
		DiscoveryService discServ = this.commProt.getGlobalPG().getDiscoveryService();
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
			ItemReference groupRes = new ItemReference (
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
		DiscoveryService discServ = this.commProt.getGlobalPG().getDiscoveryService();		
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
			searchGroupResult.add(new ItemReference(peerAdv.getPeerID(),
								  									peerAdv.getName(),
								  									peerAdv.getDescription()));
		}
		return searchGroupResult;
	}
	
	
	private void addToMemberOfGroups (PeerGroup pg) {
		memberOfGroups.put(pg.getPeerGroupID(), pg);
	}
	
	protected Collection memberOfGroupsByValues () {
		return memberOfGroups.values();
	}
	
	private void removeFromMemberOfGroups (String groupIDasString) {
		PeerGroup pg = getPeerGroupFromMemberOfGroups(groupIDasString);
		if (pg != null) {memberOfGroups.remove(pg.getPeerGroupID());
		}
	}

	private PeerGroup getPeerGroupFromMemberOfGroups (String groupIDasString) {
		Enumeration enum = memberOfGroups.elements();
		while (enum.hasMoreElements()) {
			PeerGroup pg = (PeerGroup) enum.nextElement();
			if (pg.getPeerGroupID().toString().equals(groupIDasString)) {
				return pg;
			} 
		}
		return null;
	}
	
	private boolean memberOfGroupsContains (PeerGroupID peerGroupId) {
		return memberOfGroups.containsKey(peerGroupId);
	}
	
	
	/**
	* Transfer a peer group ID as a String to the actual object representing the group 
	* @param groupIDasString the ID as a string
	* @return the group ID as an object
	* @version P2P-OntoRama 1.0.0
	*/
	private PeerGroupID getPeerGroupID(String groupIDasString) {
		PeerGroupID retVal = null;
		PeerGroupAdvertisement pgAdv = null;
		DiscoveryService discServ = this.commProt.getGlobalPG().getDiscoveryService();
		Enumeration enum = null;
		try {
			enum = discServ.getLocalAdvertisements(
													DiscoveryService.GROUP,
													"GID",
													groupIDasString);
		} catch (IOException e) {
			System.out.println("Error");
			e.printStackTrace();
			System.exit(1);   
		}
        
		while (enum.hasMoreElements()) {
		   //found at least one adv
			pgAdv = (PeerGroupAdvertisement) enum.nextElement();
			retVal = pgAdv.getPeerGroupID();
		}
		return retVal;
	}

	/**
	* Returns a PeerGroup with a certain groupID
	* @param groupIDasString the ID as a string
	* @return the object representing the group
	* @version P2P-OntoRama 1.0.0
	*/
	protected PeerGroup getPeerGroup(String groupIDasString) {
		PeerGroup pg = null;
		try {
			pg = this.commProt.getGlobalPG().newGroup(this.getPeerGroupID(groupIDasString));
		} catch (PeerGroupException e) {
			System.out.println("Error");
			e.printStackTrace();			
		}
		return pg;
	}
	
	
	public Vector peerDiscoveryForGlobalGroup () {
		PeerGroup globalGroup = this.commProt.getGlobalPG();
		Vector result = new Vector();
		try {
			DiscoveryService discServ = this.commProt.getGlobalPG().getDiscoveryService();
			discServ.getRemoteAdvertisements(null,	DiscoveryService.PEER,
											null,null,	10);
			Enumeration e = discServ.getLocalAdvertisements(DiscoveryService.PEER,
													null,null);
			while (e.hasMoreElements()){
				PeerAdvertisement cur = (PeerAdvertisement) e.nextElement();
				ItemReference element = new ItemReference(cur.getID(), cur.getName(), cur.getDescription());
			  	result.addElement(element);
			}
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return result;
	}
	
}
