package ontorama.ontotools.writer.rdf.test;

import com.hp.hpl.mesa.rdf.jena.model.NsIterator;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.ResIterator;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Util {
	protected static int getStmtIteratorSize (StmtIterator it) throws RDFException {
		int res = 0;
		while (it.hasNext()) {
			it.next();
			res++;
		}
		return res;
	}

	protected static int getResIteratorSize (ResIterator it) throws RDFException {
		int res = 0;
		while (it.hasNext()) {
			it.next();
			res++;
		}
		return res;
	}

	protected static int getNsIteratorSize (NsIterator it) throws RDFException {
		int res = 0;
		while (it.hasNext()) {
			it.next();
			res++;
		}
		return res;
	}
	

}
