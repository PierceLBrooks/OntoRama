package ontorama.test.webkbtools.datamodel;

import junit.framework.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.test.IteratorUtil;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class TestOntologyTypeImplementation extends TestCase {

  private String typeName;
  private String typeFullName;

  private OntologyType ontType;
  private OntologyType type1;
  private OntologyType type2;
  private OntologyType type3;

  private String propName1 = "Description";
  private String propName2 = "Creator";

  private String propValue1 = "description for ontType";
  private String propValue2 = "creator for ontType";
  private String propValue2_1 = "another creator for ontType";


  /**
   *
   */
  public TestOntologyTypeImplementation(String name) {
    super(name);
  }

  /**
   *
   */
  protected void setUp() throws NoSuchRelationLinkException,
                            NoSuchPropertyException {
    typeName = "ontTypeName";
    typeFullName = "fullNameForOntType";

    ontType = new OntologyTypeImplementation(typeName, typeFullName);
    type1 = new OntologyTypeImplementation("type1");
    type2 = new OntologyTypeImplementation("type2");
    type3 = new OntologyTypeImplementation("type3");

    ontType.addRelationType(type1, 1);
    ontType.addRelationType(type2, 2);
    ontType.addRelationType(type3, 1);

    ontType.addTypeProperty(propName1, propValue1);
    ontType.addTypeProperty(propName2, propValue2);
    ontType.addTypeProperty(propName2, propValue2_1);
  }

  /**
   *
   */
  public void testGetIterator () throws NoSuchRelationLinkException {
    assertEquals(2, IteratorUtil.getIteratorSize(ontType.getIterator(1)));
    assertEquals(1, IteratorUtil.getIteratorSize(ontType.getIterator(2)));
    assertEquals(0, IteratorUtil.getIteratorSize(ontType.getIterator(3)) );

    assertEquals("iterator for rel link 1 should contain type1",
          true, IteratorUtil.objectIsInIterator(type1, ontType.getIterator(1)));
    assertEquals("iterator for rel link 1 should contain type3",
          true, IteratorUtil.objectIsInIterator(type3, ontType.getIterator(1)));
    assertEquals("iterator for rel link 2 should contain type2",
          true, IteratorUtil.objectIsInIterator(type2, ontType.getIterator(2)));
  }

  /**
   *
   */
  public void testIsRelation () throws NoSuchRelationLinkException {
    assertEquals("ontType has relation link 1 to type1", true, ontType.isRelationType(type1,1));
    assertEquals("ontType has relation link 2 to type2", true, ontType.isRelationType(type2,2));
    assertEquals("ontType doesn't have relation link 3 to type3", false, ontType.isRelationType(type2,1));
  }

  /**
   *
   */
  public void testRemoveRelationForGivenRelLink () throws NoSuchRelationLinkException {
    ontType.removeRelation(1);
    assertEquals(0, IteratorUtil.getIteratorSize(ontType.getIterator(1)));
    ontType.removeRelation(2);
    assertEquals(0, IteratorUtil.getIteratorSize(ontType.getIterator(2)));
  }

  /**
   *
   */
  public void testRemoveRelationForGivenTypeAndLink () throws NoSuchRelationLinkException {
    ontType.removeRelation(type3,1);
    assertEquals("ontType should have iterator size for relationLink 1",
                  1, IteratorUtil.getIteratorSize(ontType.getIterator(1)));
    assertEquals("ontType iterator for relationLink1 shouldn't have type3 in it",
                  false, IteratorUtil.objectIsInIterator(type3, ontType.getIterator(1)));
    assertEquals("ontType iterator for relationLink1 should have type1 in it",
                  true, IteratorUtil.objectIsInIterator(type1, ontType.getIterator(1)));

  }

  /**
   *
   */
  public void testAddRelationType () throws NoSuchRelationLinkException {
    OntologyType type4 = new OntologyTypeImplementation("type4");
    OntologyType type5 = new OntologyTypeImplementation("type5");
    ontType.addRelationType(type4,1);
    ontType.addRelationType(type5,2);

    assertEquals(3, IteratorUtil.getIteratorSize(ontType.getIterator(1)));
    assertEquals(2, IteratorUtil.getIteratorSize(ontType.getIterator(2)));

    assertEquals("iterator for rel link 1 should contain type4",
              true, IteratorUtil.objectIsInIterator(type4, ontType.getIterator(1)));
    assertEquals("iterator for rel link 2 should contain type5",
              true, IteratorUtil.objectIsInIterator(type5, ontType.getIterator(2)));

  }

  /**
   *
   */
  public void testGetTypeProperty () throws NoSuchPropertyException {
    LinkedList expectedPropValue1 = new LinkedList();
    LinkedList expectedPropValue2 = new LinkedList();

    expectedPropValue1.add(propValue1);
    expectedPropValue2.add(propValue2);
    expectedPropValue2.add(propValue2_1);

    assertEquals(expectedPropValue1, ontType.getTypeProperty(propName1));
    assertEquals(expectedPropValue2, ontType.getTypeProperty(propName2));
  }

  /**
   *
   */
  public void testAddTypeProperty () throws NoSuchPropertyException {
    String propValue1 = "property1 for type1";
    String propValue1_1 = "another property1 for type1";
    String propValue2 = "property2 for type2";

    LinkedList expectedPropValue1 = new LinkedList ();
    expectedPropValue1.add(propValue1);
    expectedPropValue1.add(propValue1_1);

    LinkedList expectedPropValue2 = new LinkedList();
    expectedPropValue2.add(propValue2);

    type1.addTypeProperty(propName1, propValue1);
    type1.addTypeProperty(propName1, propValue1_1);
    type1.addTypeProperty(propName2, propValue2);

    assertEquals(expectedPropValue1, type1.getTypeProperty(propName1));
    assertEquals(expectedPropValue2, type1.getTypeProperty(propName2));
  }

  /**
   *
   */
  public void testGetName () {
    assertEquals("type name", typeName, ontType.getName());
  }

  /**
   *
   */
  public void testGetFullName() {
    assertEquals("type full name", typeFullName, ontType.getFullName());
  }

  /**
   *
   */
  public void testSetFullName (String fullName) {
    String testFullName = "anotherFullName";
    ontType.setFullName(testFullName);

    assertEquals("testing setFullName()", testFullName, ontType.getFullName());
  }


}