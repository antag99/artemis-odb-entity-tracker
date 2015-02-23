package net.namekdev.entity_tracker.network;

public interface ConnectionOutputListener {
	void send(byte[] buffer, int offset, int length);
}
