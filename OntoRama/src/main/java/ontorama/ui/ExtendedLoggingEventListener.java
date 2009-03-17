package ontorama.ui;

import java.io.PrintStream;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.LoggingEventListener;

public class ExtendedLoggingEventListener extends LoggingEventListener {

	private EventBroker eventBroker;
	private PrintStream printStream;
	
	public ExtendedLoggingEventListener(EventBroker eventBroker, Class<? extends Event> eventType, Class<?> subjectType, PrintStream printStream) {
		super(eventBroker, eventType, subjectType, printStream);
		this.eventBroker = eventBroker;
		this.printStream = printStream;
	}
	
	public void processEvent(Event e) {
		printStream.println("Event: " + e.getClass() + "  Subject: " + e.getSubject().getClass() + "\n\t EventBroker: " + this.eventBroker);
	}

}
