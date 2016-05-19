package com.sc.rawls.histonet.net;

import java.nio.ByteBuffer;

public class DataConversion {

	public static int byteToInt(byte[] buf)
	{
		return buf[0] + (buf[1] >> 8) + (buf[2] >> 16) + (buf[3] >> 24);
	}
	
	public static float byteToFloat(byte[] buf)
	{
		int bits = (buf[0] & 0xFF) << 24 | (buf[1] & 0xFF) << 16 | (buf[2] & 0xFF) << 8 | (buf[3] & 0xFF);
		return 	Float.intBitsToFloat(bits);
	}
	
	public static byte[] intToByte(int i)
	{
		byte[] buf = new byte[4];
		
		buf[0] = (byte)(i & 0xFF);
		buf[1] = (byte)((i << 8) & 0xFF);
		buf[2] = (byte)((i << 16) & 0xFF);
		buf[3] = (byte)((i << 24) & 0xFF);
		
		return buf;
	}
	
	public static byte[] floatToByte(float f)
	{
		return ByteBuffer.allocate(4).putFloat(f).array();
	}
	
}
