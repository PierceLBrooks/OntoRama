package ontorama.backends.p2p.p2pmodule;

/**
 * This class handles the error handling
 * 
 * @author henrika
 * @author johang
 * 
 * @version P2P-OntoRama 1.0.0
 * 
 * <b>Copyright:</b>		Copyright (c) 2002<br>
 * <b>Company:</b>			DSTC<br>
 */
public class LogObject {
	private String msg = null;
	private Exception e = null;


	public LogObject(String msg) {
		this.msg = msg;		
	}
	
	public LogObject(Exception e, String msg) {
		this.msg = msg;		
		this.e = e;
	}

	public String getMsg() {
		return this.msg;	
	}

	public Exception getException() {
		return this.e;	
	}

}
