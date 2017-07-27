package fatbeats.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import fatbeats.lib.MyGUI;

class Launcher {

	private static void showGUI() {
		MyGUI fatBeatsGUI = new MainGUI();
		JFrame frame = fatBeatsGUI.getGUIframe();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showGUI();
			}
		});
	}
}
