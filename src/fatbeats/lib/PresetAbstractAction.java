package fatbeats.lib;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public abstract class PresetAbstractAction extends AbstractAction {

	private static final long serialVersionUID = -5721813228041189665L;

	protected PresetAbstractAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
		super(text);
		putValue(SHORT_DESCRIPTION, tooltip);
		putValue(MNEMONIC_KEY, mnemonic);
		putValue(ACCELERATOR_KEY, accelerator);
	}
}
