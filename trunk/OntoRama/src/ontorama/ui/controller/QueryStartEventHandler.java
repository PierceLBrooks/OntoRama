package ontorama.ui.controller;

import ontorama.OntoramaConfig;
import ontorama.ontotools.QueryFailedException;
import ontorama.ontotools.query.Query;
import ontorama.ontotools.query.QueryEngine;
import ontorama.ui.ErrorDialog;
import ontorama.ui.OntoRamaApp;
import ontorama.ui.events.QueryEngineThreadStartEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * Handle QueryStartEvents - get active backend and corresponding
 * query engine, then create a new event to start this query
 * engine with given query.
 * 
 * @author nataliya
 */
public class QueryStartEventHandler implements EventBrokerListener {
	
	private EventBroker _eventBroker;
	
	/**
	 * @param eventBroker - event broker capable of processing a query. In OntoRamaApp
	 * context - model event broker is used.
	 */
	public QueryStartEventHandler (EventBroker eventBroker) {
		_eventBroker = eventBroker;
	}
	
	public void processEvent(Event event) {
		try {
			Query query = (Query) event.getSubject();
			QueryEngine qe = OntoramaConfig.getBackend().getQueryEngine();
			_eventBroker.processEvent(new QueryEngineThreadStartEvent(qe, query));
			System.out.println("QueryStartEventHandler, query term = " + query.getQueryTypeName());
			System.out.println("QueryStartEventHandler, event broker = " + _eventBroker);
		}
		catch (QueryFailedException e) {
			ErrorDialog.showError(OntoRamaApp.getMainFrame(), "Error", e.getMessage());
			e.printStackTrace();
		}
	}

}
