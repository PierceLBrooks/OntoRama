package ontorama.backends.p2p.p2pmodule;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import ontorama.backends.p2p.GroupItemReference;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.PeerItemReference;
import ontorama.backends.p2p.events.GroupIsLeftEvent;
import ontorama.backends.p2p.events.GroupJoinedEvent;
import ontorama.backends.p2p.gui.P2PMainPanel;
import ontorama.backends.p2p.gui.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocol;
import ontorama.backends.p2p.p2pprotocol.GroupException;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionFlush;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionNotAllowed;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class P2PSender {
    public final static int TAGPROPAGATEADD = 1;
    public final static int TAGPROPAGATEDELETE = 2;
    public final static int TAGPROPAGATEUPDATE = 3;
    public final static int TAGPROPAGATEINIT = 4;
    public final static int TAGPROPAGATEJOINGROUP = 5;
    public final static int TAGPROPAGATELEAVEGROUP = 6;

    private CommunicationProtocol commProt = null;
    private P2PBackend backend = null;
    private PeersPanel peersPanel = null;

    //Constructor
    public P2PSender(CommunicationProtocol commProt, P2PBackend backend) {
        this.commProt = commProt;
        this.backend = backend;
        this.peersPanel = ((P2PMainPanel) backend.getPanel()).getPeerPanel();
       }

    /**
    * Sends a Logout Command to every Peer in the network that response to
    * the PeerDiscovery.
    *
    * @exception
    *
    * @version P2P-OntoRama 1.0.0
    */
    public void sendLogoutCommand() throws GroupExceptionThread, GroupExceptionFlush{
        //@todo this method is never called when the application is closed down
            this.commProt.sendLogoutCommand();
     }

    /**
    * Propagate information to every peer that
    * response to the PeerDiscovery.
    *
    * @param TAG the kind of propagate to send
    * @param recieverID the peer id of the reciever (null if the message shall be sent to every peer)
    * @param message the information that should be sent
    * @exception
    */
    public void sendPropagate(int TAG, String recieverID, String message) throws GroupExceptionThread{
        this.commProt.sendPropagate(TAG, recieverID, message);
    }


    /**
    * Is called to do searches for at other peers. The methods sends out a search request and
    * then waits for 20 seconds to get responses
    *
    * @param query a string containing the query
    * @return a vector of searchResultElement
    * @exception
    */
    public List sendSearch(String query) throws IOException, GroupExceptionThread {
            return this.commProt.sendSearch(query);
    }


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
    public List sendSearchGroup(String searchAttrib, String searchString) throws IOException, GroupExceptionThread{
          return this.commProt.sendSearchGroup(searchAttrib, searchString);
    }


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
    public void sendCreateGroup(String name, String descr) throws GroupException{
        PeerGroup pg = this.commProt.sendCreateGroup(name, descr);
        GroupItemReference groupRefElement = new GroupItemReference(
        											pg.getPeerGroupID(), pg.getPeerGroupName(), 
        											pg.getPeerGroupAdvertisement().getDescription());
        this.backend.getEventBroker().processEvent(new GroupJoinedEvent(groupRefElement));
    }



    /**
     * Join the group that has given groupID.
     *
     * @param groupID group to join
     * @return true if joined sucessfully
     * @exception
     *
     * @version P2P-OntoRama 1.0.0
     */
    public boolean sendJoinGroup(PeerGroupID groupID) throws GroupExceptionNotAllowed, GroupException {
    	String groupName = this.commProt.sendJoinGroup(groupID.toString());
        if (groupName != null){
        	this.backend.getEventBroker().processEvent(new GroupJoinedEvent(new GroupItemReference(groupID, groupName, "")));
            this.sendPropagate(TAGPROPAGATEJOINGROUP, null, groupID.toString());
            return true;
        }
    return false;

    }



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
    public boolean sendLeaveGroup(PeerGroupID groupID) throws GroupException, IOException {
        boolean leaved = this.commProt.sendLeaveGroup(groupID.toString());
        if (leaved){
            /// @todo pretty dodgy passing null's to the constructor.
            this.backend.getEventBroker().processEvent(new GroupIsLeftEvent(new GroupItemReference(groupID, null, null)));
        	this.sendPropagate(TAGPROPAGATELEAVEGROUP, null, groupID.toString());
            return true;
        }
        return false;
    }


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
    public void sendSearchResponse(String recieverPeerID, String body) throws GroupExceptionThread{
          this.commProt.sendSearchResponse(recieverPeerID, body);

    }
    
    public void peerDiscoveryForGlobalGroup ()  {
    	try {
    		Iterator it = this.commProt.sendSearchAllPeers().iterator();
			
			System.out.println("\n\nPeer Discovery returned for global net group ");
			while (it.hasNext()){
				PeerItemReference element = (PeerItemReference) it.next();
				System.out.println("+++ name = " + element.getName() + ", id = " + element.getID());
				this.peersPanel.addPeerInGlobalList(element);
			}
		}
		catch (GroupExceptionThread e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
    * Is called to do searches for at other peers. The methods sends out a search request and
    * then waits for 20 seconds to get responses
    */
    public void peerDiscovery (){
    	Iterator it = this.joinedGroups().iterator();
    	while (it.hasNext()) {
        	PeerGroup curGroup = (PeerGroup) it.next();
            String groupName = curGroup.getPeerGroupName();

            List result = peerDiscovery(groupName);

			Iterator resIterator = result.iterator();
			System.out.println("\n\nPeer Discovery returned: size = " + result.size() + " for group " + groupName + ", id = " + curGroup.getPeerGroupID());
			while (resIterator.hasNext()){
			  PeerItemReference element = (PeerItemReference) resIterator.next();
			  System.out.println("--- name = " + element.getName() + ", id = " + element.getID());
			  this.peersPanel.addPeer(element, curGroup.getPeerGroupID().toString());
			}
        }
    }
    
    /**
    * Is called to do searches for at other peers. The methods sends out a search request and
    * then waits for 20 seconds to get responses
    *
    * @param groupName a string with the peer group id for the group to send a peer discovery in
    */
    private List peerDiscovery (String groupName){
        try {
              List searchGroupResult = this.commProt.sendSearchGroup("Name",groupName);
              Iterator it = searchGroupResult.iterator();
              if (!it.hasNext()) {
				System.out.println("Couldn't find any group with name " + groupName);
              }
              else {
                  GroupItemReference searchGroupResultElement = (GroupItemReference) it.next();
                  String tmpGroupID = searchGroupResultElement.getID().toString();

                  this.backend.getEventBroker().processEvent(new GroupJoinedEvent(searchGroupResultElement));

                  return this.commProt.peerDiscovery(tmpGroupID);
               } 
          } catch (GroupExceptionThread e) {
                  System.out.println("ERROR:");
                  e.printStackTrace();
          } catch (IOException e) {
                  System.out.println("ERROR:");
                  e.printStackTrace();
          }
          return new Vector();

        }

	/**
    * Return all the groups the peer has joined
    *
    * @return a vector of IDs of group that have been joined
    *
    * @version P2P-OntoRama 1.0.0
    */
    public List joinedGroups(){
        return this.commProt.getMemberOfGroups();
 	}
 	
 	/**
 	 * a hack to get a vector of SearchGroupResultElements instead of
 	 * a vector of PeerGroups (needed in gui in order to be able 
 	 * to move between joined groups and search results without 
 	 * getting ClassCastException).
 	 */
 	public List getJoinedGroupsInSearchGroupResultFormat () {
 		Vector res = new Vector();
 		Iterator it = joinedGroups().iterator();
 		while (it.hasNext()) {
			PeerGroup pg = (PeerGroup) it.next();
			GroupItemReference element = new GroupItemReference(pg.getPeerGroupID(), 
											pg.getPeerGroupName(), 
											pg.getPeerGroupAdvertisement().getDescription());
			res.add(element);
			
		}
 		return res;
 	}





   private static void printSearchGroupResult(String tag, Collection obj) {
        GroupItemReference searchGroupResultElement = null;
        System.out.println("Found following matching " + tag + "s");
        Iterator it = obj.iterator();
            while (it.hasNext()) {
            searchGroupResultElement = (GroupItemReference)it.next();
            System.out.println(tag + ":" +
                searchGroupResultElement.getName()
                + " (ID:" + searchGroupResultElement.getID() + ")");
                System.out.println("Description:" +
                searchGroupResultElement.getDescription());
        }
    }


}