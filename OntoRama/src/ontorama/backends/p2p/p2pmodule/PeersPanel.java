package ontorama.backends.p2p.p2pmodule;

import java.util.Enumeration;
import java.util.Hashtable;

import ontorama.backends.p2p.P2PBackend;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

//public class PeersPanel extandes JPanel{
//TODO this class should probobly use PeerDiscovery if it is working

public class PeersPanel {
       Hashtable groups = null;
       Hashtable peers = null; 
       P2PBackend backend = null;
    
    public PeersPanel(P2PBackend backend){
        this.backend = backend;
        this.groups = new Hashtable();
        this.peers = new Hashtable();
    }
    
    
    public void addPeer(String peerID,String peerName, String groupID){
    //null means groupID means the global group
      peers.put(peerID, peerName);
     }
    
    public void addGroup(String groupID, String groupName){
        groups.put(groupID, groupName);
     }
    
    public void removePeer(String peerId){
      peers.remove(peerId);    
    }
    
    public void removeGroup(String groupId){
     groups.remove(groupId);    
    }
    
    public void empty(){
          groups.clear();
          peers.clear();    
    }
    
    public void update(){
		Hashtable groupsCopy = (Hashtable) this.groups.clone();
        Enumeration enum = groupsCopy.elements();		
		this.empty();
        String groupIDasString = null;
        while (enum.hasMoreElements()){
        	groupIDasString = (String)enum.nextElement();
            this.backend.peerDiscovery(groupIDasString);   
        }              
    }
    
    
    //This method should me removed when we using JPanel
    public void show(){
		System.out.println("Peers Panel");
        System.out.println("------------");

        System.out.println("The active groups are: ");
        Enumeration enum1 = groups.elements();
        while (enum1.hasMoreElements()){
            System.out.println(enum1.nextElement());   
        }
        System.out.println("The active peers are: ");
        Enumeration enum2 = peers.elements();
        while (enum2.hasMoreElements()){
            System.out.println(enum2.nextElement());   
        }  
        
    }
}
