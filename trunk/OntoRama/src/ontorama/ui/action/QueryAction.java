package ontorama.ui.action;

import ontorama.ontotools.query.Query;
import ontorama.ui.events.GeneralQueryEvent;
import ontorama.ui.QueryPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

import org.tockit.events.EventBroker;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 10, 2002
 * Time: 11:07:17 AM
 * To change this template use Options | File Templates.
 */
public class QueryAction extends AbstractAction {

    private static final String ACTION_COMMAND_KEY_COPY = "execute-query-command";
    private static final String NAME_COPY = "Get";
    private static final String SHORT_DESCRIPTION_COPY = "Execute Query";
    private static final String LONG_DESCRIPTION_COPY = "Execute Query";

    private EventBroker _eventBroker;
    private QueryPanel _queryPanel;

    /**
     *
     */
    public QueryAction(EventBroker eventBroker, QueryPanel queryPanel) {
        _eventBroker = eventBroker;
        _queryPanel = queryPanel;
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
    }

    /**
     *
     */
    public void actionPerformed(ActionEvent parm1) {
        Query query = new Query(_queryPanel.getQueryField(), _queryPanel.getWantedRelationLinks());
        query.setDepth(_queryPanel.getDepthField());
        _eventBroker.processEvent(new GeneralQueryEvent(query));
    }

}
