package fatbeats.main;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import fatbeats.lib.OKCancelDialog;

class BroadcastDialog extends OKCancelDialog {
	private static final long serialVersionUID = 872600479636064192L;

	private NetworkClient networkClient;
	private boolean[] patternState;
	private int numberOfUses;

	private JTextField patternNameField;

	BroadcastDialog(Frame ownerFrame, NetworkClient networkClient) {
		super(ownerFrame, "Enter fat beat's name");
		this.networkClient = networkClient;
		numberOfUses = 1;
		patternNameField = new JTextField("", 20);
		patternNameField.addActionListener(getOKListener());

		backgroundPanel.add(patternNameField);
		backgroundPanel.add(getOKButton());

		this.getContentPane().add(backgroundPanel, BorderLayout.CENTER);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	void setPatternState(boolean[] patternState) {
		this.patternState = patternState;
	}

	void setNameFieldText(String text) {
		patternNameField.setText(text);
		patternNameField.selectAll();
	}

	@Override
	protected void oKPerformed() {
		if (networkClient.isConnected()) {
			try {
				BroadcastMessage message = new BroadcastMessage(patternState, patternNameField.getText(), numberOfUses);
				message.setSenderName(networkClient.getUserName());
				networkClient.sendPatternMessage(message);
				numberOfUses++;
				patternNameField.setText("");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this.getOwner(),
					"No connection established.\nPlease connect to a server first.");
		}
		this.setVisible(false);
		this.dispose();
	}
}
