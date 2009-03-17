package ontorama.ui;


import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;

/**
 * Status bar will display status of processes, such as loading 
 * graph for example. It includes progress bar that indicates
 * that application is awaiting for a process to finish and
 * a label that can be used to give feedback to a user about
 * current activity.
 */
@SuppressWarnings("serial")
public class StatusBar extends JPanel {

	JLabel _statusLabel;
	JProgressBar _progressBar;

	public StatusBar() {
		super();
		setLayout(new BorderLayout());
		_statusLabel = new JLabel();
		_progressBar = new JProgressBar();
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(_progressBar, BorderLayout.WEST);
		add(_statusLabel, BorderLayout.CENTER);
	}
	
	public void setStatusLabel(String statusMessage) {
		_statusLabel.setText(statusMessage);
	}
	
	public void startProgressBar(String message) {
		_progressBar.setIndeterminate(true);
		_statusLabel.setText(message);
	}
	
	public void stopProgressBar() {
		_progressBar.setIndeterminate(false);
		_statusLabel.setText("");
	}

}
