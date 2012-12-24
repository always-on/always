package edu.wpi.always.cm.perceptors.physical.pir;

import java.nio.*;

public class SingleByteBuffer {

   private byte[] buffer = new byte[1];
   private ByteBuffer nativeBuffer = ByteBuffer.wrap(buffer);

   public Buffer getNativeBuffer () {
      return nativeBuffer;
   }

   public byte getValue () {
      return buffer[0];
   }
}
