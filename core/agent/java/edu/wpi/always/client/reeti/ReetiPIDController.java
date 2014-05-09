package edu.wpi.always.client.reeti;

import edu.wpi.always.client.ClientProxy;

public class ReetiPIDController {

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
   
   private ClientProxy proxy;
   
   ReetiPIDController (ReetiJsonConfiguration config, ClientProxy proxy) {
      // initialize controller with current reality from proxy
      this.proxy = proxy;
      reset(config, proxy);
   }
   
   // if proxy is null then use all neutral positions from config
   // otherwise only neutral eye positions
   void reset (ReetiJsonConfiguration config, ClientProxy proxy) {
      // eyes in neutral position
      eyeXPIDoutput = config.getLeftEyePan();
      eyeYPIDoutput = config.getLeftEyeTilt();
      // neck to neutral or proxy gaze position
      neckXPIDoutput = proxy == null ? config.getNeckRotat() : ReetiTranslationHor(proxy.getGazeHor());
      neckYPIDoutput = proxy == null ? config.getNeckTilt() : ReetiTranslationVer(proxy.getGazeVer());
      // no new input
      inputXPID = TranslateReetiToImageX(neckXPIDoutput);
      inputYPID = TranslateReetiToImageY(neckYPIDoutput);
      // reset all error terms to new error value since in correct position
      eyeXError = neckXError = setPointXPID - inputXPID;
      eyeYError = neckYError = setPointYPID - inputYPID;
      
      eyeReachedXLimit = false;
      eyeReachedYLimit = false;
   }

   private static int TranslateReetiToImageX(double rotatValue) {
      return (int) Math.round(320 - 3.2*rotatValue);
   }
   
   private static int TranslateReetiToImageY(double tiltValue) {
      return (int) Math.round(240 - 2.4*tiltValue);
   }

   private static double ReetiTranslationHor (double hor) {
      return ((hor + 1) * 50);
   }
   
   private static double ReetiTranslationVer (double ver) {
      return ((ver + 1) * 50);
   }

   public static float translateReetiToAgentX(double rotatValue) {
      return (((float)(rotatValue/50)) - 1);
   }

   public static float translateReetiToAgentY(double tiltValue) {
      return (((float)(tiltValue/50)) - 1);
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
   
   public void computeX () {

      double error = setPointXPID - inputXPID;

      if ( Math.abs(error) > 40 )
      {
         if ( eyeReachedXLimit == true ) {
            neckXError = error;
            neckXPIDcontroller();
            proxy.setGazeHor(translateReetiToAgentX(neckXPIDoutput));
         } else {
            eyeXError = error;
            eyeXPIDcontroller();
         }
      }
   }

   public void computeY () {

      double error = setPointYPID - inputYPID;

      if ( Math.abs(error) > 40 ) {
         if ( eyeReachedYLimit == true ) {
            neckYError = error;
            neckYPIDcontroller();
            proxy.setGazeVer(translateReetiToAgentY(neckYPIDoutput));
         } else {
            eyeYError = error;
            eyeYPIDcontroller();
         }
      }
   }
  
   void setXinput (double input) { inputXPID = input; }
   void setYinput (double input) { inputYPID = input; }

   double getNeckYPIDoutput () { return neckYPIDoutput; }
   double getNeckXPIDoutput () { return neckXPIDoutput; }
   double getEyeXPIDoutput () { return eyeXPIDoutput; }
   double getEyeYPIDoutput () { return eyeYPIDoutput; }
}