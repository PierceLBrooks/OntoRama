package ontorama.textDescription.view;

import java.util.Iterator;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * Base class for node properties panels.
 * 
 * @author nataliya
 *
 */
public abstract class AbstractPropertiesPanel extends JPanel {

	JLabel _propNameLabel = new JLabel();

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
