package ontorama.webkbtools.query;

import junit.framework.TestCase;
import ontorama.OntoramaConfig;
import ontorama.util.IteratorUtil;
import ontorama.util.TestingUtils;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description:
 * Test if returned iterator of ontology types contains the same
 * types as expected. Using example of wn#wood_mouse to test this.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestQueryEngine extends TestCase {

    private String queryTerm;
    private List relationLinksList;

    private Query query1;
    private QueryEngine queryEngine1;
    private QueryResult queryResult1;
    private List queryResultList1;

    private Query query2;
    private QueryEngine queryEngine2;
    private QueryResult queryResult2;
    private List queryResultList2;

    private OntologyType testType_chair;


    private String queryTerm_cat;
    private List relationLinksList_cat;
    private Query query_cat;
    private QueryEngine queryEngine_cat;
    private QueryResult queryResult_cat;
    private List queryResultList_cat;

//  private TypeQuery typeQuery;
//
//  private List expectedTypesList = new LinkedList();



    /**
     *
     */
    public TestQueryEngine(String name) {
        super(name);
    }

    /**
     *
     */
    protected void setUp() throws NoSuchPropertyException,
            NoSuchRelationLinkException, Exception {
        OntoramaConfig.loadAllConfig("examples/test/data/testCase-examplesConfig.xml",
                "ontorama.properties", "examples/test/data/testCase-config.xml");
        OntoramaConfig.setCurrentExample(TestingUtils.getExampleByName("testCase"));

        queryTerm = OntoramaConfig.ontologyRoot;
        relationLinksList = new LinkedList();
        relationLinksList.add(new Integer(3));
        relationLinksList.add(new Integer(6));
        relationLinksList.add(new Integer(9));

        query1 = new Query(queryTerm);
        queryEngine1 = new QueryEngine(query1);
        queryResult1 = queryEngine1.getQueryResult();
        System.out.println("queryResult1 = " + queryResult1);
        queryResultList1 = IteratorUtil.copyIteratorToList(queryResult1.getOntologyTypesIterator());

        query2 = new Query(queryTerm, relationLinksList);
        queryEngine2 = new QueryEngine(query2);
        queryResult2 = queryEngine2.getQueryResult();
        queryResultList2 = IteratorUtil.copyIteratorToList(queryResult2.getOntologyTypesIterator());

        // load ambiguous case
//    List examplesList = OntoramaConfig.getExamplesList();
//    for (int i = 0; i < examplesList.size(); i++) {
//      OntoramaExample curExample = (OntoramaExample) examplesList.get(i);
//      if (curExample.getName().equals("test webkb: cat")) {
//        System.out.println("GOT cat example");
//        OntoramaConfig.setCurrentExample(curExample);
//      }
//    }
//    queryTerm_cat = OntoramaConfig.ontologyRoot;
//    relationLinksList_cat = new LinkedList ();
//
//    query_cat = new Query(queryTerm_cat);
//    queryEngine_cat = new QueryEngine(query_cat);
//    queryResult_cat = queryEngine_cat.getQueryResult();
//    System.out.println("queryResult_cat = " + queryResult_cat);
//    queryResultList_cat = IteratorUtil.copyIteratorToList(queryResult_cat.getOntologyTypesIterator());






//    OntoramaConfig.loadAllConfig("examples/test/data/examplesConfig.xml",
//                            "ontorama.properties","examples/test/data/config.xml");
//
//
//    queryTerm = OntoramaConfig.ontologyRoot;
//
//    typeQuery = new TypeQueryImplementation();
//
//    // expected ontology types:
//    OntologyType type_PygmyMouse = new OntologyTypeImplementation("wn#PygmyMouse");
//    type_PygmyMouse.addTypeProperty("Synonym", "pygmy_mouse");
//    type_PygmyMouse.addTypeProperty("Synonym", "Baiomys_taylori");
//    type_PygmyMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    type_PygmyMouse.addTypeProperty("Description", "very small dark grayish brown mouse resembling a house mouse; of Texas and Mexico");
//
//    expectedTypesList.add(type_PygmyMouse);
//
//    OntologyType type_CottonMouse = new OntologyTypeImplementation("wn#CottonMouse");
//    type_CottonMouse.addTypeProperty("Synonym", "cotton_mouse");
//    type_CottonMouse.addTypeProperty("Synonym", "Peromyscus_gossypinus");
//    type_CottonMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    //type_CottonMouse.addTypeProperty("Creator", "~wn");
//
//    type_CottonMouse.addTypeProperty("Description", "large dark mouse of southeastern United States");
//
//    expectedTypesList.add(type_CottonMouse);
//
//    OntologyType type_CactusMouse = new OntologyTypeImplementation("wn#CactusMouse");
//    type_CactusMouse.addTypeProperty("Synonym", "cactus_mouse");
//    type_CactusMouse.addTypeProperty("Synonym", "Peromyscus_eremicus");
//    type_CactusMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    type_CactusMouse.addTypeProperty("Description", "burrowing mouse of desert areas of southwestern United States");
//
//    expectedTypesList.add(type_CactusMouse);
//
//    OntologyType type_DeerMouse = new OntologyTypeImplementation("wn#DeerMouse");
//    type_DeerMouse.addTypeProperty("Synonym", "deer_mouse");
//    type_DeerMouse.addTypeProperty("Synonym", "Peromyscus_maniculatus");
//    type_DeerMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    type_DeerMouse.addTypeProperty("Description", "brownish New World mouse; most widely distributed member of the genus");
//
//    expectedTypesList.add(type_DeerMouse);
//
//    OntologyType type_White_footedMouse = new OntologyTypeImplementation("wn#White-footedMouse");
//    type_White_footedMouse.addTypeProperty("Synonym", "white-footed_mouse");
//    type_White_footedMouse.addTypeProperty("Synonym", "vesper_mouse");
//    type_White_footedMouse.addTypeProperty("Synonym", "Peromyscus_leucopus");
//    type_White_footedMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    type_White_footedMouse.addTypeProperty("Description", "American woodland mouse with white feet and underparts");
//
//    expectedTypesList.add(type_White_footedMouse);
//
//    OntologyType type_WoodMouse = new OntologyTypeImplementation("wn#WoodMouse");
//    type_WoodMouse.addTypeProperty("Synonym", "wood_mouse");
//    type_WoodMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
//    type_WoodMouse.addTypeProperty("Description", "any of various New World woodland mice");
//    type_WoodMouse.addRelationType(type_PygmyMouse, 1);
//    type_WoodMouse.addRelationType(type_CottonMouse,1);
//    type_WoodMouse.addRelationType(type_White_footedMouse,1);
//    type_WoodMouse.addRelationType(type_CactusMouse,1);
//    type_WoodMouse.addRelationType(type_DeerMouse,1);
//
//    expectedTypesList.add(type_WoodMouse);
//
//    OntologyType type_Mouse = new OntologyTypeImplementation("wn#Mouse");
//    type_Mouse.addRelationType(type_WoodMouse,1);
//
//    expectedTypesList.add(type_Mouse);
//
//    OntologyType type_rdf_Class = new  OntologyTypeImplementation("PR-rdf-schema-19990303#Class");
//    type_CottonMouse.addRelationType(type_rdf_Class,6);
//    type_CactusMouse.addRelationType(type_rdf_Class,6);
//    type_DeerMouse.addRelationType(type_rdf_Class, 6);
//    type_PygmyMouse.addRelationType(type_rdf_Class, 6);
//    type_White_footedMouse.addRelationType(type_rdf_Class, 6);
//    type_WoodMouse.addRelationType(type_rdf_Class, 6);
//
//
//
//    expectedTypesList.add(type_rdf_Class);
    }

    /**
     *
     */
    public void testGetQueryResultForQuery1() throws NoSuchRelationLinkException {
        assertEquals("size of query result iterator for query1", 14, queryResultList1.size());
        testType_chair = IteratorUtil.getOntologyTypeFromList("test#Chair", queryResultList1);
        checkRelationIteratorSize("query1", testType_chair, 1, 1);
        checkRelationIteratorSize("query1", testType_chair, 2, 1);
        checkRelationIteratorSize("query1", testType_chair, 3, 0);
        checkRelationIteratorSize("query1", testType_chair, 4, 2);
        checkRelationIteratorSize("query1", testType_chair, 5, 1);
        checkRelationIteratorSize("query1", testType_chair, 6, 0);
        checkRelationIteratorSize("query1", testType_chair, 7, 1);
        checkRelationIteratorSize("query1", testType_chair, 8, 1);
        checkRelationIteratorSize("query1", testType_chair, 9, 1);
        checkRelationIteratorSize("query1", testType_chair, 10, 2);
        checkRelationIteratorSize("query1", testType_chair, 11, 1);
        checkRelationIteratorSize("query1", testType_chair, 12, 0);
    }

    /**
     *
     */
    public void testGetQueryResultForQuery2() throws NoSuchRelationLinkException {
        assertEquals("size of query result iterator for query2", 14, queryResultList2.size());
        testType_chair = IteratorUtil.getOntologyTypeFromList("test#Chair", queryResultList2);
        checkRelationIteratorSize("query2", testType_chair, 3, 0);
        checkRelationIteratorSize("query2", testType_chair, 6, 0);
        checkRelationIteratorSize("query2", testType_chair, 9, 1);

    }

    /**
     *
     */
    private void checkRelationIteratorSize(String queryName, OntologyType ontType,
                                           int relLinkId, int expectedIteratorSize)
            throws NoSuchRelationLinkException {
        String message = "query " + queryName + ", iterator size for ";
        message = message + " ontology type " + ontType.getName() + " and relation link ";
        message = message + relLinkId;
        assertEquals(message, expectedIteratorSize, IteratorUtil.getIteratorSize(ontType.getIterator(relLinkId)));
    }


//  /**
//   *
//   */
//  public void testGetTypeRelative () throws Exception {
//
//    int queryIteratorSize = IteratorUtil.getIteratorSize(typeQuery.getTypeRelative(queryTerm));
//    int expectedIteratorSize = expectedTypesList.size();
//
//    assertEquals("results iterator size", expectedIteratorSize, queryIteratorSize);
//
//    Iterator queryIterator = typeQuery.getTypeRelative(queryTerm);
//
//    while (queryIterator.hasNext()) {
//      OntologyType cur = (OntologyType) queryIterator.next();
//      //System.out.println("---" + cur);
//      findOntologyTypeInIterator(cur, expectedTypesList.iterator());
//
//    }
//
//  }
//
//    /**
//     *
//     */
//    public void findOntologyTypeInIterator (OntologyType type, Iterator it)
//                                throws NoSuchPropertyException, NoSuchRelationLinkException {
//        while (it.hasNext()) {
//            OntologyType cur = (OntologyType) it.next();
//            String curName = cur.getName();
//            if (curName.equals(type.getName())) {
//                // compare properties
//                Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
//                while (e.hasMoreElements()) {
//                    String propName = (String) e.nextElement();
//                    List curTypePropValue = cur.getTypeProperty(propName);
//                    List expectedTypePropValue = type.getTypeProperty(propName);
//                    assertEquals("property " + propName + " for ontology type " + curName,
//                                 curTypePropValue, expectedTypePropValue);
//                }
//                // compare relation links
//                Set relLinksSet = OntoramaConfig.getRelationLinksSet();
//                Iterator relLinksIterator = relLinksSet.iterator();
//                while (relLinksIterator.hasNext()) {
//                    int relLink = ((Integer) relLinksIterator.next()).intValue();
//                    Iterator curTypeRel = cur.getIterator(relLink);
//                    Iterator expectedTypeRel = type.getIterator(relLink);
//                    //// what should happen here???
//                    assertEquals("iterator size for relation link id=" + relLink +
//                              " for ontology type " + curName,
//                              IteratorUtil.getIteratorSize(expectedTypeRel),
//                              IteratorUtil.getIteratorSize(curTypeRel));
//                }
//            }
//        }
//    }

}