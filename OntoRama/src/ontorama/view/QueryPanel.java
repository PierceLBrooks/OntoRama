
package ontorama.view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.Image;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;
import ontorama.hyper.view.simple.*;
import ontorama.model.GraphNode;

import ontorama.webkbtools.query.Query;


import ontorama.util.event.ViewEventListener;
import ontorama.util.event.ViewEventObserver;

/**
 * QueryPanel is responsible for building an interface for a query that
 * will be used to query Ontology Server (using GraphBuilder)
 *
 */
public class QueryPanel extends JPanel implements ViewEventObserver {

    private JTextField queryField;
    private JButton querySubmitButton;

    /**
     *
     */
    private JPanel queryFieldPanel = new JPanel();

    /**
     *
     */
    private JPanel relationLinksPanel = new JPanel();

    /**
     * table of check boxes for relation links
     * keys - checkBoxes, values - relation type integers
     */
    private Hashtable relationLinksCheckBoxes = new Hashtable();

    /**
     * holds relation links that user has chosen to display
     */
    private LinkedList wantedRelationLinks = new LinkedList ();

    //temp variable for creating svg image of hyper view
    private TextField imgNameField = new TextField("", 10);
    private SimpleHyperView hyperView;

    /**
     *
     */
    private ViewEventListener viewListener;

    /**
     *
     */
    private OntoRamaApp ontoRamaApp;

    /**
     * @todo  constructor doesn't need parameter hyperView, this is just temporary.Remove later!!!
     * @todo  maybe OntoRamaApp shouldn't be a parameter in constructor
     *        (this is done for executing queries). Better way to do this is to follow
     *        Observer Pattern
     */
    public QueryPanel (SimpleHyperView hyperView, ViewEventListener viewListener, OntoRamaApp ontoRamaApp) {
        this.hyperView = hyperView;

        this.viewListener = viewListener;
        this.viewListener.addObserver(this);

        this.ontoRamaApp = ontoRamaApp;

        // create a query panel
        queryField = new JTextField(25);
        querySubmitButton = new JButton("Get");
        querySubmitButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            doQuery();
          }
        });

        queryFieldPanel.add(queryField);
        queryFieldPanel.add(querySubmitButton);

        this.setLayout(new BorderLayout());

        buildRelationLinksQueryPanel();
        this.add(relationLinksPanel,BorderLayout.NORTH);

        JButton makeSVG = new JButton("Run Spring and force test");
        makeSVG.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                testSpringAndForceAlgorthms();
            }});
        queryFieldPanel.add(makeSVG);

        queryFieldPanel.add( this.imgNameField );
        JButton snapshot = new JButton("Take snap shot");
        snapshot.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                takeSnapshot();
            }});
        queryFieldPanel.add(snapshot);


        this.add(queryFieldPanel, BorderLayout.CENTER);
    }

    /**
     *
     */
    public String getQueryField () {
        return queryField.getText();
    }

    /**
     *
     */
    public void setQueryField (String queryString) {
        queryField.setText(queryString);
    }

    /**
     *
     */
    public List getWantedRelationLinks () {
      return this.wantedRelationLinks;
    }

    /**
     *
     */
    private void buildRelationLinksQueryPanel() {
      RelationLinkDetails[] relationLinksDetails =  OntoramaConfig.getRelationLinkDetails();

      for (int i = 0; i < relationLinksDetails.length; i++) {
        RelationLinkDetails cur = relationLinksDetails[i];
        if (cur == null) {
          System.out.println("i = " + i);
        }
        else {
          JCheckBox curCheckBox = new JCheckBox(cur.getLinkName());
          //JCheckBox curCheckBox = new JCheckBox(cur.getLinkName(),cur.getDisplayIcon());
          ImageIcon displayIcon = cur.getDisplayIcon();
          JLabel displayIconLabel = new JLabel (displayIcon);
          //curCheckBox.setIcon(cur.getDisplayIcon());
          curCheckBox.setSelected(true);
          curCheckBox.addItemListener(new CheckBoxListener());
          this.relationLinksCheckBoxes.put(curCheckBox,new Integer(i));
          relationLinksPanel.add(displayIconLabel);
          relationLinksPanel.add(curCheckBox);
        }
      }

    }


    /**
     * Method to test layouting usin spring and force algorthms
     */
    private void testSpringAndForceAlgorthms() {
        for( int i = 0; i < 300; i++) {
            //generate spring length values between 50 - 250;
            double springLength = (Math.random() * 200) + 51;
            //generate stiffness values between 0 - .99999
            double stiffness = Math.random();
            //generate electric_charge values between 0 - 1000;
            double electric_charge = (Math.random() * 1000) + 1;
            this.hyperView.testSpringAndForceAlgorthms(springLength, stiffness, electric_charge);
            this.hyperView.saveCanvasToFile( springLength+"_"+stiffness+"_"+electric_charge);
        }
        System.out.println("Test Finished...");
    }

    /**
     * Temp method to take a snap shot of hyper view
     */
    private void takeSnapshot() {
        if( !this.imgNameField.getText().equals("") ) {
            hyperView.saveCanvasToFile( this.imgNameField.getText());
        }
    }

    /**
     * ItemListener
     */
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            wantedRelationLinks = new LinkedList();
            Object source = e.getItemSelectable();
            //if (source == buttonX) { }
            Enumeration en = relationLinksCheckBoxes.keys();
            while (en.hasMoreElements()) {
              JCheckBox key = (JCheckBox) en.nextElement();
              if (key.isSelected()) {
                Integer relLinkType = (Integer) relationLinksCheckBoxes.get(key);
                wantedRelationLinks.add(relLinkType);
              }
            }

        }
    }

    public void doQuery () {
      Query newQuery = ontoRamaApp.buildNewQuery();
      ontoRamaApp.executeQuery(newQuery);
    }

	//////////////////////////ViewEventObserver interface implementation////////////////

	/**
	 *
	 */
	public void focus ( GraphNode node) {
		System.out.println();
		System.out.println("******* queryPanel got focus for node " + node.getName());
		this.queryField.setText(node.getName());
		System.out.println();
	}

    /**
     *
     */
    public void toggleFold ( GraphNode node) {
    }

    /**
     *
     */
    public void query ( GraphNode node) {
            System.out.println("QUERY action !!!");
            this.queryField.setText(node.getName());
            doQuery();
    }


}
