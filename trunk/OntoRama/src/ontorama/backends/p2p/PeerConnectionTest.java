package ontorama.backends.p2p;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredTextDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;


/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PeerConnectionTest implements DiscoveryListener {
	
	private PeerGroup netPeerGroup;
	private DiscoveryService discovery;
	PeerAdvertisement peerAdv;
	
	public void startJxta() {
		try {
			
		    PeerGroupFactory.setPlatformClass(Class.forName("net.jxta.impl.peergroup.Platform"));
//			PeerGroupFactory.setStdPeerGroupClass(Class.forName("ontorama.backends.p2p.OntoRamaPeerGroup"));

			PeerGroup worldPeerGroup = PeerGroupFactory.newPlatform();
			
			System.out.println("world group name: " + worldPeerGroup.getPeerGroupName());
			System.out.println("world group id: " + worldPeerGroup.getPeerGroupID());
			
			netPeerGroup = PeerGroupFactory.newNetPeerGroup(worldPeerGroup);
			//netPeerGroup = PeerGroupFactory.newPeerGroup();
			
			//netPeerGroup = PeerGroupFactory.newNetPeerGroup();
			System.out.println("peer group name: " + netPeerGroup.getPeerGroupName());
			System.out.println("peer group id: " + netPeerGroup.getPeerGroupID());
			System.out.println("peer name: " + netPeerGroup.getPeerName());
			System.out.println("peer id: " + netPeerGroup.getPeerID());
		}
		catch (PeerGroupException e)  {
			e.printStackTrace();
			System.exit(1);
		}
		catch (ClassNotFoundException e ) {
			e.printStackTrace();
			System.exit(1);
		}

		discovery = netPeerGroup.getDiscoveryService();
	}

	/**
	 * This thread loops forever discovering peer
	 * every minute, and displaying the results.
	 */
	public void run() {
		 try {
		 // Add ourselves as a DiscoveryListener for DiscoveryResponse events
		 discovery.addDiscoveryListener(this);
		
		 while (true) {
			 System.out.println("Sending a Discovery Message");
			 // look for any peer
			 discovery.getRemoteAdvertisements(null, DiscoveryService.PEER,
			 									null, null, 5, null);
			
			 // wait a bit before sending next discovery message
			 try {
				 Thread.sleep(10 * 1000);
			 }
			 catch(Exception e) {
			 }
			
		 } //end while
		 }
		 catch(Exception e) {
		 	e.printStackTrace();
		 }
	 }

	/**
	 * by implementing DiscoveryListener we must define this method
	 * to deal to discovery responses
	 */
	public void discoveryEvent(DiscoveryEvent ev) {

		DiscoveryResponseMsg res = ev.getResponse();
		
		// Get the responding peer's advertisement
		String aRes = res.getPeerAdv();
		
		try {
			// create a peer advertisement
			InputStream is = new ByteArrayInputStream( (aRes).getBytes() );
			peerAdv = (PeerAdvertisement)
			AdvertisementFactory.newAdvertisement(
			new MimeMediaType( "text/xml" ), is);
			
			System.out.println (" [ Got a Discovery Response ["+
			res.getResponseCount()+ " elements] from peer : " +
			peerAdv.getName() +" ]");
			
		} catch (java.io.IOException e) {
			// bogus peer, skip this message alltogether.
			System.out.println("error parsing remote peer's advertisement");
			e.printStackTrace();
			return;
		}

		// now print out each discovered peer
		Enumeration enum = res.getResponses();
		
		String str=null;
		PeerAdvertisement newAdv=null;
		
		while (enum.hasMoreElements()) {
			try {
				str = (String) enum.nextElement();
				// create an advertisement object from each element
				newAdv =(PeerAdvertisement)
				AdvertisementFactory.newAdvertisement
				(new MimeMediaType ("text/xml"),
				new ByteArrayInputStream(str.getBytes()));
				
				System.out.println(" Peer name = " + newAdv.getName() + ", and group id = " + newAdv.getPeerGroupID());
			}
			catch (java.io.IOException e) {
				// got a bad response. continue to the next response
				System.out.println("error parsing response element");
				e.printStackTrace();
				continue;
			}
		} // end while
	}
	
	
	private PeerGroup createGroup() {
		PeerGroupAdvertisement adv;
		PeerGroup pg;
		
		System.out.println("Creating a new group advertisement");
		
		try {
			// create a new all purpose peergroup.
			ModuleImplAdvertisement implAdv =
					netPeerGroup.getAllPurposePeerGroupImplAdvertisement();
			pg = netPeerGroup.newGroup(null, // Assign new group ID
						implAdv, // The implem. adv
						"PubTest", // The name
						"testing group adv"); // Helpful descr.
			// print the name of the group and the peer group ID
			adv = pg.getPeerGroupAdvertisement();
			PeerGroupID GID = adv.getPeerGroupID();
			System.out.println(" Group = " +adv.getName() +
							"\n Group ID = " + GID.toString());
		}
		catch (Exception eee) {
			System.out.println("Group creation failed with " + eee.toString());
			return null;
		}
		
		try {
			// publish this advertisement
			//(send out to other peers/rendezvous peers)
			discovery.remotePublish(adv, DiscoveryService.GROUP);
			System.out.println("Group published successfully.");
			return pg;
		}
		catch (Exception e) {
			System.out.println("Error publishing group advertisement");
			e.printStackTrace();
			return null;
		}
	}	
	
	
	private void joinGroup(PeerGroup grp) {
		System.out.println("Joining peer group...");
		
		StructuredDocument creds = null;
		try {
			// Generate the credentials for the Peer Group
			AuthenticationCredential authCred =
					new AuthenticationCredential( grp, null, creds );
					
			// Get the MembershipService from the peer group
			MembershipService membership = grp.getMembershipService();
			
			// Get the Authenticator from the Authentication creds
			Authenticator auth = membership.apply( authCred );
			
			// Check if everything is okay to join the group
			if (auth.isReadyForJoin()){
				Credential myCred = membership.join(auth);
				System.out.println("Successfully joined group " + grp.getPeerGroupName());
				
				// display the credential as a plain text document.
				System.out.println("\nCredential: ");
				StructuredTextDocument doc = (StructuredTextDocument) myCred.getDocument(new MimeMediaType("text/plain"));
				StringWriter out = new StringWriter();
				doc.sendToWriter(out);
				System.out.println(out.toString());
				out.close();
			}
			else {
				System.out.println("Failure: unable to join group");
			}
		}
		catch (Exception e){
			System.out.println("Failure in authentication.");
			e.printStackTrace();
		}
	}	
	
	public static void main (String[] args) {
		PeerConnectionTest connection = new PeerConnectionTest();
		connection.startJxta();
		PeerGroup newGroup = connection.createGroup();
		if (newGroup != null) {
			connection.joinGroup(newGroup);
		}
		//connection.run();
		System.exit(0);
	}

}
