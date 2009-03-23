package ontorama.ontotools.query;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ontorama.model.graph.Edge;
import ontorama.model.graph.EdgeType;
import ontorama.model.graph.Node;
import ontorama.ontotools.CancelledQueryException;
import ontorama.ontotools.NoSuchTypeInQueryResult;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.parser.Parser;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.source.Source;
import ontorama.ontotools.source.SourceResult;

/**
 * Query Engine will query Ontology Server with the given
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
 * @see ontorama.ontotools.query.Query
 */

public class QueryEngine {

	private ParserResult _parserResult;
    private List<Node> _resultNodesList;
    private List<Edge> _resultEdgesList;

	private final Parser parser;
	private final Source source;
	private final String ontolgySourceUri;


    /**
     * Execute a query to OntologyServer and get a query result
     */
    public QueryEngine (Source source, Parser parser, String ontologySourceUri) {
        this.parser = parser;
        this.source = source;
        this.ontolgySourceUri = ontologySourceUri;
    }

    private QueryResult executeQuery(Query query) throws NoSuchTypeInQueryResult, 
    											QueryFailedException, CancelledQueryException {
    	QueryResult queryResult = null;
    	_parserResult = null;
    	_resultNodesList = null;
    	_resultEdgesList = null;
        try {
	        Reader r = null;
	        Query newQuery = null;
	
	        SourceResult sourceResult = source.getSourceResult(this.ontolgySourceUri, query);
	        if (!sourceResult.queryWasSuccess()) {
	            newQuery = sourceResult.getNewQuery();
	            queryResult = executeQuery(newQuery);
	        } else {
	            r = sourceResult.getReader();
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
     *
     * @param termName  - search term
     * @return term: the same as search term OR updated term corresponding to
     * the ontology type name matching this term.
     */
    private String checkResultSetContainsSearchTerm(List<Node> nodesList, String termName)
            throws NoSuchTypeInQueryResult {
        boolean found = false;
        Iterator<Node> it = nodesList.iterator();
        String newTermName = termName;
        while (it.hasNext()) {
            Node cur = it.next();
            if (cur.getIdentifier().equals(termName)) {
                found = true;
                newTermName = cur.getName();
            }
            if (cur.getName().equals(termName)) {
                found = true;
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
        List<EdgeType> wantedLinks = query.getRelationLinksList();
        // iterator of wanted links (links we are interested in)
        Iterator<EdgeType> queryRelationLinks = wantedLinks.iterator();

        // wanted links iterator is empty
        if (!queryRelationLinks.hasNext()) {
            _resultNodesList = _parserResult.getNodesList();
            _resultEdgesList = _parserResult.getEdgesList();
            return;
        }

        _resultNodesList = new ArrayList<Node>();
        _resultEdgesList = new ArrayList<Edge>();
        List<Node> nodes = _parserResult.getNodesList();

        for (Edge curEdge : _parserResult.getEdgesList()) {
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

        _resultNodesList.addAll(nodes);
    }

    public QueryResult getQueryResult(Query query) throws QueryFailedException, CancelledQueryException,
    														NoSuchTypeInQueryResult {
    	return executeQuery(query);
    }

}