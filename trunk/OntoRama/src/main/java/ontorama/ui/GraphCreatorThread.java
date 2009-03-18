package ontorama.ui;import ontorama.OntoramaConfig;import ontorama.backends.Backend;import ontorama.model.graph.Graph;import ontorama.ontotools.query.Query;import ontorama.ontotools.query.QueryResult;import ontorama.ui.events.GraphIsLoadedEvent;import ontorama.ui.events.QueryCancelledEvent;import ontorama.ui.events.UpdateViewsWithQueryCancelledEvent;import org.tockit.events.Event;import org.tockit.events.EventBroker;import org.tockit.events.EventBrokerListener;/** * <p>Copyright: Copyright (c) 2002</p> * <p>Company: DSTC</p> */public class GraphCreatorThread extends Thread {    /**     * graph that will be built as a result of query     */    private Graph _graph;    private final EventBroker _viewsEventBroker;        private boolean _interrupted = false;        private final QueryResult _queryResult;	class LocalQueryCancelledEventHandler implements EventBrokerListener {		public void processEvent(Event event) {					Query query = (Query) event.getSubject();			interrupt();			_interrupted = true;			_viewsEventBroker.processEvent(new UpdateViewsWithQueryCancelledEvent(query));		}	}        /**     * Constructor.     */    public GraphCreatorThread(QueryResult queryResult, EventBroker modelEventBroker, EventBroker viewsEventBroker) {        super();        _queryResult = queryResult;        _viewsEventBroker = viewsEventBroker;		_viewsEventBroker.subscribe(new LocalQueryCancelledEventHandler(), QueryCancelledEvent.class, Object.class);            }    @Override    public void run() {        try {            Backend backend = OntoramaConfig.getBackend();            _graph = backend.createGraph(_queryResult);        }         catch (Exception e) {			e.printStackTrace();			ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error", "Unable to build graph:\n\t" + e.getMessage());			return;        }		if (!_interrupted) {			_viewsEventBroker.processEvent(new GraphIsLoadedEvent(_graph));		}    }        /**     * get graph that has been built     */    public Object getResult() {        return _graph;    }}