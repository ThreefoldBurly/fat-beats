package fatbeats.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import fatbeats.lib.FontConstants;
import fatbeats.lib.OKCancelDialog;

class ConnectionSettingsDialog extends OKCancelDialog {
	private static final long serialVersionUID = 1178292267207934750L;

	private NetworkClient networkClient;
	private JTextField addressField, portField, nameField;

	public ConnectionSettingsDialog(Frame ownerFrame, NetworkClient networkClient) {
		super(ownerFrame, "Connection Settings");
		this.networkClient = networkClient;

		backgroundPanel.setLayout(new BorderLayout());
		backgroundPanel.add(createAddressPanel(), BorderLayout.NORTH);
		backgroundPanel.add(createNamePanel(), BorderLayout.CENTER);
		backgroundPanel.add(createButtonPanel(), BorderLayout.SOUTH);

		this.getContentPane().add(backgroundPanel, BorderLayout.CENTER);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	@Override
	protected void oKPerformed() {
		String addressInput = addressField.getText();
		String portInput = portField.getText();
		String nameInput = nameField.getText();

		if (!"".equals(addressInput)) {
			if (!addressInput.equals(networkClient.getServerAddress())) {
				networkClient.setServerAddress(addressInput);
				/*gui.incoming.append("---> Server address set to: *" + client.serverAddress
						+ "*\n");*/
				System.out.println("---> Server address set to: *" + networkClient.getServerAddress() + "*");
			}
		}

		if (!"".equals(portInput)) {
			int intPortInput = Integer.parseInt(portField.getText());
			if (!(networkClient.getServerPort() == intPortInput)) {
				networkClient.setServerPort(intPortInput);
				//gui.incoming.append("---> Port set to: *" + client.serverPort + "*\n");
				System.out.println("---> Server port set to: *" + networkClient.getServerPort() + "*");
			}
		}

		if (!"".equals(nameInput)) {
			if (!nameInput.equals(networkClient.getUserName())) {
				//String oldName = client.userName;
				networkClient.setUserName(nameInput);
				if (networkClient.isConnected()) {
					/*client.writer.println("---> " + oldName + " has changed his name to *"
							+ client.userName + "*");
					client.writer.flush();*/
				} else {
					//gui.incoming.append("---> Username set to: *" + client.userName + "*\n");
					System.out.println("---> Username set to: *" + networkClient.getUserName() + "*");
				}
			}
		}

		this.setVisible(false);
		this.dispose();
	}

	private JPanel createAddressPanel() {
		JPanel lowerLevelAddressPanel = new JPanel();
		JLabel addressLabel = new JLabel("Server address: ");
		addressField = new JTextField(12);
		addressField.addActionListener(getOKListener());
		addressField.setMargin(new Insets(0, 2, 0, 0));
		if (!networkClient.getServerAddress().equals("none")) {
			addressField.setText(networkClient.getServerAddress());
		}
		JLabel portLabel = new JLabel("Port: ");
		portField = new JTextField(4);
		portField.addActionListener(getOKListener());
		portField.setMargin(new Insets(0, 2, 0, 0));
		if (!(networkClient.getServerPort() == -1)) {
			portField.setText(Integer.toString(networkClient.getServerPort()));
		}
		lowerLevelAddressPanel.add(addressLabel);
		lowerLevelAddressPanel.add(addressField);
		lowerLevelAddressPanel.add(portLabel);
		lowerLevelAddressPanel.add(portField);
		TitledBorder addressBorder = BorderFactory.createTitledBorder("Connection details");
		addressBorder.setTitleFont(FontConstants.SMALL_FONT_PLAIN);
		addressBorder.setBorder(new LineBorder(Color.GRAY));
		lowerLevelAddressPanel.setBorder(addressBorder);
		JPanel addressPanel = new JPanel();
		addressPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		addressPanel.add(lowerLevelAddressPanel);

		return addressPanel;
	}

	private JPanel createNamePanel() {
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Enter your name here: ");
		nameField = new JTextField(17);
		nameField.addActionListener(getOKListener());
		nameField.setMargin(new Insets(0, 2, 0, 0));
		if (!networkClient.getUserName().equals("unknown user")) {
			nameField.setText(networkClient.getUserName());
		}
		namePanel.add(nameLabel);
		namePanel.add(nameField);

		return namePanel;
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(getOKButton());
		buttonPanel.add(getCancelButton());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 5));

		return buttonPanel;
	}
}
