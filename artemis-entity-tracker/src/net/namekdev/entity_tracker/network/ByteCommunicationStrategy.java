package net.namekdev.entity_tracker.network;

/**
 * Deserializes data from network and serializes data sent to the network.
 *
 * @author Namek
 */
public class ByteCommunicationStrategy implements ConnectionListener {
	private ConnectionOutputListener output;

	@Override
	public void connected(ConnectionOutputListener output) {
		this.output = output;
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void bytesReceived(byte[] bytes, int length) {
		// TODO Auto-generated method stub

	}

}
