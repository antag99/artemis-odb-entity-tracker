package net.namekdev.entity_tracker.utils.serialization;

import java.util.BitSet;

public class NetworkSerializer extends NetworkSerialization {
	private byte[] _ourBuffer;
	private byte[] _buffer;
	private int _pos;

	private final SerializeResult _serializeResult = new SerializeResult();


	public NetworkSerializer() {
		this(new byte[10240]);
	}

	public NetworkSerializer(byte[] buffer) {
		_ourBuffer = buffer;
	}

	public NetworkSerializer beginPacket() {
		return beginPacket(_ourBuffer);
	}

	public NetworkSerializer beginPacket(byte[] buffer) {
		_pos = 0;
		_buffer = buffer;
//		_buffer[_pos++] = PACKET_BEGIN;
		return this;
	}

	public NetworkSerializer addByte(byte value) {
		_buffer[_pos++] = TYPE_BYTE;
		_buffer[_pos++] = value;
		return this;
	}

	public NetworkSerializer addRawByte(byte value) {
		_buffer[_pos++] = value;
		return this;
	}

	protected void addRawShort(short value) {
		_buffer[_pos++] = (byte) (value >> 8);
		_buffer[_pos++] = (byte) (value);
	}

	public NetworkSerializer addInt(int value) {
		_buffer[_pos++] = TYPE_INT;
		addRawInt(value);
		return this;
	}

	protected void addRawInt(int value) {
		_buffer[_pos++] = (byte) (value >> 24);
		_buffer[_pos++] = (byte) (value >> 16);
		_buffer[_pos++] = (byte) (value >> 8);
		_buffer[_pos++] = (byte) (value);
	}

	public NetworkSerializer addString(String value) {
		_buffer[_pos++] = TYPE_STRING;

		short n = (short) value.length();
		addRawShort(n);

		for (short i = 0; i < n; ++i) {
			_buffer[_pos++] = (byte) value.charAt(i);
		}

		return this;
	}

	public NetworkSerializer addBitset(BitSet bitset) {
		_buffer[_pos++] = TYPE_BITSET;

		int bitsCount = bitset.size();
		addRawInt(bitsCount);

		int i = 0;
		while (i < bitsCount) {
			int value = 0;
			for (int j = 0; j < 32 && j < bitsCount; ++j) {
				boolean bit = bitset.get(i++);
				value |= (bit ? 1 : 0);
				value <<= 1;
			}

			addRawInt(value);
		}

		return this;
	}

	public SerializeResult endPacket() {
//		_buffer[_pos++] = PACKET_END;

		return _buffer == _ourBuffer
			? _serializeResult.setup(_buffer, _pos)
			: new SerializeResult(_buffer, _pos);
	}


	public static class SerializeResult {
		public byte[] buffer;
		public int size;

		private SerializeResult() {
		}

		private SerializeResult(byte[] buffer, int size) {
			this.buffer = buffer;
			this.size = size;
		}

		private SerializeResult setup(byte[] buffer, int size) {
			this.buffer = buffer;
			this.size = size;
			return this;
		}
	}
}
