package ontorama.textDescription.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ontorama.model.GraphNode;

import ontorama.util.event.ViewEventListener;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ParentsPanel extends AbstractMultiValuesPanel {
	
	public ParentsPanel (String clonesPropName, ViewEventListener viewListener) {
    	super(clonesPropName, viewListener);
    }

	/**
	 * @see ontorama.textDescription.view.AbstractMultiValuesPanel#createPropertyComponent(GraphNode)
	 */
	protected JComponent createPropertyComponent(GraphNode node) {
		String labelText = "<html><font color=blue><u>" + node.getName() + "</u></font></html>";
		JLabel label = new JLabel(labelText);
		label.setToolTipText("Click to browse to this parent");
		_componentToPropValueMapping.put(label, node);
		label.addMouseListener(new MouseListener() {
			public void mouseClicked (MouseEvent me) {
				//System.out.println("mouseClicked");
				GraphNode graphNode = (GraphNode) _componentToPropValueMapping.get(me.getSource());
				_viewListener.notifyChange(graphNode, ViewEventListener.MOUSE_SINGLECLICK_KEY_CTRL);
			}
			public void mousePressed (MouseEvent me) {
				//System.out.println("mousePressed");
			}
			public void mouseReleased (MouseEvent me) {
				//System.out.println("mouseReleased");
			}			
			public void mouseEntered (MouseEvent me) {
				//System.out.println("mouseEntered");
			}
			public void mouseExited (MouseEvent me) {
				//System.out.println("mouseExited");
			}
		});
		
		return (JComponent) label;
	}

}

