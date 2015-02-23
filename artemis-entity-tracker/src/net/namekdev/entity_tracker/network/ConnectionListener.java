package net.namekdev.entity_tracker.network;

public interface ConnectionListener {
	void connected(ConnectionOutputListener output);
	void disconnected();
	void bytesReceived(byte[] bytes, int length);
}
