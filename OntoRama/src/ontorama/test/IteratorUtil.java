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
}