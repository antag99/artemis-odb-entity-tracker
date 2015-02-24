package net.namekdev.entity_tracker.utils.serialization;

import java.util.BitSet;

public class NetworkDeserializer extends NetworkSerialization {
//	protected static final int SUPER_TYPE_NONE = TYPE_NONE;
//	protected static final int SUPER_TYPE_ARRAY = TYPE_ARRAY;
//
//	private byte _currentType = TYPE_NONE;
//	private byte _currentSuperType = SUPER_TYPE_NONE;
//	private int _currentTypePos = 0;
//	private int _currentTypeSize = 0;
//	private int _currentSuperTypePos = 0;
//	private int _currentSuperTypeSize = 0;

	private byte[] _buffer, _source;
	private int _pos = 0, _sourcePos, _sourceMaxPos;
	private final DeserializeResult _deserializeResult = new DeserializeResult();


	public NetworkDeserializer() {
		this(10240);
	}

	public NetworkDeserializer(int internalBufferSize) {
		_buffer = new byte[internalBufferSize];
	}

//	public DeserializeResult continueDetection(byte[] bytes, int offset, int length) {
//		for (int i = offset, end = offset + length; i < end; ++i) {
//			byte b = bytes[i];
//
//			if (_currentType == TYPE_NONE) {
//				if (b == TYPE_INT) {
//					_currentType = TYPE_INT;
//					consume(bytes, i, 4);
//				}
//				else if (b == TYPE_STRING) {
//
//				}
//				// else... TODO
//			}
//			else {
//
//			}
//
//		}
//
//
//		return _deserializeResult;
//	}
//
//	private void consume(byte[] bytes, int startIndex, int byteCount) {
//		for (int i = startIndex, j = 0; j < byteCount; ++j, ++i) {
//			_buffer[j] = bytes[i];
//		}
//	}




	public void setSource(byte[] bytes, int offset, int length) {
		_source = bytes;
		_sourcePos = offset;
		_sourceMaxPos = offset + length;
	}

	public byte readByte() {
		checkType(TYPE_BYTE);
		return _source[_sourcePos++];
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
			sb.append(_source[_sourcePos++]);
		}

		return sb.toString();
	}

	public BitSet readBitSet() {
		// TODO
		throw new RuntimeException("not yet implemented");
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


	public static class DeserializeResult {
		public int bytesConsumed = 0;
		public boolean hasFullObject = false;
		public byte type;
		public Object data;


		public String getString() {
			return (String) data;
		}

		public BitSet getBitSet() {
			return (BitSet) data;
		}
	}
}
