package edu.wpi.always.client.reeti;

import edu.wpi.always.client.*;

public class ReetiPIDMessages extends ReetiJsonConfiguration {
   private ReetiPIDController XPID;

   private ReetiPIDController YPID;

   private ReetiJsonConfiguration reetiJsonConfig;
   
   public ReetiPIDMessages () {
      XPID = new ReetiPIDController();
      YPID = new ReetiPIDController();
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

      // TODO: This actual values should come from reading the json file.
      command = "Global.servo.color=\"red\",Global.servo.neckRotat=" + reetiJsonConfig.getNeckRotat() + " smooth:0.50s; " //Was 50
         + "Global.servo.leftEyePan=" + reetiJsonConfig.getLeftEyePan() + ", Global.servo.rightEyePan=" + reetiJsonConfig.getRightEyePan() + " smooth:0.50s, "  //Was 40 and 60
         + "Global.servo.neckTilt=" + reetiJsonConfig.getNeckTilt() + " smooth:0.50s, " // Was 55.56 
         + "Global.servo.leftEyeTilt=" + reetiJsonConfig.getLeftEyeTilt() + " smooth:0.50s, Global.servo.rightEyeTilt=" + reetiJsonConfig.getRightEyeTilt() + " smooth:0.50s;"; // Were 42.55
      
      XPID.setNeckXPIDoutput(reetiJsonConfig.getNeckRotat());   //Old value: 50
      
      YPID.setNeckYPIDoutput(reetiJsonConfig.getNeckTilt());    //Old value: 55.56

      XPID.setEyeXPIDoutput(reetiJsonConfig.getLeftEyePan());   //Old value: 50
      
      YPID.setEyeYPIDoutput(reetiJsonConfig.getLeftEyeTilt());  //Old value: 42.55

      System.out.println("Search command sent...");

      return command;
   }
}