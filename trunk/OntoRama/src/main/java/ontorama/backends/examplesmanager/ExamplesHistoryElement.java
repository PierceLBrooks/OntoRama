package ontorama.backends.examplesmanager;

import ontorama.ontotools.query.Query;
import ontorama.ui.HistoryElement;

import org.tockit.events.EventBroker;

/**
 * <p>Description: Model History Element properties.</p>
 * <p>Copyright: Copyright (c) DSTC 2002</p>
 * <p>Company: DSTC</p>
 */
public class ExamplesHistoryElement implements HistoryElement {

    private final Query _query;
    private final String _menuDisplayName;
	private String _toolTipText;
	private final OntoramaExample _example;
	private final ExamplesBackend _backend;

    /**
     * Create history element with given _getRelationLinksList, query and _example.
     * @param	menuDisplayName - is a string that appears on the lable on corresponding menu
     * item for this _example. also may be used as an id.
     */
	public ExamplesHistoryElement(Query query, EventBroker eventBroker, 
									ExamplesBackend backend, OntoramaExample example) {
        _query = query;
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

    public Query getQuery() {
        return _query;
    }
    
	public void displayElement() {
		_backend.processQueryFromHistoryElement(_example, _query);
	}
    
	public String getToolTipText() {
		return _toolTipText;
	}
	
    @Override
    public String toString() {
        String str = "HistoryElement: ";
        str = str + "display name = " + _menuDisplayName;
        str = str + ", query = " + _query;
        return str;
    }



}