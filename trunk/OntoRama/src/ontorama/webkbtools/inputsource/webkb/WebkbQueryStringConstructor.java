package ontorama.webkbtools.inputsource.webkb;

import ontorama.webkbtools.query.Query;
import ontorama.webkbtools.inputsource.UrlQueryStringConstructor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Hashtable;

/**
 * Description:   Implementation of QueryStringConstructorInterface that can
 * be used for WebKB-2 Ontology Server.
 *
 * Copyright:    Copyright (c) 2002
 * Company: DSTC
 * @version 1.0
 */
public class WebkbQueryStringConstructor {

    /**
     * Get query string for given query
     * @param query
     * @return  WebKB-2 query string ready to append to the cgi script uri.
     *
     * @todo 	we are ignoring UnsupportedEncodingException  because we assume
     * that at the time of writing this method we would have debugged this,
     * thus insuring that this encoding is correct. check if this assumption
     * is acceptable
     */
    public String getQueryString(Query query, String queryOutputFormat) {
        Hashtable paramTable = new Hashtable();
        paramTable.put("term", query.getQueryTypeName());
        paramTable.put("recursLink", ">");
        paramTable.put("format",queryOutputFormat);
        int queryDepth = query.getDepth();
        if (queryDepth > 0) {
            paramTable.put("depth", String.valueOf(queryDepth));
        }
        UrlQueryStringConstructor queryStringConstructor = new UrlQueryStringConstructor();
        String queryString = queryStringConstructor.getQueryString(paramTable);
        queryString = queryString + "&noHTML";
        return queryString;
    }
}