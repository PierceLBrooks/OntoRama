package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;

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
public class CommunicationInit extends Communication {
	private CommunicationProtocolJxta commProt = null;
	
    /** The constructor
    * 
    * @param obj the object that is going to use this object.
    *
    * @version P2P-On
    * toRama 1.0.0
    *
    */ 
    
	public CommunicationInit(CommunicationProtocolJxta obj) {
		commProt = obj;
	}
	/** This method intiatiates all the communication by setting up the global p2p group, 
     * which every peer have to belong too.
    * 
    * @exception 
    *
    * @version P2P-OntoRama 1.0.0
    *
    */ 
	protected void initJxtaTopGroup() throws GroupExceptionInit {
		try {
			//create and start the default JXTA Platform and NetPeerGroup
		    PeerGroupFactory.setPlatformClass(Class.forName("net.jxta.impl.peergroup.Platform"));
			this.setGlobalPlatform(PeerGroupFactory.newPlatform());
			this.setGlobalPG(PeerGroupFactory.newNetPeerGroup(this.getGlobalPlatform()));
			}
		catch (PeerGroupException e) {
			throw new GroupExceptionInit(e,"The platform could not be instansiated");
		} catch (ClassNotFoundException e) {
			throw new GroupExceptionInit(e,"The platform could not be instansiated");
        }
	}




	/**
	* This method creates a propagate pipe advertisement
	* 
	* @param pg the peer group where the pipe will be started
	* @exception 
	*
	* @version P2P-OntoRama 1.0.0
	*
	*/  
	protected PipeAdvertisement startPropagatePipeEndpoint(PeerGroup pg) throws GroupExceptionInit {
		PipeService pipeService = pg.getPipeService();
		PipeAdvertisement pipeAdvert = null;
		InputPipeListener pipeMessageListener = null;
				
		//Create a advertisement to represent the PROPAGATE pipe
		pipeAdvert = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(
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
			pipeMessageListener = new InputPipeListener(this.commProt,
														this,
														commProt.getRecieverObject());

			//Create a new inputPipe from adverisement and pipeMessageListerner
			pipeService.createInputPipe(pipeAdvert,pipeMessageListener);	
		
			//Publish the pipes advertisement both localy and global
			//discServ.publish(pipeAdvert,DiscoveryService.ADV); 
			//discServ.remotePublish(pipeAdvert,DiscoveryService.ADV); 	
					
			//SEts the PipeAdvertisement
			this.setPipeAdvertisement(pg.getPeerGroupID(),pipeAdvert);
			
		} catch (IOException e) {
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
				
		return pipeAdvert;
	}

	/**
	* This method creates a input pipe advertisement and publish it
	* 
	* @param pg the peer group where the pipe will be started
	* @exception 
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
														this,
														commProt.getRecieverObject());
	
			//Create a new inputPipe from adverisement and pipeMessageListerner
			pipeService.createInputPipe(pipeAdvert,pipeMessageListener);	
	
			//Publish the pipes advertisement both localy and global
			discServ.publish(pipeAdvert,
							DiscoveryService.ADV,
							discServ.DEFAULT_LIFETIME,
							30*1000);
							
			discServ.remotePublish(pipeAdvert,
									DiscoveryService.ADV,
									30*1000);
									
			this.setPipeAdvertisement(pg.getPeerGroupID(),pipeAdvert);			
			
		} catch (IOException e) {
			throw (GroupExceptionInit) e.fillInStackTrace();
		}
		return pipeAdvert;

	}	                                                                                                                                                           
}
