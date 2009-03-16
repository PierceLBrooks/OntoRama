package ontorama.backends.filemanager;

import org.tockit.events.EventBroker;

import ontorama.ontotools.query.Query;
import ontorama.ui.HistoryElement;

/**
 * <p>Copyright: Copyright (c) DSTC 2003</p>
 * <p>Company: DSTC</p>
 * @version 1.0
 */
public class FileHistoryElement implements HistoryElement {

    private Query _query;
    private String _menuDisplayName;
	private EventBroker _eventBroker;
	private String _toolTipText;
	private FileBackend _backend;
	private QuerySettings _querySettings;

	public FileHistoryElement(Query query, EventBroker eventBroker, 
									FileBackend backend, QuerySettings querySettings) {
        _query = query;
        _eventBroker = eventBroker;
        _backend = backend;
        _querySettings = querySettings;

		if ((query != null) && ( query.getQueryTypeName() != null) ) {
			_menuDisplayName = query.getQueryTypeName() + " - " + querySettings.getSourceUri();
		}
		else {
			_menuDisplayName = querySettings.getSourceUri();
		}
		_toolTipText = _menuDisplayName;

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
		_backend.processQueryFromHistoryElement(_query, _querySettings);
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