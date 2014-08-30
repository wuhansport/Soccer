package com.whs.soccer.utils;

import java.io.IOException;
import java.io.Reader;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * Class StringUtils
 * @author antoniochen
 */
public final class StringUtils {
    /**
     * The <tt>0-length</tt> String.
     */
    public static final String EMPTY_STRING = "";

    /**
     * Reverses the specified string.
     * @param s The string to reverse.
     * @return A reversed string.
     * @see #reverse(String, int, int)
     */
    public static String reverse(String s) {
        return reverse(s, 0, s.length());
    }

    /**
     * Reverses the specified string.
     * @param s The string to reverse.
     * @param start The inclusive beginning index of the <em>s</em>.
     * @param end The exclusive end index of the <em>s</em>.
     * @return A reversed string.
     * @see #reverse(String)
     */
    public static String reverse(String s, int start, int end) {
        final char[] data = new char[end - start];
        for (int i = 0; --end >= start; ++i) {
            data[i] = s.charAt(end);
        }

        return new String(data);
    }

    /**
     * Reverses the specified character array.
     * @param data The array to reverse.
     * @see #reverse(char[], int, int)
     */
    public static void reverse(char[] data) {
        reverse(data, 0, data.length);
    }

    /**
     * Reverses the specified character array.
     * @param data The array to reverse.
     * @param start The inclusive beginning index of the <em>data</em>.
     * @param end The exclusive end index of the <em>data</em>.
     * @see #reverse(char[])
     */
    public static void reverse(char[] data, int start, int end) {
        for (--end; start < end; ++start, --end) {
            final char temp = data[start];
            data[start] = data[end];
            data[end]   = temp;
        }
    }

    /**
     * Converts the specified byte array to a hexadecimal string.
     * @param data The array to convert.
     * @param lower Whether to convert a lower hexadecimal string, using "abcdef".
     * @return The hexadecimal string.
     * @see #toHexString(byte[], int, int, boolean)
     * @see #toHexString(Formatter, byte[], int, int, boolean)
     */
    public static String toHexString(byte[] data, boolean lower) {
        return toHexString(data, 0, data.length, lower);
    }

    /**
     * Converts the specified byte array to a hexadecimal string.
     * @param data The array to convert.
     * @param start The inclusive beginning index of the <em>data</em>.
     * @param end The exclusive end index of the <em>data</em>.
     * @param lower Whether to convert a lower hexadecimal string, using "abcdef".
     * @return The hexadecimal string.
     * @see #toHexString(byte[], boolean)
     * @see #toHexString(Formatter, byte[], int, int, boolean)
     */
    public static String toHexString(byte[] data, int start, int end, boolean lower) {
        final Formatter out = new Formatter(new StringBuilder((end - start) << 1));
        toHexString(out, data, start, end, lower);
        return out.toString();
    }

    /**
     * Converts the specified byte array to a hexadecimal string.
     * @param out The output destination of the converted hexadecimal string.
     * @param data The array to convert.
     * @param start The inclusive beginning index of the <em>data</em>.
     * @param end The exclusive end index of the <em>data</em>.
     * @param lower Whether to convert a lower hexadecimal string, using "abcdef".
     * @see #toHexString(byte[], boolean)
     * @see #toHexString(byte[], int, int, boolean)
     */
    public static void toHexString(Formatter out, byte[] data, int start, int end, boolean lower) {
        final String format = (lower ? "%02x" : "%02X");
        for (; start < end; ++start) {
            out.format(format, data[start]);
        }
    }

    /**
     * Converts the specified hexadecimal string to a byte array.
     * @param hex The hexadecimal string to convert.
     * @return The converted byte array.
     * @see #toByteArray(CharSequence, int, int)
     * @see #toByteArray(CharSequence, byte[], int)
     * @see #toByteArray(CharSequence, int, int, byte[], int)
     */
    public static byte[] toByteArray(CharSequence hex) {
        return toByteArray(hex, 0, hex.length());
    }

    /**
     * Converts the specified hexadecimal string to a byte array.
     * @param hex The hexadecimal string to convert.
     * @param start The inclusive beginning index of the <em>hex</em>.
     * @param end The exclusive end index of the <em>hex</em>.
     * @return The converted byte array.
     * @see #toByteArray(CharSequence)
     * @see #toByteArray(CharSequence, byte[], int)
     * @see #toByteArray(CharSequence, int, int, byte[], int)
     */
    public static byte[] toByteArray(CharSequence hex, int start, int end) {
        final byte[] result = new byte[(end - start) >> 1];
        toByteArray(hex, start, end, result, 0);
        return result;
    }

    /**
     * Converts the specified hexadecimal string into the specified byte array.
     * @param hex The hexadecimal string to convert.
     * @param out The byte array to store the converted result.
     * @param offset The starting offset of the <em>out</em>.
     * @see #toByteArray(CharSequence)
     * @see #toByteArray(CharSequence, int, int)
     * @see #toByteArray(CharSequence, int, int, byte[], int)
     */
    public static void toByteArray(CharSequence hex, byte[] out, int offset) {
        toByteArray(hex, 0, hex.length(), out, offset);
    }

    /**
     * Converts the specified hexadecimal string into the specified byte array.
     * @param hex The hexadecimal string to convert.
     * @param start The inclusive beginning index of the <em>hex</em>.
     * @param end The exclusive end index of the <em>hex</em>.
     * @param out The byte array to store the converted result.
     * @param offset The starting offset of the <em>out</em>.
     * @see #toByteArray(CharSequence)
     * @see #toByteArray(CharSequence, int, int)
     * @see #toByteArray(CharSequence, byte[], int)
     */
    public static void toByteArray(CharSequence hex, int start, int end, byte[] out, int offset) {
        for (; start < end; start += 2, ++offset) {
            out[offset] = (byte)((Character.digit(hex.charAt(start), 16) << 4) + Character.digit(hex.charAt(start + 1), 16));
        }
    }

    /**
     * This utility class cannot be instantiated.
     */
    private StringUtils() {
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String loadFromReader(Reader reader) throws IOException {
        int len;
        char[] buf = new char[8192];
        StringBuilder sb = new StringBuilder();
        while ((len = reader.read(buf)) > 0)
            sb.append(buf, 0, len);
        return sb.toString();
    }
    
    public static String toQuotedStringValue(String str, char quoteChar) {
        return str.replace("\\", "\\\\").replace(String.valueOf(quoteChar), "\\" + quoteChar)
                .replace("\r", "\\r").replace("\n", "\\n");
    }
    
    public static String replaceWithArguments(String str, String[] args) {
        Pattern p = Pattern.compile("\\$(\\d+)");
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int idx = Integer.parseInt(m.group(1)) - 1;
            String replacement = idx < args.length ? args[idx] : "";
            m.appendReplacement(sb, replacement != null ? Matcher.quoteReplacement(replacement) : "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static String formatSpeed(Context context, long speed) {
        long value = 0;
        if (speed > 0) {
            value = speed;
        }
        return android.text.format.Formatter.formatFileSize(context, value) + "/s";
    }

    public static int parseInteger(Object value, int def) {
        try {
            return value != null ? Integer.parseInt(value.toString()) : def;
        }
        catch (NumberFormatException ex) {
            return def;
        }
    }

    public static long parseLong(Object value, long def) {
        try {
            return value != null ? Long.parseLong(value.toString()) : def;
        }
        catch(NumberFormatException ex) {
            return def;
        }
    }
}
