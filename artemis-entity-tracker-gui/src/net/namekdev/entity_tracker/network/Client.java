package net.namekdev.entity_tracker.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	protected Socket socket;

	protected String serverName;
	protected int serverPort;


	public Client() {
	}

	public Client(String serverName) {
		this(serverName, Server.DEFAULT_PORT);
	}

	public Client(String serverName, int port) {
		connect(this.serverName, this.serverPort);
	}

	public void connect(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;

		try {
			socket = new Socket(serverName, serverPort);
		}
		catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
