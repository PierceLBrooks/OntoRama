package ontorama.backends.p2p.p2pmodule;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.jxta.peergroup.PeerGroup;
import ontorama.backends.Backend;
import ontorama.backends.p2p.P2PBackend;
import ontorama.backends.p2p.gui.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.CommunicationProtocol;
import ontorama.backends.p2p.p2pprotocol.GroupException;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionFlush;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionNotAllowed;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;
/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class P2PSender{
    public final static int TAGPROPAGATEADD = 1;
    public final static int TAGPROPAGATEDELETE = 2;
    public final static int TAGPROPAGATEUPDATE = 3;
    public final static int TAGPROPAGATEINIT = 4;
    public final static int TAGPROPAGATEJOINGROUP = 5;
    public final static int TAGPROPAGATELEAVEGROUP = 6;

    private CommunicationProtocol comm = null;
    private Backend backend = null;
    private PeersPanel peersPanel = null;


    //Constructor
    public P2PSender(CommunicationProtocol commProt, P2PBackend backend){
        this.comm = commProt;
        this.backend = backend;
        this.peersPanel = (PeersPanel) backend.getPanels().get(0);
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
            this.comm.sendLogoutCommand();
     }

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
    //TODO we are not sending internalModel therefor we should change the name, we
    //are not using INIT either, since no one can receive your message if you
    //are not in a group.
    public void sendPropagate(int TAG, String recieverID, String internalModel) throws GroupExceptionThread{
        this.comm.sendPropagate(TAG, recieverID, internalModel);
    }


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
    public Vector sendSearch(String query) throws IOException, GroupExceptionThread {
            return this.comm.sendSearch(query);
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
    public Vector sendSearchGroup(String searchAttrib, String searchString) throws IOException, GroupExceptionThread{
          return this.comm.sendSearchGroup(searchAttrib, searchString);
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
        PeerGroup pg = this.comm.sendCreateGroup(name, descr);
        this.peersPanel.addGroup(pg.getPeerGroupID().toString(), name);
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
    public boolean sendJoinGroup(String groupID) throws GroupExceptionNotAllowed, GroupException {
    	String groupName = this.comm.sendJoinGroup(groupID);
        if (groupName != null){
            this.peersPanel.addGroup(groupID, groupName);
            this.sendPropagate(TAGPROPAGATEJOINGROUP, null, groupID);
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
    public boolean sendLeaveGroup(String groupID) throws GroupException, IOException {
        boolean leaved = this.comm.sendLeaveGroup(groupID);
        if (leaved){
            peersPanel.removeGroup(groupID);
            this.sendPropagate(TAGPROPAGATELEAVEGROUP, null, groupID);
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
          this.comm.sendSearchResponse(recieverPeerID, body);

    }

    /**
    * Is called to do searches for at other peers. The methods sends out a search request and
    * then waits for 20 seconds to get responses
    */
    public void peerDiscovery (){
        Enumeration enum = this.joinedGroups().elements();
        this.peersPanel.clear();
        String groupName = null;
        while (enum.hasMoreElements()){
            groupName = ((PeerGroup) enum.nextElement()).getPeerGroupName();
            this.peerDiscovery(groupName);
        }
    }

    /**
    * Is called to do searches for at other peers. The methods sends out a search request and
    * then waits for 20 seconds to get responses
    *
    * @param groupName a string with the peer group id for the group to send a peer discovery in
    */
    public void peerDiscovery (String groupName){
        try {
              Vector searchGroupResult = this.comm.sendSearchGroup("Name",groupName);
              Enumeration tmpEnumernation = searchGroupResult.elements();
              if (tmpEnumernation.hasMoreElements()) {
                  SearchGroupResultElement searchGroupResultElement = (SearchGroupResultElement)tmpEnumernation.nextElement();
                  String tmpGroupID = searchGroupResultElement.getID().toString();
                  this.peersPanel.addGroup(tmpGroupID, groupName);

                  Vector result = this.comm.peerDiscovery(tmpGroupID);
                  Enumeration enum = result.elements();
                  while (enum.hasMoreElements()){
                          SearchGroupResultElement element = (SearchGroupResultElement)enum.nextElement();
                          this.peersPanel.addPeer(element.getID().toString(), element.getName(), tmpGroupID);
                  }
               } else {
                  System.out.println("Couldn't find any group with that name");
                        }
          } catch (GroupExceptionThread e) {
                  System.out.println("ERROR:");
                  e.printStackTrace();
          } catch (IOException e) {
                  System.out.println("ERROR:");
                  e.printStackTrace();
          }

        }

/**
    * Return all the groups the peer has joined
    *
    * @return a vector of IDs of group that have been joined
    *
    * @version P2P-OntoRama 1.0.0
    */
    public Vector joinedGroups(){
        return this.comm.getMemberOfGroups();
 }












   private static void printSearchGroupResult(String tag, Vector obj) {
        SearchGroupResultElement searchGroupResultElement = null;
        System.out.println("Found following matching " + tag + "s");
        Enumeration tmpEnumernation = obj.elements();
            while (tmpEnumernation.hasMoreElements()) {
            searchGroupResultElement = (SearchGroupResultElement)tmpEnumernation.nextElement();
            System.out.println(tag + ":" +
                searchGroupResultElement.getName()
                + " (ID:" + searchGroupResultElement.getID() + ")");
                System.out.println("Description:" +
                searchGroupResultElement.getDescription());
        }
    }


}