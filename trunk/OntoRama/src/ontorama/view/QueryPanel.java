
package ontorama.view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;

/**
 * QueryPanel is responsible for building an interface for a query that
 * will be used to query Ontology Server (using GraphBuilder)
 *
 */
public class QueryPanel extends JPanel {

    private JTextField queryField;
    private JButton querySubmitButton;

    public QueryPanel () {
		// create a query panel
		queryField = new JTextField(10);
        //queryField.setText(termName);
        querySubmitButton = new JButton("Get");

        this.add(queryField);
        this.add(querySubmitButton);
    }

    public void addActionListener (ActionListener l) {
        querySubmitButton.addActionListener(l);
    }

    public String getQueryField () {
        return queryField.getText();
    }

    public void setQueryField (String queryString) {
        queryField.setText(queryString);
    }

}