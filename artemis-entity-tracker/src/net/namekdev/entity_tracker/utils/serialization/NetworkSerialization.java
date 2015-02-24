package net.namekdev.entity_tracker.utils.serialization;

public class NetworkSerialization {
//	protected final static byte PACKET_BEGIN = 2;
//	protected final static byte PACKET_END = 3;
	protected final static byte TYPE_NONE = 0;
	protected final static byte TYPE_ARRAY = 6;

	// high level types
	public final static byte TYPE_BYTE = 10;
	public final static byte TYPE_SHORT = 11;
	public final static byte TYPE_INT = 12;
	public final static byte TYPE_LONG = 13;
	public final static byte TYPE_STRING = 14;
	public final static byte TYPE_BITSET = 17;


	public static NetworkSerializer createSerializer() {
		return new NetworkSerializer();
	}

	public static NetworkSerializer createSerializer(byte[] buffer) {
		return new NetworkSerializer(buffer);
	}

	public static NetworkDeserializer createDeserializer() {
		return new NetworkDeserializer();
	}
}
