/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 22/10/2002
 * Time: 16:55:21
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.model.graph.controller;

import org.tockit.events.Event;
import ontorama.ontotools.query.Query;

public class GeneralQueryEvent implements Event {

    private Query query;

    public GeneralQueryEvent(Query subject) {
        this.query = subject;
    }


    public Object getSubject() {
        return query;
    }

}
