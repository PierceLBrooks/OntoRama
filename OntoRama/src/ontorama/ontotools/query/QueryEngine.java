package ontorama.ontotools.query;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.Edge;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;
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

public class QueryEngine {

    private ParserResult _parserResult;
    private List _resultNodesList;
    private List _resultEdgesList;

	private Parser parser;
	private Source source;
	private String sourceUri;

    /**
     * debug
     */
    //Debug debug = new Debug(OntoramaConfig.DEBUG);
    Debug debug = new Debug(true);

    /**
     * Execute a query to OntologyServer and get a query resul
     */
    public QueryEngine (String sourcePackageName, String parserPackageName, String ontologySourceUri)
                                                    throws QueryFailedException {
        this.sourceUri = ontologySourceUri;
        try {
	        this.parser = (Parser) (Class.forName(parserPackageName)).newInstance();
	        this.source = (Source) (Class.forName(sourcePackageName).newInstance());
        }
        catch (IllegalAccessException e) {
        	throw new QueryFailedException("Couldn't intstantiate Parser or Source", e);
        }
        catch (ClassNotFoundException e) {
        	throw new QueryFailedException("Couldn't find class you specified to use as Parser or Source in configuration file ", e);
        }
        catch (InstantiationException e) {
        	throw new QueryFailedException("Couldn't intstantiate Parser or Source", e);
        }
    }

    /**
     *
     */
    private QueryResult executeQuery(Query query) throws NoSuchTypeInQueryResult, 
    											QueryFailedException, CancelledQueryException {
    	QueryResult queryResult = null;
    	_parserResult = null;
    	_resultNodesList = null;
    	_resultEdgesList = null;
        try {
	        Reader r = null;
	        Query newQuery = null;
	
	        SourceResult sourceResult = source.getSourceResult(this.sourceUri, query);
	        if (!sourceResult.queryWasSuccess()) {
	            newQuery = sourceResult.getNewQuery();
	            queryResult = executeQuery(newQuery);
	        } else {
	            r = sourceResult.getReader();
	            //this.typeRelativesCollection = parser.getOntologyTypeCollection(r);
	            _parserResult = parser.getResult(r);
	            r.close();
	            if (query.getQueryTypeName()!= null) {
	                String newTermName = checkResultSetContainsSearchTerm(_parserResult.getNodesList(), query.getQueryTypeName());
	                if (!newTermName.equals(query.getQueryTypeName())) {
	                    query.setQueryTypeName(newTermName);
	                }
	            }
	            filterUnwantedEdges(query);
	            queryResult = new QueryResult(query, _resultNodesList, _resultEdgesList);
	        }
        } catch (IOException e) {
        	throw new QueryFailedException(e.getMessage(), e);
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
    private void filterUnwantedEdges(Query query) {
        List wantedLinks = query.getRelationLinksList();
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
    public QueryResult getQueryResult(Query query) throws QueryFailedException, CancelledQueryException,
    														NoSuchTypeInQueryResult {
    	return executeQuery(query);
    }

}