package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import ontorama.backends.p2p.P2PGlobals;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 * This class is used when sending messages to other peers. This class is
 * designed to run in it's own thread. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */

public class SendMessageThread extends Thread{
	boolean anyErrors = false;
   	private CommunicationProtocolJxta commProt;
   
	public SendMessageThread(CommunicationProtocolJxta commProt) {
		this.commProt = commProt;
	} 
    
   // The threads run method
    public void run() {
    }
    
    /** 
	* Sends a message to everyone in the groups where this peer belongs. 
	* Using Propagate pipes
	*
	* @param propType the type of propagation (if it is a propagataion)
	* @param ownPeerID this peers id
	* @param ownGroupID the id of the group
	* @param tag the tag for the message
	* @param message the message to send
	*/
    public void sendToAllPropagate(int propType,  String ownPeerID, 
    						  String ownGroupID, int tag, String message) {
        	
		PeerGroup globalGroup = this.commProt.getGlobalPG();
		sendMessageToPeerGroup(	globalGroup,propType, ownPeerID, ownGroupID,tag,message);
				        

	 	//while travese to send the mesage to all groups this peer is a member of
	 	Iterator it = this.commProt.getMemberOfGroups().iterator();
		while (it.hasNext()) {
			PeerGroup pg = (PeerGroup) it.next();
			sendMessageToPeerGroup(	pg,	propType, ownPeerID, ownGroupID,tag,message);
		}
    }
    
	private void sendMessageToPeerGroup(PeerGroup pg, int propType,
									String ownPeerID, String ownGroupID,
									int tag, String message) {
		//Prepare a message to be sent
		Message msgToSend = this.createMsg(pg, propType, ownPeerID,
		                                  ownGroupID, tag, message);
		
		OutputPipe outputPipe = this.commProt.getOutputPropagatePipe(pg.getPeerGroupID());
		System.out.println("SendMessageThread:: sending message  for group " + pg.getPeerGroupName() + " on outputPipe = " + outputPipe);
		
		try {
			outputPipe.send(msgToSend);
		} catch (IOException e) {
			//do nothing
			this.anyErrors = true;
			e.printStackTrace();
		}
	}
    
    

	//using Point-To-Point Pipes
    public void sendToAllPointToPoint(int propType,
    						  String ownPeerID, 
    						  String ownGroupID, 
    						  int tag, 
    						  String message) {
        
		InputpipeDiscoveryListener inputpipeDiscoveryListener = null;				 

	 	//while travese to send the mesage to all groups this peer is a member of
		Iterator it = this.commProt.getMemberOfGroups().iterator();
		while (it.hasNext()) {
			PeerGroup pg = (PeerGroup) it.next();
			DiscoveryService discoveryService = pg.getDiscoveryService();
			
			//Prepare a message to be sent
			Message msgToSend = this.createMsg(pg, propType, ownPeerID, 
												ownGroupID, tag, message);


	        /*
	 		inputpipeDiscoveryListener = new InputpipeDiscoveryListener(msgToSend,this.comm,pg);
	        //Add the listener that will be invoked when a response from the query will be resived
	        discoveryService.addDiscoveryListener(inputpipeDiscoveryListener);
	          */
			//Ask other peers for inputpipes with or without a species peerID  
			discoveryService.getRemoteAdvertisements(null,
						       					 DiscoveryService.ADV,
						       					 P2PGlobals.ADV_Name,
						       					 P2PGlobals.ADV_InputPipe,
						       					 100);
			 try{
				Thread.sleep(3*1000);
				Enumeration adverts = discoveryService.getLocalAdvertisements(
				 										DiscoveryService.ADV,
				                                        P2PGlobals.ADV_Name,
				                                        P2PGlobals.ADV_InputPipe);
				PipeService pipeService = pg.getPipeService();                   
				
				PipeAdvertisement tmpAdv = null;
				OutputPipe tmpOutputPipe = null;
				Message msgToSendCopy = null;
				while(adverts.hasMoreElements()){
					try {  
						tmpAdv = (PipeAdvertisement) adverts.nextElement();
						//Create a OutputPipe  
						tmpOutputPipe = pipeService.createOutputPipe(tmpAdv, 1*1000);
						//Send message through new pipe and close pipe
						msgToSendCopy = (Message) msgToSend.clone(); 
						tmpOutputPipe.send(msgToSendCopy);
						tmpOutputPipe.close();
					} catch (IOException e) {
						//Couldn't find a host to a given Adv.
					}             
				}  
	         }catch (InterruptedException e) {
				this.anyErrors = true;
				e.printStackTrace();
		     }catch (IOException e) {
	            //Couldn't find a host to a given Adv.
	            e.printStackTrace();
	         }finally {
				it = this.commProt.getMemberOfGroups().iterator();
				while (it.hasNext()) {
					pg = (PeerGroup) it.next();
			        discoveryService = pg.getDiscoveryService();	
					discoveryService.removeDiscoveryListener(inputpipeDiscoveryListener);
				}
			}
	        }
	}
    
   /** 
	* Sends a message to a peer in tone of the groups where this peer belongs
	*
	* @param propType the type of propagation (if it is a propagataion)
	* @param ownPeerID this peers id
	* @param tag the tag for the message
	* @param message the message to send
	*/
	public void sendToPeerInGlobal(int propType,
    						  String recieverPipeAdvID,
    						  String ownPeerID, 
    						  String ownGroupID, 
    						  int tag, 
    						  String message)  {
		
		DiscoveryService discoveryService = null;
		InputpipeDiscoveryListener inputpipeDiscoveryListener = null;
		PeerGroup pg = this.commProt.getGlobalPG();
        System.err.println("SendMessageThread::sendToPeer (one specific)" );

        Message msgToSend = this.createMsg(pg, propType, ownPeerID,
                                          ownGroupID, tag, message);

        discoveryService = this.commProt.getGlobalPG().getDiscoveryService();

 		//inputpipeDiscoveryListener = new InputpipeDiscoveryListener(msgToSend,this.comm,pg);
        //Add the listener that will be invoked when a response from the query will be resived
        
        //discoveryService.addDiscoveryListener(inputpipeDiscoveryListener);

		//Ask other peers for inputpipes with or without a species peerID  
		discoveryService.getRemoteAdvertisements(null,
			       					 DiscoveryService.ADV,
			       					 P2PGlobals.ADV_Name, P2PGlobals.ADV_InputPipe, 100);
                                             
		try{
			Thread.sleep(3*1000);
			Enumeration adverts = discoveryService.getLocalAdvertisements(
													DiscoveryService.ADV,
                                                    P2PGlobals.ADV_Id,
                                                    recieverPipeAdvID);
			PipeService pipeService = pg.getPipeService();                    
			while(adverts.hasMoreElements()){
				PipeAdvertisement tmpAdv = (PipeAdvertisement) adverts.nextElement(); 

				//Create a OutputPipe  
				OutputPipe tmpOutputPipe = pipeService.createOutputPipe(tmpAdv,5*1000);

				//Send message through new pipe and close pipe
				tmpOutputPipe.send(msgToSend);
				tmpOutputPipe.close();
			}  
		} catch (InterruptedException e) {
			System.err.println("Thread:: gonna do getREmoteAdvertisements FAILED" );
			this.anyErrors = true;
		} catch (IOException e) {
		      //Couldn't find a host to a given Adv.
		      e.printStackTrace();
		} finally {
			discoveryService = pg.getDiscoveryService();	
			discoveryService.removeDiscoveryListener(inputpipeDiscoveryListener);
		}
	}
						
	/** 
	 * This method creates a message with the given. 
	 * 
	 * @return The message to be sent.
	 * @param propType the type of propagation (if it is a propagataion)
	 * @param ownPeerID this peers id
	 * @param tag the tag for the message
	 * @param message the message to send
	 */
   	private Message createMsg(PeerGroup pg, int propType,
    						  String ownPeerID, String ownGroupID, 
    						  int tag, String message){
		MimeMediaType mimeType = new MimeMediaType("text/xml");					  	
		Message result = pg.getPipeService().createMessage();
		
        result.addElement( result.newMessageElement( 
        							P2PGlobals.STR_TAG, 
									mimeType, 
									Integer.toString(tag).getBytes()));
        
        if (tag == P2PGlobals.TAGPROPAGATE){
	        result.addElement( result.newMessageElement(
	        						P2PGlobals.STR_propType,
	        						mimeType, 
	            					Integer.toString(propType).getBytes()));
		}

		byte[] peerIdInBytes = this.commProt.getGlobalPG().getPeerID().toString().getBytes();
	    result.addElement( result.newMessageElement(
	    							P2PGlobals.STR_SenderPeerID,
	    							mimeType,
	    							peerIdInBytes));

		byte[] peerNameInBytes = this.commProt.getGlobalPG().getPeerName().toString().getBytes();
        result.addElement( result.newMessageElement(
        							P2PGlobals.STR_SenderPeerName,
        							mimeType,
        							peerNameInBytes));

        if (ownPeerID != null){
            result.addElement( result.newMessageElement(
            						P2PGlobals.STR_PeerID,
            						mimeType,
            						ownPeerID.getBytes()));
	    }
        if (ownGroupID == null) {
			byte[] peerGroupIdInBytes = this.commProt.getGlobalPG().getPeerGroupID().toString().getBytes();
            result.addElement( result.newMessageElement(
            						P2PGlobals.STR_GroupID,
            						mimeType,
            						peerGroupIdInBytes));
	     } else {
	        result.addElement( result.newMessageElement(
	        						P2PGlobals.STR_GroupID,
	        						mimeType,
	        						ownGroupID.getBytes()));
	     }
	     result.addElement( result.newMessageElement(
	     							P2PGlobals.STR_Body,
	     							mimeType,
	     							message.getBytes()));

        return result;
   	}




	
   	/** 
	* Returns true if there was any errors while running the thread else false
	*  
	* @return true if there was any errors, otherwise false
	*/
	public boolean anyErrors() {
		return this.anyErrors;
	}
}

