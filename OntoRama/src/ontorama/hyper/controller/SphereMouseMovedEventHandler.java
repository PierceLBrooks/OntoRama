/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: 21/08/2002
 * Time: 10:11:53
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ontorama.hyper.controller;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemMouseMovementEvent;
import ontorama.hyper.view.simple.SimpleHyperView;
import ontorama.hyper.view.simple.SphereView;

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
