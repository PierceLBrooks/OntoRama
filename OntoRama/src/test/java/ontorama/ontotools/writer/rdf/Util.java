package ontorama.ontotools.writer.rdf;

import com.hp.hpl.mesa.rdf.jena.model.NsIterator;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.ResIterator;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;

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
