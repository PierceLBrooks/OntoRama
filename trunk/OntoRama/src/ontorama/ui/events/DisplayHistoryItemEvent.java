package ontorama.ui.events;

import org.tockit.events.Event;

import javax.swing.*;

/**
 * This event is used to notify all interested parties that
 * history item should be displayed for given menu item.
 * Normally HistoryMenu will emit this event when one of
 * it's items is selected or when one of Back/Forward buttons
 * is pressed.
 *  
 * User: nataliya
 * Date: Dec 9, 2002
 */
public class DisplayHistoryItemEvent implements Event {
    protected JMenuItem subject;

    public DisplayHistoryItemEvent (JMenuItem subject) {
        this.subject = subject;
    }

    public Object getSubject() {
        return subject;
    }
}
