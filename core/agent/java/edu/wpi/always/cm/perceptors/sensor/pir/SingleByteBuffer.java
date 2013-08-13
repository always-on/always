package edu.wpi.always.cm.perceptors.sensor.pir;

import java.nio.Buffer;
import java.nio.ByteBuffer;

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
