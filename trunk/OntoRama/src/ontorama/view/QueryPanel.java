
package ontorama.view;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.AbstractButton;

import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextField;
import java.awt.Image;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;

import ontorama.OntoramaConfig;
import ontorama.ontologyConfig.*;

import ontorama.view.action.StopQueryAction;

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
public class QueryPanel extends JPanel implements ViewEventObserver, ActionListener {
	
	private int _depth = -1;

    private JTextField _queryField;
    private JButton _querySubmitButton;
    private JButton _queryStopButton;
    private JTextField _depthField;

    /**
     *
     */
    private JPanel _relationLinksPanel = new JPanel();

    /**
     * table of check boxes for relation links
     * keys - checkBoxes, values - relation type integers
     */
    private Hashtable _relationLinksCheckBoxes = new Hashtable();

    /**
     * holds relation links that user has chosen to display
     */
    private LinkedList _wantedRelationLinks = new LinkedList ();

    //temp variable for creating svg image of hyper view
    private TextField _imgNameField = new TextField("", 10);

    /**
     *
     */
    private ViewEventListener _viewListener;

    /**
     *
     */
    private OntoRamaApp _ontoRamaApp;

    /**
     * @todo  maybe OntoRamaApp shouldn't be a parameter in constructor
     *        (this is done for executing queries). Better way to do this is to follow
     *        Observer Pattern
     */
    public QueryPanel (ViewEventListener viewListener, OntoRamaApp ontoRamaApp) {

        _viewListener = viewListener;
        _viewListener.addObserver(this);

        _ontoRamaApp = ontoRamaApp;

        JPanel queryFieldPanel = new JPanel();

        _queryField = new JTextField(25);
        _queryField.setToolTipText("Type query term here");
        _queryField.addActionListener(this);

		_depthField = new JTextField(1);
		_depthField.setToolTipText("Specify query depth (integer from 1 to 9)");
		_depthField.addActionListener(this);
		_depthField.addKeyListener(new KeyListener() {
			public void keyPressed (KeyEvent ke) {
			}
			public void keyTyped (KeyEvent ke) {
			}
			public void keyReleased (KeyEvent ke) {
				//System.out.println("key char = " + ke.getKeyChar() + ", isDigit = " + Character.isDigit(ke.getKeyChar()) );
				if ( ( ! Character.isDigit(ke.getKeyChar()) ) && ( ! Character.isLetter(ke.getKeyChar()) ) ){
					return;
				}
				int depth = -1;
		    	try {
		    		depth = (new Integer(_depthField.getText())).intValue();
		    	}
		    	catch (NumberFormatException nfe) {
		    		_ontoRamaApp.showErrorDialog("Please use integers to specify depth");
		    		_depthField.selectAll();
		    	}
				if (depth > 4) {
					_ontoRamaApp.showErrorDialog("Please choose smaller integers " + 
					"for depth setting. " + 
					"Large setting for this parameter may result in long load times");
		    		_depthField.selectAll();
				}
				_depth = depth;
			}
		});

        _querySubmitButton = new JButton(new QueryAction());
        _queryStopButton = new JButton(_ontoRamaApp._stopQueryAction);

//		ImageIcon leftButtonIcon = new ImageIcon("images/right.gif");
//		JButton newButton = new JButton("Test button", leftButtonIcon);
//		newButton.setMnemonic(KeyEvent.VK_T);
//		newButton.setVerticalTextPosition(AbstractButton.BOTTOM);
//        newButton.setHorizontalTextPosition(AbstractButton.CENTER);
//        newButton.setLabel("test button label");
//		//newButton.setEnabled(false);

		queryFieldPanel.add(new JLabel("Search for: "));
        queryFieldPanel.add(_queryField);
        
        queryFieldPanel.add(new JLabel("depth: "));
        queryFieldPanel.add(_depthField);

        queryFieldPanel.add(_querySubmitButton);
        queryFieldPanel.add(_queryStopButton);
//        queryFieldPanel.add(newButton);

        setLayout(new BorderLayout());

        buildRelationLinksQueryPanel();
        add(_relationLinksPanel,BorderLayout.NORTH);

        add(queryFieldPanel, BorderLayout.CENTER);
    }
    
    /**
     * implementation of action performed
     */
    public void actionPerformed (ActionEvent ae) {
    	
    	if (ae.getSource() == _queryField) {
    		_querySubmitButton.doClick();
    	}
    	if  (ae.getSource() == _depthField) {
    		_querySubmitButton.doClick();
    	}
    }
    
    /**
     * 
     */
    public Query getQuery () {
    	return buildNewQuery();
    }
    

    /**
     *
     */
    private String getQueryField () {
    	return _queryField.getText();
    }

    /**
     *
     */
    public void setQueryField (String queryString) {
        _queryField.setText(queryString);
    }
    
    /**
     * 
     */
    private int getDepthField () {
		return _depth;
		//return (new Integer(_depthField.getText())).intValue();
    }
    
    /**
     * 
     */
    public void setDepthField (int depth) {
    	_depthField.setText( String.valueOf(depth));
    }

    /**
     *
     */
    private List getWantedRelationLinks () {
      return _wantedRelationLinks;
    }

    /**
     *
     */
    private void setWantedRelationLinks (List wantedLinks) {
      Enumeration enum = _relationLinksCheckBoxes.keys();
      while (enum.hasMoreElements()) {
        JCheckBox curCheckBox = (JCheckBox) enum.nextElement();
        Integer correspondingRelLink = (Integer) _relationLinksCheckBoxes.get(curCheckBox);
        if (wantedLinks.contains(correspondingRelLink)) {
          curCheckBox.setSelected(true);
        }
        else {
          curCheckBox.setSelected(false);
        }
      }
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
          ImageIcon displayIcon = cur.getDisplayIcon();
          JLabel displayIconLabel = new JLabel (displayIcon);
          curCheckBox.setSelected(true);
          curCheckBox.addItemListener(new CheckBoxListener());
          _relationLinksCheckBoxes.put(curCheckBox,new Integer(i));
          _relationLinksPanel.add(displayIconLabel);
          _relationLinksPanel.add(curCheckBox);
        }
      }

    }

//    /**
//     * Method to test layouting usin spring and force algorthms
//     */
//    private void testSpringAndForceAlgorthms() {
//        for( int i = 0; i < 300; i++) {
//            //generate spring length values between 50 - 250;
//            double springLength = (Math.random() * 200) + 51;
//            //generate stiffness values between 0 - .99999
//            double stiffness = Math.random();
//            //generate electric_charge values between 0 - 1000;
//            double electric_charge = (Math.random() * 1000) + 1;
//            _hyperView.testSpringAndForceAlgorthms(springLength, stiffness, electric_charge);
//            _hyperView.saveCanvasToFile( springLength+"_"+stiffness+"_"+electric_charge);
//        }
//        System.out.println("Test Finished...");
//    }

//    /**
//     * Temp method to take a snap shot of hyper view
//     */
//    private void takeSnapshot() {
//        if( ! _imgNameField.getText().equals("") ) {
//            _hyperView.saveCanvasToFile( _imgNameField.getText());
//        }
//    }

    /**
     * ItemListener
     */
    class CheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            _wantedRelationLinks = new LinkedList();
            Object source = e.getItemSelectable();
            //if (source == buttonX) { }
            Enumeration en = _relationLinksCheckBoxes.keys();
            while (en.hasMoreElements()) {
              JCheckBox key = (JCheckBox) en.nextElement();
              if (key.isSelected()) {
                Integer relLinkType = (Integer) _relationLinksCheckBoxes.get(key);
                _wantedRelationLinks.add(relLinkType);
              }
            }

        }
    }

    /**
     * query Action
     */
    class QueryAction extends AbstractAction {

      private static final String ACTION_COMMAND_KEY_COPY = "execute-query-command";
      private static final String NAME_COPY = "Get";
      private static final String SHORT_DESCRIPTION_COPY = "Execute Query";
      private static final String LONG_DESCRIPTION_COPY = "Execute Query";

      /**
       *
       */
      public QueryAction() {
        putValue(Action.NAME, NAME_COPY);
        putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_COPY);
        putValue(Action.LONG_DESCRIPTION, LONG_DESCRIPTION_COPY);
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND_KEY_COPY);
      }

      /**
       *
       */
      public void actionPerformed(ActionEvent parm1) {
        /**@todo: implement this javax.swing.AbstractAction abstract method*/
        System.out.println("___action: query");
        doQuery();
      }
  }


    /**
     *
     */
    //protected void notifyQueryAction () {
    //  viewListener.notifyChange(ViewEventListener.MOUSE_SINGLECLICK_KEY_CTRL);
    //}

    /**
     *
     */
    private Query buildNewQuery () {
      //Query query = new Query (queryPanel.getQueryField(), queryPanel.getWantedRelationLinks());
      Query query = new Query (_queryField.getText(), _wantedRelationLinks);
      query.setDepth(getDepthField());
      return query;
    }

    /**
     *
     */
    protected void doQuery () {
      Query newQuery = buildNewQuery();
      _ontoRamaApp.executeQuery(newQuery);
      _ontoRamaApp.appendHistoryMenu(newQuery);
    }
    


    //////////////////////////ViewEventObserver interface implementation////////////////

    /**
     *
     */
    public void focus ( GraphNode node) {
      System.out.println();
      System.out.println("******* queryPanel got focus for node " + node.getName());
      _queryField.setText(node.getName());
      System.out.println();
    }

    /**
     *
     */
    public void toggleFold ( GraphNode node) {
    }

    /**
     */
    public void query ( GraphNode node) {
      System.out.println("QUERY action !!!");
      _queryField.setText(node.getName());
      doQuery();
    }


}
