package edu.wpi.always.client;
import edu.wpi.always.client.*;

/*********************CLASS DEFINITION****************/
public class ReetiPIDMessages
{
   private ReetiPIDController XPID;
   private ReetiPIDController YPID;
   
   /********CONSTRUCTOR**********/
   public ReetiPIDMessages( double InXPID, double OutXPID, int SetXPID, double InYPID, double OutYPID,int SetYPID ) //: XPID( InXPID, OutXPID, SetXPID,0.03,0.00,0,6,7 ), YPID( InYPID, OutYPID, SetYPID,0.03,0,0,20,5.5 )  //0.03,0,0,12,5.5 //neck xpid //0.03,0.00,0,3,7 //4.5
   {
      XPID = new ReetiPIDController( InXPID, OutXPID, SetXPID,0.03,0.00,0,7,2 );
      YPID = new ReetiPIDController( InYPID, OutYPID, SetYPID,0.03,0,0,20,3 );
   //   SearchCount = -1;
   };
   
   /*********SEARCH COUNT VARIABLE***********/
   //private int SearchCount;

   /******************SET XPID*************/
   private void SetXPID( double center )
   {  
      XPID.setInput( center );
   }
   /******************SET YPID*************/
   private void SetYPID( double center )
   {
      YPID.setInput( center );
   }
   /******************SET PID*************/
   private void SetPID( double Xcenter, double Ycenter )
   {
      //cout<<endl<<"Setting Both X & Y PIDs"<<endl;
      SetXPID( Xcenter );
      SetYPID( Ycenter );
   }
   
   /************COMPUTE NECK ROTATE (XPID)*************/
   private double ComputeNeckXPID()
   {
      XPID.compute();
      double Output = XPID.getOutput();
      return Output;
   }

   /************COMPUTE NECK TILT (YPID)**************/
   private double ComputeNeckYPID()
   {
      YPID.compute();
      double Output = YPID.getOutput();
      return Output;
   }

   /************COMPUTE EYE PAN (XPID)**************/
   private double ComputeEyeXPID()
   {
      double Output = XPID.getEyeOut();
      return Output;
   }

   /************COMPUTE EYE TILT (YPID)***************/
   private double ComputeEyeYPID()
   {
      double Output = YPID.getEyeOut();
      return Output;
   }

   private String XTrack(boolean alone)
   {
      //Declare Message String
      String Message;

      /*Compute the PID OUTPUTS*/
      double Xout = ComputeNeckXPID();
      //double out=50;
      double XeyeLOut = ComputeEyeXPID();
      //double eyeLOut = 40;
      double XeyeROut = XeyeLOut + 20;

      /*Create Message to be sent over socket*/
      Message = "Global.servo.color=\"green\",";   //Change LED Color to indicate Face Detection
      Message += "Global.servo.neckRotat=";        //Rotate Neck
      Message += Xout ;
      Message += "," ;        
      Message += "Global.servo.leftEyePan=";    //Move leftEye
      Message += XeyeLOut;
      if(XPID.getEyeFlag())
      {
         Message += "smooth: 1s";
      }
      Message += ",Global.servo.rightEyePan=" ;    //Move rightEye
      Message += XeyeROut ;
      if(alone)
      {
         Message += ";" ;
      }
      else
      {
         Message += "," ;
      }
   
      return Message;
   }

   private String YTrack(boolean alone)
   {
      //Declare Message String
      String Message = null;

      double Yout = ComputeNeckYPID();
      //double out=50;
      double YeyeLOut = ComputeEyeYPID()+2.55;
      //double eyeLOut = 40;
      double YeyeROut = YeyeLOut;

      if(alone)
      {
        Message = "Global.servo.color=\"green\",";
        Message += "Global.servo.neckTilt=";
      }
      else
      {
         //Same with YPID
         Message = "Global.servo.neckTilt=";
         Message += Yout;
         Message += ",";   
         Message += "Global.servo.leftEyeTilt=";      //Move leftEye
         Message += YeyeLOut;
         Message += ",Global.servo.rightEyeTilt=" ;      //Move rightEye
         Message += YeyeROut ;
         Message += ";" ; 
      }
      return Message;
   }
 
   
   public String Track( double center, char XorY )
   {
      
      if(XorY == 'X')
      {
         SetXPID( center );
         //Declare Message String
         String Message;
         
         Message = XTrack(true);
         
         return Message; 
      }
      else if (XorY == 'Y')
      {

         SetYPID( center );
         //Declare Message String
         String Message;
         
         Message = YTrack(true);

         return Message; 
      }
      return null;
   }
   /***************TRACKING**********/
   public String Track( double Xcenter,double Ycenter )
   {
      // cout<<"Tracking"<<endl;
      
      //Set PID INPUTS
      SetPID( Xcenter,Ycenter ); 

      //Declare Message String
      String Message;
      
      Message = XTrack(false);

      Message += YTrack(false);

      return Message; 
   }

   /***********SEND CONSTANT COMMANDS WHEN NO FACE IS DETECTED**********/
   String constant_commands()
   {
      double Xout = XPID.getOutput();
      //double out=50;
      double XeyeLOut = XPID.getEyeOut();
      double XeyeROut = XeyeLOut+20;

      String Message;

      Message = "Global.servo.color=\"green\",";
      Message += "Global.servo.neckRotat=";
      Message += Xout ;
      Message += "," ;
      Message += "Global.servo.leftEyePan=";
      Message += XeyeLOut;
      Message += ",Global.servo.rightEyePan=" ;
      Message += XeyeROut ;
      Message += ";" ;

      double Yout = YPID.getOutput();
      //double out=50;
      double YeyeLOut = (YPID.getEyeOut())+2.55;
      //double eyeLOut = 40;
      double YeyeROut = YeyeLOut;

      //Same with YPID
      Message += "Global.servo.neckTilt=";
      Message += Yout;
      Message += ",";   
      Message += "Global.servo.leftEyeTilt=";      //Move leftEye
      Message += YeyeLOut;
      Message += ",Global.servo.rightEyeTilt=" ;      //Move rightEye
      Message += YeyeROut ;
      Message += ";" ;

      //System.out.println("Constant Command Sent");
      return Message;
   }
   /*******************SEARCH FOR A FACE***************/
   String Search() 
   {
      String command;
   /* if( SearchCount >=0 && SearchCount < 4)
      {
         SearchCount++;
   
      }  
      if( SearchCount == 1 || SearchCount == 3)
      {     */
         command = "Global.servo.color=\"red\",Global.servo.neckRotat=50 smooth:0.50s; Global.servo.leftEyePan=40, Global.servo.rightEyePan=60 smooth:0.50s, Global.servo.neckTilt=55.56 smooth:0.50s, Global.servo.leftEyeTilt=42.55 smooth:0.50s, Global.servo.rightEyeTilt=42.55 smooth:0.50s;";
         XPID.setOutput( 50 );
         YPID.setOutput( 55.56 );
   // }
         
/*    else if( SearchCount == 2)
      {
         command = "Global.servo.color=\"red\",Global.servo.neckRotat=25 smooth:0.50s; Global.servo.leftEyePan=40, Global.servo.rightEyePan=60 smooth:0.50s, Global.servo.neckTilt=55.56 smooth:0.50s, Global.servo.leftEyeTilt=42.55 smooth:0.50s, Global.servo.rightEyeTilt=42.55 smooth:0.50s;";
         cout<<endl<<command<<endl;
         XPID.setOutput(25);
      
      }
      else if(SearchCount==4)
      {
         command="Global.servo.color=\"red\",Global.servo.neckRotat=75 smooth:0.50s; Global.servo.leftEyePan=40, Global.servo.rightEyePan=60 smooth:0.50s,Global.servo.neckTilt=55.56 smooth:0.50s, Global.servo.leftEyeTilt=42.55 smooth:0.50s, Global.servo.rightEyeTilt=42.55 smooth:0.50s;";
         cout<<endl<<command<<endl;
         flag.repeat=true;
         XPID.setOutput(75);
      }*/
      System.out.println("Search Command sent");  
      return command;
   }
//   void setSearchCount( int count )
//   {
//      SearchCount = count;
//   }
};