package ontorama.ontotools.source.webkb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import ontorama.model.graph.Node;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: DSTC</p>
 * @author nataliya
 * @version 1.0
 */

public class AmbiguousChoiceDialog extends JDialog {

    /**
     * list to hold objects to choose from
     * (in our case - ontology types)
     */
    private List choiceList;
    private int numChoices;

    /**
     * group of choice buttons
     */
    private ButtonGroup group;

    /**
     * remember selected button
     */
    private JButton selectedButton;

    /**
     * title string
     */
    private String titleStr = "Search Result is ambiguous";

    /**
     * 'explanation' string that will appear above choices
     */
    private String topLabelStr = "Search term is ambiguous...Please choose one of the following ";

    /**
     * Create a dialog that will display all choices and propmt user
     * to make a selection.
     * @param typesList - list holding all alternative ontology types
     * @param frame - owner frame
     */
    public AmbiguousChoiceDialog(List typesList, Frame frame) {
        super(frame);
        setTitle(titleStr);

        this.choiceList = typesList;
        this.numChoices = choiceList.size();

        JRootPane rootPane = getRootPane();
        Container contentPanel = rootPane.getContentPane();

        JPanel choicesPanel = buildChoicePanel();

        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(choicesPanel, BorderLayout.NORTH);

        setModal(true);
        setLocationRelativeTo(frame);
        pack();
        setVisible(true);
    }

    /**
     * build a panel with radio buttons representing ontology types
     */
    private JPanel buildChoicePanel() {
        JPanel choiceButtonsPanel = new JPanel();
        JLabel label = new JLabel(topLabelStr);

        choiceButtonsPanel.setLayout(new BoxLayout(choiceButtonsPanel, BoxLayout.Y_AXIS));
        choiceButtonsPanel.add(label);

        JButton[] buttons = new JButton[choiceList.size()];
        group = new ButtonGroup();

        for (int i = 0; i < numChoices; i++) {
            Node node= (Node) choiceList.get(i);
            JButton curButton = new JButton(node.getName());
            curButton.setToolTipText(node.getDescription());
            buttons[i] = curButton;
            if (i == 0) {
                curButton.setSelected(true);
                getRootPane().setDefaultButton(curButton);
                selectedButton = curButton;
            }
            curButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    JButton selected = (JButton) ae.getSource();
                    selectedButton = selected;
                    closeDialog();
                }
            });
            group.add(curButton);
            choiceButtonsPanel.add(curButton);
        }
        return choiceButtonsPanel;
    }

    /**
     * close dialog window
     */
    public void closeDialog() {
        setVisible(false);
    }


    /**
     * get text of selected button (which should correspond to the selected
     * node name)
     */
    public String getSelected() {
        return selectedButton.getText();
    }


}