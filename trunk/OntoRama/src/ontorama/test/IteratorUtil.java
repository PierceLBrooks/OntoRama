package ontorama.test;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;


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


}