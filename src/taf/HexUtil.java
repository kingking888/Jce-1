package taf;

import java.nio.ByteBuffer;

public class HexUtil {
    private static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final byte[] emptybytes = new byte[0];

    public static String byte2HexStr(byte b) {
        char[] cArr = new char[2];
        cArr[1] = digits[b & 15];
        cArr[0] = digits[((byte) (b >>> 4)) & 15];
        return new String(cArr);
    }

    public static String bytes2HexStr(ByteBuffer byteBuffer) {
        ByteBuffer duplicate = byteBuffer.duplicate();
        duplicate.flip();
        byte[] bArr = new byte[duplicate.limit()];
        duplicate.get(bArr);
        return bytes2HexStr(bArr);
    }

    public static String bytes2HexStr(byte[] bArr) {
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            cArr[(i * 2) + 1] = digits[b & 15];
            cArr[(i * 2) + 0] = digits[((byte) (b >>> 4)) & 15];
        }
        return new String(cArr);
    }

    public static byte char2Byte(char c) {
        return (c < '0' || c > '9') ? (c < 'a' || c > 'f') ? (c < 'A' || c > 'F') ? (byte) 0 : (byte) ((c - 65) + 10) : (byte) ((c - 97) + 10) : (byte) (c - 48);
    }

    public static byte hexStr2Byte(String str) {
        return (str == null || str.length() != 1) ? (byte) 0 : char2Byte(str.charAt(0));
    }

    public static byte[] hexStr2Bytes(String str) {
        if (str == null || str.equals("")) {
            return emptybytes;
        }
        byte[] bArr = new byte[(str.length() / 2)];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) ((char2Byte(str.charAt(i * 2)) * 16) + char2Byte(str.charAt((i * 2) + 1)));
        }
        return bArr;
    }
}
