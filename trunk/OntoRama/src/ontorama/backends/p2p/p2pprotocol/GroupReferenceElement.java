package ontorama.backends.p2p.p2pprotocol;

import net.jxta.id.ID;

/**
 * This class contains the search result element from a search for peer groups. 
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class GroupReferenceElement {

	private ID id = null;
	private String name = null;
	private String descr = null;

						
	/**
	 * The constructor
	 * 
	 * @param id the ID for the peer group
	 * @param name the name of the peer group
	 * @param descr the description fo the peer group
	 * 
	 * @version P2P-OntoRama 1.0.0
	 */	
	public GroupReferenceElement(ID id, String name, String descr) {
		this.id = id;
		this.name = name;
		this.descr = descr;		
	}	
	
	/**
	 * Returns the peer group ID
	 * 
	 * @return the ID of the peer group
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public ID getID () {
		return this.id;
	}
		
	/**
	 * Returns the peer group name
	 * 
	 * @return name the name of the peer group
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public String getName() {
		return this.name;	
	}

	/**
	 * Returns the description of the peer group
	 * 
	 * @return the description of the peer group
	 *
	 * @version P2P-OntoRama 1.0.0
	 */
	public String getDescription() {
		return this.descr;	
	}
}
