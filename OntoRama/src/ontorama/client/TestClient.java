package ontorama.client;

/**
 * Title:
 * Description:
 * Copyright:    Copyright DSTC (c) 2001
 * Company:
 * @author
 * @version 1.0
 */
import java.util.Iterator;
import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;
import ontorama.webkbtools.datamodel.OntologyRelationType;
import ontorama.webkbtools.query.TypeQueryImplementation;
import ontorama.webkbtools.query.TypeQuery;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

/**
 * Test client class
 * Try to replicate client behaviour.
 */
public class TestClient {
  public TestClient() {
  }

  public static void main(String[] args)  {
    //String termName = "wn#cat";
    String termName = "wn#TrueCat";
    System.out.println ("starting TestClient...");

    try {
    	TypeQuery query = new TypeQueryImplementation();
        System.out.println ("created object TypeQueryImplementation query: " + query);
        Iterator ontIterator = query.getTypeRelative(termName);

        System.out.println("-------------Iterator returned--------" + ontIterator + "-------");
        while (ontIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
            System.out.println("---ontology type: \n" + ot);
        }
    }
    catch (ClassNotFoundException ce) {
        System.out.println("ClassNotFoundException: " + ce);
        System.exit(1);
    }
    catch (Exception e) {
        System.out.println("Exception: " + e);
    }
    System.out.println ("end of TestClient...");
  }

}
