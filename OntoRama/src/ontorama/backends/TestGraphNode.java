package ontorama.backends;

import junit.framework.TestCase;
import ontorama.util.IteratorUtil;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

public class TestGraphNode extends TestCase {

    private GraphNode node1;
    private GraphNode node2;
    private GraphNode node3;

    private String fullNameNode1 = "this is node1 full name";

    private GraphNode cloneNode2;
    private GraphNode cloneNode3;

    private String propName = "Description";
    private List propValue = new LinkedList();

    /**
     *
     */
    public TestGraphNode(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws NoSuchRelationLinkException {
        node1 = new GraphNode("node1", fullNameNode1);

        propValue.add("description str1");
        propValue.add("description str2");

        node1.setProperty(propName, "http://undefined.se",propValue);

        node1.setFoldState(true);
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
        assertEquals("node1 full name", fullNameNode1, node1.getFullName());
        assertEquals("node2 full name", "node2", node2.getFullName());
    }

    /**
     *
     */
    public void testSetFullName() {
        String testFullName1 = "another full name";
        String testFullName2 = "yet another full name";
        node1.setFullName(testFullName1);
        node2.setFullName(testFullName2);

        assertEquals("testing setFullName() for node1", testFullName1, node1.getFullName());
        assertEquals("testing setFullName() for node2", testFullName2, node2.getFullName());
    }

    /**
     *
     */
    public void testSetProperty() throws NoSuchRelationLinkException {
        node2.setProperty(propName, "http://undefined.se",propValue);

        List node2PropValue = node2.getProperty(propName);
        assertEquals(propValue.size(), node2PropValue.size());
        compareLists(propValue, node2PropValue);
    }


    /**
     *
     */
    public void testGetProperty() throws Throwable {
		try {
	        // test when we do have a property
	        List node1PropValue;
	
			node1PropValue = node1.getProperty(propName);

	        assertEquals(propValue.size(), node1PropValue.size());
	        compareLists(propValue, node1PropValue);
		} catch (NoSuchRelationLinkException e) {
			throw e.fillInStackTrace();
		}
    
	
		try {

	        // test for a node that doesn't have properties
	        List node2PropValue;
			node2PropValue = node2.getProperty(propName);
	        assertEquals(0, node2PropValue.size());
		} catch (NoSuchRelationLinkException e) {
			//ok
		}
	    
    }

    /**
     * test method getClones()
     */
    public void testGetClones() {

        // test nodes with clones
        assertEquals("number of clones for node2", 2, IteratorUtil.getIteratorSize(node2.getClones()));
        assertEquals("number of clones for cloneNode2", 2, IteratorUtil.getIteratorSize(cloneNode2.getClones()));

        assertEquals("clones for node2 contain cloneNode2", true,
                IteratorUtil.objectIsInIterator(cloneNode2, node2.getClones()));
        assertEquals("clones for cloneNode3 contain node2", true,
                IteratorUtil.objectIsInIterator(node2, cloneNode3.getClones()));

        // test nodes without clones
        assertEquals("number of clones for node1", 0, IteratorUtil.getIteratorSize(node1.getClones()));
    }


    /**
     * test method hasClones()
     */
    public void testHasClones() {
        assertEquals("node2 should have clones", true, node2.hasClones());
        assertEquals("node1 shouln't have any clones", false, node1.hasClones());
    }

    /**
     * test method setFoldedState
     */
    public void testSetFoldedState() {
        cloneNode2.setFoldState(true);
        cloneNode3.setFoldState(false);

        assertEquals("cloneNode2 should have folded state true", true, cloneNode2.getFoldedState());
        assertEquals("cloneNode3 should have folded state false", false, cloneNode3.getFoldedState());
    }

    /**
     * test method getFoldedState
     */
    public void testGetFoldedState() {
        assertEquals("node1 is folded, folded state should be true", true, node1.getFoldedState());
        assertEquals("node3 is unfolded, folded state should be false", false, node3.getFoldedState());
    }

    ///////////////////****** Helper methods ***********///////////////////

    /**
     *
     */
    private void compareLists(List list1, List list2) {
        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i), list2.get(i));
        }
    }

    /**
     * check for given graph node name in the given iterator
     */
    public static boolean graphNodeNameIsInIterator(String name, Iterator it) {
        while (it.hasNext()) {
            GraphNode cur = (GraphNode) it.next();
            if (name.equals(cur.getName())) {
                return true;
            }
        }
        return false;
    }
}