package ontorama.backends.p2p;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;


import ontorama.backends.Menu;
import ontorama.backends.p2p.p2pmodule.ChangePanel;
import ontorama.backends.p2p.p2pmodule.P2PSender;
import ontorama.backends.p2p.p2pmodule.PeersPanel;
import ontorama.backends.p2p.p2pprotocol.GroupException;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionNotAllowed;
import ontorama.backends.p2p.p2pprotocol.GroupExceptionThread;
import ontorama.backends.p2p.p2pprotocol.SearchGroupResultElement;
import ontorama.backends.p2p.p2pprotocol.Communication;


import net.jxta.peergroup.PeerGroupID;

/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
//TODO public class Menu extends JMenu{
public class P2PMenu extends Menu{
   
    private P2PSender sender = null;
    private P2PBackend backend = null;
    private PeersPanel peerspanel = null;
	private ChangePanel changePanel = null;       
        
        public P2PMenu(P2PSender sender, P2PBackend backend){
            this.sender = sender;
            this.backend = backend;
            this.peerspanel = (PeersPanel) this.backend.getPanels().get(0);
            this.changePanel = (ChangePanel) this.backend.getPanels().get(1);
        }
        
        public void showMenu(){
            System.out.println("-----------------");
            System.out.println("P2P MENU");
            System.out.println("a => Search for group by name"); 
            System.out.println("b => Search for group by description"); 
            System.out.println("");
            System.out.println("c => Create group");
            System.out.println("d => Join group");
            System.out.println("e => Leave group ");
            System.out.println("");     
            System.out.println("f => Update Peer Panel"); 
            System.out.println("g => Reset Change Panel"); 
            System.out.println("");
            System.out.println("h => Close Menu"); 
            
            System.out.print(">");
            
            char charIn = readChar(); 
            String lineIn = null;           
            String lineIn2 = null;    
            Vector searchGroupResult = null;
             
            switch(charIn) {
                 case 'a' :
                    System.out.println("Search for groups by Name");
                        System.out.println("(Hint: you can use ? and *, press Enter for all groups)");
                        System.out.print("Name:");
                        lineIn = readLine();
                        if (lineIn.length() == 0) {
                            try {
                                searchGroupResult = sender.sendSearchGroup(null,null);
                            } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                searchGroupResult = sender.sendSearchGroup(Communication.SEARCHGROUPNAME, lineIn);
                            } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                        }
                        printSearchGroupResult("PeerGroup",searchGroupResult);
                        break;
                case 'b' :
                        System.out.println("Search for groups by description");
                        System.out.println("(Hint: you can use ? and *, press Enter for all groups)");
                        System.out.print("Description:");
                        lineIn = readLine();
                        if (lineIn.length() == 0) {
                            try {
                                searchGroupResult = sender.sendSearchGroup(null,null);
                            } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                searchGroupResult = sender.sendSearchGroup(Communication.SEARCHGROUPDESCR,lineIn);
                            } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                        }

                        printSearchGroupResult("PeerGroup",searchGroupResult);
                        break;

                case 'c' :
                        System.out.println("Create group");
                        System.out.print("Name of the group to create:");
                        lineIn = readLine();
                        System.out.print("Description of the group to create:");
                        lineIn2 = readLine();
                        if (lineIn.length() == 0) {
                            System.out.println("You forgot to write a name.");
                        } else if (lineIn2.length() == 0) {
                            System.out.println("You forgot to write a description");
                        } else {
                            try {
                                sender.sendCreateGroup(lineIn,lineIn2);
                            } catch (GroupException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                        }
                    break;
                case 'd' :
                        Enumeration tmpEnumernation = null;
                        SearchGroupResultElement searchGroupResultElement = null;
                        System.out.println("Join group");
                        System.out.print("Name of the group to join:");
                        lineIn = readLine();
                        if (lineIn.length() == 0) {
                            System.out.println("You forgot to write something...");
                        } else {
                            try {
                                searchGroupResult = sender.sendSearchGroup("Name",lineIn);
                            } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                            //Changed
                            tmpEnumernation = searchGroupResult.elements();
                            if (tmpEnumernation.hasMoreElements()) {
                                searchGroupResultElement = (SearchGroupResultElement)tmpEnumernation.nextElement();
                                try {
                                    this.sender.sendJoinGroup(
                                        ((PeerGroupID) searchGroupResultElement.getID()).toString());
                                } catch (GroupExceptionNotAllowed e) {
                                    System.out.println("ERROR:");
                                    e.printStackTrace();
                                } catch (GroupException e) {
                                    System.out.println("ERROR:");
                                    e.printStackTrace();
                                }
                                System.out.println("Joined: "
                                    + searchGroupResultElement.getName()
                                    + " (ID:" 
                                    + searchGroupResultElement.getID()
                                    + ")");
                            } else {
                                System.out.println("Couldn't find any group with that name");   
                            }
                        }                   
                
                    break;
                case 'e' :
                    System.out.println("Leave group");
                    System.out.print("Name of the group to leave:");
                    lineIn = readLine();
                    if (lineIn.length() == 0) {
                        System.out.println("You forgot to write something...");
                    } else {
                        try {
                            searchGroupResult = sender.sendSearchGroup("Name",lineIn);
                        } catch (GroupExceptionThread e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                        } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                        }
                        tmpEnumernation = searchGroupResult.elements();
                        if (tmpEnumernation.hasMoreElements()) {
                            searchGroupResultElement = (SearchGroupResultElement)tmpEnumernation.nextElement();
                            try {
                                sender.sendLeaveGroup(
                                    ((PeerGroupID) searchGroupResultElement.getID()).toString());
                            } catch (IOException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            } catch (GroupException e) {
                                System.out.println("ERROR:");
                                e.printStackTrace();
                            }
                            System.out.println("Leaved: "
                                + searchGroupResultElement.getName()
                                + " (ID:" + searchGroupResultElement.getID()
                                + ")");
                        } else {
                            System.out.println("Couldn't find any group with that name");   
                        }
                    }                
                    break;
				case 'f' :
					peerspanel.update();
                     break;          
				case 'g' :
					changePanel.empty();
                     break;          

                 case 'h' :
                    break; 
            }
     
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