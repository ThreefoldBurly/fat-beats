package fatbeats.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

class NetworkClient {

	private Socket connection;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;

	private String userName, serverAddress;
	private int serverPort;
	private boolean isConnected;
	private boolean isTerminatedByUser;

	public NetworkClient() {
		serverAddress = "none";
		serverPort = -1;
		userName = "unknown user";
		isConnected = false;
	}

	ObjectInputStream getInputStream() {
		return inputStream;
	}

	String getUserName() {
		return userName;
	}

	void setUserName(String userName) {
		this.userName = userName;
	}

	String getServerAddress() {
		return serverAddress;
	}

	void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	int getServerPort() {
		return serverPort;
	}

	void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	boolean isConnected() {
		return isConnected;
	}

	void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	boolean isTerminatedByUser() {
		return isTerminatedByUser;
	}

	void setUpConnection() {
		try {
			connection = new Socket(serverAddress, serverPort);
			outputStream = new ObjectOutputStream(connection.getOutputStream());
			inputStream = new ObjectInputStream(connection.getInputStream());
			isConnected = true;
			isTerminatedByUser = false;
			System.out.println("Client has connected to server");
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	void shutDownConnection() {
		try {
			isTerminatedByUser = true;
			outputStream.close();
			inputStream.close();
			connection.close();
			isConnected = false;
			System.out.println("Client has successfully shut down the connection");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	boolean checkDetails(JFrame guiFrame) {
		boolean hasDetails = false;
		if (serverAddress.equals("none") || serverPort == -1) {
			JOptionPane.showMessageDialog(guiFrame, "Please fill out the connection details first.");
		} else if (userName.equals("(unknown user)")) {
			JOptionPane.showMessageDialog(guiFrame, "Please set the username.");
		} else {
			hasDetails = true;
		}
		return hasDetails;
	}

	void sendPatternMessage(BroadcastMessage message) throws IOException {
		outputStream.writeObject(message);
	}
}
