package ontorama.ontotools.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ontorama.model.graph.EdgeType;

/**
 * Query consist of query term and an iterator of relation links that we
 * are interested in (for example: subtype, memberOf).
 * This list holds Integers for the relation links defined in OntoramaConfig
 * 
 * @see ontorama.OntoramaConfig
 */

public class Query {
    /**
     * name of ontology type we are querying for
     */
    private String _typeName;

    /**
     * list of relation links for the ontology type typeName.
     */
    private List<EdgeType> _relationLinks = new LinkedList<EdgeType>();

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
     * Convenience constructor.
     * 
     * Initialise query type name and relation links
     */
    public Query(String typeName, List<EdgeType> relationLinks) {
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
    public void setRelationLinks(List<EdgeType> relationLinks) {
        _relationLinks = relationLinks;
    }

    /**
     * Get Relation types iterator
     * @return relationLinks Iterator
     */
    public Iterator<EdgeType> getRelationLinksIterator() {
        return _relationLinks.iterator();
    }

    /**
     * Get Relation types list
     * @return relationLinks List
     */
    public List<EdgeType> getRelationLinksList() {
        return _relationLinks;
    }

    /**
     *
     */
    public Collection<EdgeType> getRelationLinksCollection() {
        return (Collection<EdgeType>) _relationLinks;
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
		Iterator<EdgeType> it = _relationLinks.iterator();
		while (it.hasNext()) {
			EdgeType edgeType = it.next();
			str = str + edgeType.getName() + ", ";
		}
		return str;
	}

}