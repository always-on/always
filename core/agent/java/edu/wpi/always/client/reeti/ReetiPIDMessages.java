package edu.wpi.always.client.reeti;

import edu.wpi.always.client.reeti.ReetiJsonConfiguration;

public class ReetiPIDMessages {

   private final ReetiPIDController XPID, YPID;

   private ReetiJsonConfiguration config;

   public ReetiPIDMessages (ReetiJsonConfiguration config) {
      XPID = new ReetiPIDController(config);
      YPID = new ReetiPIDController(config);

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

   private String XTrack (boolean terminateCommand) {
      String Message = null;

      double Xout = ComputeNeckXPID();
      double XeyeLOut = ComputeEyeXPID();
      double XeyeROut = XeyeLOut + 20;

      Message = "Global.servo.color=\"green\"";
      Message += ",Global.servo.neckRotat=";
      Message += Xout;
      Message += ",Global.servo.leftEyePan=";
      Message += XeyeLOut;
      Message += ",Global.servo.rightEyePan=";
      Message += XeyeROut;

      Message += terminateCommand ? ";" : ",";

      return Message;
   }

   private String YTrack (boolean neededLED) {
      String Message = "";

      double Yout = ComputeNeckYPID();
      double YeyeLOut = ComputeEyeYPID() + 2.55;
      double YeyeROut = YeyeLOut;

      if ( neededLED )
         Message = "Global.servo.color=\"green\",";

      Message += "Global.servo.neckTilt=";
      Message += Yout;
      Message += ",Global.servo.leftEyeTilt=";
      Message += YeyeLOut;
      Message += ",Global.servo.rightEyeTilt=";
      Message += YeyeROut;
      Message += ";";

      return Message;
   }

   public String Track (double Xcenter, double Ycenter, Directions direction) {
      String Message = "";

      switch (direction) {
         case xDIRECTION:
            SetXPID(Xcenter);
            Message = XTrack(true);
            break;

         case yDIRECTION:
            SetYPID(Ycenter);
            Message = YTrack(true);
            break;

         case bothDIRECTIONS:
            SetXYPID(Xcenter, Ycenter);
            Message = XTrack(false);
            Message += YTrack(false);
            break;
      }

      return Message;
   }

   public String faceSearch () {
      String command;

      command = "Global.servo.color=\"red\",Global.servo.neckRotat="
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

      XPID.setNeckXPIDoutput(config.getNeckRotat()); // Was: 50

      YPID.setNeckYPIDoutput(config.getNeckTilt()); // Was: 55.56

      XPID.setEyeXPIDoutput(config.getLeftEyePan()); // Was: 50

      YPID.setEyeYPIDoutput(config.getLeftEyeTilt()); // Was: 42.55

      System.out.println("Search command sent...");

      return command;
   }
}