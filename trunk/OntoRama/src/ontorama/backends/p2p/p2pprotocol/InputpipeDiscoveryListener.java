package ontorama.backends.p2p.p2pprotocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;


/**
* InputpipeDiscoveryListener is a listener for responses from a 
* previous query asking for other peers that has a Inputpipe open to
* receive messages on. When such pipes are found this class starts a 
* output pipe and connects it to the other peer. The class uses the new
* pipe to send a message to each one of the found peers.
* 
* @author Henrika 
* @author Johang
* @version P2P-OntoRama 1.0.0
* 
* <b>Copyright:</b>Copyright (c) 2002
* <br>
* <b>Company:</b>DSTC
*
*/

public class InputpipeDiscoveryListener implements DiscoveryListener{
  private Message messageToBeSent = null;
  private Map peersAlreadySentTo = null;
  private CommunicationSender comm = null;
  private PeerGroup pg = null;
  
	/** 
	* Constructor for class. This method constructs the object and saves the
	* message so it could be sent when a inputpipe is found.
	*
	* @param message 	the message to be send to peers.
	* @version P2P-OntoRama 1.0.0
	*/
	public InputpipeDiscoveryListener(Message message,
  									 CommunicationSender comm, 
  									 PeerGroup pg){
 		this.messageToBeSent = message;	
   		this.peersAlreadySentTo = new HashMap();  	
 		this.comm = comm;
 		this.pg = pg;
	}

	/**
	* This method is called by the underlying Jxta protocol when a inputpipe is found.
	* It opens a pipe and sends a message to the other peer.
	*
	* @param event the event passed to the method when a inputpipe is found.
	* @version P2P-OntoRama 1.0.0
	*/ 
	public void discoveryEvent(DiscoveryEvent event) {
        System.out.println("Received a response when looking for adv");
		String pipeAdvText = null; 
	  	PipeAdvertisement tmpAdv = null;
		OutputPipe tmpOutputPipe = null;
        boolean sending = false;

		Enumeration response = event.getResponse().getResponses();
 		
 		//Loop through all inputpipes found
 		while (response.hasMoreElements()){
 			pipeAdvText = (String) response.nextElement(); 
 			try
 			 {
				
                //Making an Advertisment tobe used when constructing a outputpipe
				tmpAdv = (PipeAdvertisement) 
				AdvertisementFactory.newAdvertisement(new MimeMediaType("text/xml"),
				new ByteArrayInputStream(pipeAdvText.getBytes()));
				
			               
                //Has to be synchronized since this method could be invoked by different threads
                  System.out.println("before sync");
				synchronized(this){
                      System.out.println("after sync");
					//The message should only be sent to each peer once
					if (this.peersAlreadySentTo.isEmpty() || 
						 (this.peersAlreadySentTo.get(tmpAdv.getID()) == null)) {
					      
                           this.peersAlreadySentTo.put(tmpAdv.getID(), new Boolean(true));
                           System.out.println("TmpAdv:" + tmpAdv.getID());
                           sending = true;
                       }
                    System.out.println("leaving sync");
                 }
                  
                 System.out.println("sending == " + sending);     
                 if (sending == true){  
        				 //Create a OutputPipe	
        				 PipeService pipeService = this.pg.getPipeService();
        		 		 tmpOutputPipe = pipeService.createOutputPipe(tmpAdv,1*1000);
        		 			                 
                         //Send message through new pipe and close pipe
        		 		  System.out.println("before send");
                          tmpOutputPipe.send(this.messageToBeSent);
                          System.out.println("after send");
        				  tmpOutputPipe.close();
        		    }
             } catch (IOException e) {
				  //TODO handle the error new PeerNotFoundException("Found an advertisement without any peer conencted");
				  e.printStackTrace();
             } catch (NullPointerException e) {
				//TODO handle the error throw (NullPointerException) e.fillInStackTrace();
				e.printStackTrace();
			}	
						
 		}
    } 	
}
