
package ontorama.util.event;

import java.util.LinkedList;
import java.util.Iterator;

import ontorama.model.GraphNode;

public class ViewEventListener implements ViewEventObservable {
	
	/**
	 *
	 */
	public static int MOUSE_SINGLECLICK = 1;
	
	/**
	 *
	 */
	private LinkedList observers  = new LinkedList();
	
	/**
	 *
	 */
	public void addObserver( Object obj ) {
		observers.add(obj);
	}
	
	/**
	 *
	 */
	public void notifyChange (GraphNode node, int eventType) {
		Iterator i = observers.iterator();
		while (i.hasNext()) {
			ViewEventObserver cur = (ViewEventObserver) i.next();
			if ( eventType == 1 ) {
				cur.focus( node );
			}
		}
		
	}
}
