package ontorama.ontotools.parser.rdf.test;

import ontorama.ontotools.parser.rdf.test.p2p.TestShortP2pExample;
import ontorama.ontotools.parser.rdf.test.p2p.TestVerboseP2pExample;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author nataliya
 */
public class TestRdfP2pParser extends TestCase {
	
	public TestRdfP2pParser (String name) {
		super(name);
	}
	public static Test suite() {
		TestSuite suite = new TestSuite("OntoRama P2P Parser Test");

		suite.addTest(new TestSuite(TestShortP2pExample.class));

		suite.addTest(new TestSuite(TestVerboseP2pExample.class));
			
		return suite;
	}
	
}
