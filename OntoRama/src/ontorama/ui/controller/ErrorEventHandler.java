package ontorama.ui.controller;

import org.tockit.events.EventBrokerListener;
import org.tockit.events.Event;

import java.awt.*;

import ontorama.ui.ErrorPopupMessage;
import ontorama.ui.OntoRamaApp;

/**
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Dec 10, 2002
 * Time: 11:26:10 AM
 * To change this template use Options | File Templates.
 */
public class ErrorEventHandler implements EventBrokerListener {
    public void processEvent(Event event) {
        String errMessage = (String) event.getSubject();
        Frame parentFrame = OntoRamaApp.getMainFrame();
        new ErrorPopupMessage(errMessage, parentFrame);
    }
}
