package ontorama.textDescription.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ontorama.controller.QueryEvent;
import ontorama.model.Node;
import org.tockit.events.EventBroker;

/**
 * @author nataliya
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ParentsPanel extends AbstractMultiValuesPanel {

    public ParentsPanel(String propName,  EventBroker eventBroker) {
        super(propName,eventBroker);
    }

    /**
     * @see ontorama.textDescription.view.AbstractMultiValuesPanel#createPropertyComponent(ontorama.model.Node)
     */
    protected JComponent createPropertyComponent(final Node node) {
        String labelText = "<html><font color=blue><u>" + node.getName() + "</u></font></html>";
        JLabel label = new JLabel(labelText);
        label.setToolTipText("Click to browse to this term");
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                _eventBroker.processEvent(new QueryEvent(node));
            }
        });

        return label;
    }

}

