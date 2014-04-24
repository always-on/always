package edu.wpi.always.client.reeti;

import edu.wpi.always.client.ClientProxy;

class ReetiPIDController {

   // For the future improvements: kiNeck = 0, kdNeck = 0;
   private final double kpXNeck = 0.05, kpYNeck = 0.08; 
   // For the future improvements: kiEye = 0, kdEye = 0;
   private final double kpXEye = 0.03, kpYEye = 0.03; 
   
   private final int neckOvershootTolerancePercentage = 70,
                     eyeOvershootTolerancePercentage = 50;
   
   private final int neckOvershootMovePercentage = 5,
                     eyeOvershootMovePercentage = 5;
   
   private final int setPointXPID = 160, setPointYPID = 120;
   
   private boolean eyeReachedXLimit = false, eyeReachedYLimit = false;

   private double inputXPID = 160, inputYPID = 120, 
         neckXPIDoutput, neckYPIDoutput, eyeXPIDoutput, eyeYPIDoutput;

   private double neckXError = 0, neckYError = 0, eyeXError = 0, eyeYError = 0;
   
   ReetiPIDController (ReetiJsonConfiguration config, ClientProxy proxy) {
      // initialize controller with current reality from proxy
      reset(config, proxy);
   }
   
   // if proxy is null then use all neutral positions from config
   // otherwise only neutral eye positions
   void reset (ReetiJsonConfiguration config, ClientProxy proxy) {
      // eyes in neutral position
      eyeXPIDoutput = config.getLeftEyePan();
      eyeYPIDoutput = config.getLeftEyeTilt();
      // neck to neutral or proxy gaze position
      double reetiHor = ReetiTranslationHor(proxy.getGazeHor());
      double reetiVer = ReetiTranslationVer(proxy.getGazeVer());
      neckXPIDoutput = proxy == null ? config.getNeckRotat() : ((reetiHor != -1) ? reetiHor : config.getNeckRotat());
      neckYPIDoutput = proxy == null ? config.getNeckTilt()  : ((reetiVer != -1) ? reetiVer : config.getNeckTilt());
      // no new input
      inputXPID = TranslateReetiToImageX(neckXPIDoutput);
      inputYPID = TranslateReetiToImageY(neckYPIDoutput);
      // reset all error terms to new error value since in correct position
      eyeXError = neckXError = setPointXPID - inputXPID;
      eyeYError = neckYError = setPointYPID - inputYPID;
      
      System.out.println(neckXError + " and " + neckYError);
      System.out.println("\ninputXPID: " + inputXPID);
      System.out.println("\ninputYPID: " + inputYPID);
//      System.out.println("\nEye X: " + eyeXPIDoutput);
//      System.out.println("\nEye Y: " + eyeYPIDoutput);
      System.out.println("\nNeck X: " + neckXPIDoutput);
      System.out.println("\nNeck Y: " + neckYPIDoutput);
      
      eyeReachedXLimit = false;
      eyeReachedYLimit = false;
   }

   private static int TranslateReetiToImageX(double rotatValue) {
      return (int) Math.round(320 - 3.2*rotatValue);
   }
   
   private static int TranslateReetiToImageY(double tiltValue) {
      return (int) Math.round(240 - 2.4*tiltValue);
   }
   
   // Note: Formulae in following two methods copied from ReetiTranslation.cs
   
   private static double ReetiTranslationHor (double hor) {
      if ( (hor >= -1) && (hor <=1) ) return ((hor + 1) * 50);
      else { System.out.println("Invalid Reeti horizontal command value."); return -1; }
   }
   
   private static double ReetiTranslationVer (double ver) {
      if ( (ver >= -1) && (ver <=1) ) return ((ver + 1) * 50);
      else {  System.out.println("Invalid Reeti vertical command value."); return -1; }
   }

   private void neckXPIDcontroller () {

      double output = kpXNeck * neckXError + neckXPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += ki * errorAccumulation;
      // output += kd * (error - lastError);

      if ( output > 100 ) {
         if ( (output > (((neckOvershootTolerancePercentage * neckXPIDoutput) / 100) + neckXPIDoutput))
            && ((((neckOvershootTolerancePercentage * neckXPIDoutput) / 100) + neckXPIDoutput) <= 100) ) {
            output = neckXPIDoutput
               + ((neckOvershootMovePercentage * neckXPIDoutput) / 100);
         } else {
            output = 100;
         }
      } else if ( output < 0 ) {
         if ( (output < (neckXPIDoutput - ((neckOvershootTolerancePercentage * neckXPIDoutput) / 100)))
            && ((neckXPIDoutput - ((neckOvershootTolerancePercentage * neckXPIDoutput) / 100)) >= 0) ) {
            output = neckXPIDoutput
               - ((neckOvershootMovePercentage * neckXPIDoutput) / 100);
         } else {
            output = 0;
         }
      }
      neckXPIDoutput = output;
      eyeReachedXLimit = false;
   }

   private void neckYPIDcontroller () {

      double output = kpYNeck * neckYError + neckYPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += ki * errorAccumulation;
      // output += kd * (error - lastError);

      if ( output > 100 ) {
         if ( (output > (((neckOvershootTolerancePercentage * neckYPIDoutput) / 100) + neckYPIDoutput))
            && ((((neckOvershootTolerancePercentage * neckYPIDoutput) / 100) + neckYPIDoutput) <= 100) ) {
            output = neckYPIDoutput
               + ((neckOvershootMovePercentage * neckYPIDoutput) / 100);
         } else {
            output = 100;
         }
      } else if ( output < 0 ) {
         if ( (output < (neckYPIDoutput - ((neckOvershootTolerancePercentage * neckYPIDoutput) / 100)))
            && ((neckYPIDoutput - ((neckOvershootTolerancePercentage * neckYPIDoutput) / 100)) >= 0) ) {
            output = neckYPIDoutput
               - ((neckOvershootMovePercentage * neckYPIDoutput) / 100);
         } else {
            output = 0;
         }
      }

      neckYPIDoutput = output;
      eyeReachedYLimit = false;
   }

   private void eyeXPIDcontroller () {

      double output = kpXEye * eyeXError + eyeXPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += ki * errorAccumulation;
      // output += kd * (error - lastError);

      if ( output > 60 ) {
         if ( (output > (((eyeOvershootTolerancePercentage * eyeXPIDoutput) / 100) + eyeXPIDoutput))
            && ((((eyeOvershootTolerancePercentage * eyeXPIDoutput) / 100) + eyeXPIDoutput) <= 60) ) {
            output = eyeXPIDoutput
               + ((eyeOvershootMovePercentage * eyeXPIDoutput) / 100);
         } else {
            output = 60;
         }
      } else if ( output < 20 ) {
         if ( (output < (eyeXPIDoutput - ((eyeOvershootTolerancePercentage * eyeXPIDoutput) / 100)))
            && ((eyeXPIDoutput - ((eyeOvershootTolerancePercentage * eyeXPIDoutput) / 100)) >= 20) ) {
            output = eyeXPIDoutput
               - ((eyeOvershootMovePercentage * eyeXPIDoutput) / 100);
         } else {
            output = 20;
         }
      }

      if ( (output == 20) || (output == 60) )
         eyeReachedXLimit = true;

      eyeXPIDoutput = output;
   }

   private void eyeYPIDcontroller () {

      double output = kpYEye * eyeYError + eyeYPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += ki * errorAccumulation;
      // output += kd * (error - lastError);

      if ( output > 80 ) {
         if ( (output > (((eyeOvershootTolerancePercentage * eyeYPIDoutput) / 100) + eyeYPIDoutput))
            && ((((eyeOvershootTolerancePercentage * eyeYPIDoutput) / 100) + eyeYPIDoutput) <= 80) ) {
            output = eyeYPIDoutput
               + ((eyeOvershootMovePercentage * eyeYPIDoutput) / 100);
         } else {
            output = 80;
         }
      } else if ( output < 20 ) {
         if ( (output < (eyeYPIDoutput - ((eyeOvershootTolerancePercentage * eyeYPIDoutput) / 100)))
            && ((eyeYPIDoutput - ((eyeOvershootTolerancePercentage * eyeYPIDoutput) / 100)) >= 20) ) {
            output = eyeYPIDoutput
               - ((eyeOvershootMovePercentage * eyeYPIDoutput) / 100);
         } else {
            output = 20;
         }
      }

      if ( (output == 20) || (output == 80) )
         eyeReachedYLimit = true;

      eyeYPIDoutput = output;
   }

   void computeX () {

      double error = setPointXPID - inputXPID;

      if ( Math.abs(error) > 20 )
      {
         if ( eyeReachedXLimit == true ) {
            neckXError = error;
            neckXPIDcontroller();
   
         } else {
            eyeXError = error;
            eyeXPIDcontroller();
         }
      }
   }

   void computeY () {

      double error = setPointYPID - inputYPID;

      if ( Math.abs(error) > 20 ) {
         if ( eyeReachedYLimit == true ) {
            neckYError = error;
            neckYPIDcontroller();
   
         } else {
            eyeYError = error;
            eyeYPIDcontroller();
         }
      }
   }
  
   void setXinput (double input) { inputXPID = input; }
   void setYinput (double input) { inputYPID = input; }

//   public void setEyeReachedXLimit (boolean flag) { eyeReachedXLimit = flag; }
//   public void setEyeReachedYLimit (boolean flag) { eyeReachedYLimit = flag; }

//   public double getXInput () { return inputXPID; }
//   public double getYInput () { return inputYPID; }

//   public void setNeckXPIDoutput (double output) { neckXPIDoutput = output; }
//   public void setNeckYPIDoutput (double output) { neckYPIDoutput = output; }
//   public void setEyeXPIDoutput (double output) { eyeXPIDoutput = output; }
//   public void setEyeYPIDoutput (double output) { eyeYPIDoutput = output; }

   double getNeckYPIDoutput () { return neckYPIDoutput; }
   double getNeckXPIDoutput () { return neckXPIDoutput; }
   double getEyeXPIDoutput () { return eyeXPIDoutput; }
   double getEyeYPIDoutput () { return eyeYPIDoutput; }
}