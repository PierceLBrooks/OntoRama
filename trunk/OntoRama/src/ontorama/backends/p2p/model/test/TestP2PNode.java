package ontorama.backends.p2p.model.test;

import junit.framework.TestCase;
import ontorama.backends.p2p.model.*;

import java.util.HashSet;
import java.util.Iterator;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * methods not tested:
 * - all private and protected methods
 * - calculateDepths()
 * - setDepth() because there are no way to check it since getDepth is protected.
 */

public class TestP2PNode extends TestCase {

    private P2PNode node1;
    private P2PNode node2;
    private P2PNode node3;

    private String nodeIdentifier1 = "some.node.identifier";


    private URI creatorUri1;
    private URI creatorUri2;

    /**
     *
     */
    public TestP2PNode(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws URISyntaxException {
        creatorUri1 = new URI("ontoMailto:someone@ontorama.org");
        creatorUri2 = new URI("ontoHttp://ontorama.ort/someone.html");
        node1 = new P2PNodeImpl("node1", nodeIdentifier1, creatorUri1,null);
        node2 = new P2PNodeImpl("node2", creatorUri1,null);
        node3 = new P2PNodeImpl("node3", null,creatorUri2);

        node1.setFoldState(true);
        node3.setFoldState(false);

        node1.setCreatorUri(creatorUri1);
        node2.setCreatorUri(creatorUri2);
    }

    /**
     * test method getName()
     */
    public void testGetName() {
        assertEquals("node1 name", "node1", node1.getIdentifier());
        assertEquals("node2 name", "node2", node2.getIdentifier());
    }

    /**
     *
     */
    public void testGetFullName() {
        assertEquals("node1 full name", nodeIdentifier1, node1.getIdentifier());
        assertEquals("node2 full name", "node2", node2.getIdentifier());
    }

    /**
     *
     */
    public void testSetFullName() throws URISyntaxException {
        String testIdentifier1 = "htpp://somewhere.com/ontorama/node1";
        String testIdentifier2 = "http://somewhere.com/ontorama/node2";
        node1.setIdentifier(testIdentifier1);
        node2.setIdentifier(testIdentifier2);

        assertEquals("testing setIdentifier() for node1", testIdentifier1, node1.getIdentifier());
        assertEquals("testing setIdentifier() for node2", testIdentifier2, node2.getIdentifier());
    }


    /**
     * test method setFoldedState
     */
    public void testSetFoldedState() {
        node2.setFoldState(true);
        node3.setFoldState(false);

        assertEquals("cloneNode2 should have folded state true", true, node2.getFoldedState());
        assertEquals("cloneNode3 should have folded state false", false, node3.getFoldedState());
    }

    /**
     * test method getFoldedState
     */
    public void testGetFoldedState() {
        assertEquals("node1 is folded, folded state should be true", true, node1.getFoldedState());
        assertEquals("node3 is unfolded, folded state should be false", false, node3.getFoldedState());
    }

    public void testGetCreator() {
        assertEquals("creatorUri for node1", creatorUri1, node1.getCreatorUri());
        assertEquals("creatorUri for node2", creatorUri2, node2.getCreatorUri());
    }

    public void testSetCreator() throws URISyntaxException {
        URI creatorUri = new URI("mailto:someone@ontorama.org");
        node3.setCreatorUri(creatorUri);
        assertEquals("creatorUri for node3", creatorUri, node3.getCreatorUri());
    }



	public void testGetAssertionList() {
		HashSet set = new HashSet();
		set.add(creatorUri1);
		
		assertEquals("getAssertion for node1", set, (HashSet) node1.getAssertionsList());
		assertEquals("getAssertion for node2", set, (HashSet) node2.getAssertionsList());
	}

	public void testGetRejectionList() {
		HashSet set = new HashSet();

		assertEquals("getRejections for node2", set, (HashSet) node2.getRejectionsList());

		set.add(creatorUri2);
		assertEquals("getRejections for node3", set, (HashSet) node3.getRejectionsList());
	}

	public void testAddAssertion() {
		HashSet set = new HashSet();		
		assertEquals("addAssertion for node3a", set, (HashSet) node3.getAssertionsList());
		set.add(creatorUri1);
		
		node1.addAssertion(creatorUri1);
		assertEquals("addAssertion for node1", set, (HashSet) node1.getAssertionsList());
		
		set.add(creatorUri2);
		node3.addAssertion(creatorUri1);
		node3.addAssertion(creatorUri2);
		assertEquals("addAssertion for node3b", set, (HashSet) node3.getAssertionsList());
	}

	public void testAddRejection() {
		HashSet set = new HashSet();	

		set.add(creatorUri2);
		node3.addRejection(creatorUri2);
		assertEquals("addRejection for node3", set, (HashSet) node3.getRejectionsList());

		set.add(creatorUri1);
		node3.addRejection(creatorUri1);
		assertEquals("addRejection for node3", set, (HashSet) node3.getRejectionsList());
	}
	
	



    ///////////////////****** Helper methods ***********///////////////////

    /**
     * check for given graph node name in the given iterator
     */
    public static boolean graphNodeNameIsInIterator(String name, Iterator it) {
        while (it.hasNext()) {
            P2PNode cur = (P2PNode) it.next();
            if (name.equals(cur.getIdentifier())) {
                return true;
            }
        }
        return false;
    }
}