package ontorama.ui.events;

import org.tockit.events.Event;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 10, 2002
 * Time: 11:23:56 AM
 * To change this template use Options | File Templates.
 */
public class ErrorEvent implements Event  {
    protected String subject;

    public ErrorEvent (String errString) {
        this.subject = errString;
    }

    public Object getSubject() {
        return subject;
    }
}
