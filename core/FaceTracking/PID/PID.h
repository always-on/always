#ifndef PID_H
#define PID_H

#include <Windows.h>
#include <iostream>
#include <string>
#include <stdio.h>
#include <time.h>
#include <math.h>

class PID{

	private:
	
		double kp;                  // * (P)roportional Tuning Parameter
		double ki;                  // * (I)ntegral Tuning Parameter
		double kd;                  // * (D)erivative Tuning Parameter

		static const int tol = 50;	//50 pixels

		static const int trans_tol = 90;	//transition tolerance = xx pixels

		double Input;              // * Pointers to the Input, Output, and Setpoint variables
		double Output;             //   This creates a hard link between the variables and the 
		double Setpoint;           //   PID, freeing the user from having to constantly tell us what these values are.  with pointers we'll just know.	
                                
		double eyeOut;

		time_t lastTime;
		double ITerm, lastInput;
		static const int SampleTime = 250;
	
		double BigTolFactor,SmallTolFactor;

	public:
		
		// Constructor.  
		PID(double Input, double Output, double Setpoint, double Kp, double Ki, double Kd, double BigTolFactor, double SmallTolFactor)
		{
			//links the PID to the Input, Output, Setpoint along with the gain
			long lastTime =  GetTickCount() - SampleTime;

			setOutput(Output);
			setInput(Input);
			setSetpoint(Setpoint);
			setEyeOut(40); 
			
			kp = Kp;
			ki = Ki;
			kd = Kd;
			
			setBigTolFactor(BigTolFactor);
			setSmallTolFactor(SmallTolFactor);		
		} 
		void Compute()
		{	
			 long now =  GetTickCount();
			 long timeChange = (now - lastTime);

			 /*******DEBUG*********/
			 cout<<"\n Time Change: "<<timeChange<<endl;
			 cout<<"\nSample Time: " <<SampleTime<<endl;
			 
			 if(timeChange>=SampleTime)
			 {
				  /*Compute all the working error variables*/
				  double input = Input;
				  double error = Setpoint - input;
				  
				  /*******DEBUG*********/
				  cout<<"\nInput: "<<input;
				  cout<<"\nSetpoint: "<<Setpoint;
				  cout<<"\nError: "<<error<<endl;

				  //Exceeds Tolerance
				  if(abs(error) > tol)	
				  {
					  ITerm += (ki * error);
		    		  double dInput = (input - lastInput);

		    		  /*Compute PID Output*/
			 		  double output =kp * error + ITerm- kd * dInput;
					 
			 		  /*******DEBUG*********/
			 		  cout<<endl<<"PID OUTPUT: "<<output;

			 		  output=output*BigTolFactor; 

					  /*******DEBUG*********/
					  cout<<"\nBig OPT Output: "<<output<<endl;
			   		
					  //my right
			   		  if(error > 0)	
					  {
						  if(output > 0)
			   				output=(output + Output);
			   			  else
			   				output=((output*(-1)) + Output);
					  }

					  //my left
			   		  else 	
			   		  {
			   	 		 output = Output - (output * (-1));
			   	 	  }	
					 
					  /* Ensure that the output will be received by the motor*/
					  if(output > 100) output = 100;
					  else if(output < 0) output = 0;
					  
					
			   		  setOutput(output);
			   		  setEyeOut(40);
					  cout<<endl<<"EyeOut : "<<getEyeOut()<<endl;
				  }
				  else if(abs(error) <= tol && abs(error) > 5)
				  {
					  /*******DEBUG*********/
					  cout<<endl<<"Error within tolerance"<<endl;
					
					  ITerm += (ki * error);
					  double dInput = (input - lastInput);
					   
					  /*Compute PID Output*/
					  double output = kp * error + ITerm- kd * dInput;
						
					  /*******DEBUG*********/
					  cout<<endl<<"PID OUTPUT: "<<output;
					  
					  output = output * SmallTolFactor;

					  /*******DEBUG*********/					
					  cout<<"\nSmall OPT Output: "<<output<<endl;

					  //my right
					  if(error > 0)	
					  {
						  if(output > 0)
							  output = (output + getEyeOut());
			   			  else
			   				  output = ((output*(-1)) + getEyeOut());
					  }
					  
					  //my left
					  else 
					  {
						  output = getEyeOut() - (output * (-1));
					  }	
					  if(output > 60)
						  output = 60;
					  else if(output < 20)
						  output = 20;
					  setEyeOut(output);
				  }
				  /*Remember some variables for next time*/
				  lastInput = input;
				  lastTime = now;
		     }
			 else
				  cout<<endl<<"Time didn't change much "<<timeChange<<endl;
		}
		void setInput(double myInput)
		{
			 Input = myInput;
		}
		double getInput()
		{
			return Input;
		}
		void setOutput(double myOutput) 
		{
			Output = myOutput;
		}
		double getOutput() 
		{
			return Output;
		}
		void setSetpoint(double setpoint) 
		{
			Setpoint = setpoint;
		}
		double getSetpoint() 
		{
			return Setpoint;
		}
		void setEyeOut(double myeyeOut) 
		{
			eyeOut = myeyeOut;
		}
		double getEyeOut() 
		{
			return eyeOut;
		}
		void setBigTolFactor(double myBigTolFactor)
		{
			BigTolFactor = myBigTolFactor;
		}
		void setSmallTolFactor(double mySmallTolFactor)
		{
			SmallTolFactor = mySmallTolFactor;
		}
};
#endif // IMAGE_H