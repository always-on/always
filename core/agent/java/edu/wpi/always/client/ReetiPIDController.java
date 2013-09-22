package edu.wpi.always.client;

public class ReetiPIDController {
   private double kp; // * (P)roportional Tuning Parameter

   private double ki; // * (I)ntegral Tuning Parameter

   private double kd; // * (D)erivative Tuning Parameter

   int tol = 100;

   private double Input; // * Pointers to the Input, Output, and Setpoint
                         // variables

   private double Output; // This creates a hard link between the variables and
                          // the

   private double Setpoint; // PID, freeing the user from having to constantly
                            // tell us

   // what these values are. with pointers we'll just know.
   private double eyeOut = 40;

   private long lastTime;

   private double ITerm, lastInput;

   private int SampleTime;

   private boolean EyeFlag;

   private double BigTolFactor, SmallTolFactor;

   public ReetiPIDController (double Input, double Output, double Setpoint,
         double Kp, double Ki, double Kd, double BigTolFactor,
         double SmallTolFactor) {
      this.SampleTime = 250;
      this.lastTime = System.currentTimeMillis() - SampleTime;
      setOutput(Output);
      setInput(Input);
      setSetpoint(Setpoint);
      setEyeOut(40);
      setEyeFlag(false);
      this.kp = Kp;
      this.ki = Ki;
      this.kd = Kd;
      setBigTolFactor(BigTolFactor);
      setSmallTolFactor(SmallTolFactor);
   } // * constructor. links the PID to the Input, Output, and

   public void compute () {
      long now = System.currentTimeMillis();
      long timeChange = (now - lastTime);
      if ( timeChange >= SampleTime ) {
         /* Compute all the working error variables */
         double input = this.Input;
         double error = this.Setpoint - input;
         if ( Math.abs(error) > tol ) // Exceeds Tolerance
         {
            ITerm += (this.ki * error);
            double dInput = (input - this.lastInput);

            /* Compute PID Output */
            double output = this.kp * error + ITerm - this.kd * dInput;

            output = output * this.BigTolFactor;

            if ( error > 0 ) // my right
            {
               if ( output > 0 )
                  output = (output + this.Output);
               else
                  output = ((output * (-1)) + this.Output);
            } else // my left
            {
               output = this.Output - (output * (-1));
            }
            /* Ensure that the output will be received by the vex motor */
            if ( output > 100 )
               output = 100;
            else if ( output < 0 )
               output = 0;
            setOutput(output);
            setEyeFlag(true);
            setEyeOut(40);
         } else if ( Math.abs(error) <= tol && Math.abs(error) > 5 ) {
            ITerm += (this.ki * error);
            double dInput = (input - this.lastInput);

            /* Compute PID Output */
            double output = this.kp * error + ITerm - this.kd * dInput;

            output = output * SmallTolFactor;

            if ( error > 0 ) // my right
            {
               if ( output > 0 )
                  output = (output + getEyeOut());
               else
                  output = ((output * (-1)) + getEyeOut());

            } else // my left
            {
               output = getEyeOut() - (output * (-1));
            }
            if ( output > 60 )
               output = 60;
            else if ( output < 20 )
               output = 20;
            setEyeOut(output);
         }
         /* Remember some variables for next time */
         this.lastInput = input;
         this.lastTime = now;
      } else {
         // System.out.println("Time didn't change much");
      }
   }

   public void setEyeFlag (boolean flag) {
      EyeFlag = flag;
   }

   public boolean getEyeFlag () {
      return EyeFlag;
   }

   public void setInput (double myInput) {
      Input = myInput;
   }

   public double getInput () {
      return Input;
   }

   public void setOutput (double myOutput) {
      Output = myOutput;
   }

   public double getOutput () {
      return Output;
   }

   public void setSetpoint (double setpoint) {
      Setpoint = setpoint;
   }

   public double getSetpoint () {
      return Setpoint;
   }

   public void setEyeOut (double myeyeOut) {
      eyeOut = myeyeOut;
   }

   public double getEyeOut () {
      return eyeOut;
   }

   public void setBigTolFactor (double myBigTolFactor) {
      BigTolFactor = myBigTolFactor;
   }

   public void setSmallTolFactor (double mySmallTolFactor) {
      SmallTolFactor = mySmallTolFactor;
   }
}