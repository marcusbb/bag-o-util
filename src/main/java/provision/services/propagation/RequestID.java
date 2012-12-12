package provision.services.propagation;

import java.net.InetAddress;
import java.rmi.server.UID;

/**
 * 
 * Uses {@link UID} as the basis for generating a unique id upon construction.
 * Padding and formatting the resulting string.
 * 
 * @author mhogan
 *
 */
public class RequestID  {

	
	private StringBuilder id;

	public RequestID() throws Exception {
		UID uid = new UID();
		MyDataOutput output = new MyDataOutput();
		uid.write(output);

		id = new StringBuilder(Long.toString(output.time, Character.MAX_RADIX)).append(pad(4, Integer.toString(output.count, Character.MAX_RADIX)));

		byte[] addressBytes = InetAddress.getLocalHost().getAddress();
		for (byte b : addressBytes) {
			int addressByte = (0xFF & (int) b);
			String byteString = Integer.toString(addressByte, 16);
			if (byteString.length() == 1) {
				id.append('0');
			}
			id.append(byteString);
		}
		id.append(Integer.toString(output.unique, Character.MAX_RADIX));
	}
	/**
	 * 
	 * @param zeros
	 * @param aString
	 * @return
	 */
	private StringBuilder pad(int zeros, String aString) {
		int insertIndex = aString.charAt(0) == '-' ? 1 : 0;
		StringBuilder aStringBuilder = new StringBuilder(aString);
		while (aStringBuilder.length() < zeros) {
			aStringBuilder.insert(insertIndex, '0');
		}
		return aStringBuilder;
	}

	public String toString() {
		return id.toString();
	}

	class MyDataOutput implements java.io.DataOutput {
		int count;
		long time;
		int unique;

		public void write(byte[] b) {
		}

		public void write(byte[] b, int off, int len) {
		}

		public void write(int b) {
		}

		public void writeBoolean(boolean v) {
		}

		public void writeByte(int v) {
		}

		public void writeBytes(String s) {
		}

		public void writeChar(int v) {
		}

		public void writeChars(String s) {
		}

		public void writeDouble(double v) {
		}

		public void writeFloat(float v) {
		}

		public void writeInt(int v) {
			unique = v;
		}

		public void writeLong(long v) {
			time = v;
		}

		public void writeShort(int v) {
			count = v;
		}

		public void writeUTF(String str) {
		}
	}

	
}
