package ontorama.backends.examplesmanager;

import org.tockit.events.EventBroker;

import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ui.HistoryElementInterface;
import ontorama.ui.events.HistoryQueryStartEvent;


/**
 * <p>Title: </p>
 * <p>Description: Model History Element properties.</p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */
public class ExamplesHistoryElement implements HistoryElementInterface {

    private Query _query;
    private String _menuDisplayName;
    private QueryEngine _queryEngine;
	private EventBroker _eventBroker;

    /**
     * Create history element with given _getRelationLinksList, query and _example.
     * @param	menuDisplayName - is a string that appears on the lable on corresponding menu
     * item for this _example. also may be used as an id.
     */
    public ExamplesHistoryElement(String menuDisplayName, Query query, QueryEngine queryEngine, EventBroker eventBroker) {
        _menuDisplayName = menuDisplayName;
        _query = query;
        _queryEngine = queryEngine;
        _eventBroker = eventBroker;
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
    
    public QueryEngine getQueryEngine() {
    	return _queryEngine;
    }
    
	/**
	 * @see ontorama.ui.HistoryElementInterface#displayElement()
	 */
	public void displayElement() {
		_eventBroker.processEvent(new HistoryQueryStartEvent(this));		
	}
    

    /**
     *
     */
    public String toString() {
        String str = "HistoryElement: ";
        str = str + "display name = " + _menuDisplayName;
        str = str + ", query = " + _query;
        return str;
    }


}