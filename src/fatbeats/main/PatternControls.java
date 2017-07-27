package fatbeats.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import fatbeats.lib.*;

class PatternControls {

	private MusicBox musicBox;
	private NetworkClient networkClient;
	private PatternPanel patternPanel;

	private JButton playOrStopButton;
	private JLabel tempoLabel;
	//actions
	private UndoAction undoAction;
	private RedoAction redoAction;
	private PlayOrStopAction playOrStopAction;
	private UpTempoAction upTempoAction;
	private DownTempoAction downTempoAction;
	private ClearAction clearAction;
	private BroadcastAction broadcastAction;
	private Action showOrHideAction;

	private Action playlistAddAction; //granted by ConnectionControls for enabling/disabling playlist adding
	private boolean playlistShown; //should adding be enabled?

	PatternControls(PatternPanel patternPanel, Action showOrHideAction) {
		musicBox = new MusicBox();
		networkClient = new NetworkClient();
		this.patternPanel = patternPanel;
		this.showOrHideAction = showOrHideAction;
		//actions set up
		undoAction = new UndoAction("Undo", "Click to undo a pattern change", KeyEvent.VK_U, KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		redoAction = new RedoAction("Redo", "Click to redo a pattern change", KeyEvent.VK_R, KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		playOrStopAction = new PlayOrStopAction("Play", new Integer(KeyEvent.VK_P), KeyStroke.getKeyStroke(
				KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		upTempoAction = new UpTempoAction("Tempo Up", "Click to push the tempo", null, KeyStroke.getKeyStroke(
				KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
		downTempoAction = new DownTempoAction("Tempo Down", "Click to lower the tempo", null, KeyStroke.getKeyStroke(
				KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		clearAction = new ClearAction("Clear", "Click to clear the pattern", new Integer(KeyEvent.VK_E),
				KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		broadcastAction = new BroadcastAction("Broadcast...", "Click to broadcast the fat beat", new Integer(
				KeyEvent.VK_B), KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
	}

	NetworkClient getNetworkClient() {
		return networkClient;
	}

	PatternPanel getPatternPanel() {
		return patternPanel;
	}

	JButton getPlayOrStopButton() {
		return playOrStopButton;
	}

	void setPlaylistAddAction(Action playlistAddAction) {
		this.playlistAddAction = playlistAddAction;
	}

	void setPlaylistShown(boolean playlistShown) {
		this.playlistShown = playlistShown;
	}

	Box createControlBox() {
		Box controlBox = Box.createVerticalBox();

		playOrStopButton = new JButton(playOrStopAction);
		playOrStopButton.setFont(FontConstants.MEDIUM_FONT_BOLD);
		playOrStopButton.setMnemonic(0);
		playOrStopAction.setEnabled(false);

		JButton upTempoButton = new JButton(upTempoAction);
		upTempoButton.requestFocus();
		tempoLabel = new JLabel("");
		tempoLabel.setFont(FontConstants.BIG_FONT_BOLD);
		tempoLabel.setText(musicBox.readTempo());
		tempoLabel.setToolTipText("Tempo in Beats Per Minute");
		DoubleLineButton downTempoButton = new DoubleLineButton(downTempoAction);
		downTempoButton.setDoubleText("Tempo", "Down");

		DoubleLineButton clearPatternButton = new DoubleLineButton(clearAction);
		clearPatternButton.setDoubleText("Clear", "Pattern");
		clearPatternButton.setMnemonic(0);

		DoubleLineButton broadcastButton = new DoubleLineButton(broadcastAction);
		broadcastButton.setDoubleText("Broadcast", "Fat Beat");
		broadcastButton.setMnemonic(0);
		broadcastAction.setEnabled(false);

		JButton showOrHideButton = new JButton(showOrHideAction);
		OKCancelDialog.addKeyboardShortcutToComponent(showOrHideButton,
				KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK), showOrHideAction);

		controlBox.add(playOrStopButton);
		controlBox.add(Box.createVerticalStrut(25));
		controlBox.add(upTempoButton);
		controlBox.add(Box.createVerticalStrut(3));
		controlBox.add(tempoLabel);
		controlBox.add(Box.createVerticalStrut(3));
		controlBox.add(downTempoButton);
		controlBox.add(Box.createVerticalStrut(35));
		controlBox.add(clearPatternButton);
		controlBox.add(Box.createVerticalStrut(15));
		controlBox.add(broadcastButton);
		controlBox.add(Box.createVerticalStrut(40));
		controlBox.add(showOrHideButton);
		for (int i = 0; i < controlBox.getComponentCount(); i++) {
			JComponent jc = (JComponent) controlBox.getComponent(i);
			jc.setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		controlBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 10));

		return controlBox;
	}

	JMenu createPatternMenu() {
		JMenu patternMenu = new JMenu("Pattern");
		patternMenu.setMnemonic(KeyEvent.VK_P);

		JMenuItem undoItem = new JMenuItem(undoAction);
		undoAction.setEnabled(false);
		JMenuItem redoItem = new JMenuItem(redoAction);
		redoAction.setEnabled(false);
		JMenuItem playOrStopItem = new JMenuItem(playOrStopAction);
		JMenuItem upTempoItem = new JMenuItem(upTempoAction);
		JMenuItem downTempoItem = new JMenuItem(downTempoAction);
		JMenuItem clearItem = new JMenuItem(clearAction);
		JMenuItem randomizeItem = new JMenuItem(new RandomizeAction("Randomize", "Click to randomize the pattern",
				KeyEvent.VK_Z, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)));
		JMenuItem broadcastItem = new JMenuItem(broadcastAction);

		patternMenu.add(undoItem);
		patternMenu.add(redoItem);
		patternMenu.addSeparator();
		patternMenu.add(playOrStopItem);
		patternMenu.add(upTempoItem);
		patternMenu.add(downTempoItem);
		patternMenu.addSeparator();
		patternMenu.add(clearItem);
		patternMenu.add(randomizeItem);
		patternMenu.add(broadcastItem);
		RecentFilesMenu.paintMenuItems(patternMenu.getMenuComponents(), Color.WHITE);

		return patternMenu;
	}

	void doStop() { //used when music is stopped not directly by the user
		musicBox.getSequencer().stop();
		if ("Stop".equals(playOrStopButton.getText())) {
			playOrStopButton.doClick();
		}
	}

	void doPlay() { //used when user double-clicks on a playlist item
		if ("Play".equals(playOrStopButton.getText())) {
			playOrStopButton.doClick();
		}
	}

	void enableBroadcastPlayAndAdd() {
		if (!broadcastAction.isEnabled()) {
			broadcastAction.setEnabled(true);
		}
		if (!playOrStopAction.isEnabled()) {
			playOrStopAction.setEnabled(true);
		}
		if (playlistShown && !playlistAddAction.isEnabled()) {
			playlistAddAction.setEnabled(true);
		}
	}

	void disableBroadcastPlayAndAdd() {
		if (broadcastAction.isEnabled()) {
			broadcastAction.setEnabled(false);
		}
		String toTest = (String) playOrStopAction.getValue(PresetAbstractAction.NAME); //we want to disable PlayOrStop action ONLY if nothing is being played at the time (so we can first stop what is being played AND THEN disable it - this is done within PlayOrStopAction)
		if ("Play".equals(toTest)) {
			playOrStopAction.setEnabled(false);
		}
		if (playlistAddAction.isEnabled()) {
			playlistAddAction.setEnabled(false);
		}
	}

	void enableUndo() {
		if (!undoAction.isEnabled()) {
			undoAction.setEnabled(true);
		}
	}

	void disableUndo() {
		if (undoAction.isEnabled()) {
			undoAction.setEnabled(false);
		}
	}

	void enableRedo() {
		if (!redoAction.isEnabled()) {
			redoAction.setEnabled(true);
		}
	}

	void disableRedo() {
		if (redoAction.isEnabled()) {
			redoAction.setEnabled(false);
		}
	}

	//code economy methods
	private void manageBroadcastAndPlay() {
		if (patternPanel.hasNothingSelected()) {
			disableBroadcastPlayAndAdd();
		} else {
			enableBroadcastPlayAndAdd();
		}
	}

	private class PlayOrStopAction extends PresetAbstractAction {
		private static final long serialVersionUID = -1936387109596737412L;

		private static final String PLAY_TOOLTIP = "Click to play";
		private static final String STOP_TOOLTIP = "Click to stop";
		private int counter;

		public PlayOrStopAction(String text, Integer mnemonic, KeyStroke accelerator) {
			super(text, PLAY_TOOLTIP, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (counter % 2 == 0) {
				musicBox.buildTrackAndPlay(patternPanel.extractPatternState());
				putValue(NAME, "Stop");
				putValue(SHORT_DESCRIPTION, STOP_TOOLTIP);
			} else {
				musicBox.getSequencer().stop();
				putValue(NAME, "Play");
				putValue(SHORT_DESCRIPTION, PLAY_TOOLTIP);
				if (patternPanel.hasNothingSelected()) { //if we stopped while the pattern was blank - let's disable it so we won't play an empty track
					this.setEnabled(false);
				}
			}
			counter++;
		}

	}

	private class UpTempoAction extends PresetAbstractAction {
		private static final long serialVersionUID = -5960379917731636276L;

		public UpTempoAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			musicBox.speedUp();
			tempoLabel.setText(musicBox.readTempo());
		}
	}

	private class DownTempoAction extends PresetAbstractAction {
		private static final long serialVersionUID = 5257891521626034372L;

		public DownTempoAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			musicBox.slowDown();
			tempoLabel.setText(musicBox.readTempo());
		}
	}

	private class ClearAction extends PresetAbstractAction {
		private static final long serialVersionUID = -8018008876012165270L;

		public ClearAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean shouldAddState = false; //add state only if it's not already blank
			if (!patternPanel.hasNothingSelected()) {
				shouldAddState = true;
			}

			patternPanel.clearPattern();
			if (shouldAddState) {
				patternPanel.addPatternState();
				enableUndo(); //we added state, so if Undo has ended - reenable it
				disableRedo(); //if we were in the middle of redoing - shut it down
			}

			disableBroadcastPlayAndAdd(); //here it's ALWAYS blank, so turn those actions off
		}
	}

	private class UndoAction extends PresetAbstractAction {
		private static final long serialVersionUID = 6864741144950397926L;

		public UndoAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			patternPanel.doUndo();
			if (!patternPanel.isUndoable()) { //check if Undo shouldn't be ended
				this.setEnabled(false);
			}
			enableRedo(); //if Redo has ended (due to file operations) - reenable it
			manageBroadcastAndPlay(); //if we undoed to a blank state - disable Broadcast and Play actions
		}
	}

	private class RedoAction extends PresetAbstractAction {
		private static final long serialVersionUID = 8697136193666912387L;

		public RedoAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			patternPanel.doRedo();
			if (!patternPanel.isRedoable()) { //check if Redo shouldn't be ended
				this.setEnabled(false);
			}
			enableUndo(); //if Undo has ended (due to file operations) - reenable it
			manageBroadcastAndPlay(); //if we redoed to a blank state - disable Broadcast and Play actions
		}
	}

	private class RandomizeAction extends PresetAbstractAction {
		private static final long serialVersionUID = 8697136193666912387L;

		public RandomizeAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			patternPanel.randomizePattern();
			patternPanel.addPatternState();
			enableBroadcastPlayAndAdd(); //if it was blank before, now we have sth to play or broadcast again
			enableUndo(); //we added state, so if Undo has ended - reenable it
			disableRedo(); //if we were in the middle of redoing - shut it down
		}
	}

	private class BroadcastAction extends PresetAbstractAction {
		private static final long serialVersionUID = -6385526356417253193L;
		private BroadcastDialog broadcastDialog;

		public BroadcastAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent source = (JComponent) e.getSource();
			if (networkClient.isConnected()) {
				if (null == broadcastDialog) {
					broadcastDialog = new BroadcastDialog((JFrame) source.getTopLevelAncestor(), networkClient);
				}
				broadcastDialog.setPatternState(patternPanel.extractPatternState());
				broadcastDialog.pack();
				broadcastDialog.setNameFieldText(patternPanel.getCurrentName());
				broadcastDialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(source.getTopLevelAncestor(),
						"No connection established.\nPlease connect to a server first.");
			}
		}
	}
}
