package ontorama.ontotools.query;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.OntoramaConfig;
import ontorama.backends.Backend;
import ontorama.model.graph.Edge;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.ParserException;
import ontorama.ontotools.SourceException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.source.Source;
import ontorama.ontotools.source.SourceResult;
import ontorama.util.Debug;

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
 * @see ontorama.ontotools.query.Query
 */

public class QueryEngine implements QueryEngineInterface {

    private ParserResult _parserResult;
    private List _resultNodesList;
    private List _resultEdgesList;

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
    //Debug debug = new Debug(OntoramaConfig.DEBUG);
    Debug debug = new Debug(true);


    /**
     * Execute a query to OntologyServer and get a query result
     */
    public QueryEngine(Query query) throws ParserException, IOException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException, SourceException, NoSuchTypeInQueryResult, CancelledQueryException {
        this.query = query;
        Backend backend = OntoramaConfig.getBackend();
        String queryUrl = backend.getSourceUri();
        Parser parser = (Parser) (Class.forName(backend.getParser()).newInstance());
        if (OntoramaConfig.DEBUG) {
            System.out.println("OntoramaConfig.sourceUri = " + backend.getSourceUri());
            System.out.println("OntoramaConfig.parserPackageName = " + backend.getParser());
        }
        Source source = (Source) (Class.forName(backend.getSourcePackageName()).newInstance());

        this.queryResult = executeQuery(source, parser, queryUrl, query);
    }

    /**
     * alternative constructor allowing to bypass config manager's settings and specify where to find
     * all needed packages.
     * @todo probably don't need this, just added this in order to refactor p2p backend. consider removing later (
     * probably need better backends schema allowing to set config for each backend).
     * @param sourcePackageName
     * @param parserPackageName
     * @param ontologySourceUri
     * @param query
     */
    public QueryEngine (String sourcePackageName, String parserPackageName, String ontologySourceUri, Query query)
                                                    throws ParserException, IOException,
                                                    ClassNotFoundException, InstantiationException,
                                                    IllegalAccessException, SourceException,
                                                    NoSuchTypeInQueryResult, CancelledQueryException {
        this.query = query;
        Parser parser = (Parser) (Class.forName(parserPackageName)).newInstance();
        Source source = (Source) (Class.forName(sourcePackageName).newInstance());
        this.queryResult = executeQuery(source, parser, ontologySourceUri, query);
    }

    /**
     *
     */
    private QueryResult executeQuery(Source source, Parser parser, String queryUrl, Query query)
            throws NoSuchTypeInQueryResult, SourceException,
            ParserException, IOException, CancelledQueryException {
        QueryResult queryResult = null;
        Reader r = null;
        Query newQuery = null;

        SourceResult sourceResult = source.getSourceResult(queryUrl, query);
        if (!sourceResult.queryWasSuccess()) {
            newQuery = sourceResult.getNewQuery();
            queryResult = executeQuery(source, parser, queryUrl, newQuery);
        } else {
            r = sourceResult.getReader();
            //this.typeRelativesCollection = parser.getOntologyTypeCollection(r);
            _parserResult = parser.getResult(r);
            r.close();
            if (query.getQueryTypeName()!= null) {
                String newTermName = checkResultSetContainsSearchTerm(_parserResult.getNodesList(), query.getQueryTypeName());
                if (!newTermName.equals(query.getQueryTypeName())) {
                    query = new Query(newTermName, query.getRelationLinksList());
                }
            }
            filterUnwantedEdges();
            queryResult = new QueryResult(query, _resultNodesList, _resultEdgesList);

        }
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
     * @param termName  - search term
     * @return term: the same as search term OR updated term corresponding to
     * the ontology type name matching this term.
     * @todo  may not be a good idea to do this here, since identifiers with hashes
     * may be specific to WebKB. Maybe should do some checking in WebKB2Source.
     */
    private String checkResultSetContainsSearchTerm(List nodesList, String termName)
            throws NoSuchTypeInQueryResult {
        boolean found = false;
        Iterator it = nodesList.iterator();
        String newTermName = termName;
        while (it.hasNext()) {
            Node cur = (Node) it.next();
            String termIdentifierSuffix = "#" + termName;
            if (cur.getIdentifier().equals(termName)) {
                found = true;
                newTermName = cur.getName();
            }
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
     * Method will build list of nodes and list of _graphEdges, including only _graphEdges we are
     *  interested in (see this Class Description for more details).
     *  If iterator of wanted links is empty - assumption is that a user
     *  asked for ALL available links/_graphEdges.
     *
     * @todo query should have as a links set not Integer list, but edge types list.
     */
    private void filterUnwantedEdges() {
        List wantedLinks = this.query.getRelationLinksList();
        // iterator of wanted links (links we are interested in)
        Iterator queryRelationLinks = wantedLinks.iterator();

        // wanted links iterator is empty
        if (!queryRelationLinks.hasNext()) {
            _resultNodesList = _parserResult.getNodesList();
            _resultEdgesList = _parserResult.getEdgesList();
            return;
        }

        _resultNodesList = new LinkedList();
        _resultEdgesList = new LinkedList();
        List nodes = _parserResult.getNodesList();

        Iterator edgesIt = _parserResult.getEdgesList().iterator();
        while (edgesIt.hasNext()) {
            Edge curEdge = (Edge) edgesIt.next();
            if (wantedLinks.contains(curEdge.getEdgeType())) {
                _resultEdgesList.add(curEdge);
                Node fromNode = curEdge.getFromNode();
                Node toNode = curEdge.getToNode();
                if (!_resultNodesList.contains(fromNode)) {
                     _resultNodesList.add(fromNode);
                    nodes.remove(fromNode);
                }
                if (!_resultNodesList.contains(toNode)) {
                    _resultNodesList.add(toNode);
                    nodes.remove(toNode);
                }
            }
        }
        
        Iterator nodesIt = nodes.iterator();
        while (nodesIt.hasNext()) {
            Node curNode = (Node) nodesIt.next();
            _resultNodesList.add(curNode);
        }
    }

    /**
     *
     */
    public QueryResult getQueryResult() {
        return (this.queryResult);
    }

}