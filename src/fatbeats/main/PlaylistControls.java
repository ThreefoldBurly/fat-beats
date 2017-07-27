package fatbeats.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fatbeats.lib.PresetAbstractAction;
import fatbeats.lib.RecentFilesMenu;

class PlaylistControls {

	private PatternControls patternControls;
	private PatternPanel patternPanel;

	private JList playlist;
	private DefaultListModel playlistModel;
	private LinkedList<DefaultListModel> playlistStates;
	//step forward mechanism controllers
	private ArrayList<DefaultListModel> statesSnapshot;
	private int numberOfHops;

	//actions
	private DeselectAction deselectAction;
	private AddAction addAction;
	private RemoveAction removeAction;
	private ClearAction clearAction;
	private StepBackAction stepBackAction;
	private StepForwardAction stepForwardAction;

	PlaylistControls(PatternControls patternControls) {
		this.patternControls = patternControls;
		this.patternPanel = patternControls.getPatternPanel();

		playlistModel = new DefaultListModel();
		playlist = new JList(playlistModel);
		playlistStates = new LinkedList<DefaultListModel>();
		playlistStates.addLast(extractPlaylistState()); //adding ZERO-state

		//actions set up
		deselectAction = new DeselectAction("Deselect", "Click to deselect", KeyEvent.VK_E, KeyStroke.getKeyStroke(
				KeyEvent.VK_SPACE, ActionEvent.CTRL_MASK));
		addAction = new AddAction("Add", "Click to add current pattern", KeyEvent.VK_D, KeyStroke.getKeyStroke(
				KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		removeAction = new RemoveAction("Remove", "Click to remove selected element", KeyEvent.VK_V,
				KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		clearAction = new ClearAction("Clear", "Click to clear the playlist", KeyEvent.VK_C, KeyStroke.getKeyStroke(
				KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		stepBackAction = new StepBackAction("Step Back", "Click to step back", null, KeyStroke.getKeyStroke(
				KeyEvent.VK_COMMA, ActionEvent.CTRL_MASK));
		stepForwardAction = new StepForwardAction("Step Forward", "Click to step forward", null,
				KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, ActionEvent.CTRL_MASK));

		deselectAction.setEnabled(false);
		addAction.setEnabled(false);
		removeAction.setEnabled(false);
		clearAction.setEnabled(false);
		stepBackAction.setEnabled(false);
		stepForwardAction.setEnabled(false);
	}

	DefaultListModel getPlaylistModel() {
		return playlistModel;
	}

	AddAction getAddAction() {
		return addAction;
	}

	StepBackAction getStepBackAction() {
		return stepBackAction;
	}

	StepForwardAction getStepForwardAction() {
		return stepForwardAction;
	}

	JMenu createPlaylistMenu() {
		JMenu playlistMenu = new JMenu("Playlist");
		playlistMenu.setMnemonic(KeyEvent.VK_Y);

		JMenuItem deselectItem = new JMenuItem(deselectAction);
		JMenuItem addItem = new JMenuItem(addAction);
		JMenuItem removeItem = new JMenuItem(removeAction);
		JMenuItem clearItem = new JMenuItem(clearAction);
		JMenuItem stepBackItem = new JMenuItem(stepBackAction);
		JMenuItem stepForwardItem = new JMenuItem(stepForwardAction);

		playlistMenu.add(deselectItem);
		playlistMenu.addSeparator();
		playlistMenu.add(addItem);
		playlistMenu.add(removeItem);
		playlistMenu.add(clearItem);
		playlistMenu.add(stepBackItem);
		playlistMenu.add(stepForwardItem);
		RecentFilesMenu.paintMenuItems(playlistMenu.getMenuComponents(), Color.WHITE);

		return playlistMenu;
	}

	JPanel createPlaylistPanel() {
		JPanel playlistPanel = new JPanel(new BorderLayout());
		playlist.addListSelectionListener(new PlaylistSelectionListener());
		playlist.addMouseListener(new PlaylistMouseListener());
		playlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Box playlistButtonsBox = Box.createHorizontalBox();
		JButton addButton = new JButton(addAction);
		addButton.setMnemonic(0);
		JButton removeButton = new JButton(removeAction);
		removeButton.setMnemonic(0);
		playlistButtonsBox.add(addButton);
		playlistButtonsBox.add(Box.createHorizontalStrut(10));
		playlistButtonsBox.add(removeButton);
		playlistButtonsBox.add(Box.createHorizontalGlue());
		playlistButtonsBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		playlistPanel.add(new JScrollPane(playlist), BorderLayout.CENTER);
		playlistPanel.add(playlistButtonsBox, BorderLayout.SOUTH);

		return playlistPanel;
	}

	void clearPlaylistSelection() {
		playlist.clearSelection();
	}

	void switchOnMainControls() {
		if (!playlist.isSelectionEmpty() && !deselectAction.isEnabled()) {
			deselectAction.setEnabled(true);
		}
		if (!patternPanel.hasNothingSelected() && !addAction.isEnabled()) {
			addAction.setEnabled(true);
		}
		if (!playlistModel.isEmpty()) {
			enableRemoveAndClear();
		}
	}

	void switchOffMainControls() {
		if (deselectAction.isEnabled()) {
			deselectAction.setEnabled(false);
		}
		if (addAction.isEnabled()) {
			addAction.setEnabled(false);
		}
		disableRemoveAndClear();
	}

	void enableStepBack() {
		if (!stepBackAction.isEnabled()) {
			stepBackAction.setEnabled(true);
		}
	}

	void disableStepBack() {
		if (stepBackAction.isEnabled()) {
			stepBackAction.setEnabled(false);
		}
	}

	void enableStepForward() {
		if (!stepForwardAction.isEnabled()) {
			stepForwardAction.setEnabled(true);
		}
	}

	void disableStepForward() {
		if (stepForwardAction.isEnabled()) {
			stepForwardAction.setEnabled(false);
		}
	}

	void enableRemoveAndClear() {
		if (!removeAction.isEnabled()) {
			removeAction.setEnabled(true);
		}
		if (!clearAction.isEnabled()) {
			clearAction.setEnabled(true);
		}
	}

	//step back/step forward mechanism methods
	void addPlaylistState() {
		playlistStates.addLast(extractPlaylistState());
		if (!stepBackAction.isEnabled()) { //if sth's been added, we can backtrack
			stepBackAction.setEnabled(true);
		}
		numberOfHops = 0; //resetting the step forward controllers
		statesSnapshot = null;
		if (playlistStates.size() > 50) { //let's limit the mechanism to 50 steps
			playlistStates.removeFirst();
		}
	}

	private void doStepBack() {
		if (numberOfHops == 0) {
			takeSnapshot();
		}
		if (playlistStates.size() > 1) { //do Step Back unless we've reached the initial state
			playlistStates.removeLast();
			numberOfHops++;
			changePlaylistState();
			if (playlistStates.size() == 1) { //we're at the initial state, so let's disable Step Back
				stepBackAction.setEnabled(false);
			}
			if (!stepForwardAction.isEnabled()) { //if we've made a step back, we can go forward
				stepForwardAction.setEnabled(true);
			}
		}
	}

	private void doStepForward() {
		if (numberOfHops > 0) { //do Step Forward unless we've already backtracked all Step Back hops
			playlistStates.addLast(statesSnapshot.get(statesSnapshot.size() - numberOfHops));
			numberOfHops--;
			changePlaylistState();
			if (numberOfHops == 0) {
				stepForwardAction.setEnabled(false); //we've gone the whole way forward, so let's disable Step Forward
			}
			if (!stepBackAction.isEnabled()) { //if we've made a step forward, we can go back
				stepBackAction.setEnabled(true);
			}
		}
	}

	private void takeSnapshot() { //called at each beginning of Step Back sequence - we have to know how to return with Step Forward
		statesSnapshot = new ArrayList<DefaultListModel>();
		for (DefaultListModel stateToCopy : playlistStates) {
			statesSnapshot.add(stateToCopy);
		}
	}

	private DefaultListModel extractPlaylistState() {
		DefaultListModel playlistState = new DefaultListModel();
		for (int i = 0; i < playlistModel.getSize(); i++) {
			PatternMessage playlistElement = (PatternMessage) playlistModel.getElementAt(i);
			playlistState.addElement(playlistElement);
		}
		return playlistState;
	}

	//code economy methods
	private void doPlaylistSelection() {
		PatternMessage selectedItem = (PatternMessage) playlist.getSelectedValue();
		if (selectedItem != null) {
			patternPanel.changePattern(selectedItem.getPatternState());
			patternPanel.addPatternState();
			patternControls.enableBroadcastPlayAndAdd(); //if it was blank before, now we have sth to play or broadcast again
			patternControls.enableUndo(); //we added state, so if Undo has ended - reenable it
			patternControls.disableRedo(); //if we were in the middle of redoing - shut it down
			patternControls.doStop();
			MainGUI.changeFrameName((JFrame) playlist.getTopLevelAncestor(), selectedItem.toString());
			patternPanel.setCurrentName(selectedItem.getPatternName());
			deselectAction.setEnabled(true);
		}
	}

	private void changePlaylistState() {
		playlistModel = playlistStates.getLast();
		playlist.setModel(playlistModel);
		//playlist.revalidate();
		//playlist.repaint();
	}

	private void disableRemoveAndClear() {
		if (removeAction.isEnabled()) {
			removeAction.setEnabled(false);
		}
		if (clearAction.isEnabled()) {
			clearAction.setEnabled(false);
		}
	}

	private void manageRemoveAndClear() {
		if (playlistModel.isEmpty()) {
			disableRemoveAndClear();
		} else {
			enableRemoveAndClear();
		}
	}

	private class PlaylistSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				doPlaylistSelection();
			}
		}
	}

	private class PlaylistMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1 && playlistModel.getSize() == 1) { //fire it only when there's no value change possible - otherwise it's gonna double the pattern changes already reported to PatternPanel by the selection listener
				doPlaylistSelection();
			}
			if (e.getClickCount() == 2) {
				patternControls.doPlay();
			}
		}
	}

	private class DeselectAction extends PresetAbstractAction {
		private static final long serialVersionUID = 1L;

		public DeselectAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			playlist.clearSelection();
			this.setEnabled(false);
		}
	}

	private class AddAction extends PresetAbstractAction {
		private static final long serialVersionUID = 1987032091201423998L;
		private PlaylistAddDialog playlistAddDialog;

		public AddAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent source = (JComponent) e.getSource();
			if (null == playlistAddDialog) {
				playlistAddDialog = new PlaylistAddDialog((JFrame) source.getTopLevelAncestor(), PlaylistControls.this);
			}
			playlist.clearSelection();
			playlistAddDialog.setPatternState(patternPanel.extractPatternState());
			playlistAddDialog.pack();
			playlistAddDialog.setNameFieldText(patternPanel.getCurrentName());
			playlistAddDialog.setVisible(true);
		}
	}

	private class RemoveAction extends PresetAbstractAction {
		private static final long serialVersionUID = -3900860537985959191L;

		public RemoveAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedIndex = playlist.getSelectedIndex();
			if (selectedIndex != -1) { //if remove succeeds...
				playlistModel.remove(selectedIndex);
				addPlaylistState();
				//unless the list is empty, select one element higher and if that way we've reached the top before emptying the list - make one step down 
				int modelSize = playlistModel.getSize();
				if (modelSize == 0) { //Nobody's left, disable removing and deselecting.
					this.setEnabled(false);
					deselectAction.setEnabled(false);
				} else { //now, the list isn't empty yet, select one higher or...
					selectedIndex--;
					if (selectedIndex >= 0) {
						playlist.setSelectedIndex(selectedIndex);
						playlist.ensureIndexIsVisible(selectedIndex);
					} else { //...so we've just removed the top element, then why not make one step back and grab the new top one
						selectedIndex++;
						playlist.setSelectedIndex(selectedIndex);
					}
				}
			}
		}
	}

	private class ClearAction extends PresetAbstractAction {
		private static final long serialVersionUID = 4970123000816371878L;

		public ClearAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			playlistModel.clear();
			addPlaylistState();
			this.setEnabled(false);
			removeAction.setEnabled(false);
		}
	}

	private class StepBackAction extends PresetAbstractAction {
		private static final long serialVersionUID = 4970123000816371878L;

		public StepBackAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doStepBack();
			manageRemoveAndClear(); //if we stepped back into a blank state - disable Remove
		}
	}

	private class StepForwardAction extends PresetAbstractAction {
		private static final long serialVersionUID = -3220920619425972082L;

		public StepForwardAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			doStepForward();
			manageRemoveAndClear(); //if we stepped forward into a blank state - disable Remove
		}
	}
}
