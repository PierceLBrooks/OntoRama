/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:11:53
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.SphereView;
import org.tockit.canvas.events.CanvasItemMouseMovementEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

/**
 *
 */
public class SphereMouseMovedEventHandler implements EventListener {
    private SimpleHyperView simpleHyperView;

    public SphereMouseMovedEventHandler(SimpleHyperView simpleHyperView, EventBroker eventBroker) {
        this.simpleHyperView = simpleHyperView;
        eventBroker.subscribe(this, CanvasItemMouseMovementEvent.class, SphereView.class);
    }

    public void processEvent(Event e) {
        CanvasItemMouseMovementEvent pointedEvent = (CanvasItemMouseMovementEvent) e;
        //System.out.println("processEvent: SpherePointed ");
        simpleHyperView.highlightPathToRoot(pointedEvent);
    }
}
