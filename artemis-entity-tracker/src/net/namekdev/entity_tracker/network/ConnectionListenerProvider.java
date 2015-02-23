package net.namekdev.entity_tracker.network;

public interface ConnectionListenerProvider {
	ConnectionListener getListener(String serverName);
}
