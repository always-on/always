package edu.wpi.always.client.reeti;

import edu.wpi.always.client.ClientProxy;

class ReetiPIDMessages {

   private final ReetiPIDController XPID, YPID;
   private final ReetiJsonConfiguration config;

   ReetiPIDMessages (ReetiJsonConfiguration config, ClientProxy proxy) {
      XPID = new ReetiPIDController(config, proxy);
      YPID = new ReetiPIDController(config, proxy);
      this.config = config;
   };

   private void SetXPID (double center) {
      XPID.setXinput(center);
   }

   private void SetYPID (double center) {
      YPID.setYinput(center);
   }

   private void SetXYPID (double Xcenter, double Ycenter) {
      SetXPID(Xcenter);
      SetYPID(Ycenter);
   }

   private double ComputeNeckXPID () {
      XPID.computeX();
      return XPID.getNeckXPIDoutput();
   }

   private double ComputeNeckYPID () {
      YPID.computeY();
      return YPID.getNeckYPIDoutput();
   }

   private double ComputeEyeXPID () {
      XPID.computeX();
      return XPID.getEyeXPIDoutput();
   }

   private double ComputeEyeYPID () {
      YPID.computeY();
      return YPID.getEyeYPIDoutput();
   }

   private String XTrack (boolean terminateCommand, boolean neededLED) {
      double Xout = ComputeNeckXPID();
      double XeyeLOut = ComputeEyeXPID();
      double XeyeROut = XeyeLOut + 20;

      String Message = neededLED ? "Global.servo.color=\"green\"," : ""; 

      Message += "Global.servo.neckRotat=";
      Message += Xout;
      Message += ",Global.servo.leftEyePan=";
      Message += XeyeLOut;
      Message += ",Global.servo.rightEyePan=";
      Message += XeyeROut;

      Message += terminateCommand ? ";" : ",";

      return Message;
   }

   private String YTrack (boolean neededLED) {
      double Yout = ComputeNeckYPID();
      double YeyeLOut = ComputeEyeYPID() + 2.55;
      double YeyeROut = YeyeLOut;

      String Message = neededLED ? "Global.servo.color=\"green\"," : "";
      
      Message += "Global.servo.neckTilt=";
      Message += Yout;
      Message += ",Global.servo.leftEyeTilt=";
      Message += YeyeLOut;
      Message += ",Global.servo.rightEyeTilt=";
      Message += YeyeROut;
      Message += ";";

      return Message;
   }

   String Track (double Xcenter, double Ycenter, Directions direction) {
      String Message = "";

      switch (direction) {
         case xDIRECTION:
            SetXPID(Xcenter);
            Message = XTrack(true, false);
            break;

         case yDIRECTION:
            SetYPID(Ycenter);
            Message = YTrack(false);
            break;

         case bothDIRECTIONS:
            SetXYPID(Xcenter, Ycenter);
            Message = XTrack(false, false);
            Message += YTrack(false);
            break;
      }

      return Message;
   }

   String faceSearch (boolean neededLED) {
      String command = "Global.servo.neckRotat="
         + config.getNeckRotat()
         + " smooth:0.50s; " // Was 50
         + "Global.servo.leftEyePan="
         + config.getLeftEyePan()
         + ", Global.servo.rightEyePan="
         + config.getRightEyePan()
         + " smooth:0.50s, " // Was 40 and 60
         + "Global.servo.neckTilt="
         + config.getNeckTilt()
         + " smooth:0.50s, " // Was 55.56
         + "Global.servo.leftEyeTilt=" + config.getLeftEyeTilt()
         + " smooth:0.50s, Global.servo.rightEyeTilt="
         + config.getRightEyeTilt() + " smooth:0.50s;"; // Were 42.55

      if ( neededLED ) command += "Global.servo.color=\"red\",";

      System.out.println("Face search command sent...");

      // since we are about to change all motor positions to neutral
      // outside of PID loop we need to inform and reset the controllers
      XPID.reset(config, null);
      YPID.reset(config, null);
      
      return command;
   }
}