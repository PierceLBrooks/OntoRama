package ontorama.ui.events;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class TakeThinkingCapOffEvent implements Event {
    protected String subject;

    public TakeThinkingCapOffEvent(String subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
