package ontorama.backends.p2p.gui;

import ontorama.view.ErrorPopupMessage;
import ontorama.view.OntoRamaApp;
import ontorama.backends.BackendSearch;
import ontorama.webkbtools.query.Query;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Oct 23, 2002
 * Time: 2:17:10 PM
 * To change this template use Options | File Templates.
 */
public class SearchPanel extends JPanel {

    private JTextField queryField;
    private JTextField queryDepth;
    private JButton submitButton;
    

    public SearchPanel() {
        super();

        queryField = new JTextField(10);
        queryDepth = new JTextField(2);
  
        submitButton = new JButton("Search");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String searchTerm = queryField.getText();
                String searchDepth = queryDepth.getText();
                
                if (searchTerm.length() == 0) {
                    new ErrorPopupMessage("Please type search term", OntoRamaApp.getMainFrame());
                }
                else if (searchDepth.length() == 0) {
                    new ErrorPopupMessage("Please type search depth", OntoRamaApp.getMainFrame());
                }
                else {
                    Query tmpQuery = new Query(searchTerm);
                    Integer depth = new Integer(searchDepth);
                    tmpQuery.setDepth(depth.intValue());
                    BackendSearch.search(tmpQuery);
                }
            }
        });
        add(queryField);
        add(queryDepth);
        add(submitButton);
    }

}
