package ontorama.webkbtools.query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import java.io.Reader;
import java.io.IOException;

import ontorama.OntoramaConfig;
import ontorama.util.Debug;

import ontorama.webkbtools.query.parser.Parser;

import ontorama.webkbtools.inputsource.Source;
import ontorama.webkbtools.inputsource.SourceResult;

import ontorama.webkbtools.datamodel.OntologyType;
import ontorama.webkbtools.datamodel.OntologyTypeImplementation;

import ontorama.webkbtools.util.NoSuchRelationLinkException;
import ontorama.webkbtools.util.ParserException;
import ontorama.webkbtools.util.SourceException;
import ontorama.webkbtools.util.NoSuchTypeInQueryResult;
import ontorama.webkbtools.util.CancelledQueryException;

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

public class QueryEngine implements QueryEngineInterface {

    /**
     * Holds Iterator returned from the Ontology Server
     * @see ontorama.webkbtools.datamodel.OntologyType
     */
    private Collection typeRelativesCollection;

    /**
     * Query
     */
    private Query query;

    /**
     * Query Result
     */
    private QueryResult queryResult;


    /**
     * debug
     */
    Debug debug = new Debug (OntoramaConfig.DEBUG);


    /**
     * Execute a query to OntologyServer and get a query result
     */
    public QueryEngine(Query query) throws ParserException, IOException,
                      ClassNotFoundException, InstantiationException,
                      IllegalAccessException, SourceException, NoSuchTypeInQueryResult, CancelledQueryException {
        this.query = query;

        String queryUrl = OntoramaConfig.sourceUri;
        //String queryOutputFormat = OntoramaConfig.queryOutputFormat;

//        if (OntoramaConfig.isSourceDynamic) {
//        }

        Parser parser = (Parser) (Class.forName(OntoramaConfig.getParserPackageName()).newInstance());
        if (OntoramaConfig.DEBUG) {
            System.out.println("OntoramaConfig.sourceUri = " + OntoramaConfig.sourceUri);
            System.out.println("OntoramaConfig.queryOutputFormat = " + OntoramaConfig.queryOutputFormat);
            System.out.println("OntoramaConfig.parserPackageName = " + OntoramaConfig.getParserPackageName());
        }

        Source source = (Source) (Class.forName(OntoramaConfig.sourcePackageName).newInstance());

        this.queryResult = executeQuery(source, parser, queryUrl, query);

        System.out.println("QueryEngine: got query result from executeQuery method");
    }

    /**
     *
     */
    private QueryResult executeQuery (Source source, Parser parser, String queryUrl, Query query)
                                      throws NoSuchTypeInQueryResult, SourceException,
                                      ParserException, IOException, CancelledQueryException {
      QueryResult queryResult = null;
      Reader r = null;
      Query newQuery = null;
      System.out.println("\n\n\n Executing query for " + query.getQueryTypeName() + "\n");

      SourceResult sourceResult = source.getSourceResult(queryUrl, query);
      System.out.println(sourceResult.toString());
      if (! sourceResult.queryWasSuccess()) {
        //System.out.println("successfull: NO");
        newQuery = sourceResult.getNewQuery();
        queryResult = executeQuery(source, parser, queryUrl, newQuery);
        //System.out.println("executeQuery returned from recursive");
      }
      else {
        //System.out.println("successfull: YES");
        r = sourceResult.getReader();
        this.typeRelativesCollection = parser.getOntologyTypeCollection(r);
        r.close();
        String newTermName = checkResultSetContainsSearchTerm(this.typeRelativesCollection, query.getQueryTypeName());
        if (! newTermName.equals(query.getQueryTypeName())) {
          query = new Query(newTermName, query.getRelationLinksList());
        }
        //System.out.println("query result list = " + getQueryTypesList().size());
        queryResult = new QueryResult(query, getQueryTypesList().iterator());

        //System.out.println("Returning query result for " + query.getQueryTypeName() + "\n");
      }
      //System.out.println("Returning query result " + queryResult + "\n");
      return queryResult;
    }

    /**
     * Check if result set contains term we searched for.
     * Check for exact term and also for a variant of it as if it was
     * a webkb identifier. For example, if we searched for 'wood_mouse',
     * result set will contain 'wn#wood_mouse'. Therefore, we check if result
     * collection contains anything ending with '#wood_mouse'. If this is the
     * case - we return this new term so we can update the original query term
     * to reflect this.
     *
     * @param resultSet - collection of ontology types returned by source.
     * @param termName  - search term
     * @return term: the same as search term OR updated term corresponding to
     * the ontology type name matching this term.
     * @todo  may not be a good idea to do this here, since identifiers with hashes
     * may be specific to WebKB. Maybe should do some checking in WebKB2Source.
     */
    private String checkResultSetContainsSearchTerm (Collection resultSet, String termName)
                            throws NoSuchTypeInQueryResult {
      boolean found = false;
      Iterator it = resultSet.iterator();
      String newTermName = termName;
      while (it.hasNext()) {
        OntologyType cur = (OntologyType) it.next();
        String termIdentifierSuffix = "#" + termName;
        //System.out.println("cur = " + cur.getName() + " checking against '" + termName + ", and '" + termIdentifierSuffix);
        if (cur.getName().equals(termName)) {
          found = true;
        }
        if (cur.getName().endsWith(termIdentifierSuffix)) {
          found = true;
          newTermName = cur.getName();
        }
      }
      if (!found) {
        throw new NoSuchTypeInQueryResult(termName);
      }
      return newTermName;
    }


    /**
     * get result: collection of OntologyTypes
     * @return  collection of OntologyTypes, each having only relations we are
     *  interested in (see this Class Description for more details).
     *  If iterator of wanted links is empty - assumption is that a user
     *  asked for ALL available links.
     */
    private Collection getQueryTypesList() {
        // iterator of wanted links (links we are interested in)
        Iterator queryRelationLinks = this.query.getRelationLinksIterator();

        // wanted links iterator is empty
        if ( ! queryRelationLinks.hasNext()) {
            System.out.println("query relation links iterator is empty, so no need to work on iterator");
            return this.typeRelativesCollection;
        }

        // get a set of all available relation links
        // copy them into allLinksCopy as otherwise once  we
        // remove unwanted links - we will modify the static var
        // in OntoramaConfig and this is not what we want to do.
        Set allLinks = OntoramaConfig.getRelationLinksSet();
        LinkedList allLinksCopy = new LinkedList();
        Iterator allLinksIterator = allLinks.iterator();
        while (allLinksIterator.hasNext()) {
          Integer nextRelLink = (Integer) allLinksIterator.next();
          allLinksCopy.add(nextRelLink);
        }

        // remove all wanted relations from the allLinks set so
        // we end up with a list of unwanted relations
        //allLinks.removeAll(query.getRelationLinksCollection());
        allLinksCopy.removeAll(query.getRelationLinksCollection());
        debug.message("QueryEngine","getQueryTypesIterator()","allLinks = " + OntoramaConfig.getRelationLinksSet());
        debug.message("QueryEngine","getQueryTypesIterator()","unwantedLinksList = " + allLinksCopy);
        Iterator unwantedLinks = allLinksCopy.iterator();


        // list to hold all updated types
        LinkedList updatedTypeRelativesList = new LinkedList();

        // iterate over each ontology type and remove all unwanted links
        Iterator typeRelativesIterator = this.typeRelativesCollection.iterator();
        while (typeRelativesIterator.hasNext()) {
            OntologyType ontType = (OntologyTypeImplementation) typeRelativesIterator.next();
            //System.out.println("*** ontType before link removed: " + ontType);
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
            unwantedLinks = allLinksCopy.iterator();
            // add updated ontology type to the list
            //System.out.println("*** ontType after link removed: " + ontType);
            updatedTypeRelativesList.add(ontType);
        }
        //System.out.println("result = " + updatedTypeRelativesList);
        return updatedTypeRelativesList;
    }

    /**
     *
     */
     public QueryResult getQueryResult () {
        return ( this.queryResult);
     }

}