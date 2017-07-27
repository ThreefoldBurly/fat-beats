package fatbeats.main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.*;

import fatbeats.lib.PresetAbstractAction;
import fatbeats.lib.RecentFilesMenu;

class FileMenu extends RecentFilesMenu {
	private static final long serialVersionUID = -5794175461488989058L;

	private final static File HOME_DIR = new File("C:\\eclipse\\eclipse_workspace\\tinkering.FatBeats\\");
	private File savedFile;

	private PatternPanel patternPanel;
	private PatternControls patternControls;

	FileMenu(PatternControls patternControls) {
		super("File");
		this.patternControls = patternControls;
		patternPanel = patternControls.getPatternPanel();
		savedFile = HOME_DIR;

		this.setMnemonic(KeyEvent.VK_F);
		JMenuItem loadItem = new JMenuItem(new LoadAction("Load...", "Click to load pattern from file", KeyEvent.VK_L,
				KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK)));
		JMenuItem saveItem = new JMenuItem(new SaveAction("Save", "Click to save pattern to file", KeyEvent.VK_S,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)));
		JMenuItem saveAsItem = new JMenuItem(new SaveAsAction("Save as...",
				"Click to save pattern to file and name it", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_S,
						ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK)));
		JMenuItem exitItem = new JMenuItem(new ExitAction("Exit", "Click to exit the program", KeyEvent.VK_X, null));

		this.add(loadItem);
		this.add(saveItem);
		this.add(saveAsItem);
		this.addSeparator();
		this.add(exitItem);
		paintMenuItems(this.getMenuComponents(), Color.WHITE);
	}

	static boolean[] fetchPatternStateFromFile(File file) throws FileNotFoundException, IOException,
			ClassNotFoundException {
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fileIn);
		boolean[] patternState = (boolean[]) ois.readObject();
		ois.close();

		return patternState;
	}

	private void doLoad(File file) {
		boolean[] patternState = null;

		try {
			patternState = fetchPatternStateFromFile(file);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		if (null != patternState) {
			useLoadedDataAndSetControls(patternState, file);
			addRecentItem(new RecentItemAction(file, patternState));
		} else {
			JOptionPane.showMessageDialog(this.getTopLevelAncestor(),
					"Unable to open the designated file (Unsupported file format):\n\"" + file.toString() + "\"",
					"Loading error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doSave(File file) {
		boolean[] patternState = patternPanel.extractPatternState();

		try {
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fileOut);
			oos.writeObject(patternState);
			oos.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		MainGUI.changeFrameName((JFrame) this.getTopLevelAncestor(), file.toString());
		patternPanel.setCurrentName(file.getName());
	}

	//code economy methods
	private void bringUpSaveChooser() {
		File oldSave = savedFile;
		JFileChooser saveChooser = new JFileChooser();
		saveChooser.setCurrentDirectory(savedFile);
		if (!HOME_DIR.equals(savedFile)) {
			saveChooser.setSelectedFile(savedFile);
		}
		saveChooser.showSaveDialog(this.getTopLevelAncestor());
		savedFile = saveChooser.getSelectedFile();
		if (null != savedFile) {
			doSave(savedFile);
		} else {
			savedFile = oldSave;
		}
	}

	private void useLoadedDataAndSetControls(boolean[] patternState, File file) {
		patternPanel.changePattern(patternState);
		patternPanel.resetToLoadedState();
		patternControls.disableUndo(); //if we were in the middle of undoing or redoing - disable both - we've just started a new states sequence
		patternControls.disableRedo();
		patternControls.doStop();
		patternControls.enableBroadcastPlayAndAdd();
		MainGUI.changeFrameName((JFrame) this.getTopLevelAncestor(), file.toString());
		patternPanel.setCurrentName(file.getName());
	}

	private class LoadAction extends PresetAbstractAction {
		private static final long serialVersionUID = -7593236453126488537L;
		File loadedFile = HOME_DIR;

		public LoadAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			File oldLoad = loadedFile;
			JFileChooser loadChooser = new JFileChooser();
			loadChooser.setCurrentDirectory(loadedFile);
			if (!HOME_DIR.equals(loadedFile)) {
				loadChooser.setSelectedFile(loadedFile);
			}
			loadChooser.showOpenDialog(FileMenu.this.getTopLevelAncestor());
			loadedFile = loadChooser.getSelectedFile();
			if (null != loadedFile) {
				doLoad(loadedFile);
			} else {
				loadedFile = oldLoad;
			}
		}
	}

	private class SaveAction extends PresetAbstractAction {
		private static final long serialVersionUID = 2513159993844906503L;

		public SaveAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (HOME_DIR.equals(savedFile)) {
				bringUpSaveChooser();
			} else {
				doSave(savedFile);
			}
		}
	}

	private class SaveAsAction extends PresetAbstractAction {
		private static final long serialVersionUID = -5674671629995071510L;

		public SaveAsAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			bringUpSaveChooser();
		}
	}

	private class RecentItemAction extends AbstractAction {
		private static final long serialVersionUID = -5807484641763125139L;

		File recentFile;
		boolean[] patternState;

		public RecentItemAction(File recentFile, boolean[] patternState) {
			super();
			this.recentFile = recentFile;
			this.patternState = patternState;
			putValue(SHORT_DESCRIPTION, "Click to load file");
			putValue(DEFAULT, recentFile.getName());
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			useLoadedDataAndSetControls(patternState, recentFile);
			highlightRecent((JMenuItem) event.getSource());
		}
	}

	private class ExitAction extends PresetAbstractAction { //it's placed in MainGUI, because it operates directly on MainGUI and passing a networkClient to FileMenu only for the sake of it seems redundant as the client's already here.
		private static final long serialVersionUID = 6804385708302336398L;

		public ExitAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();
			NetworkClient networkClient = patternControls.getNetworkClient();
			if (networkClient.isConnected()) {
				networkClient.shutDownConnection();
			}
			JFrame gUIframe = (JFrame) source.getTopLevelAncestor();
			gUIframe.setVisible(false);
			gUIframe.dispose();
			System.exit(0);
		}
	}
}
