package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Enumeration;

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
   	private CommunicationSender comm = null;
   
	public SendMessageThread(CommunicationSender comm) {
		this.comm = comm;	
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
        	
		PeerGroup globalGroup = Communication.getGlobalPG();
		sendMessageToPeerGroup(	globalGroup,propType, ownPeerID, ownGroupID,tag,message);
				        

	 	//while travese to send the mesage to all groups this peer is a member of
		Enumeration enum = this.comm.memberOfGroupsEnumeration();
		while (enum.hasMoreElements()) {
			PeerGroup pg = (PeerGroup) enum.nextElement();
			sendMessageToPeerGroup(	pg,	propType, ownPeerID, ownGroupID,tag,message);
		}
    }
    
	private void sendMessageToPeerGroup(PeerGroup pg, int propType,
									String ownPeerID, String ownGroupID,
									int tag, String message) {
		//Prepare a message to be sent
		Message msgToSend = this.createMsg(pg, propType, ownPeerID,
		                                  ownGroupID, tag, message);
		
		OutputPipe outputPipe = this.comm.getOutputPropagatePipe(pg.getPeerGroupID());
		System.out.println("outputPipe = " + outputPipe + " for group " + pg.getPeerGroupName());
		
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
        
		PeerGroup pg = null;
		InputpipeDiscoveryListener inputpipeDiscoveryListener = null;				 
		DiscoveryService discoveryService = null;


	 	//while travese to send the mesage to all groups this peer is a member of
		Enumeration enum = this.comm.memberOfGroupsEnumeration();
		while (enum.hasMoreElements()) {
			pg = (PeerGroup) enum.nextElement();
	        discoveryService = pg.getDiscoveryService();

            //Prepare a message to be sent
             Message msgToSend = this.createMsg(pg,
                                              propType,
                                              ownPeerID,
                                              ownGroupID,
                                              tag,
                                              message);


            /*
	 		inputpipeDiscoveryListener = new InputpipeDiscoveryListener(msgToSend,this.comm,pg);
	        //Add the listener that will be invoked when a response from the query will be resived
	        discoveryService.addDiscoveryListener(inputpipeDiscoveryListener);
	          */
			//Ask other peers for inputpipes with or without a species peerID  
			discoveryService.getRemoteAdvertisements(null,
						       					 DiscoveryService.ADV,
						       					 "Name",
						       					 "InputPipe",
						       					 100);
		 try{
             Thread.sleep(3*1000);
             Enumeration adverts = discoveryService.getLocalAdvertisements(DiscoveryService.ADV,
                                                                        "Name",
                                                                        "InputPipe");
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
	     }catch (IOException e) {
                    //Couldn't find a host to a given Adv.
         }finally {
			enum = this.comm.memberOfGroupsEnumeration();
			while (enum.hasMoreElements()) {
				pg = (PeerGroup) enum.nextElement();
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
		PeerGroup pg = Communication.getGlobalPG();
        System.err.println("SendMessageThread::sendToPeer (one specific)" );

        Message msgToSend = this.createMsg(pg,
                                          propType,
		        						  ownPeerID,
                                          ownGroupID,
		        						  tag,
		        						  message);

        discoveryService = Communication.getGlobalPG().getDiscoveryService();

 		//inputpipeDiscoveryListener = new InputpipeDiscoveryListener(msgToSend,this.comm,pg);
        //Add the listener that will be invoked when a response from the query will be resived
        
        //discoveryService.addDiscoveryListener(inputpipeDiscoveryListener);

		//Ask other peers for inputpipes with or without a species peerID  
		discoveryService.getRemoteAdvertisements(null,
					       					 DiscoveryService.ADV,
					       					 "Name",
					       					 "InputPipe",
					       					 100);
                                             
         try{
             Thread.sleep(3*1000);
             Enumeration adverts = discoveryService.getLocalAdvertisements(DiscoveryService.ADV,
                                                                        "Id",
                                                                        recieverPipeAdvID);
                   PipeService pipeService = pg.getPipeService();                    
          while(adverts.hasMoreElements()){
             try {  
                    PipeAdvertisement tmpAdv = (PipeAdvertisement) adverts.nextElement(); 
                  //Create a OutputPipe  
                  
                   OutputPipe tmpOutputPipe = pipeService.createOutputPipe(tmpAdv,5*1000);
                                                
                   //Send message through new pipe and close pipe
                   tmpOutputPipe.send(msgToSend);
                   tmpOutputPipe.close();
                 } catch (IOException e) {
                    //Couldn't find a host to a given Adv.
             }             
          }  
         } catch (InterruptedException e) {
	        System.err.println("Thread:: gonna do getREmoteAdvertisements FAILED" );
			this.anyErrors = true;
	     } catch (IOException e) {
              //Couldn't find a host to a given Adv.
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
   	private Message createMsg(PeerGroup pg,
                              int propType,
    						  String ownPeerID, 
                              String ownGroupID, 
    						  int tag, 
    						  String message){
    		MimeMediaType mimeType = new MimeMediaType("text/xml");					  	
			Message tmpMessage = null;
		 	 tmpMessage = pg.getPipeService().createMessage();
	         	tmpMessage.addElement(tmpMessage.newMessageElement(
	         								"TAG", 
				                            mimeType, 
                                                new Integer(tag).toString().getBytes()));
        if (tag == Communication.TAGPROPAGATE){
	        tmpMessage.addElement(
	            tmpMessage.newMessageElement("propType",mimeType, new Integer(propType).toString().getBytes()));
		}

	    tmpMessage.addElement(
            tmpMessage.newMessageElement("SenderPeerID",mimeType,
				Communication.getGlobalPG().getPeerID().toString().getBytes()));
                    
        tmpMessage.addElement(
            tmpMessage.newMessageElement("SenderPeerName",mimeType,
				Communication.getGlobalPG().getPeerName().toString().getBytes()));

        if (ownPeerID != null){
            tmpMessage.addElement(
	            tmpMessage.newMessageElement("PeerID",mimeType,ownPeerID.getBytes()));
	    }
        if (ownGroupID == null) {
            tmpMessage.addElement(
	            tmpMessage.newMessageElement("GroupID",mimeType,
					Communication.getGlobalPG().getPeerGroupID().toString().getBytes()));
	     } else {
	        tmpMessage.addElement(
			    tmpMessage.newMessageElement("GroupID",mimeType,ownGroupID.getBytes()));
	     }
	     tmpMessage.addElement(
	        tmpMessage.newMessageElement("Body",mimeType,message.getBytes()));

        return tmpMessage;
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

