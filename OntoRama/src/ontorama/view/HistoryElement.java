package ontorama.view;

import ontorama.ontologyConfig.examplesConfig.OntoramaExample;
import ontorama.webkbtools.query.Query;

/**
 * <p>Title: </p>
 * <p>Description: Model History Element properties.</p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */
public class HistoryElement {

    private String _getRelationLinksList;
    private OntoramaExample _example;
    private Query _query;
    private String _menuDisplayName;

    /**
     * Create history element with given _getRelationLinksList, query and _example.
     * @param	_menuDisplayName - is a string that appears on the lable on corresponding menu
     * item for this _example. also may be used as an id.
     * @param	query - query corresponding to this history element
     * @param	_example - corresponding ontorama _example
     *
     */
    public HistoryElement(String menuDisplayName, Query query, OntoramaExample example) {
        _menuDisplayName = menuDisplayName;
        _query = query;
        _example = example;
        //System.out.println("history element constructor: " + this.toString());
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


    /**
     * Get _example corresponding to this history element
     */
    public OntoramaExample getExample() {
        return _example;
    }

    /**
     *
     */
    public String toString() {
        String str = "HistoryElement: ";
        str = str + "display name = " + _menuDisplayName;
        str = str + ", query = " + _query + ", example = " + _example;
        return str;
    }

}