package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.discovery.DiscoveryService;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 * This base class handles the common P2P functionality.
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class Communication {
	//The serach result from a query (on tontologies)
	private static Vector searchResult = null;
	//The top p2p group
	private static PeerGroup globalP2PGroup = null;
	//The p2p platform
	private static PeerGroup globalP2PPlatform = null;	
	//Keeps track of which group this peer belongs to
	private static Vector memberOfGroups = null;
	//The pipe advertisement
	private static Hashtable pipeAdvertisement = null;	
	

	//Used by sendSearchGroup
	public final static String SEARCHGROUPNAME = "Name";
	public final static String SEARCHGROUPDESCR = "Desc";
	//Used by sendMessage
	public final static int TAGSEARCH = 1;
	public final static int TAGLOGOUT = 2;
	public final static int TAGPROPAGATE = 3;
	public final static int TAGSEARCHRESPONSE = 4;
  	public final static int TAGFLUSHPEER = 5;
  	
	public Communication() {
		this.setMemberOfGroups(new Vector(2));
		this.pipeAdvertisement = new Hashtable();
	}


	//Getters & Setters
	/**
	* Gets the search result 
	* 
	* @return a vector of SearchResultElement
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	
	protected Vector getSearchResult() {
		return this.searchResult;
	}
	

	/**
	* Sets the search result
	* 
	* @param a vector of SearchResultElement that should be set
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected void setSearchResult(Vector obj) {
		this.searchResult = obj;
	}


	/**
	* Gets the global platform
	* 
	* @return the platform 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected PeerGroup getGlobalPlatform() {
		return this.globalP2PPlatform;
	}
	
	/**
	* Sets the global platform
	* 
	* @param the platform that should be used as the global platform
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected void setGlobalPlatform(PeerGroup obj)  {
		this.globalP2PPlatform = obj;
	}
	
	/**
	* Gets the PeerID of this peer
	* 
	* @return a peer id 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/	
	public String getPeerIDasString() {
		return this.getGlobalPG().getPeerID().toString();
	}


	/**
	* Sets the memberOfGroups object, which is used to save all the groups the peer 
    * belongs to.
	* 
	* @param obj a Hashtable cointaining the groups the peer belongs to. 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	private void setMemberOfGroups(Vector obj)  {
		this.memberOfGroups = obj;
	}
	
	/**
	* Gets the member of groups object, which is used to save all the groups the peer 
    * belongs to.
	* 
	* @return cointaining the groups the peer belongs to. 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/	
	protected Vector getMemberOfGroups() {
		return this.memberOfGroups;
	}

	/**
	* Sets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
    * from other peers.
	* 
	* @param obj the pipe advertisement to set
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected void setPipeAdvertisement(PeerGroupID groupID,PipeAdvertisement obj)  {
		this.pipeAdvertisement.put(groupID,obj);
	}
	

	/**
	* Gets the pipe advertisement for the own peer, which the peer uses to revieve incoming messages
    * from other peers.
	* 
	* @return the pipe advertisement
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected PipeAdvertisement getPipeAdvertisement(PeerGroupID groupID)  {
		return (PipeAdvertisement) this.pipeAdvertisement.get(groupID);
	}
	


	/**
	* Gets the global PeerGroup. The global PeerGroup is the group every peer belongs to.
	* 
	* @return the globa peergroup 
	*
	* @version P2P-OntoRama 1.0.0
	*/
	public PeerGroup getGlobalPG() {
		return this.globalP2PGroup;
	}
	
	/**
	* Sets the global peergroup. The global PeerGroup is the group every peer belongs to.
	* 
	* @param PeerGroup the peer group that should be the global peergroup
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/
	protected void setGlobalPG(PeerGroup obj)  {
		this.globalP2PGroup = obj;
	}	


    /**
    * Transfer a peer group ID as a String to the actual object representing the group 
    * 
    * @param groupIDasString the ID as a string
    * @return the group ID as an object
    *
    * @version P2P-OntoRama 1.0.0
    *
    */
    
	protected PeerGroupID getPeerGroupID(String groupIDasString) {
		PeerGroupID retVal = null;

		PeerGroupAdvertisement pgAdv = null;
		DiscoveryService discServ = this.getGlobalPG().getDiscoveryService();
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
    * 
    * @param groupIDasString the ID as a string
    * @return the object representing the group
    *
    * @version P2P-OntoRama 1.0.0
    *
    */
    protected PeerGroup getPeerGroup(String groupIDasString) {
		PeerGroup pg = null;
		try {
			pg = this.getGlobalPG().newGroup(this.getPeerGroupID(groupIDasString));
		} catch (PeerGroupException e) {
			System.out.println("Error");
			e.printStackTrace();			
		}
        
        return pg;
	}
	

	
}
