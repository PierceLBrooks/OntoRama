package ontorama.backends.p2p.p2pprotocol;

/**
 * This class contains the search result element from a search for ontologies. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class SearchResultElement {
	private String peerID = null;
	private String bodyText = null;							
						
	/**
	 * The constructor
	 * 
	 * @param peerID the id of the peer
	 * 
	 * @version P2P-OntoRama 1.0.0
	 */	
	public SearchResultElement(String peerID) {		
		this.peerID = peerID;
	}	

	/**
	 * The constructor
	 * 
	 * @param peerID the id of the peer
	 * @param resultText the text of the element
	 * 
	 * @version P2P-OntoRama 1.0.0
	 */	
    public SearchResultElement(String peerID, String resultText) {
        this.peerID = peerID;
        this.bodyText = resultText;
    }   		

	/**
	 * Returns the peer id
	 * 
	 * @return a peer id as a string
	 *
	 * @version P2P-OntoRama 1.0.0
	 */	
	public String getPeerID () {
		return this.peerID;
	}							
    
	/**
	 * Returns the result text
	 * 
	 * @return the result text
	 *
	 * @version P2P-OntoRama 1.0.0
	 */	
	public String getResultText(){
		return this.bodyText;   
	}
}