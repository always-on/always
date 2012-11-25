package edu.wpi.always.cm.perceptors.physical.pir;

import java.io.*;
import java.nio.*;
import java.util.*;

public class CStringBuffer {
	private byte[] buffer;
	private ByteBuffer nativeBuffer;
	
	public CStringBuffer(){
		this(100);
	}
	public CStringBuffer(int size){
		buffer = new byte[size];
		nativeBuffer = ByteBuffer.wrap(buffer);
	}
	public void clear(){
		Arrays.fill(buffer, (byte)0);
	}
	public Buffer getNativeBuffer(){
		return nativeBuffer;
	}
	
	@Override
	public String toString(){
		try {
			return new String(buffer, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
