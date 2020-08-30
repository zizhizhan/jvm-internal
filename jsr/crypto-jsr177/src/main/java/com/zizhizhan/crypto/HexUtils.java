package com.zizhizhan.crypto;

import java.util.Formatter;

public final class HexUtils {

    private HexUtils() {}

    private static final char[] DIGIT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    public static byte[] decodeHex(final String data) {
        return decodeHex(data.toCharArray());
    }

    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    /**
     * @see com.zizhizhan.jsr177.HexUtils#encodeHexString
     */
    public static String bytes2Hex(final byte[] data) {
        Formatter formatter = new Formatter();
        for (byte b : data) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGIT_CHARS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGIT_CHARS[0x0F & data[i]];
        }
        return out;
    }

    private static byte[] decodeHex(final char[] data) {
        final int len = data.length;
        if ((len & 0x01) != 0) {
            throw new CryptoException("Odd number of characters.");
        }

        final byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    protected static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new CryptoException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}
