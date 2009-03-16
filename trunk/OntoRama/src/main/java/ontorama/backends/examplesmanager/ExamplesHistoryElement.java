package ontorama.backends.examplesmanager;

import org.tockit.events.EventBroker;

import ontorama.ontotools.query.Query;
import ontorama.ui.HistoryElement;

/**
 * <p>Title: </p>
 * <p>Description: Model History Element properties.</p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */
public class ExamplesHistoryElement implements HistoryElement {

    private Query _query;
    private String _menuDisplayName;
	private EventBroker _eventBroker;
	private String _toolTipText;
	private OntoramaExample _example;
	private ExamplesBackend _backend;

    /**
     * Create history element with given _getRelationLinksList, query and _example.
     * @param	menuDisplayName - is a string that appears on the lable on corresponding menu
     * item for this _example. also may be used as an id.
     */
	public ExamplesHistoryElement(Query query, EventBroker eventBroker, 
									ExamplesBackend backend, OntoramaExample example) {
        _query = query;
        _eventBroker = eventBroker;
        _backend = backend;
        _example = example;

		_menuDisplayName = query.getQueryTypeName() + " (" + example.getName() + ")";
		_toolTipText = query.getQueryTypeName();
		if (query.getDepth() > -1) {
			_toolTipText = _toolTipText + ", depth = " + query.getDepth();
		}
		_toolTipText = _toolTipText + ", rel links = " + query.getRelationLinksList();

    }

    /**
     * Get this element's name
     */
    public String getMenuDisplayName() {
        return _menuDisplayName;
    }


    /**
     * Get query
     */
    public Query getQuery() {
        return _query;
    }
    
    
	/**
	 * @see ontorama.ui.HistoryElementInterface#displayElement()
	 */
	public void displayElement() {
		_backend.processQueryFromHistoryElement(_example, _query);
	}
    
	/**
	 * @see ontorama.ui.HistoryElement#getToolTipText()
	 */
	public String getToolTipText() {
		return _toolTipText;
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