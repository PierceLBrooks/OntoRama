package ontorama.webkbtools.query;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;

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
    private String typeName;

    /**
     * list of relation links for the ontology type typeName.

     */
    private List relationLinks = new LinkedList();

    /**
     * Constructor. Initialise query type name
     */
    public Query (String typeName) {
        this.typeName = typeName;
    }

    /**
     * Convinience Constructor.
     * Initialise query type name and relation links
     */
    public Query (String typeName,List relationLinks) {
        this.typeName = typeName;
        this.relationLinks = relationLinks;
    }

    /**
     * Get query type name
     * @return typeName
     */
    public String getQueryTypeName () {
        return typeName;
    }

    /**
     * Set Relation types iterator.
     * @param   List relationLinks
     */
    public void setRelationLinks (List relationLinks) {
        this.relationLinks = relationLinks;
    }

    /**
     * Get Relation types iterator
     * @return relationLinks Iterator
     */
    public Iterator getRelationLinksIterator () {
        return this.relationLinks.iterator();
    }

    /**
     *
     */
    public Collection getRelationLinksCollection () {
        return (Collection) this.relationLinks;
    }

}