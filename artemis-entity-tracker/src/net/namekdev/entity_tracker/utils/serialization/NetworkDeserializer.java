package net.namekdev.entity_tracker.utils.serialization;

import java.util.BitSet;

public class NetworkDeserializer extends NetworkSerialization {
	private byte[] _source;
	private int _sourcePos, _sourceBeginPos;


	public NetworkDeserializer() {
	}

	public void setSource(byte[] bytes, int offset, int length) {
		_source = bytes;
		_sourcePos = offset;
		_sourceBeginPos = offset;
	}

	public int getConsumedBytesCount() {
		return _sourcePos - _sourceBeginPos;
	}

	public byte readByte() {
		checkType(TYPE_BYTE);
		return readRawByte();
	}

	public short readShort() {
		checkType(TYPE_SHORT);
		return readRawShort();
	}

	public int readInt() {
		checkType(TYPE_INT);
		return readRawInt();
	}

	public long readLong() {
		checkType(TYPE_LONG);
		long value = readRawInt();
		value <<= 32;
		value |= readRawInt();

		return value;
	}

	public String readString() {
		checkType(TYPE_STRING);
		short length = readRawShort();

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; ++i) {
			sb.append((char) _source[_sourcePos++]);
		}

		return sb.toString();
	}

	public BitSet readBitSet() {
		// TODO
		throw new RuntimeException("not yet implemented");
	}

	public byte readRawByte() {
		return _source[_sourcePos++];
	}

	public short readRawShort() {
		short value = (short) _source[_sourcePos++];
		value <<= 8;
		value |= _source[_sourcePos++];

		return value;
	}

	protected int readRawInt() {
		int value = (short) _source[_sourcePos++];
		value <<= 8;
		value |= _source[_sourcePos++];
		value <<= 8;
		value |= _source[_sourcePos++];
		value <<= 8;
		value |= _source[_sourcePos++];

		return value;
	}

	protected void checkType(byte type) {
		byte srcType = _source[_sourcePos++];

		if (srcType != type) {
			throw new RuntimeException("Types are divergent, expected: " + type + ", got: " + srcType);
		}
	}
}
