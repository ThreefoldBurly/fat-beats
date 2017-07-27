package fatbeats.main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import fatbeats.lib.FontConstants;
import fatbeats.lib.PresetAbstractAction;

class ClearRowPanel extends JPanel {
	private static final long serialVersionUID = -4347579481515582224L;

	private PatternPanel patternPanel;
	private PatternControls patternControls;

	ClearRowPanel(PatternControls patternControls) {
		this.patternControls = patternControls;
		patternPanel = patternControls.getPatternPanel();
		GridLayout grid = new GridLayout(16, 1);
		grid.setVgap(2);
		this.setLayout(grid);
		for (int i = 0; i < 16; i++) {
			JButton clearRowButton = new JButton(new ClearRowAction("clear", "Click to clear one row", null, null, i));
			clearRowButton.setFont(FontConstants.VERY_SMALL_FONT_BOLD);
			this.add(clearRowButton);
		}
		this.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
	}

	private class ClearRowAction extends PresetAbstractAction {
		private static final long serialVersionUID = -806123961398393233L;

		public ClearRowAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator, int row) {
			super(text, tooltip, mnemonic, accelerator);
			putValue(DEFAULT, new Integer(row));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean shouldAddState = false; //add state only if it's not already blank
			if (!patternPanel.hasNothingSelected()) {
				shouldAddState = true;
			}

			int row = (Integer) getValue(DEFAULT);
			patternPanel.clearRow(row);
			if (shouldAddState) {
				patternPanel.addPatternState();
				patternControls.enableUndo(); //we added state, so if Undo has ended - reenable it
				patternControls.disableRedo(); //if we were in the middle of redoing - shut it down
			}

			if (patternPanel.hasNothingSelected()) { //if pattern's blank there's nothing to play, broadcast or add
				patternControls.disableBroadcastPlayAndAdd();
			}
		}
	}
}
