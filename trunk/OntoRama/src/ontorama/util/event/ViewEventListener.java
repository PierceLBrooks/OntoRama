package ontorama.util.event;

import ontorama.model.GraphNode;

import java.util.Iterator;
import java.util.LinkedList;

public class ViewEventListener implements ViewEventObservable {

    /**
     * constants for different events
     */
    public static final int MOUSE_SINGLECLICK = 1;
    public static final int MOUSE_DOUBLECLICK = 2;
    public static final int MOUSE_SINGLE_RIGHTCLICK = 3;
    public static final int MOUSE_DOUBLE_RIGHTCLICK = 4;
    public static final int KEY_CTRL = 5;
    public static final int KEY_SHIFT = 6;
    public static final int MOUSE_SINGLECLICK_KEY_CTRL = 7;

    /**
     * observers list
     */
    private LinkedList observers = new LinkedList();

    /**
     * Add an observer
     * interface implementation method.
     */
    public void addObserver(Object obj) {
        observers.add(obj);
    }

    /**
     * Notify all observers that an event has occured.
     * @param	node - node that event has occured on
     * @param	eventType - type of event, should be one of contants
     *			declared in ViewEventListener
     */
    public void notifyChange(GraphNode node, int eventType) {
        Iterator i = observers.iterator();
        while (i.hasNext()) {
            ViewEventObserver cur = (ViewEventObserver) i.next();
            switch (eventType) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    System.out.println("SENDING QUERY EVENT for node " + node.getName());
                    cur.query(node);
                    break;
            }
        }
    }

    /**
     *
     */
    public void notifyChange(int eventType) {
        Iterator i = observers.iterator();
        while (i.hasNext()) {
            ViewEventObserver cur = (ViewEventObserver) i.next();
            switch (eventType) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
            }
        }
    }
}
