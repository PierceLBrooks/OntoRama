/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 2, 2002
 * Time: 2:45:55 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.backends.p2p.model;

import java.net.URI;

import ontorama.model.graph.GraphModificationException;
import ontorama.ontotools.NoSuchRelationLinkException;
import ontorama.ontotools.parser.ParserResult;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryResult;

public interface P2PGraph extends ontorama.model.graph.Graph {
	public P2PGraph search(Query query);

	/**
	 * Adds a query result to the graph
	 */
	public void add(QueryResult queryResult)
		throws GraphModificationException, NoSuchRelationLinkException ;

    /**
     * Adds a graph to the graph
     */
    public void add(P2PGraph graph)
        throws GraphModificationException, NoSuchRelationLinkException ;

    /**
     * Assert a node.
     * 
     * @param node to assert
     * @param userIdUri for the asserter
     */
    public void assertNode(P2PNode node, URI userIdUri) throws GraphModificationException;


    /**
     * Reject a node.
     * 
     * @param node to reject
     * @param userIdUri for the rejecter
     */
    public void rejectNode (P2PNode node, URI userIdUri) throws GraphModificationException ;

   /**
     * Assert an edge.
     * 
     * @param edge to assert
     * @param userIdUri for the asserter
     */
    public void assertEdge (P2PEdge edge, URI userIdUri) throws GraphModificationException, NoSuchRelationLinkException;


    /**
     * Reject an edge.
     * 
     * @param edge to reject
     * @param userIdUri for the rejecter
     */
     public void rejectEdge (P2PEdge edge, URI userIdUri) throws GraphModificationException, NoSuchRelationLinkException;
}
