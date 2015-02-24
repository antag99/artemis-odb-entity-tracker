package net.namekdev.entity_tracker.network;

import net.namekdev.entity_tracker.ui.EntityTrackerMainWindow;

public class NetworkWindowConnector extends EntityTrackerCommunicator {
	private EntityTrackerMainWindow window;

	public NetworkWindowConnector(EntityTrackerMainWindow window) {
		this.window = window;
	}



}
