package ontorama.ui;

import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;


/**
 * <p>Title: </p>
 * <p>Description: Model History Element properties.</p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */
public class HistoryElement {

    private Query _query;
    private String _menuDisplayName;
    private QueryEngine _queryEngine;

    /**
     * Create history element with given _getRelationLinksList, query and _example.
     * @param	menuDisplayName - is a string that appears on the lable on corresponding menu
     * item for this _example. also may be used as an id.
     */
    public HistoryElement(String menuDisplayName, Query query, QueryEngine queryEngine) {
        _menuDisplayName = menuDisplayName;
        _query = query;
        _queryEngine = queryEngine;
    }

    /**
     * Get this element's name
     */
    public String getMenuDisplayName() {
        return _menuDisplayName;
    }

    /**
     * Get term name
     */
    public String getTermName() {
        return _query.getQueryTypeName();
    }

    /**
     * Get query
     */
    public Query getQuery() {
        return _query;
    }
    
    public QueryEngine getQueryEngine() {
    	return _queryEngine;
    }

    /**
     *
     */
    public String toString() {
        String str = "HistoryElement: ";
        str = str + "display name = " + _menuDisplayName;
        str = str + ", query = " + _query;
        return str;
    }

}