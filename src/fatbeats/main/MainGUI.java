package fatbeats.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import fatbeats.lib.MyGUI;

class MainGUI implements MyGUI {

	final static String THE_NAME = "Fat Beats";

	private PatternPanel patternPanel;
	private InstrumentControls instrumentControls;
	private PatternControls patternControls;
	private ConnectionControls connectionControls;
	private PlaylistControls playlistControls;

	private JFrame gUIframe;
	private JPanel background;
	private JPanel communicationPanel;

	MainGUI() {
		gUIframe = new JFrame(THE_NAME);
		patternPanel = new PatternPanel(new PatternListener());
		patternControls = new PatternControls(patternPanel, new ShowOrHideAction("Show >>",
				"Click to show communication pane"));
		instrumentControls = new InstrumentControls(patternPanel);
		connectionControls = new ConnectionControls(patternControls);
		playlistControls = connectionControls.getPlaylistControls();
		communicationPanel = connectionControls.createCommunicationPanel();
	}

	static void changeFrameName(JFrame frameToRename, String newName) {
		frameToRename.setTitle(THE_NAME + " - " + newName);
	}

	@Override
	public JFrame getGUIframe() {
		gUIframe.getContentPane().add(createBackground());
		gUIframe.setJMenuBar(createMenuBar());
		gUIframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gUIframe.setLocationRelativeTo(null);
		gUIframe.setResizable(false);
		gUIframe.getRootPane().setDefaultButton(patternControls.getPlayOrStopButton());
		gUIframe.pack();

		return gUIframe;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(new FileMenu(patternControls));
		menuBar.add(instrumentControls.createInstrumentsMenu());
		menuBar.add(patternControls.createPatternMenu());
		menuBar.add(playlistControls.createPlaylistMenu());
		menuBar.add(connectionControls.createConnectionMenu());

		return menuBar;
	}

	private JPanel createBackground() {
		background = new JPanel(new GridBagLayout());
		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		background.add(instrumentControls.createInstrumentsPanel(), gbc1);

		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		background.add(patternPanel, gbc2);

		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		background.add(new ClearRowPanel(patternControls), gbc3);

		GridBagConstraints gbc4 = new GridBagConstraints();
		gbc4.gridx = 3;
		gbc4.gridy = 0;
		background.add(patternControls.createControlBox(), gbc4);

		return background;
	}

	private void addExistingCommunicationPanel() {
		GridBagConstraints gbc5 = new GridBagConstraints();
		gbc5.gridx = 4;
		gbc5.gridy = 0;
		background.add(communicationPanel, gbc5);
		background.revalidate();
		background.repaint();
		gUIframe.pack();
	}

	private void removeCommunicationPanel() {
		background.remove(communicationPanel);
		background.revalidate();
		background.repaint();
		gUIframe.pack();
	}

	//nested classes
	private class PatternListener implements ActionListener { //it's placed in MainGUI, because it needs access to PatternControls and none is possible from PatternPanel level
		@Override
		public void actionPerformed(ActionEvent e) {
			patternPanel.addPatternState();
			patternControls.enableUndo(); //we added state, so if Undo has ended - reenable it
			patternControls.disableRedo(); //if we were in the middle of redoing - shut it down
			playlistControls.clearPlaylistSelection();
			if (patternPanel.hasNothingSelected()) {
				patternControls.disableBroadcastPlayAndAdd(); //nothing to broadcast, play or add
			} else {
				patternControls.enableBroadcastPlayAndAdd(); //if it had been blank before, it's not anymore
			}
		}
	}

	private class ShowOrHideAction extends AbstractAction { //it's placed in MainGUI, because it operates directly on MainGUI
		private static final long serialVersionUID = 392043886914790908L;

		private static final String SHOW_TOOLTIP = "Click to show communication pane";
		private static final String HIDE_TOOLTIP = "Click to hide communication pane";
		private int counter;

		private boolean playlistStepBackWasEnabled, playlistStepForwardWasEnabled;

		public ShowOrHideAction(String text, String tooltip) {
			super(text);
			putValue(SHORT_DESCRIPTION, tooltip);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (counter % 2 == 0) {
				addExistingCommunicationPanel();
				putValue(NAME, "<< Hide");
				putValue(SHORT_DESCRIPTION, HIDE_TOOLTIP);

				patternControls.setPlaylistShown(true); //set a flag to let Pattern Controls know if it should enable Playlist Add
				playlistControls.switchOnMainControls();

				if (playlistStepBackWasEnabled) {
					playlistControls.enableStepBack();
				}
				if (playlistStepForwardWasEnabled) {
					playlistControls.enableStepForward();
				}
			} else {
				removeCommunicationPanel();
				putValue(NAME, "Show >>");
				putValue(SHORT_DESCRIPTION, SHOW_TOOLTIP);

				patternControls.setPlaylistShown(false); //set a flag to let Pattern Controls know if it should enable Playlist Add
				playlistControls.switchOffMainControls();

				checkPlaylistStepControls();
				playlistControls.disableStepBack();
				playlistControls.disableStepForward();
			}
			counter++;
		}

		private void checkPlaylistStepControls() {
			Action playlistStepBackAction = playlistControls.getStepBackAction();
			Action playlistStepForwardAction = playlistControls.getStepForwardAction();
			if (playlistStepBackAction.isEnabled()) {
				playlistStepBackWasEnabled = true;
			} else {
				playlistStepBackWasEnabled = false;
			}
			if (playlistStepForwardAction.isEnabled()) {
				playlistStepForwardWasEnabled = true;
			} else {
				playlistStepForwardWasEnabled = false;
			}
		}
	}
}
