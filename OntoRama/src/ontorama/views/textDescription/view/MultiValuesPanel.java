package ontorama.views.textDescription.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ontorama.ui.events.QueryNodeEvent;
import ontorama.model.graph.Node;
import org.tockit.events.EventBroker;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MultiValuesPanel extends AbstractMultiValuesPanel {

    public MultiValuesPanel(String propName,  EventBroker eventBroker) {
        super(propName,eventBroker);
    }

    /**
     * @see ontorama.views.textDescription.view.AbstractMultiValuesPanel#createPropertyComponent(ontorama.model.graph.Node)
     */
    protected JComponent createPropertyComponent(final Node node) {
        String labelText = "<html><font color=blue><u>" + node.getName() + "</u></font></html>";
        JLabel label = new JLabel(labelText);
        label.setToolTipText("Click to browse to this term");
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                _eventBroker.processEvent(new QueryNodeEvent(node));
            }
        });

        return label;
    }

}
