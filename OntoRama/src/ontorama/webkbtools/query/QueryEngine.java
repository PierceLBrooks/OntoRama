package ontorama.webkbtools.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import ontorama.OntoramaConfig;
import ontorama.webkbtools.util.NoSuchRelationLinkException;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

/**
 * Description: Query Engine will query Ontology Server with the given
 * query and return Result.
 *  Query consist of query term and an iterator of relation links that we
 * are interested in (for example: subtype, memberOf).
 *  Here is current process:
 * 1. query Ontology Server for ALL available relation links for this type.
 * 2. filter out relation links that we are not interested in by removing
 *      them from returned ontology types.
 *  This may not be the best way to handle this. Alternatively, we could:
 * - ask Ontology Server for relation links we are interested in. Currently
 * WebKB doesn't have this functionality.
 * - make a serious of queries to the Ontology Server each of those would be
 * a termName and ONE relation link we are interested in. This would mean
 * building up OntologyType from multiple queries to the WebKB.
 *  Currently we think, our solution is the most efficient and uncomplicated. Perhaps,
 * this will have to change in future.
 *
 * Copyright:    Copyright (c) 2001
 * Company: DSTC
 * @author
 * @version 1.0
 * @see ontorama.webkbtools.query.Query
 */

public class QueryEngine {

    /**
     * Holds Iterator returned from the Ontology Server
     * @see ontorama.webkbtools.datamodel.OntologyType
     */
    private Iterator typeRelativesIterator;

    /**
     * Query
     */
    private Query query;

    /**
     * Query Result
     */
    private QueryResult queryResult;


    /**
     * Constructor
     * @todo: think what to do with exception
     */
    public QueryEngine(Query query) throws Exception {
        this.query = query;
        try {
            // query WebKB
            TypeQuery typeQuery = new TypeQueryImplementation ();
            this.typeRelativesIterator = typeQuery.getTypeRelative(query.getQueryTypeName());

        }
        catch (ClassNotFoundException ce) {
          throw ce;
        }
        catch (Exception e) {
          throw e;
        }
    }

    /**
     * get result: Iterator of OntologyTypes
     * @return  iterator of OntologyTypes, each having only relations we are
     *  interested in (see this Class Description for more details).
     *  If iterator of wanted links is empty - assumption is that a user
     *  asked for ALL available links.
     */
    private Iterator getQueryTypesIterator() {
        // iterator of wanted links (links we are interested in)
        Iterator queryRelationLinks = this.query.getRelationLinksIterator();

        // wanted links iterator is empty
        if ( ! queryRelationLinks.hasNext()) {
            return this.typeRelativesIterator;
        }

        // get a set of all available relation links
        Set allLinks = OntoramaConfig.getRelationLinksSet();

        // remove all wanted relations from the allLinks set so
        // we end up with a list of unwanted relations
        allLinks.removeAll(query.getRelationLinksCollection());
        Iterator unwantedLinks = allLinks.iterator();

        // list to hold all updated types
        LinkedList updatedTypeRelativesList = new LinkedList();

        // iterate over each ontology type and remove all unwanted links
        while (this.typeRelativesIterator.hasNext()) {
            OntologyType ontType = (OntologyTypeImplementation) this.typeRelativesIterator.next();
            while (unwantedLinks.hasNext()) {
                Integer relationLink = (Integer) unwantedLinks.next();
                try {
                    ontType.removeRelation(relationLink.intValue());
                }
                catch (NoSuchRelationLinkException e ) {
                    // ignore it since we are removing this relation anyway
                }
            }
            // get iterator of unwanted links again since we used it up above
            unwantedLinks = allLinks.iterator();
            // add updated ontology type to the list
            updatedTypeRelativesList.add(ontType);
        }
        return updatedTypeRelativesList.iterator();
        //queryResult = new QueryResult(query, getQueryTypesList);
    }

    /**
     *
     */
     public QueryResult getQueryResult () {
        return ( new QueryResult(query, getQueryTypesIterator()));
     }

}