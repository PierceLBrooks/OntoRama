package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Hashtable;

import ontorama.backends.p2p.p2pmodule.P2PRecieverInterface;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 * This class handles the P2P communication which is related to Initialization 
 * functionality.
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class CommunicationInit {

	// The p2p platform
	private PeerGroup globalP2PPlatform;
	
	// The top p2p group
	private PeerGroup globalP2PGroup;
	
	//Object to use for receiving messages
	P2PRecieverInterface reciever;	
	
	
	private CommunicationProtocolJxta commProt;

    private Hashtable inputPipes = null;
    
    /** The constructor
    * 
    * @param obj the object that is going to use this object.
    *
    * @version P2P-On
    * toRama 1.0.0
    *
    */     
	public CommunicationInit(CommunicationProtocolJxta commProt, P2PRecieverInterface reciever) {
		this.commProt = commProt;
		this.reciever = reciever;
        this.inputPipes = new Hashtable();
	}
	
	/** This method intiatiates all the communication by setting up the global p2p group, 
     * which every peer have to belong too.
     * 
     * @exception GroupExceptionInit
     * @version P2P-OntoRama 1.0.0
     */ 
	protected void initJxtaTopGroup() throws GroupExceptionInit {
		try {
			//create and start the default JXTA Platform and NetPeerGroup
		    PeerGroupFactory.setPlatformClass(Class.forName("net.jxta.impl.peergroup.Platform"));
			this.globalP2PPlatform = PeerGroupFactory.newPlatform();

			this.globalP2PGroup = PeerGroupFactory.newNetPeerGroup(this.globalP2PPlatform);
			System.out.println("pg: name =  " + this.globalP2PGroup.getPeerGroupName() + ", id = " + this.globalP2PGroup.getPeerGroupID());
			
			startInputPipeEndpoint(this.globalP2PGroup);
			startPropagatePipeEndpoint(this.globalP2PGroup);
			
		} catch (PeerGroupException e) {
			throw new GroupExceptionInit(e,"The platform could not be instansiated");
		} catch (ClassNotFoundException e) {
			throw new GroupExceptionInit(e,"The platform could not be instansiated");
		}       
	}

	/**
	* This method creates a propagate pipe advertisement
	* 
	* @param pg the peer group where the pipe will be started
	* @exception  GroupExceptionInit
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/

	protected PipeAdvertisement startPropagatePipeEndpoint(PeerGroup pg) throws GroupExceptionInit {
        PipeService pipeService = pg.getPipeService();
		DiscoveryService discServ = pg.getDiscoveryService();

		//Create a advertisement to represent the PROPAGATE pipe
		PipeAdvertisement pipeAdvert = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
									PipeAdvertisement.getAdvertisementType());
		pipeAdvert.setName("PropagatePipe");
		//Changed
		pipeAdvert.setType(PipeService.PropagateType);
		
		//Set the an ID on the pipe advertisement
		String seedStr = "12345";
		byte[] seed = seedStr.getBytes();
		pipeAdvert.setPipeID(IDFactory.newPipeID(pg.getPeerGroupID(),seed)); 				

		try {
			//Create a listerner to listen for incomming messages to a pipe
			InputPipeListener pipeMessageListener = new InputPipeListener(this.commProt,
														this.reciever);

			//Create a new inputPipe from adverisement and pipeMessageListerner
			InputPipe inputPipe = pipeService.createInputPipe(pipeAdvert,pipeMessageListener);
			this.inputPipes.put(pg.getPeerGroupID(),inputPipe);

            //Create an output pipe and save it in the hashtable
            OutputPipe outputPipe = pipeService.createOutputPipe(pipeAdvert,1000);

			//Publish the pipes advertisement both localy and global
			discServ.publish(pipeAdvert,DiscoveryService.ADV);
			discServ.remotePublish(pipeAdvert,DiscoveryService.ADV);
					
			//SEts the PipeAdvertisement
			this.commProt.addOutputPropagatePipe(pg.getPeerGroupID(),outputPipe);
			
		} catch (IOException e) {
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
				
		return pipeAdvert;
	}

	/**
	* This method creates a input pipe advertisement and publish it
	* 
	* @param pg the peer group where the pipe will be started
	* @exception  GroupExceptionInit
	*
	* @version P2P-OntoRama 1.0.0
	*/  
	protected PipeAdvertisement startInputPipeEndpoint(PeerGroup pg) throws GroupExceptionInit {
		PipeService pipeService = pg.getPipeService();
		DiscoveryService discServ = pg.getDiscoveryService();
		InputPipeListener pipeMessageListener = null;
		PipeAdvertisement pipeAdvert = null;
		
		//Create a advertisement to represent the input pipe
		pipeAdvert = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
										PipeAdvertisement.getAdvertisementType());
		pipeAdvert.setName("InputPipe");
		
		//Set the an ID on the pipe advertisement
		pipeAdvert.setPipeID(IDFactory.newPipeID(pg.getPeerGroupID())); 	

		try {
			//Create a listerner to listen for incomming messages to a pipe
			pipeMessageListener = new InputPipeListener(this.commProt,
														this.reciever);
	
			//Create a new inputPipe from adverisement and pipeMessageListerner
			pipeService.createInputPipe(pipeAdvert,pipeMessageListener);	
	
			//Publish the pipes advertisement both localy and global
			discServ.publish(pipeAdvert,
							DiscoveryService.ADV,
							DiscoveryService.DEFAULT_LIFETIME,
							30*1000);
							
			discServ.remotePublish(pipeAdvert,
									DiscoveryService.ADV,
									30*1000);
									
			this.commProt.addInputPipeAdvertisement(pg.getPeerGroupID(),pipeAdvert);
			
		} catch (IOException e) {
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
		return pipeAdvert;

	}	
	
	public PeerGroup getGlobalPG () {
		return this.globalP2PGroup;
	}
}
