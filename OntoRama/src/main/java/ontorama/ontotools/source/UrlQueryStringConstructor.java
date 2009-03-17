package ontorama.ontotools.source;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class UrlQueryStringConstructor {
    /**
     * @param queryParamTable table containing query parameter names and values
     *                      keys - parameter names
     *                      values - parameter values
     * @return
     */
   public String getQueryString(Map<String,String> queryParamTable) {

        String encoding = "UTF-8";
        String queryString = "?";
        boolean firstEntry = true;
        for (Entry<String, String> entry : queryParamTable.entrySet()) {
	        try {
	        	if(!firstEntry) {
                    queryString = queryString + "&";
	        	}
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                queryString = queryString + paramName + "=" + URLEncoder.encode(paramValue, encoding);
                firstEntry = false;
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
        }
        return queryString;
    }
}
