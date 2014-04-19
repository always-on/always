package edu.wpi.always.client.reeti;

import java.awt.Point;
import edu.wpi.always.Always;
import edu.wpi.always.client.*;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;

public class ReetiPIDController {

   private final double kpXNeck = 0.03, kpYNeck = 0.04; // For the future
                                                  // improvements: kiNeck = 0,
                                                  // kdNeck = 0;

   private final double kpXEye = 0.02, kpYEye = 0.02; // For the future improvements:
                                                // kiEye = 0, kdEye = 0;

   private double inputXPID = 0, inputYPID = 0, neckXPIDoutput,
         neckYPIDoutput, eyeXPIDoutput, eyeYPIDoutput;

   private final int neckOvershootTolerancePercentage = 70;

   private final int eyeOvershootTolerancePercentage = 50;

   private final int neckOvershootMovePercentage = 10;
   
   private final int eyeOvershootMovePercentage = 5;
   
   private boolean eyeReachedXLimit = false;

   private boolean eyeReachedYLimit = false;

   private final double neckInitialOutputXPID, neckInitialOutputYPID;

   // private double neckInitialOutputYPID = config.getNeckTilt(); // Was 55.56

   private final double eyeInitialOutputXPID, eyeInitialOutputYPID;

   // private double eyeInitialOutputYPID = config.getLeftEyeTilt(); // Was 42.55

   private final int setPointXPID = 160;

   private final int setPointYPID = 120;

   private double neckXError = 0;

   private double neckYError = 0;

   private double eyeXError = 0;

   private double eyeYError = 0;

   ReetiPIDController (ReetiJsonConfiguration config) {

      float hor, ver;
      
      ClientProxy proxy = Always.THIS.getCM().getContainer().getComponent(ClientProxy.class);
      
      Point point = GazeRealizer.translateAgentTurn(proxy.getGazeHor(), proxy.getGazeVer());
      
//      neckInitialOutputXPID = point.x;
//      neckInitialOutputYPID = point.y;
//      eyeInitialOutputXPID  = point.x;
//      eyeInitialOutputYPID  = point.y;
      
      neckInitialOutputXPID = config.getNeckRotat();
      neckInitialOutputYPID = config.getNeckTilt();
      eyeInitialOutputXPID  = config.getLeftEyePan();
      eyeInitialOutputYPID  = config.getLeftEyeTilt();
      
      setNeckXPIDoutput(this.neckInitialOutputXPID);
      setNeckYPIDoutput(this.neckInitialOutputYPID);
      setEyeXPIDoutput(this.eyeInitialOutputXPID);
      setEyeYPIDoutput(this.eyeInitialOutputYPID);
   }

   private void neckXPIDcontroller () {

      double output = this.kpXNeck * this.neckXError + this.neckXPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += this.ki * errorAccumulation;
      // output += this.kd * (error - lastError);

      if ( output > 100 ) {
         if ( (output > (((neckOvershootTolerancePercentage * this.neckXPIDoutput) / 100) + this.neckXPIDoutput))
            && ((((neckOvershootTolerancePercentage * this.neckXPIDoutput) / 100) + this.neckXPIDoutput) <= 100) ) {
            output = this.neckXPIDoutput
               + ((neckOvershootMovePercentage * this.neckXPIDoutput) / 100);
         } else {
            output = 100;
         }
      } else if ( output < 0 ) {
         if ( (output < (this.neckXPIDoutput - ((neckOvershootTolerancePercentage * this.neckXPIDoutput) / 100)))
            && ((this.neckXPIDoutput - ((neckOvershootTolerancePercentage * this.neckXPIDoutput) / 100)) >= 0) ) {
            output = this.neckXPIDoutput
               - ((neckOvershootMovePercentage * this.neckXPIDoutput) / 100);
         } else {
            output = 0;
         }
      }

      setNeckXPIDoutput(output);
      setEyeReachedXLimit(false);
   }

   private void neckYPIDcontroller () {

      double output = this.kpYNeck * this.neckYError + this.neckYPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += this.ki * errorAccumulation;
      // output += this.kd * (error - lastError);

      if ( output > 100 ) {
         if ( (output > (((neckOvershootTolerancePercentage * this.neckYPIDoutput) / 100) + this.neckYPIDoutput))
            && ((((neckOvershootTolerancePercentage * this.neckYPIDoutput) / 100) + this.neckYPIDoutput) <= 100) ) {
            output = this.neckYPIDoutput
               + ((neckOvershootMovePercentage * this.neckYPIDoutput) / 100);
         } else {
            output = 100;
         }
      } else if ( output < 0 ) {
         if ( (output < (this.neckYPIDoutput - ((neckOvershootTolerancePercentage * this.neckYPIDoutput) / 100)))
            && ((this.neckYPIDoutput - ((neckOvershootTolerancePercentage * this.neckYPIDoutput) / 100)) >= 0) ) {
            output = this.neckYPIDoutput
               - ((neckOvershootMovePercentage * this.neckYPIDoutput) / 100);
         } else {
            output = 0;
         }
      }

      setNeckYPIDoutput(output);
      setEyeReachedYLimit(false);
   }

   private void eyeXPIDcontroller () {

      double output = this.kpXEye * this.eyeXError + this.eyeXPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += this.ki * errorAccumulation;
      // output += this.kd * (error - lastError);

      if ( output > 60 ) {
         if ( (output > (((eyeOvershootTolerancePercentage * this.eyeXPIDoutput) / 100) + this.eyeXPIDoutput))
            && ((((eyeOvershootTolerancePercentage * this.eyeXPIDoutput) / 100) + this.eyeXPIDoutput) <= 60) ) {
            output = this.eyeXPIDoutput
               + ((eyeOvershootMovePercentage * this.eyeXPIDoutput) / 100);
         } else {
            output = 60;
         }
      } else if ( output < 20 ) {
         if ( (output < (this.eyeXPIDoutput - ((eyeOvershootTolerancePercentage * this.eyeXPIDoutput) / 100)))
            && ((this.eyeXPIDoutput - ((eyeOvershootTolerancePercentage * this.eyeXPIDoutput) / 100)) >= 20) ) {
            output = this.eyeXPIDoutput
               - ((eyeOvershootMovePercentage * this.eyeXPIDoutput) / 100);
         } else {
            output = 20;
         }
      }

      if ( (output == 20) || (output == 60) )
         setEyeReachedXLimit(true);

      setEyeXPIDoutput(output);
   }

   private void eyeYPIDcontroller () {

      double output = this.kpYEye * this.eyeYError + this.eyeYPIDoutput;

      // These can be used in the future if Integral and Derivative terms of the
      // PID controller are required.
      // output += this.ki * errorAccumulation;
      // output += this.kd * (error - lastError);

      if ( output > 80 ) {
         if ( (output > (((eyeOvershootTolerancePercentage * this.eyeYPIDoutput) / 100) + this.eyeYPIDoutput))
            && ((((eyeOvershootTolerancePercentage * this.eyeYPIDoutput) / 100) + this.eyeYPIDoutput) <= 80) ) {
            output = this.eyeYPIDoutput
               + ((eyeOvershootMovePercentage * this.eyeYPIDoutput) / 100);
         } else {
            output = 80;
         }
      } else if ( output < 20 ) {
         if ( (output < (this.eyeYPIDoutput - ((eyeOvershootTolerancePercentage * this.eyeYPIDoutput) / 100)))
            && ((this.eyeYPIDoutput - ((eyeOvershootTolerancePercentage * this.eyeYPIDoutput) / 100)) >= 20) ) {
            output = this.eyeYPIDoutput
               - ((eyeOvershootMovePercentage * this.eyeYPIDoutput) / 100);
         } else {
            output = 20;
         }
      }

      if ( (output == 20) || (output == 80) )
         setEyeReachedYLimit(true);

      setEyeYPIDoutput(output);
   }

   public void computeX () {

      double error = this.setPointXPID - this.inputXPID;

      if ( this.eyeReachedXLimit == true ) {
         this.neckXError = error;
         neckXPIDcontroller();

      } else if ( Math.abs(error) > 10 ) {
         this.eyeXError = error;
         eyeXPIDcontroller();
      }
   }

   public void computeY () {

      double error = this.setPointYPID - this.inputYPID;

      if ( this.eyeReachedYLimit == true ) {
         this.neckYError = error;
         neckYPIDcontroller();

      } else if ( Math.abs(error) > 10 ) {
         this.eyeYError = error;
         eyeYPIDcontroller();
      }
   }

   private void setEyeReachedXLimit (boolean flag) {
      this.eyeReachedXLimit = flag;
   }

   private void setEyeReachedYLimit (boolean flag) {
      this.eyeReachedYLimit = flag;
   }

   public void setXinput (double input) {
      this.inputXPID = input;
   }

   public void setYinput (double input) {
      this.inputYPID = input;
   }

   public double getXInput () {
      return this.inputXPID;
   }

   public double getYInput () {
      return this.inputYPID;
   }

   public void setNeckXPIDoutput (double output) {
      this.neckXPIDoutput = output;
   }

   public double getNeckXPIDoutput () {
      return this.neckXPIDoutput;
   }

   public void setNeckYPIDoutput (double output) {
      this.neckYPIDoutput = output;
   }

   public double getNeckYPIDoutput () {
      return this.neckYPIDoutput;
   }

   public void setEyeXPIDoutput (double output) {
      this.eyeXPIDoutput = output;
   }

   public double getEyeXPIDoutput () {
      return this.eyeXPIDoutput;
   }

   public void setEyeYPIDoutput (double output) {
      this.eyeYPIDoutput = output;
   }

   public double getEyeYPIDoutput () {
      return this.eyeYPIDoutput;
   }
}