package ontorama.backends.p2p.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ontorama.backends.BackendSearch;
import ontorama.ui.ErrorPopupMessage;
import ontorama.ui.OntoRamaApp;
import ontorama.ontotools.query.Query;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 23, 2002
 * Time: 2:17:10 PM
 * To change this template use Options | File Templates.
 */
public class SearchPanel extends JPanel {

    private JTextField queryField;
    private JButton submitButton;
    

    public SearchPanel() {
        super();

        queryField = new JTextField(10);

        submitButton = new JButton("Search");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String searchTerm = queryField.getText();

                if (searchTerm.length() == 0) {
                    new ErrorPopupMessage("Please type search term", OntoRamaApp.getMainFrame());
                }
                else {
                    Query tmpQuery = new Query(searchTerm);
                    tmpQuery.setDepth(2);
                    BackendSearch.search(tmpQuery);
                }
            }
        });
        add(queryField);
        add(submitButton);
    }
}
