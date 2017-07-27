package fatbeats.main;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JTextField;

import fatbeats.lib.OKCancelDialog;

class PlaylistAddDialog extends OKCancelDialog {
	private static final long serialVersionUID = 8863731703852041969L;

	private boolean[] patternState;
	private int numberOfUses;
	private PlaylistControls playlistControls;

	private JTextField patternNameField;

	PlaylistAddDialog(Frame ownerFrame, PlaylistControls playlistControls) {
		super(ownerFrame, "Enter fat beat's name");
		this.playlistControls = playlistControls;

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
		PatternMessage message = new PatternMessage(patternState, patternNameField.getText(), numberOfUses);
		playlistControls.getPlaylistModel().addElement(message);
		playlistControls.addPlaylistState();
		playlistControls.enableRemoveAndClear();

		numberOfUses++;

		patternNameField.setText("");
		this.setVisible(false);
		this.dispose();
	}
}
