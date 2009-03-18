package ontorama.backends.filemanager;

import ontorama.ontotools.query.Query;
import ontorama.ui.HistoryElement;

import org.tockit.events.EventBroker;

/**
 * <p>Copyright: Copyright (c) DSTC 2003</p>
 * <p>Company: DSTC</p>
 */
public class FileHistoryElement implements HistoryElement {

    private final Query _query;
    private String _menuDisplayName;
	private final String _toolTipText;
	private final FileBackend _backend;
	private final QuerySettings _querySettings;

	public FileHistoryElement(Query query, EventBroker eventBroker, 
									FileBackend backend, QuerySettings querySettings) {
        _query = query;
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

    public Query getQuery() {
        return _query;
    }
    
	public void displayElement() {
		_backend.processQueryFromHistoryElement(_query, _querySettings);
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