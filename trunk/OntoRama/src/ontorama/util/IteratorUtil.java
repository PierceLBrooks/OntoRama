package ontorama.test;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class IteratorUtil {

    /**
     * calculate iterator size
     */
    public static int getIteratorSize (Iterator it) {
      int size = 0;
      while (it.hasNext()) {
        it.next();
        size++;
      }
      return size;
    }

    /**
     * check for given object in the given iterator
     */
    public static boolean objectIsInIterator (Object obj, Iterator it) {
      while (it.hasNext()) {
        Object cur = (Object) it.next();
        if (obj.equals(cur)) {
          return true;
        }
      }
      return false;
    }

  /**
   *
   */
  public static List copyIteratorToList (Iterator it) {
    LinkedList result = new LinkedList();
    while (it.hasNext()) {
      result.add(it.next());
    }
    return result;
  }

  /**
   * Find an ontology type in a list by given name
   *
   * Assumption: name is unique identifier for an ontology type
   */
  public static OntologyType getOntologyTypeFromList (String name, List list) {
    Iterator it = list.iterator();
    while (it.hasNext()) {
      OntologyType cur = (OntologyTypeImplementation) it.next();
      //System.out.println("cur = " + cur);
      if (cur.getName().equals(name)) {
        return cur;
      }
    }
    return null;
  }
}