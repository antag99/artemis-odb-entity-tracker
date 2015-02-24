package net.namekdev.entity_tracker;

import java.awt.EventQueue;

import net.namekdev.entity_tracker.connectors.UpdateListener;
import net.namekdev.entity_tracker.network.ExternalInterfaceCommunicator;
import net.namekdev.entity_tracker.network.base.Client;
import net.namekdev.entity_tracker.network.base.Server;
import net.namekdev.entity_tracker.ui.EntityTrackerMainWindow;

public class StandaloneMain {

	public static void main(String[] args) {
		String serverName = args.length > 0 ? args[0] : "127.0.0.1";
		int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : Server.DEFAULT_PORT;

		init(serverName, serverPort);
	}

	public static UpdateListener init(final String serverName, final int serverPort) {
		final EntityTrackerMainWindow window = new EntityTrackerMainWindow();
		final Client client = new Client(new ExternalInterfaceCommunicator(window));

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client.connect(serverName, serverPort);

					window.initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return window;
	}
}
