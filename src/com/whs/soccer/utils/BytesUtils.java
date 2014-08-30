package com.whs.soccer.utils;

public class BytesUtils {
	public static short byte2short(byte[] bytes) {  
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));  
    } 
	
	public static int byte2Int(byte[] b) {
		int intValue = 0;
		int len = b.length;
		for (int i = 0; i < len; i++) {
			intValue += (b[i] & 0xff) << (8 * (3-i));	
		}
		return intValue;
	}
	
	public static float byte2float(byte[] b) {
		int value;
		value = b[0];
		value &= 0xff;
		value |= ((long) b[1] << 8);
		value &= 0xffff;
		value |= ((long) b[2] << 16);
		value &= 0xffffff;
		value |= ((long) b[3] << 24);
		return Float.intBitsToFloat(value);
	}
}