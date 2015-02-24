package net.namekdev.entity_tracker.network.base;

public interface RawConnectionCommunicator {
	void connected(RawConnectionOutputListener output);
	void disconnected();

	/** should return consumed bytes count */
	int bytesReceived(byte[] bytes, int offset, int length);
}
