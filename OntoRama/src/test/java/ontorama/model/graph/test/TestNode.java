package ontorama.model.graph.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import junit.framework.TestCase;

import ontorama.model.graph.Node;
import ontorama.model.graph.NodeImpl;

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

public class TestNode extends TestCase {
	
    private Node node1;
    private Node node2;
    private Node node3;

    private String nodeIdentifier1 = "some.node.identifier";

    private URI creatorUri1;
    private URI creatorUri2;

    /**
     *
     */
    public TestNode(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws URISyntaxException {
    	
        creatorUri1 = new URI("ontoMailto:someone@ontorama.org");
        creatorUri2 = new URI("ontoHttp://ontorama.ort/someone.html");
        node1 = new NodeImpl("node1", nodeIdentifier1);
        node2 = new NodeImpl("node2", "node2");
        node3 = new NodeImpl("node3", "node3");

        node1.setCreatorUri(creatorUri1);
        node2.setCreatorUri(creatorUri2);
    }

    /**
     * test method getName()
     */
    public void testGetName() {
        assertEquals("node1 name", "node1", node1.getName());
        assertEquals("node2 name", "node2", node2.getName());
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

    public void testGetCreator() {
        assertEquals("creatorUri for node1", creatorUri1, node1.getCreatorUri());
        assertEquals("creatorUri for node2", creatorUri2, node2.getCreatorUri());
    }

    public void testSetCreator() throws URISyntaxException {
        URI creatorUri = new URI("mailto:someone@ontorama.org");
        node3.setCreatorUri(creatorUri);
        assertEquals("creatorUri for node3", creatorUri, node3.getCreatorUri());
    }

    ///////////////////****** Helper methods ***********///////////////////

    /**
     * check for given graph node name in the given iterator
     */
    public static boolean graphNodeNameIsInIterator(String name, Iterator it) {
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            if (name.equals(cur.getName())) {
                return true;
            }
        }
        return false;
    }
}