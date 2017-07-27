package fatbeats.lib;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

public abstract class OKCancelDialog extends JDialog {
	private static final long serialVersionUID = 1837416799120630678L;

	protected JPanel backgroundPanel;
	private JButton oKButton, cancelButton;
	private ActionListener oKListener;
	private CancelAction cancelAction;

	protected OKCancelDialog(Frame owner, String title) {
		super(owner, title);
		cancelAction = new CancelAction("Cancel", "Click to exit this window", new Integer(KeyEvent.VK_C));
		backgroundPanel = new JPanel();
		addKeyboardShortcutToComponent(backgroundPanel, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelAction,
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		oKListener = new OKListener();
		oKButton = new JButton("OK");
		oKButton.setToolTipText("Click to confirm");
		oKButton.addActionListener(oKListener);
		oKButton.setMnemonic(KeyEvent.VK_K);
		cancelButton = new JButton(cancelAction);
	}

	public static void addKeyboardShortcutToComponent(JComponent component, KeyStroke shortcut, Action action) {
		String command = (String) action.getValue(Action.NAME);
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(shortcut, command);
		component.getActionMap().put(command, action);
	}

	public static void addKeyboardShortcutToComponent(JComponent component, KeyStroke shortcut, Action action,
			int conditionForUse) {
		String command = (String) action.getValue(Action.NAME);
		component.getInputMap(conditionForUse).put(shortcut, command);
		component.getActionMap().put(command, action);
	}

	protected abstract void oKPerformed();

	protected JButton getOKButton() {
		return oKButton;
	}

	protected JButton getCancelButton() {
		return cancelButton;
	}

	protected JPanel getBackgroundPanel() {
		return backgroundPanel;
	}

	protected ActionListener getOKListener() {
		return oKListener;
	}

	protected CancelAction getCancelAction() {
		return cancelAction;
	}

	private class OKListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			oKPerformed();
		}
	}

	private class CancelAction extends AbstractAction {
		private static final long serialVersionUID = 3019325091976115676L;

		public CancelAction(String text, String tooltip, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, tooltip);
			putValue(MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			OKCancelDialog.this.setVisible(false);
			OKCancelDialog.this.dispose();
		}
	}
}
