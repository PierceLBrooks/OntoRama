package ontorama.backends;

import ontorama.OntoramaConfig;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.backends.p2p.model.P2PGraphImpl;
import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.query.Query;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


public class BackendSearch {

	public static P2PGraph search(Query query){
        P2PGraph retVal = new P2PGraphImpl();
        try {
            Peer2PeerBackend backend = (Peer2PeerBackend) OntoramaConfig.getBackend();
            System.out.println("---searching backend " + backend + " for query " + query);

            P2PGraph tempGraph = backend.search(query);

            retVal.add(tempGraph);
            System.out.println("returning " + retVal);
            System.out.println(" returned nodes: " + retVal.getNodesList());
            System.out.println(" returned edges: " + retVal.getEdgesList());

        } catch (GraphModificationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoSuchRelationLinkException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return retVal;
	}

    public static P2PGraph searchLocal(Query query) {
        P2PGraph retVal = new P2PGraphImpl();
        try{
            Peer2PeerBackend backend = (Peer2PeerBackend) OntoramaConfig.getBackend();
            System.out.println("--1---searching backend " + backend + " for query " + query);
            P2PGraph tempVal = backend.search(query);
            retVal.add(tempVal);

            System.out.println("----this backend returned nodes: " + retVal.getNodesList());
            System.out.println(" returned edges: " + retVal.getEdgesList());
            System.out.println(" returned nodes: " + retVal.getNodesList());
        } catch (GraphModificationException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoSuchRelationLinkException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return retVal;
    }

}
