package ontorama.webkbtools.inputsource.webkb;

import ontorama.webkbtools.query.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

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
        String termName = query.getQueryTypeName();
        Iterator iterator = null;

        String encoding = "UTF-8";

        //queryParamTerm = termName;
        //queryParamRecursLink = ">";

        String queryString = "?";
        try {
            queryString = queryString + "term=" + URLEncoder.encode(termName, encoding);
            //queryString = queryString + "term=" + URLEncoder.encode("wn#cat");
            queryString = queryString + "&recursLink=" + URLEncoder.encode(">", encoding);
            ;
            queryString = queryString + "&format=" + URLEncoder.encode(queryOutputFormat, encoding);
            int queryDepth = query.getDepth();
            if (queryDepth > 0) {
                queryString = queryString + "&depth=" + URLEncoder.encode(String.valueOf(queryDepth), encoding);
            }
            queryString = queryString + "&noHTML";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("\nqueryString = " + queryString + "\n");
        return queryString;
    }
}