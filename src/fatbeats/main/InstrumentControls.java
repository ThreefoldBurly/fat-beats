package fatbeats.main;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.*;

import fatbeats.lib.FontConstants;
import fatbeats.lib.PresetAbstractAction;
import fatbeats.lib.RecentFilesMenu;

class InstrumentControls {
	private static final long serialVersionUID = 9080983054030022199L;

	private final static String[] INSTRUMENT_NAMES = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
			"Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell",
			"Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"};
	private ArrayList<JToggleButton> intrumentButtons;
	private PatternPanel patternPanel;

	InstrumentControls(PatternPanel pp) {
		patternPanel = pp;
		intrumentButtons = new ArrayList<JToggleButton>();
	}

	JPanel createInstrumentsPanel() {
		JPanel instrumentsPanel = new JPanel();
		GridLayout grid = new GridLayout(16, 1);
		grid.setVgap(2);
		instrumentsPanel.setLayout(grid);
		for (int i = 0; i < 16; i++) {
			JToggleButton instrumentButton = new JToggleButton(new InstrumentButtonAction(INSTRUMENT_NAMES[i], null,
					null, i));
			instrumentButton.setFont(FontConstants.VERY_SMALL_FONT_BOLD);
			instrumentsPanel.add(instrumentButton);
			intrumentButtons.add(instrumentButton);
		}
		instrumentsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

		return instrumentsPanel;
	}

	JMenu createInstrumentsMenu() {
		JMenu instrumentsMenu = new JMenu("Instruments");
		instrumentsMenu.setMnemonic(KeyEvent.VK_I);

		JMenuItem inverseItem = new JMenuItem(new InverseAction("Inverse", "Click to inverse instruments",
				KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK)));
		JMenuItem disableItem = new JMenuItem(new DisableAction("Disable All", "Click to disable all instruments",
				KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK)));
		JMenuItem enableItem = new JMenuItem(new EnableAction("Enable All", "Click to enable all instruments",
				KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK)));

		instrumentsMenu.add(inverseItem);
		instrumentsMenu.add(disableItem);
		instrumentsMenu.add(enableItem);
		RecentFilesMenu.paintMenuItems(instrumentsMenu.getMenuComponents(), Color.WHITE);

		return instrumentsMenu;
	}

	private void inverse() { //all three methods set on separate threads to avoid visual glitches
		Thread inverseThread = new Thread(new InversingJob());
		inverseThread.start();
	}

	private void enableAll() {
		Thread enableThread = new Thread(new EnablingJob());
		enableThread.start();
	}

	private void disableAll() {
		Thread disableThread = new Thread(new DisablingJob());
		disableThread.start();
	}

	private class InstrumentButtonAction extends PresetAbstractAction {
		private static final long serialVersionUID = -4898391973124085103L;

		private final static String ON_TOOLTIP = "Click to disable instrument";
		private final static String OFF_TOOLTIP = "Click to enable instrument";
		private int counter;

		public InstrumentButtonAction(String text, Integer mnemonic, KeyStroke accelerator, int row) {
			super(text, ON_TOOLTIP, mnemonic, accelerator);
			putValue(DEFAULT, new Integer(row));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int row = (Integer) getValue(DEFAULT);

			if (counter % 2 == 0) {
				patternPanel.disableRow(row);
				putValue(SHORT_DESCRIPTION, OFF_TOOLTIP);
			} else {
				patternPanel.enableRow(row);
				putValue(SHORT_DESCRIPTION, ON_TOOLTIP);
			}
			counter++;
		}
	}

	private class InverseAction extends PresetAbstractAction {
		private static final long serialVersionUID = 5952236329163039470L;

		public InverseAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			inverse();
		}
	}

	private class EnableAction extends PresetAbstractAction {
		private static final long serialVersionUID = -6532155743226116222L;

		public EnableAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			enableAll();
		}
	}

	private class DisableAction extends PresetAbstractAction {
		private static final long serialVersionUID = -8673582968339239833L;

		public DisableAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			disableAll();
		}
	}

	private class InversingJob implements Runnable {
		@Override
		public void run() {
			for (JToggleButton jtb : intrumentButtons) {
				jtb.doClick();
			}
		}

	}

	private class EnablingJob implements Runnable {
		@Override
		public void run() {
			for (JToggleButton jtb : intrumentButtons) {
				if (jtb.isSelected()) {
					jtb.doClick();
				}
			}
		}
	}

	private class DisablingJob implements Runnable {
		@Override
		public void run() {
			for (JToggleButton jtb : intrumentButtons) {
				if (!jtb.isSelected()) {
					jtb.doClick();
				}
			}
		}
	}
}
