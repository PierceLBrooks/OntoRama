package ontorama.backends.p2p;




import java.net.URL;

import java.net.MalformedURLException;

import net.jxta.id.IDFactory;

import net.jxta.peergroup.PeerGroupID;


/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class OntoRamaNetGroupID extends PeerGroupID {


	final static  String JXTAFormat = "jxta";
	
	private static final String UNIQUEVALUE = "OntoRamaGroup";
	
	public Object clone() {
	    return this;  // netPeerGroupID is only itself.
	}
	
	public boolean equals( Object target ) {
	    return (this == target);   // netPeerGroupID is only itself.
	}
	
	private Object readResolve() {
	    return this;
	}
	
	public URL getURL() {
	    String urlText = URNNamespace + ":" + getUniqueValue();
	    
	    try {
	        return IDFactory.jxtaURL( URIEncodingName, "", urlText );
	    }
	    catch( MalformedURLException caught ) {
	        throw new IllegalStateException( "Environment incorrectly intialized." );
	    }
	}
	
	public String getIDFormat() {
	    return JXTAFormat;
	}
	
	public Object getUniqueValue() {
	    return getIDFormat() + "-" + UNIQUEVALUE;
	}
	
	public PeerGroupID getParentPeerGroupID() {
	    return PeerGroupID.worldPeerGroupID;
	}
}
