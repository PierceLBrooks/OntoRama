package ontorama.backends.p2p;

/**
 * This class is used to keep all constants needed by
 * p2p backend.
 * 
 * @author nataliya
 * Created on 7/03/2003
 */
public class P2PGlobals {

	/**
	 * Used by sendSearchGroup
	 */
	public final static String SEARCHGROUPNAME = "Name";
	public final static String SEARCHGROUPDESCR = "Desc";

	/**
	 * Used by sendMessage
	 */
	public final static int TAGSEARCH = 1;
	public final static int TAGLOGOUT = 2;
	public final static int TAGPROPAGATE = 3;
	public final static int TAGSEARCHRESPONSE = 4;
	public final static int TAGFLUSHPEER = 5;

	/** 
	 * vars used by InputPipeListener and SendMessageThread to 
	 * send and interpret messages
	 */
	public final static String STR_TAG = "TAG";
	public final static String STR_SenderPeerID = "SenderPeerID";
	public final static String STR_SenderPeerName = "SenderPeerName";
	public final static String STR_propType = "propType";
	public final static String STR_GroupID = "GroupID";
	public final static String STR_Body = "Body";
	public final static String STR_SenderPipeID = "SenderPipeID";
	public final static String STR_PeerID = "PeerID";
	
	/**
	 * vars used in advertisements
	 */
	public final static String ADV_Name = "Name";
	public final static String ADV_InputPipe = "InputPipe";
	public final static String ADV_Id = "Id";
}
