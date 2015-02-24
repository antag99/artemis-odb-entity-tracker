package net.namekdev.entity_tracker.network.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.artemis.utils.Bag;

/**
 * Multi-threaded multi-client server.
 *
 * @author Namek
 */
public class Server implements Runnable {
	public static final int DEFAULT_PORT = 21542;

	protected int listeningPort;
	protected ServerSocket socket;
	protected volatile boolean isRunning;
	protected Thread runningThread;
	protected final Bag<ClientSocketListener> clients = new Bag<ClientSocketListener>();
	protected final Bag<Thread> clientThreads = new Bag<Thread>();

	protected RawConnectionCommunicatorProvider clientListenerProvider;
	protected int listeningBitset;


	public Server(RawConnectionCommunicatorProvider clientListenerProvider) {
		this(clientListenerProvider, DEFAULT_PORT);
	}

	public Server(RawConnectionCommunicatorProvider clientListenerProvider, int listeningPort) {
		this.clientListenerProvider = clientListenerProvider;
		this.listeningPort = listeningPort;
	}

	/**
	 * Starts listening in new thread.
	 */
	public Server start() {
		runningThread = new Thread(this);
		runningThread.start();

		return this;
	}

	public void stop() {
		this.isRunning = false;

		for (int i = 0, n = clients.size(); i < n; ++i) {
			ClientSocketListener client = clients.get(i);
			client.stop();
			clients.remove(client);
		}

		for (int i = 0, n = clientThreads.size(); i < n; ++i) {
			Thread clientThread = clientThreads.get(i);
//			clientThread.interrupt(); thread should die anyway
			clientThreads.remove(clientThread);
		}

		try {
			socket.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't shutdown server.", e);
		}

	}

	@Override
	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}

		try {
			socket = new ServerSocket(listeningPort);
			isRunning = true;
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't start server on port " + listeningPort, e);
		}

		while (isRunning) {
			Socket clientSocket = null;
			try {
				clientSocket = socket.accept();
			}
			catch (IOException e) {
				if (isRunning) {
					throw new RuntimeException("Error accepting client connection", e);
				}
			}

			ClientSocketListener clientListener = createSocketListener(clientSocket);
			Thread clientThread = new Thread(clientListener);

			clients.add(clientListener);
			clientThreads.add(clientThread);
			clientThread.start();
		}
	}

	protected ClientSocketListener createSocketListener(Socket socket) {
		RawConnectionCommunicator connectionListener = clientListenerProvider.getListener(socket.getRemoteSocketAddress().toString());
		return new ClientSocketListener(socket, connectionListener);
	}


	protected class ClientSocketListener implements Runnable, RawConnectionOutputListener {
		Socket socket;
		InputStream input;
		OutputStream output;
		RawConnectionCommunicator connectionListener;

		public ClientSocketListener(Socket socket, RawConnectionCommunicator connectionListener) {
			this.socket = socket;
			this.connectionListener = connectionListener;
		}

		@Override
		public void run() {
			try {
				byte[] buffer = new byte[10240];
				int pos = 0;

				input = socket.getInputStream();
				output = socket.getOutputStream();

				if (socket.isConnected()) {
					connectionListener.connected(this);
				}

				// read as much as possible and then pass it all to listener
				while (isRunning && !socket.isClosed()) {
					 int n = input.available();

					 if (pos == buffer.length || n == 0) {
						 if (pos > 0) {
							 connectionListener.bytesReceived(buffer, 0, pos);
							 pos = 0;
						 }

						 Thread.sleep(100);
						 continue;
					 }

					 n = Math.min(n, buffer.length);

					 input.read(buffer, 0, n);
					 pos += n;
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}

			clients.remove(this);
			clientThreads.remove(Thread.currentThread());

			connectionListener.disconnected();
		}

		public void stop() {
			try {
				input.close();
			} catch (Exception e) { }

			try {
				output.close();
			} catch (Exception e) { }

			try {
				socket.close();
			} catch (Exception e) { }
		}

		@Override
		public void send(byte[] buffer, int offset, int length) {
			try {
				output.write(buffer, offset, length);
			}
			catch (IOException e) {
				throw new RuntimeException("Couldn't send data to client.", e);
			}
		}
	}

}
