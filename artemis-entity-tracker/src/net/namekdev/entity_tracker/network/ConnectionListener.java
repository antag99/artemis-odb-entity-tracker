package net.namekdev.entity_tracker.network;

public interface ConnectionListener {
	void connected(ConnectionOutputListener output);
	void disconnected();

	/** should return consumed bytes count */
	int bytesReceived(byte[] bytes, int offset, int length);
}
