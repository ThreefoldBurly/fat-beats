package fatbeats.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private ArrayList<ObjectOutputStream> streamsToClients;
	private int numberOfConnections;

	public static void main(String[] args) {
		new Server().go();
	}

	public void go() {
		streamsToClients = new ArrayList<ObjectOutputStream>();
		try {
			ServerSocket serverSock = new ServerSocket(4242);
			while (true) {
				Socket socketToClient = serverSock.accept();
				ObjectInputStream inputStream = new ObjectInputStream(socketToClient.getInputStream());
				ObjectOutputStream outputStream = new ObjectOutputStream(socketToClient.getOutputStream());
				streamsToClients.add(outputStream);
				numberOfConnections++;

				Thread handlingThread = new Thread(new ClientHandling(socketToClient, inputStream, outputStream));
				handlingThread.setName("ClientHandling_" + (numberOfConnections - 1));
				handlingThread.start();

				System.out.println("got a connection");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void tellEveryone(Object message) {
		for (ObjectOutputStream oos : streamsToClients) {
			try {
				oos.writeObject(message);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public class ClientHandling implements Runnable {
		private Socket socketToClient;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;

		ClientHandling(Socket socketToClient, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
			this.socketToClient = socketToClient;
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		@Override
		public void run() {
			String clientAddress = socketToClient.getInetAddress().toString();
			Object message;
			try {
				while ((message = inputStream.readObject()) != null) {
					System.out.println("read object");
					tellEveryone(message);
				}
			} catch (IOException ex) {
				streamsToClients.remove(outputStream);
				ex.printStackTrace();
				System.out.println();
				System.out.println("Removing client @" + clientAddress + " from list");
				System.out.println();
				System.out.println(Thread.currentThread().getName() + " thread is exiting");
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
	}
}
