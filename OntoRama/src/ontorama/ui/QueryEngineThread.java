package ontorama.ui;import ontorama.ontotools.CancelledQueryException;import ontorama.ontotools.query.Query;import ontorama.ontotools.query.QueryEngine;import ontorama.ontotools.query.QueryResult;import ontorama.ui.events.QueryCancelledEvent;import ontorama.ui.events.QueryIsFinishedEvent;import ontorama.ui.events.UpdateViewsWithQueryCancelledEvent;import org.tockit.events.Event;import org.tockit.events.EventBroker;import org.tockit.events.EventBrokerListener;/** * <p>Title: </p> * <p>Description: Using given query get a graph for this query on a * separate thread. * </p> * <p>Copyright: Copyright (c) 2002</p> * <p>Company: DSTC</p> * @author nataliya * @version 1.0 */public class QueryEngineThread extends ThreadWorker {    /**     * query     */    private Query _query;    private EventBroker _viewsEventBroker;	private QueryEngine _queryEngine;    private QueryResult _queryResult;    	class LocalQueryCancelledEventHandler implements EventBrokerListener {		public void processEvent(Event event) {			stopProcess();			Query query = (Query) event.getSubject();			_viewsEventBroker.processEvent(new UpdateViewsWithQueryCancelledEvent(query));		}	}        	/**     * Constructor.     */    public QueryEngineThread(QueryEngine queryEngine, Query query,     								EventBroker viewsEventBroker) {        super();        _query = query;        _viewsEventBroker = viewsEventBroker;        _queryEngine = queryEngine;        initAll();        _viewsEventBroker.subscribe(new LocalQueryCancelledEventHandler(), QueryCancelledEvent.class, Object.class);        //		new ExtendedLoggingEventListener(//							_viewsEventBroker,//							QueryCancelledEvent.class,//							Object.class,//							System.out);            }    /**     * Overwriting Thread run() method. This method will do all the work.     *     * @todo not sure if mechanism for interruting thread is correct.     * Problem: once the thread is interrupted, it propagates to QueryEngine, etc.     * However, these processes don't stop because they are designed by java to     * stop gracefully (finish whatever they are doing). In our case we don't want     * that - we want to stop a long process at once. To achieve this: we modified     * Source interface and WebKB2Source code to throw CacelledQueryException.     * Source checks if it's thread has been interrupted and if it was - it throws     * CancelledQueryException. This insures that all processes are stopped immediately.     * Not sure if this is a hack, because it's a bit unintuative: this class     * interrupts the thread and at the same time gets an exception that has been     * caused by the interruption. Also, this approach is very dependant on Source     * implementation: if Source is performing a few tasks in order to do its job,     * each task would have to check if thread is interrupted and throw     * the exception. this is relying on the developer too much.     */    public void run() {        initAll();        try {			_queryResult = _queryEngine.getQueryResult(_query);			_viewsEventBroker.processEvent(new QueryIsFinishedEvent(_queryResult));        } catch (CancelledQueryException cancelledExc) {            if (! _interrupted) {                System.err.println("got CancelledQueryException when didn't try to interrupt the query!");                cancelledExc.printStackTrace();                ErrorDialog.showError(OntoRamaApp.getMainFrame(), cancelledExc, "Error", "Unexpected exception: query was cancelled");            }        } catch (Exception e) {            dealWithException(e, "Unable to process query:\n\t" + e.getMessage());        }        _finished = true;    }        public Object getResult() {        return _queryResult;    }    private void dealWithException (Exception e, String errMessage) {        /// @todo this check here seems like a hack. Purpose of it: we often get exceptions        /// when parser is not finished, etc, often caused by this thread when it is interrupted.        /// Also, if thread is interrupted - user doesn't want to know about this ontology        /// and related problems anyway.        if (! _interrupted) {            System.err.println(e);            e.printStackTrace();            ErrorDialog.showError(OntoRamaApp.getMainFrame(), e, "Error", errMessage);            /// @todo this is probably 'politically incorrect' to call QueryCancelledEvent here -             /// should be more like QueryStopEvent or ErrorInQueryEvent or QueryIsUnsuccessfullEvent.             _viewsEventBroker.processEvent(new UpdateViewsWithQueryCancelledEvent(_query));        }    }}