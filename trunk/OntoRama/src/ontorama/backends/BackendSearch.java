package ontorama.backends;

import java.util.Iterator;

import ontorama.backends.filemanager.FileBackend;
import ontorama.backends.p2p.model.P2PGraph;
import ontorama.webkbtools.query.Query;
import ontorama.view.OntoRamaApp;


/**
 * @author henrika
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */


//TODO This class should maybe be in ontoRama instead..
public class BackendSearch {

    //TODO This one must return a ExtendedGraph even if it is empty, null is not accepted
	public static P2PGraph search(Query query){
		P2PGraph retVal = null;
		
		Iterator backendIt = OntoRamaApp.getBackends().iterator();
		while (backendIt.hasNext()) {
			Backend backend = (Backend) backendIt.next();
			
			if (backend instanceof FileBackend) {
				retVal = ((FileBackend) backend).search(query);
			} 
		}
		return retVal;
	}
   
}
