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

        //File file = new File(OntoramaConfig.sourceUri);
        //URL url = file.toURL();
        //System.out.println("file url = " + url);
        //System.exit(1);

    	TypeQuery query = new TypeQueryImplementation();
        System.out.println ("created object TypeQueryImplementation query: " + query);
        Iterator ontIterator = query.getTypeRelative(termName);
        //System.out.println("-------------Iterator returned---------------");
        while (ontIterator.hasNext()) {
            OntologyType ot = (OntologyTypeImplementation) ontIterator.next();
            //System.out.println("---ontology type: \n" + ot);
        }
    }
    catch (ClassNotFoundException ce) {
        System.out.println("ClassNotFoundException: " + ce);
        System.exit(1);
    }
    catch (Exception e) {
        System.out.println("Exception: " + e);
    }
    System.out.println ("end of TestClient...*********");

    /*

    OntologyType ot = new OntologyTypeImplementation("testType");



    try {



	    ot.setIterator(new OntologyTypeImplementation("childType1"), OntoramaConfig.SUBTYPE);

	    ot.setIterator(new OntologyTypeImplementation("childType2"), OntoramaConfig.SUBTYPE);



	    ot.setIterator(new OntologyTypeImplementation("superType"), OntoramaConfig.SUPERTYPE);

	    ot.setIterator(new OntologyTypeImplementation("partOfType"), OntoramaConfig.PARTOF);

	    ot.setIterator(new OntologyTypeImplementation("synonymType"), OntoramaConfig.SYNONYMTYPE);



	    System.out.println ("type = " + ot.toString());



		//Iterator iterator = ot.getIterator(OntoramaConfig.SUBTYPE);

        Iterator iterator = ot.getIterator(20);

		while (iterator.hasNext()) {

		  //System.out.println("iterator.next = " + (OntologyTypeImplementation)iterator.next());

		  Object obj = iterator.next();

		  if (obj instanceof OntologyRelationType) {

		    System.out.println("have OntologyRelationType");

		  }

		  else  {

		    System.out.println("have OntologyTypeImplementation");



		  }

		}



	    System.out.println("--------------------------");

	    OntologyType ort = new OntologyRelationType("testRelationType");

	    System.out.println ("type = " + ort.toString());

	    ort.setIterator(new OntologyRelationType("relationChildType1"), OntoramaConfig.SUBTYPE);

	    ort.setIterator(new OntologyTypeImplementation("relationChildType2"), OntoramaConfig.SUBTYPE);

	    Iterator iterator2 = ort.getIterator(OntoramaConfig.SUBTYPE);

	    while (iterator2.hasNext()) {

	      //System.out.println("iterator.next = " + (OntologyTypeImplementation)iterator.next());

	      Object obj2 = iterator2.next();



	      if (obj2 instanceof OntologyRelationType) {

	        System.out.println("have OntologyRelationType");

	      }

	      else  {

	        System.out.println("have OntologyTypeImplementation");

	      }

	    }

    }

    catch ( NoSuchRelationLinkException e) {

        System.out.println ("NoSuchRelationLinkException: " + e.getMessage());

    }

    */



  }

}
