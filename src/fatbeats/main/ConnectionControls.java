package fatbeats.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.*;

import fatbeats.lib.FontConstants;
import fatbeats.lib.PresetAbstractAction;
import fatbeats.lib.RecentFilesMenu;

class ConnectionControls {

	private NetworkClient networkClient;
	private PlaylistControls playlistControls;
	private JLabel statusText;

	private ConnectAction connectAction;
	private DisconnectAction disconnectAction;
	private SettingsAction settingsAction;

	ConnectionControls(PatternControls patternControls) {
		networkClient = patternControls.getNetworkClient();
		playlistControls = new PlaylistControls(patternControls);

		connectAction = new ConnectAction("Connect to Server", "Click to connect to server", KeyEvent.VK_N,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		disconnectAction = new DisconnectAction("Disconnect", "Click to disconnect from server", KeyEvent.VK_I,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		settingsAction = new SettingsAction("Settings...", "Click to open connection settings dialog", KeyEvent.VK_T,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		disconnectAction.setEnabled(false);

		patternControls.setPlaylistAddAction(playlistControls.getAddAction());
	}

	PlaylistControls getPlaylistControls() {
		return playlistControls;
	}

	JMenu createConnectionMenu() {
		JMenu connectionMenu = new JMenu("Connection");
		connectionMenu.setMnemonic(KeyEvent.VK_C);

		JMenuItem connectItem = new JMenuItem(connectAction);
		JMenuItem disconnectItem = new JMenuItem(disconnectAction);
		JMenuItem settingsItem = new JMenuItem(settingsAction);
		JMenuItem openChatItem = new JMenuItem("Open Chat...");
		//setting mnemonics
		openChatItem.setMnemonic(KeyEvent.VK_O);
		//setting accelerators
		openChatItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		//TODO:listener dla chatu
		connectionMenu.add(connectItem);
		connectionMenu.add(disconnectItem);
		connectionMenu.add(settingsItem);
		connectionMenu.addSeparator();
		connectionMenu.add(openChatItem);
		RecentFilesMenu.paintMenuItems(connectionMenu.getMenuComponents(), Color.WHITE);

		return connectionMenu;
	}

	JPanel createCommunicationPanel() {
		JPanel communicationPanel = new JPanel(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();

		JTextArea chatArea = new JTextArea(20, 30);
		chatArea.setLineWrap(true);
		chatArea.setWrapStyleWord(true);
		chatArea.setEditable(false);
		chatArea.setMargin(new Insets(3, 3, 3, 3));

		tabbedPane.addTab("Playlist", playlistControls.createPlaylistPanel());
		tabbedPane.addTab("Chat", new JScrollPane(chatArea));

		Box statusBox = Box.createHorizontalBox();
		JLabel statusLabel = new JLabel("Connection status: ");
		statusLabel.setFont(FontConstants.VERY_SMALL_FONT_BOLD);
		statusText = new JLabel("not connected.");
		statusText.setFont(FontConstants.VERY_SMALL_FONT_PLAIN);
		statusBox.add(statusLabel);
		statusBox.add(statusText);
		statusBox.add(Box.createHorizontalGlue());
		statusBox.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));

		communicationPanel.add(tabbedPane, BorderLayout.CENTER);
		communicationPanel.add(statusBox, BorderLayout.SOUTH);
		communicationPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		return communicationPanel;
	}

	private void startListening() {
		Thread listening = new Thread(new ListeningToServer(networkClient.getInputStream()));
		listening.start();
	}

	private class ConnectAction extends PresetAbstractAction {
		private static final long serialVersionUID = -3771559956361437013L;

		public ConnectAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();
			if (networkClient.checkDetails((JFrame) source.getTopLevelAncestor())) {
				networkClient.setUpConnection();
				if (networkClient.isConnected()) {
					startListening();
					System.out.println("---> Connection established @" + networkClient.getServerAddress() + ":"
							+ networkClient.getServerPort());
					statusText.setText("connection established @" + networkClient.getServerAddress() + ":"
							+ networkClient.getServerPort());
					connectAction.setEnabled(false);
					settingsAction.setEnabled(false);
					disconnectAction.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(source.getTopLevelAncestor(), "Unable to reach server @"
							+ networkClient.getServerAddress() + ":" + networkClient.getServerPort()
							+ ".\nPlease check if connection details are set properly.", "Connection error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private class DisconnectAction extends PresetAbstractAction {
		private static final long serialVersionUID = 1004572619408483529L;

		public DisconnectAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			networkClient.shutDownConnection();
			statusText.setText("connection terminated.");
			disconnectAction.setEnabled(false);
			connectAction.setEnabled(true);
			settingsAction.setEnabled(true);
		}
	}

	private class SettingsAction extends PresetAbstractAction {
		private static final long serialVersionUID = -8693988112232902736L;
		ConnectionSettingsDialog settingsDialog;

		public SettingsAction(String text, String tooltip, Integer mnemonic, KeyStroke accelerator) {
			super(text, tooltip, mnemonic, accelerator);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem source = (JMenuItem) e.getSource();
			if (null == settingsDialog) {
				settingsDialog = new ConnectionSettingsDialog((JFrame) source.getTopLevelAncestor(), networkClient);
			}
			settingsDialog.pack();
			settingsDialog.setVisible(true);
		}
	}

	private class ListeningToServer implements Runnable {
		ObjectInputStream inputStream;

		ListeningToServer(ObjectInputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public void run() {
			BroadcastMessage receivedMessage;
			try {
				while ((receivedMessage = (BroadcastMessage) inputStream.readObject()) != null) {
					System.out.println("got an object of class *" + receivedMessage.getClass() + "* from server");
					playlistControls.getPlaylistModel().addElement(receivedMessage);
					playlistControls.clearPlaylistSelection();
				}
			} catch (IOException ex) {
				if (!networkClient.isTerminatedByUser()) {
					statusText.setText("connection to server lost.");
				}
				networkClient.setConnected(false);
				disconnectAction.setEnabled(false);
				connectAction.setEnabled(true);
				settingsAction.setEnabled(true);

				ex.printStackTrace();
				System.out.println();
				System.out.println("ConnectionControls *ListeningToServer* thread exiting");
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
	}
}
