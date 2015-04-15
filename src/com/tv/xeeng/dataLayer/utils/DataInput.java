package com.tv.xeeng.dataLayer.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 * mlib2_IDataInput<br>
 * <p/>
 * Platform-independent interface for handling input from a data stream.
 * 
 * @since mlib2
 */
public class DataInput {
	private DataInputStream mIn;

	public DataInput(byte[] aBytes) {
		mIn = new DataInputStream(new ByteArrayInputStream(aBytes));
	}

	public DataInput(byte[] aBytes, int size) {
		mIn = new DataInputStream(new ByteArrayInputStream(aBytes, 0, size));
	}

	public DataInput(byte[] aBytes, int offset, int size) {
		mIn = new DataInputStream(
				new ByteArrayInputStream(aBytes, offset, size));
	}

	public DataInput(InputStream aInputStream) {
		mIn = new DataInputStream(aInputStream);
	}

	public int available() // throws IOException
	{
		try {
			return mIn.available();
		} catch (Throwable t) {

			return 0;
		}
	}

	public int read(byte[] aByteArray, int aOffset, int aLength) {
		try {
			return mIn.read(aByteArray, aOffset, aLength);
		} catch (Throwable t) {

			return 0;
		}
	}

	public boolean readBoolean() {
		try {
			return mIn.readBoolean();
		} catch (Throwable t) {

			return false;
		}
	}

	public byte readByte() {
		try {
			return (byte) mIn.readUnsignedByte();
		} catch (Throwable t) {

			return 0;
		}
	}

	public short readShort() {
		try {
			return mIn.readShort();
		} catch (Throwable t) {

			return 0;
		}
	}

	public int readInt() {
		try {
			return mIn.readInt();
		} catch (Throwable t) {

			return 0;
		}
	}

	public String readUTF() {
		try {
			return mIn.readUTF();
		} catch (Throwable t) {

			return null;
		}
	}

	public DataInputStream getInternalStream() {
		return mIn;
	}

	/**
	 * Skip a number of bytes from the begining of the file
	 * 
	 * @param aNumBytes
	 *            a number of bytes
	 */
	public int skip(int aNumBytes) {
		try {
			return mIn.skipBytes(aNumBytes);
		} catch (Throwable e) {
			return 0;
		}

	}

	public long readLong() {
		try {
			return mIn.readLong();
		} catch (Throwable t) {

			return 0;
		}
	}
}