package ontorama.ontotools.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.EdgeType;

/**
 * Description: Query consist of query term and an iterator of relation links that we
 * are interested in (for example: subtype, memberOf).
 * This list holds Integers for the relation links defined in OntoramaConfig
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 * @see ontorama.OntoramaConfig
 */

public class Query {
    /**
     * name of ontology type we are quering for
     */
    private String _typeName;

    /**
     * list of relation links for the ontology type typeName.
     */
    private List _relationLinks = new LinkedList();

    /**
     * depth of recursive query
     */
    private int _depth = -1;
    

    /**
     * @todo not sure if this is a good idea - default constructor for reading files in when root couldn't be specified
     * before hand.
     */
    public Query () {
        _typeName = null;
    }

    /**
     * Constructor. Initialise query type name
     */
    public Query(String typeName) {
        _typeName = typeName;
    }

    /**
     * Convinience Constructor.
     * Initialise query type name and relation links
     */
    public Query(String typeName, List relationLinks) {
        _typeName = typeName;
        _relationLinks = relationLinks;
    }

    /**
     * Get query type name
     * @return typeName
     */
    public String getQueryTypeName() {
        return _typeName;
    }
    
    public void setQueryTypeName (String newName) {
    	_typeName = newName;
    }

    /**
     * Set Relation types iterator.
     * @param   relationLinks List
     */
    public void setRelationLinks(List relationLinks) {
        _relationLinks = relationLinks;
    }

    /**
     * Get Relation types iterator
     * @return relationLinks Iterator
     */
    public Iterator getRelationLinksIterator() {
        return _relationLinks.iterator();
    }

    /**
     * Get Relation types list
     * @return relationLinks List
     */
    public List getRelationLinksList() {
        return _relationLinks;
    }

    /**
     *
     */
    public Collection getRelationLinksCollection() {
        return (Collection) _relationLinks;
    }

    /**
     * set depth for recursive query
     */
    public void setDepth(int depth) {
        _depth = depth;
    }

    /**
     * get depth of recursive query
     */
    public int getDepth() {
        return _depth;
    }

	/**
	 *
	 */
	public String toString() {
		String str = "Query: ";
		str = str + "termName = " + _typeName + ", depth = " + _depth;
		str = str + ", relation links: " ;
		Iterator it = _relationLinks.iterator();
		while (it.hasNext()) {
			EdgeType edgeType = (EdgeType) it.next();
			str = str + edgeType.getName() + ", ";
		}
		return str;
	}

}