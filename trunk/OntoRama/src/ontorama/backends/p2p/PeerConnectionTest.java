package ontorama.backends.p2p;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;


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
			PeerGroupFactory.setStdPeerGroupClass(Class.forName("ontorama.backends.p2p.OntoRamaPeerGroup"));

			PeerGroup worldPeerGroup = PeerGroupFactory.newPlatform();
			
			System.out.println("world group name: " + worldPeerGroup.getPeerGroupName());
			System.out.println("world group id: " + worldPeerGroup.getPeerGroupID());
			
			//netPeerGroup = PeerGroupFactory.newNetPeerGroup(worldPeerGroup);
			netPeerGroup = PeerGroupFactory.newPeerGroup();
			
			//netPeerGroup = PeerGroupFactory.newNetPeerGroup();
			System.out.println("peer group name: " + netPeerGroup.getPeerGroupName());
			System.out.println("peer group id: " + netPeerGroup.getPeerGroupID());
			System.out.println("peer name: " + netPeerGroup.getPeerName());
			System.out.println("peer id: " + netPeerGroup.getPeerID());
		}
//		catch (PeerGroupException e)  {
//			e.printStackTrace();
//			System.exit(1);
//		}
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
				
				System.out.println(" Peer name = " + newAdv.getName());
			}
			catch (java.io.IOException e) {
				// got a bad response. continue to the next response
				System.out.println("error parsing response element");
				e.printStackTrace();
				continue;
			}
		} // end while
	}
	
	public static void main (String[] args) {
		PeerConnectionTest connection = new PeerConnectionTest();
		connection.startJxta();
		connection.run();
		System.exit(0);
	}

}
