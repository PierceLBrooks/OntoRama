/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 16:14:47
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.ui.events;

import org.tockit.events.Event;
import ontorama.model.GeneralNode;


public class QueryEvent implements Event  {
    protected GeneralNode subject;

    public QueryEvent(GeneralNode subject) {
        this.subject = subject;
        System.out.println("new QueryEvent()");
    }

    public Object getSubject() {
        return subject;
    }

}
