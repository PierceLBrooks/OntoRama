package ontorama.textDescription.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LayoutManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Base class for node properties panels.
 * Builds a panel with label _propNameLabel on the left hand
 * side. This label describes node property name, such as
 * "Description" or "Synonyms", etc. On the right hand
 * side there could be some other component that will
 * display value of this property for the focussed node.
 *
 * This class should implement panel layout, layout of
 * _propNameLabel, and set its size. Also, any other
 * functionality common to all various property panels
 *
 * @author nataliya
 *
 */
public abstract class AbstractPropertiesPanel extends JPanel {

    /**
     * Label to show name of node property
     */
    JLabel _propNameLabel = new JLabel();

    /**
     * padding
     */
    private int _minPadding = 15;

    /**
     *
     */
    protected void layoutFirstComponent() {
        // create and set layout
        LayoutManager curLayout = new BoxLayout(this, BoxLayout.X_AXIS);
        setLayout(curLayout);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        // add first label
        add(_propNameLabel);

        // work out size of rigid area and set it
        //int curRigitAreaWidth = maxLeftLabelWidth - getLabelWidth(propNameLabel) + this.minPadding;
        int curRigitAreaWidth = _minPadding;
        Dimension d = new Dimension(curRigitAreaWidth, 0);
        add(Box.createRigidArea(d));
    }

    /**
     * get property name label width
     */
    public int getPropNameLabelWidth() {
        Font font = _propNameLabel.getFont();
        FontMetrics fontMetrics = _propNameLabel.getFontMetrics(font);
        int width = fontMetrics.stringWidth(_propNameLabel.getText());
        return width;
    }

    /**
     *
     */
    public void setPropNameLabelWidth(Dimension d) {
        _propNameLabel.setSize(d);
        _propNameLabel.setMinimumSize(d);
        _propNameLabel.setMaximumSize(d);
        _propNameLabel.setPreferredSize(d);
    }

    /**
     * get label height
     */
    public int getPropNameLabelHeight() {
        Font font = _propNameLabel.getFont();
        FontMetrics fontMetrics = _propNameLabel.getFontMetrics(font);
        int height = fontMetrics.getHeight();
        return height;
    }

    /**
     * update values of properties for selected node.
     * values are passed in the iterator param.
     */
    //public abstract void update(Iterator propValuesIterator);

    /**
     * Clear all property values
     */
    public abstract void clear();

}
