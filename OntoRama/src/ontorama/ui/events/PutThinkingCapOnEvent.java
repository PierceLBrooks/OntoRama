package ontorama.ui.events;

import org.tockit.events.Event;

/**
 * @author nataliya
 */
public class PutThinkingCapOnEvent implements Event {
    protected String subject;

    public PutThinkingCapOnEvent(String message) {
        this.subject = message;
    }

    public Object getSubject() {
        return subject;
    }
}
