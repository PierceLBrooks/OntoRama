package ontorama.backends.p2p.p2pprotocol;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;


/**
 * This class handles the P2P functionality which is related to sending 
 * information between different peers.
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class CommunicationSender extends Communication {

	private CommunicationProtocolJxta commProt = null;

	/** 
	* The constructor
	*
	* @param obj a CommunicationProtocolJxta object
	*/
	public CommunicationSender(CommunicationProtocolJxta obj) {
		commProt = obj;
	}
	
	/** 
	* This method is called to send a message to all other peers listening
	* for incoming messages. If a receiver peer id is given the message 
	* is only sent to that peer. This method runs in its' own thread.
	*
	* @param propType the type of propagation (if it is a propagataion)
	* @param recieverPeerID the peer id that will recieve the message (null if all)
	* @param ownPeerID this peers id
	* @param ownGroypID the id of the group
	* @param tag the tag for the message
	* @param message the message to send
	* 
	* @return returns true if everything went ok otherwise false
	* @exception
	* 
	* @version P2P-OntoRama 1.0.0
	*/	
	protected void sendMessage(int propType,
								String recieverPipeAdvID, 
								String ownPeerID, 
								String ownGroupID, 
								int tag, 
								String message) 
								throws GroupExceptionThread{
		SendMessageThread sendMessageThread = new SendMessageThread(this);
		sendMessageThread.start();
		
		if (recieverPipeAdvID == null) {
			System.err.println("CommunicationSender::sendMessage sendToAll() " + message);
			//Changed
            sendMessageThread.sendToAllPropagate(propType,
										  ownPeerID, 
										  ownGroupID, 
										  tag, 
										  message);
		} else {
			System.err.println("CommunicationSender::sendMEssage sendToPeer()");			
			sendMessageThread.sendToPeerInGlobal(propType,
										  recieverPipeAdvID,
										  ownPeerID, 
										  ownGroupID, 
										  tag, 
										  message);
		}
		if (sendMessageThread.anyErrors()) {
			System.err.println("CommunicationSender::sendMEssage ERROR");
			throw new GroupExceptionThread("Time out while trying to send message");	
		}
	}



    
    
    
	
	
	/**
	* Is called to do searches for at other peers. The methods sends out a search request and 
	* then waits for 20 seconds to get responses
	* 
	* @param query a string containing the query 
	* @return a vector of SearchResultElement
	* @exception
	*
	* @version P2P-OntoRama 1.0.0
	*/
    protected Vector sendSearchRequest(String query) throws GroupExceptionThread {
		DiscoveryService discoveryService = null;
		InputpipeDiscoveryListener inputpipeDiscoveryListener = null;
        //System.out.println("sendMessage::start");
        
  		MimeMediaType mimeType = new MimeMediaType("text/xml");
        Message queryTobeSent = null; 
        this.setSearchResult(new Vector());   
  
         //Build the message that is going to be sent
         queryTobeSent = this.getGlobalPG().getPipeService().createMessage();
                       
         queryTobeSent.addElement(
          queryTobeSent.newMessageElement(
                            "TAG", 
                            mimeType, 
                            new Integer(this.TAGSEARCH).toString().getBytes()));
                        
         queryTobeSent.addElement(
          queryTobeSent.newMessageElement(
                            "SenderPeerID", 
                            mimeType, 
                            this.getGlobalPG().getPeerID().toString().getBytes()));

		PipeAdvertisement pipeAdv = this.getPipeAdvertisement(
										this.getGlobalPG().getPeerGroupID());
		                                                                                           
         queryTobeSent.addElement(
          queryTobeSent.newMessageElement(
                            "SenderPipeID", 
                            mimeType, 
                            pipeAdv.getPipeID().toString().getBytes()));

        queryTobeSent.addElement(
         queryTobeSent.newMessageElement(
                        "Body", 
                        mimeType, 
                        query.getBytes()));

        /*        
        inputpipeDiscoveryListener = new InputpipeDiscoveryListener(
        								queryTobeSent,
        								this,
        								this.getGlobalPG());
       
        
        discoveryService = this.getGlobalPG().getDiscoveryService();    
       
        //Add the listener that will be invoked when a response from the query will be resived
        discoveryService.addDiscoveryListener(inputpipeDiscoveryListener);
        */
     
        Enumeration enum = this.getMemberOfGroups().elements();
        while (enum.hasMoreElements()) {
            PeerGroup pg = (PeerGroup) enum.nextElement();
            discoveryService = pg.getDiscoveryService();

            //System.err.println("We are trying to send to group: " + pg.getPeerGroupName());
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
             Enumeration adverts;
                adverts = discoveryService.getLocalAdvertisements(DiscoveryService.ADV,
                                                                  "Name",
                                                                  "InputPipe");
               PipeService pipeService = pg.getPipeService();                   
          while(adverts.hasMoreElements()){
             try {  
                    PipeAdvertisement tmpAdv = (PipeAdvertisement) adverts.nextElement();
                    System.out.println("Trying to send to peer " + tmpAdv.getPipeID());                    
                   //Create a OutputPipe  
                   OutputPipe tmpOutputPipe = pipeService.createOutputPipe(tmpAdv, 1*1000);
                                                
                   //Send message through new pipe and close pipe
                   tmpOutputPipe.send(queryTobeSent);
                   tmpOutputPipe.close();
               
              } catch (IOException e) {
                    //Couldn't find a host to a given Adv.                 
              }             
          }                         
            Thread.sleep(7*1000);
  
         }catch (IOException e) {
         }catch (InterruptedException e) {
			 throw new GroupExceptionThread(e, "The thread was interrupted");
         }      
      }
      return this.getSearchResult();
    }



	/** 
	* This method is called on logout. It calls callFLushPeerAdvertisementFrom()
	* for every group we are in at the moment.
	*
	* @exception
	* 
	* @version P2P-OntoRama 1.0.0
	*/	
	protected void callFlushPeerAdvertisementOnLogout() throws GroupExceptionFlush, GroupExceptionThread {
		//Use the GroupMember object to keep track of which group we are 
		//in at the moment. Call callFlushPeerAdvertisement for every group 
		//in this object.
		Enumeration enum = this.getMemberOfGroups().elements();
		PeerGroupID groupID = null;
		
		while (enum.hasMoreElements()) {
				groupID = ((PeerGroup) enum.nextElement()).getPeerGroupID();	
				try {
					commProt.callFlushPeerAdvertisementFrom(groupID.toString());
				} catch (GroupExceptionFlush e) {
					throw (GroupExceptionFlush) e.fillInStackTrace();
				} catch (GroupExceptionThread e) {
					throw (GroupExceptionThread) e.fillInStackTrace();
				}
		}
	}
	
		   	/** 
	* This method is called to flush a peers PeerAdvertisements from a given 
	* group. This method also propagates the flush request to other peers 
	* IFF the argument sendRemotely is true. If the peerID is null it will use 
	* the peer id of this peer.
	*
	* @param groupID the id of the peergroup in that the flush should be done
	* @param peerID the id of the peeradvertisement to be flushed
	* @param sendRemotely true if the flush request should be propagated
	*  
	* @exception
	* @version P2P-OntoRama 1.0.0
	*/	
	public void flushPeerAdvertisement(String groupIDasString,
										String peerID) 
										throws GroupExceptionThread, GroupExceptionFlush {
		DiscoveryService discServ = null;
		PeerGroup pg = null;
		String peerIDasString = peerID;
		
		if (groupIDasString.equals(this.getGlobalPG().getPeerGroupID().toString())) {
			//its the globalGroup we are looking for
			discServ = this.getGlobalPG().getDiscoveryService();
			pg =this.getGlobalPG();
			//System.out.println("used globalGroup");
		}	else {
			pg = this.getPeerGroup(groupIDasString);
			discServ = pg.getDiscoveryService();				
		}
		
		//we only have to execute command if we got a discoveryService == we had the advertisment
		if (discServ != null) {
			//if peerID hasn't been set
			if (peerIDasString.equals("")) {
				peerIDasString = pg.getPeerAdvertisement().getID().toString();
				
			}
			try {
				//Flush the peer advertisement localy
				discServ.flushAdvertisements(peerIDasString, discServ.PEER);				
			} catch (IOException e) {
				throw new GroupExceptionFlush(e, "Could not flush advertisements on this peer");				
			}
			
		}
	}
}
