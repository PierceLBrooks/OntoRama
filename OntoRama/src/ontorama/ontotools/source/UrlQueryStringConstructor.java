/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 24/08/2002
 * Time: 11:32:04
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ontotools.source;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

public class UrlQueryStringConstructor {
    /**
     *
     * @param queryParamTable table containing query parameter names and values
     *                      keys - parameter names
     *                      values - parameter values
     * @return
     */
   public String getQueryString(Hashtable queryParamTable) {

        String encoding = "UTF-8";
        String queryString = "?";
        try {
            Enumeration en = queryParamTable.keys();
            while (en.hasMoreElements()) {
                String paramName = (String) en.nextElement();
                String paramValue = (String) queryParamTable.get(paramName);
                queryString = queryString + paramName + "=" + URLEncoder.encode(paramValue, encoding);
                //System.out.println("UrlQueryStringConstructor: paramName = " + paramName + ", paramValue = " + paramValue + " encoded paramValue = " + URLEncoder.encode(paramValue, encoding));
                if (en.hasMoreElements()) {
                    queryString = queryString + "&";
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("\nqueryString = " + queryString + "\n");
        return queryString;
    }
}