package Posiscan.Secugen.utilies;

import java.util.Arrays;

public class HexConversion {

	public HexConversion() {
		// TODO Auto-generated constructor stub
	}

	public byte[] pack_stringtohex(String numString, int packBytes) {
		byte[] hexData = new byte[packBytes];
		try {

			long lint = Long.parseLong(numString);

			hexData = PackHexString(lint, packBytes);
		} catch (Exception e) {
			// PrintLogs.printD("exception " + e.getMessage());
			Arrays.fill(hexData, (byte) 0);
		}
		return hexData;

	}

	public static byte[] PackHexString(long number, int packBytes) {
		byte[] packedBytes = new byte[packBytes];

		int byteno = 0;

		while (byteno < packBytes) {
			packedBytes[byteno++] = (byte) (number & 0xFF);
			number = number >> 8;
		}

		return packedBytes;
	}

	public int copyByteArray(byte[] destArray, byte[] srcArray, int index) {
		int srclenght = srcArray.length;
		int drtlength = destArray.length;

		if (drtlength >= srclenght + index) {
			for (int i = 0; i < srcArray.length; i++) {
				destArray[index + i] = srcArray[i];
			}
			return 1;
		} else {
			return 0;
		}

	}

	public int subByteArray(byte[] destArray, byte[] srcArray, int index,
			int noofbytes) {
		if (destArray.length >= noofbytes) {
			for (int i = 0; i < noofbytes; i++) {
				destArray[i] = srcArray[i + index];
			}
			return 1;
		} else {
			return 0;
		}

	}

	public String getHexString(byte[] bytes) {
		try {
			// if (PrintLogs.logs_enable) {
			StringBuffer result = new StringBuffer();
			for (byte b : bytes) {
				result.append(String.format("%02X ", b));
				result.append(" "); // delimiter
			}
			return result.toString();
			// }
		} catch (Exception e) {
			// PrintLogs.printD(" Exception getHexString -->  " +
			// e.getMessage());
		}
		return null;
	}

}
