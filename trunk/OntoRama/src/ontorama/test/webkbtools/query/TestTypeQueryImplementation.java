package ontorama.test.webkbtools.query;

import junit.framework.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import ontorama.test.IteratorUtil;

import ontorama.OntoramaConfig;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

import ontorama.webkbtools.query.TypeQuery;
import ontorama.webkbtools.query.TypeQueryBase;
import ontorama.webkbtools.query.TypeQueryImplementation;
import ontorama.webkbtools.query.Query;

import ontorama.webkbtools.util.NoSuchPropertyException;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 *
 * Test if returned iterator of ontology types contains the same
 * types as expected. Using example of wn#wood_mouse to test this.
 */

public class TestTypeQueryImplementation extends TestCase {

  private String queryTerm;

  private TypeQuery typeQuery;

  private List expectedTypesList = new LinkedList();

  /**
   *
   */
  public TestTypeQueryImplementation (String name)  throws Exception{
    super(name);
  }

  /**
   *
   */
  protected void setUp() throws Exception {
     OntoramaConfig.loadAllConfig("examples/test/data/examplesConfig.xml",
                            "ontorama.properties","examples/test/data/config.xml");


    queryTerm = OntoramaConfig.ontologyRoot;

    typeQuery = new TypeQueryImplementation();

    // expected ontology types:
    OntologyType type_PygmyMouse = new OntologyTypeImplementation("wn#PygmyMouse");
    type_PygmyMouse.addTypeProperty("Synonym", "pygmy_mouse");
    type_PygmyMouse.addTypeProperty("Synonym", "Baiomys_taylori");
    type_PygmyMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_PygmyMouse.addTypeProperty("Description", "very small dark grayish brown mouse resembling a house mouse; of Texas and Mexico");

    expectedTypesList.add(type_PygmyMouse);

    OntologyType type_CottonMouse = new OntologyTypeImplementation("wn#CottonMouse");
    type_CottonMouse.addTypeProperty("Synonym", "cotton_mouse");
    type_CottonMouse.addTypeProperty("Synonym", "Peromyscus_gossypinus");
    type_CottonMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_CottonMouse.addTypeProperty("Description", "large dark mouse of southeastern United States");

    expectedTypesList.add(type_CottonMouse);

    OntologyType type_CactusMouse = new OntologyTypeImplementation("wn#CactusMouse");
    type_CactusMouse.addTypeProperty("Synonym", "cactus_mouse");
    type_CactusMouse.addTypeProperty("Synonym", "Peromyscus_eremicus");
    type_CactusMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_CactusMouse.addTypeProperty("Description", "burrowing mouse of desert areas of southwestern United States");

    expectedTypesList.add(type_CactusMouse);

    OntologyType type_DeerMouse = new OntologyTypeImplementation("wn#DeerMouse");
    type_DeerMouse.addTypeProperty("Synonym", "deer_mouse");
    type_DeerMouse.addTypeProperty("Synonym", "Peromyscus_maniculatus");
    type_DeerMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_DeerMouse.addTypeProperty("Description", "brownish New World mouse; most widely distributed member of the genus");

    expectedTypesList.add(type_DeerMouse);

    OntologyType type_White_footedMouse = new OntologyTypeImplementation("wn#White-footedMouse");
    type_White_footedMouse.addTypeProperty("Synonym", "white-footed_mouse");
    type_White_footedMouse.addTypeProperty("Synonym", "vesper_mouse");
    type_White_footedMouse.addTypeProperty("Synonym", "Peromyscus_leucopus");
    type_White_footedMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_White_footedMouse.addTypeProperty("Description", "American woodland mouse with white feet and underparts");

    expectedTypesList.add(type_White_footedMouse);

    OntologyType type_WoodMouse = new OntologyTypeImplementation("wn#WoodMouse");
    type_WoodMouse.addTypeProperty("Synonym", "wood_mouse");
    type_WoodMouse.addTypeProperty("Creator", "http://www.cogsci.princeton.edu/~wn/");
    type_WoodMouse.addTypeProperty("Description", "any of various New World woodland mice");
    type_WoodMouse.addRelationType(type_PygmyMouse, 1);
    type_WoodMouse.addRelationType(type_CottonMouse,1);
    type_WoodMouse.addRelationType(type_White_footedMouse,1);
    type_WoodMouse.addRelationType(type_CactusMouse,1);
    type_WoodMouse.addRelationType(type_DeerMouse,1);

    expectedTypesList.add(type_WoodMouse);

    OntologyType type_Mouse = new OntologyTypeImplementation("wn#Mouse");
    type_Mouse.addRelationType(type_WoodMouse,1);

    expectedTypesList.add(type_Mouse);

  }

  /**
   *
   */
  public void testGetTypeRelative () throws Exception {

    int queryIteratorSize = IteratorUtil.getIteratorSize(typeQuery.getTypeRelative(queryTerm));
    int expectedIteratorSize = expectedTypesList.size();

    assertEquals(expectedIteratorSize, queryIteratorSize);

    Iterator queryIterator = typeQuery.getTypeRelative(queryTerm);

    while (queryIterator.hasNext()) {
      OntologyType cur = (OntologyType) queryIterator.next();
      //System.out.println("---" + cur);
      findOntologyTypeInIterator(cur, expectedTypesList.iterator());

    }

  }

    /**
     *
     */
    public void findOntologyTypeInIterator (OntologyType type, Iterator it)
                                throws NoSuchPropertyException, NoSuchRelationLinkException {
        while (it.hasNext()) {
            OntologyType cur = (OntologyType) it.next();
            String curName = cur.getName();
            if (curName.equals(type.getName())) {
                // compare properties
                Enumeration e = OntoramaConfig.getConceptPropertiesTable().keys();
                while (e.hasMoreElements()) {
                    String propName = (String) e.nextElement();
                    List curTypePropValue = cur.getTypeProperty(propName);
                    List expectedTypePropValue = type.getTypeProperty(propName);
                    assertEquals(expectedTypePropValue, curTypePropValue);
                }
                // compare relation links
                Set relLinksSet = OntoramaConfig.getRelationLinksSet();
                Iterator relLinksIterator = relLinksSet.iterator();
                while (relLinksIterator.hasNext()) {
                    int relLink = ((Integer) relLinksIterator.next()).intValue();
                    Iterator curTypeRel = cur.getIterator(relLink);
                    Iterator expectedTypeRel = type.getIterator(relLink);
                    //// what should happen here???
                    assertEquals(IteratorUtil.getIteratorSize(expectedTypeRel), IteratorUtil.getIteratorSize(curTypeRel));
                }
            }
        }
    }

}