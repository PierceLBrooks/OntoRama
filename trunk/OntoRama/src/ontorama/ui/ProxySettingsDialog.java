package ontorama.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog allowing a user to set proxies if needed.
 * This won't work when run using Java Webstart platform.
 *
 *  
 * Code needed to set proxies: System.getProperties().put( "proxySet", "true");
 * System.getProperties().put( "proxyHost", "myProxyMachineName" ); System.
 * getProperties().put( "proxyPort", "85" );
 *
 * If authenticatoin needed:
 * URLConnection connection = url.openConnection(); 
 * String password = "username:password"; 
 * String encodedPassword = base64Encode( password ); 
 * connection.setRequestProperty ( "Proxy- Authorization", encodedPassword);
 * 
 * @author nataliya
 */
public class ProxySettingsDialog extends JDialog {
	
	private JTextField _proxyHostTextField;
	private JTextField _proxyPortTextField;
	
	private Frame _owner;
	
	private final String _proxyHostPropertyName = "proxyHost";
	private final String _proxyPortPropertyName = "proxyPort"; 
	private final String _proxySetPropertyName = "proxySet";
	
	private final String CONFIG_PROXY_SECTION_NAME = "ProxySettings";

	public ProxySettingsDialog(Frame owner, String title, boolean isModal)
											throws HeadlessException {
		super(owner, title, isModal);	
		_owner = owner;
		
		JPanel inputPanel = buildInputPanel();
		JPanel buttonsPanel = buildButtonsPanel();

		Container contentPanel = getContentPane();		
		contentPanel.add(new JLabel("Enter proxy settings: "), BorderLayout.NORTH);
		contentPanel.add(inputPanel, BorderLayout.CENTER);
		contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		setLocationRelativeTo(owner);
		pack();
		show();
	}

	private JPanel buildButtonsPanel() {

		JButton okButton = new JButton();
		Action setProxyAction = new AbstractAction("Apply proxy settings") {
			public void actionPerformed(ActionEvent event) {
				setProxies();
				dispose();
			}
		};
		
		okButton.setAction(setProxyAction);
		
		JButton cancelButton = new JButton();
		Action cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		};
		cancelButton.setAction(cancelAction);
				
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(cancelButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add(okButton);		
		return buttonsPanel;
	}

	private JPanel buildInputPanel() {
		_proxyHostTextField = new JTextField(50);
		_proxyPortTextField = new JTextField(5);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout( new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		inputPanel.add(new JLabel("Proxy host:"));
		inputPanel.add(_proxyHostTextField);
		inputPanel.add(new JLabel("Proxy port:"));
		inputPanel.add(_proxyPortTextField);
		
		if ( System.getProperty(_proxySetPropertyName) != null ) {
			if (System.getProperty(_proxyHostPropertyName) != null) {
				_proxyHostTextField.setText(System.getProperty(_proxyHostPropertyName));
			}
			if (System.getProperty(_proxyPortPropertyName) != null) {
				_proxyPortTextField.setText(System.getProperty(_proxyPortPropertyName));
			}
		}
		else {
			String proxySetValue = ConfigurationManager.fetchString(CONFIG_PROXY_SECTION_NAME, _proxySetPropertyName, null);
			if (proxySetValue != null) {
				_proxyHostTextField.setText(ConfigurationManager.fetchString(CONFIG_PROXY_SECTION_NAME, _proxyHostPropertyName, ""));
				_proxyPortTextField.setText(ConfigurationManager.fetchString(CONFIG_PROXY_SECTION_NAME, _proxyPortPropertyName, ""));
			} 
		}
		return inputPanel;
	}
	
	/**
	 * verify input and set proxies.
	 */
	private void setProxies() {
		String proxyHostInput = _proxyHostTextField.getText();
		String proxyPortInput = _proxyPortTextField.getText();
		
		if (proxyHostInput.length() <= 0) {
			JOptionPane.showMessageDialog(_owner, "Expecting name of proxy host", "Error", JOptionPane.ERROR_MESSAGE);
		}
		if (proxyPortInput.length() <= 0) {
			String errMessage = "Expecting proxy port number";
			JOptionPane.showMessageDialog(_owner, errMessage,"Error", JOptionPane.ERROR_MESSAGE);
			try {
				Integer proxyPort = new Integer(proxyPortInput);
			} 
			catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(_owner, errMessage,"Error", JOptionPane.ERROR_MESSAGE);				
			}		
		}
		System.getProperties().put( _proxySetPropertyName, "true" );
		System.getProperties().put( _proxyHostPropertyName, proxyHostInput );
		System.getProperties().put( _proxyPortPropertyName, proxyPortInput );	
		
		ConfigurationManager.storeString(CONFIG_PROXY_SECTION_NAME ,_proxySetPropertyName, "true");
		ConfigurationManager.storeString(CONFIG_PROXY_SECTION_NAME ,_proxyHostPropertyName, proxyHostInput);
		ConfigurationManager.storeString(CONFIG_PROXY_SECTION_NAME ,_proxyPortPropertyName, proxyPortInput);
	}
}
