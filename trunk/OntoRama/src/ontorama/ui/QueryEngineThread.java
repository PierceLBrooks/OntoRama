package ontorama.ui;import java.io.IOException;import ontorama.ontotools.CancelledQueryException;import ontorama.ontotools.NoSuchRelationLinkException;import ontorama.ontotools.NoSuchTypeInQueryResult;import ontorama.ontotools.ParserException;import ontorama.ontotools.SourceException;import ontorama.ontotools.query.Query;import ontorama.ontotools.query.QueryEngine;import ontorama.ontotools.query.QueryResult;import ontorama.OntoramaConfig;import ontorama.backends.Backend;import ontorama.model.graph.Graph;import ontorama.model.graph.NoTypeFoundInResultSetException;import ontorama.ui.events.ErrorEvent;import ontorama.ui.events.QueryCancelledEvent;import org.tockit.events.EventBroker;/** * <p>Title: </p> * <p>Description: Using given query get a graph for this query on a * separate thread. * </p> * <p>Copyright: Copyright (c) 2002</p> * <p>Company: DSTC</p> * @author nataliya * @version 1.0 */public class QueryEngineThread extends Thread {    /**     * query     */    private Query _query;    /**     * graph that will be built as a result of query     */    private Graph _graph;    /**     * status message     */    private String _status = "";    /**     * flag to indicate if task is finished     */    private boolean _finished = false;    /**     * flag indicating if thread has been stopped     * @todo introduced this because isInterrupted() java method is not reliable in our     * circumstances - it returnes status that thread is not interrupted although we know     * that we sent interrupt() signal to it. need to check what we are doing wrong.     */    private boolean _interrupted = false;    /**     * @todo probably shouldn't pass event broker here     */    EventBroker _eventBroker;    /**     * Constructor.     */    public QueryEngineThread(Query query, EventBroker eventBroker) {        super();        _query = query;        _eventBroker = eventBroker;        initAll();    }    /**     * initialise all vars     */    private void initAll() {        _finished = false;        _interrupted = false;        _graph = null;    }    /**     * Overriting Thread run() method. This method will do all the work.     *     * @todo not sure of mechanism for interruting thread is correct.     * Problem: once the thread is interrupted, it propagates to QueryEngine, etc.     * However, these processes don't stop because they are designed by java to     * stop gracefully (finish whatever they are doing). In our case we don't want     * that - we want to stop a long process at once. To achieve this: we modified     * Source interface and WebKB2Source code to throw CacelledQueryException.     * Source checks if it's thread has been interrupted and if it was - it throws     * CancelledQueryException. This insures that all processes are stopped immediately.     * Not sure if this is a hack, because it's a bit unintuative: this class     * interrupts the thread and at the same time gets an exception that has been     * caused by the interruption. Also, this approach is very dependant on Source     * implementation: if Source is performing a few tasks in order to do its job,     * each task would have to check if thread is interrupted and throw     * the exception. this is relying on the developer too much.     */    public void run() {        initAll();        try {            _status = "getting ontology data from the source";            QueryEngine queryEngine = new QueryEngine(_query, _query.getSourcePackage(), _query.getParserPackage(), _query.getSourceUri());            _status = "ontology data is read, building data structures";            QueryResult queryResult = queryEngine.getQueryResult();            _status = "building graph";            Backend backend = OntoramaConfig.getBackend();            _graph = backend.createGraph(queryResult, _eventBroker);            //_graph = new GraphImpl(queryResult, _eventBroker);            _status = "graph is built";        } catch (NoTypeFoundInResultSetException noTypeExc) {            dealWithException(noTypeExc, noTypeExc.getMessage());        } catch (NoSuchRelationLinkException noRelExc) {            dealWithException(noRelExc, noRelExc.getMessage());        } catch (IOException ioExc) {            dealWithException(ioExc, ioExc.getMessage());        } catch (ParserException parserExc) {            dealWithException(parserExc, parserExc.getMessage());        } catch (ClassNotFoundException classExc) {            dealWithException(classExc, "Sorry, couldn't find one of the classes you specified in config.xml.\n"             							+ "Class not found: " + classExc.getMessage());        } catch (IllegalAccessException iae) {            dealWithException(iae, iae.getMessage());        } catch (InstantiationException instExc) {            dealWithException(instExc, instExc.getMessage());        } catch (NoSuchTypeInQueryResult noSuchTypeExc) {            dealWithException(noSuchTypeExc, noSuchTypeExc.getMessage());        } catch (SourceException sourceExc) {            dealWithException(sourceExc, sourceExc.getMessage());        } catch (CancelledQueryException cancelledExc) {            //if (!isInterrupted()) {            if (! _interrupted) {                System.err.println("got CancelledQueryException when didn't try to interrupt the query!");                cancelledExc.printStackTrace();                _eventBroker.processEvent(new ErrorEvent("Unexpected exception: query was cancelled"));            }        } catch (Exception e) {            dealWithException(e, "Unable to build graph, unexpected error.");        }        _finished = true;    }    /**     * get current status message     */    public String getMessage() {        return _status;    }    /**     * get graph that has been built     */    public Graph getGraph() {        return _graph;    }    /**     * return flag indicating if task has been finished     */    public boolean done() {        return _finished;    }    /**     * return flag indicating if task has been stopped     */    public boolean isStopped() {        //return isInterrupted();        return _interrupted;    }    /**     * stop this thread     */    public void stopProcess() {        interrupt();        _interrupted = true;        _graph = null;    }    private void dealWithException (Exception e, String errMessage) {        /// @todo this check here seems like a hack. Purpose of it: we often get exceptions        /// when parser is not finished, etc, often caused by this thread when it is interrupted.        /// Also, if thread is interrupted - user doesn't want to know about this ontology        /// and related problems anyway.        if (! _interrupted) {            System.err.println(e);            e.printStackTrace();            _eventBroker.processEvent(new ErrorEvent(errMessage));            /// @todo this is probably 'politically incorrect' to call QueryCancelledEvent here -             /// should be more like QueryStopEvent.             _eventBroker.processEvent(new QueryCancelledEvent(_query));        }    }}