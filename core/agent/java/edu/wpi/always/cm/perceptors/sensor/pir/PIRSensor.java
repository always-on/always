package edu.wpi.always.cm.perceptors.sensor.pir;

import edu.wpi.disco.rt.util.Utils;

public class PIRSensor {

   private static boolean haveDevice = false;
   static {
      haveDevice = USBm2Library.INSTANCE.findDevice();
      if ( !haveDevice )
         Utils.lnprint(System.out, "Could not find PIR Device. Try disconnecting and reconnecting it.");
   }

   public static int getDeviceCount () {
      return haveDevice ? 1 : 0;
   }

   public PIRSensor () {
      USBm2Library.INSTANCE.initPorts();
   }

   public boolean getState () {
      return (USBm2Library.INSTANCE.read() & 0x20) != 0;
   }
}
