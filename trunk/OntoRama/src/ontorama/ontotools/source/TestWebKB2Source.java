package ontorama.ontotools.source;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import ontorama.OntoramaConfig;
import ontorama.backends.examplesmanager.ExamplesBackend;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.query.Query;
import ontorama.util.TestingUtils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestWebKB2Source extends TestCase {

    private WebKB2Source webkbSource;
    private String sourceUri;

    private Query query_cat;
    private SourceResult sourceResult_cat;
    private boolean queryIsAmbiguous_cat;
    private int numOfChoices_cat;
    private List choicesList_cat;


    private Query query_woodMouse;
    private SourceResult sourceResult_woodMouse;
    private boolean queryIsAmbiguous_woodMouse;
    private int numOfChoices_woodMouse;
    private List choicesList_woodMouse;

    private Query query_dog;
    private SourceResult sourceResult_dog;
    private boolean queryIsAmbiguous_dog;
    private int numOfChoices_dog;
    private List choicesList_dog;


    private Query query_nonExistentTerm;
    private SourceResult sourceResult_nonExistentTerm;
    private boolean queryIsAmbiguous_nonExistentTerm;
    private int numOfChoices_nonExistentTerm;
    private List choicesList_nonExistentTerm;

    /**
     * Execute queries to webkb, one with term name 'cat',
     * another with term name 'wn#cat'. Check both results for
     * ambiguity, number of terms returned, etc.
     */
    public TestWebKB2Source(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
    	ExamplesBackend backend = (ExamplesBackend) OntoramaConfig.getBackend();                
        backend.setCurrentExample(TestingUtils.getExampleByName("test webkb: cat"));
        sourceUri = backend.getSourceUri();
        webkbSource = new WebKB2Source();
    }

    /**
     *
     */
    public void testForUnexistingTerm() throws CancelledQueryException {
        try {
            query_nonExistentTerm = new Query("fjldsjf");
            sourceResult_nonExistentTerm = webkbSource.getSourceResult(sourceUri, query_nonExistentTerm);
            fail("Failed to catch expected SourceException (WebkbError)");
        } catch (SourceException e) {
        }
    }

    /**
     *
     */
    public void testAmbiguousQuery() throws SourceException, CancelledQueryException {
        query_cat = new Query("cat");
        sourceResult_cat = webkbSource.getSourceResult(sourceUri, query_cat);
        queryIsAmbiguous_cat = webkbSource.resultIsAmbiguous();
        numOfChoices_cat = webkbSource.getNumOfChoices();
        choicesList_cat = webkbSource.getChoicesList();

        List expectedChoices = new LinkedList();
        expectedChoices.add("wn#cat");
        expectedChoices.add("wn#true_cat");
        expectedChoices.add("wn#big_cat");
        expectedChoices.add("wn#Caterpillar");
        expectedChoices.add("wn#cat-o'-nine-tails");
        expectedChoices.add("wn#female_gossiper");
        expectedChoices.add("wn#guy");
        expectedChoices.add("wn#cat");

        checkChoicesNum("cat", 8, numOfChoices_cat);
        checkChoices("cat", expectedChoices, choicesList_cat);
        checkSourceResult_isSuccess(query_cat, false, sourceResult_cat);
        checkSourceResult_ReaderIsNull(query_cat, true, sourceResult_cat);
        checkSourceResult_NewQueryIsNull(query_cat, false, sourceResult_cat);
    }

    /**
     *
     */
    public void testQueryForAName() throws SourceException, CancelledQueryException {
        query_woodMouse = new Query("wood_mouse");
        sourceResult_woodMouse = webkbSource.getSourceResult(sourceUri, query_woodMouse);
        queryIsAmbiguous_woodMouse = webkbSource.resultIsAmbiguous();
        numOfChoices_woodMouse = webkbSource.getNumOfChoices();
        choicesList_woodMouse = webkbSource.getChoicesList();

        //List expectedChoices = new LinkedList();
        //expectedChoices.add("wn#wood_mouse");

        checkChoicesNum("wood_mouse", 0, numOfChoices_woodMouse);
        //checkChoices("wood_mouse", expectedChoices, choicesList_woodMouse);
        checkSourceResult_isSuccess(query_woodMouse, true, sourceResult_woodMouse);
        checkSourceResult_ReaderIsNull(query_woodMouse, false, sourceResult_woodMouse);
        checkSourceResult_NewQueryIsNull(query_woodMouse, true, sourceResult_woodMouse);
    }

    /**
     *
     */
    public void testForARPException() throws CancelledQueryException {
        try {
            query_dog = new Query("dog");
            sourceResult_dog = webkbSource.getSourceResult(sourceUri, query_dog);

            fail("Failed to throw expected exception");
        } catch (SourceException exc) {
            /*
            System.out.println("caught source exception for RDFError (hopefully)");
            System.out.println("error message is: " + exc.getMessage());
            System.out.println("locallised error message is: " + exc.getLocalizedMessage());
            */
        }
    }


    /**
     *
     */
    private void checkChoicesNum(String termName, int expectedChoicesNum, int returnedChoicesNum) {
        assertEquals("number of choices for term " + termName, expectedChoicesNum, returnedChoicesNum);
    }

    /**
     *
     */
    private void checkChoices(String termName, List expectedChoices, List returnedChoicesList) {

        assertEquals("size of choices list for term " + termName, expectedChoices.size(), returnedChoicesList.size());
        Iterator it = returnedChoicesList.iterator();
        while (it.hasNext()) {
            ontorama.model.graph.Node cur = (ontorama.model.graph.Node) it.next();
            assertEquals("choices list for " + termName + " should contain type " + cur.getName(),
                    true, expectedChoices.contains(cur.getName()));
        }
    }

    /**
     *
     */
    private void checkSourceResult_isSuccess(Query query, boolean expectedValue, SourceResult sourceResultToTest) {
        String message = "testing source result, method queryWasSuccess() for term " + query.getQueryTypeName();
        assertEquals(message, expectedValue, sourceResultToTest.queryWasSuccess());
    }

    /**
     *
     */
    private void checkSourceResult_ReaderIsNull(Query query, boolean readerIsNull, SourceResult sourceResultToTest) {
        String message = "testing source result, if reader is null for term " + query.getQueryTypeName();
        assertEquals(message, readerIsNull, (sourceResultToTest.getReader() == null));
    }

    /**
     *
     */
    private void checkSourceResult_NewQueryIsNull(Query query, boolean newQueryIsNull, SourceResult sourceResultToTest) {
        String message = "testing source result, if newQuery is null for term " + query.getQueryTypeName();
        assertEquals(message, newQueryIsNull, (sourceResultToTest.getNewQuery() == null));
    }

}