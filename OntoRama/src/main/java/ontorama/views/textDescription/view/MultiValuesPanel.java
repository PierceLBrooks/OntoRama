package ontorama.views.textDescription.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ontorama.model.graph.Node;
import ontorama.ui.events.QueryNodeEvent;

import org.tockit.events.EventBroker;

@SuppressWarnings("serial")
public class MultiValuesPanel extends AbstractMultiValuesPanel {

    public MultiValuesPanel(String propName,  EventBroker eventBroker) {
        super(propName,eventBroker);
    }

    @Override
    protected JComponent createPropertyComponent(final Node node) {
        String labelText = "<html><font color=blue><u>" + node.getName() + "</u></font></html>";
        JLabel label = new JLabel(labelText);
        label.setToolTipText("Click to browse to this term");
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                _eventBroker.processEvent(new QueryNodeEvent(node));
            }
        });

        return label;
    }
}

