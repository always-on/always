/**
 * @file FaceTracking.cpp
 * @author Ayesha Fathima & M. Shayganfar ( based on the classic facedetect.cpp in samples/c )
 * @Shows how to connect to a socket. Also, detect human face while tracking.
 */

#include "Communication\Communication.h"

/*****************************************FACE DETECTION MODULE**************************************************/
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/features2d/features2d.hpp"

#include "shore/Shore.h"
#include "shore/CreateFaceEngine.h"
#include "shore/ContentToText.h"
#include "shore/Image.h"

#include <stdio.h>
#include <windows.h>

#define FRAME_WIDTH 320
#define FRAME_HEIGHT 240
#define TIMER_DELAY 2000

using namespace std;
using namespace cv;

/*****************************CLASS DEFINITION******************************/
class FaceDetection
{

private:
	/******************SHORE*******************/
	//CvCapture* capture; //Alternative: VideoCapture capture(0); <- To be used for Always On Agent

	Shore::Engine* engine;
	const char* strWindowName;	//Crashes the FaceDetection executable
	
	/***********STRUCTURE FOR FACE VARIABLES FOR SHORE PROCESSING/FACE TRACKING***************/
	typedef struct FACE {
		int intLeft;
		int intRight;
		int intTop;
		int intBottom;
		int intCenter;
		int intTiltCenter;
		int intArea;
		int lastArea;
		int lastLeft;
		int lastTop;
		//int intHappiness;
	}FaceInfo;

	//Declare Object FaceInfo 
	FaceInfo faceInfo;

public:
	
	/********CONSTRUCTOR**********/
	FaceDetection()
	{
		//Initialize Face-Detection Process
		initFaceDetectionProcess(1);	
	}

	//extern "C" __declspec(dllexport)

	/*************INITIALIZER*************/
	void initFaceDetectionProcess( int intDebug ) {
		
		float         timeBase          = 0;            // Use single image mode
		bool          updateTimeBase    = false;        // Not used in video mode
		unsigned long threadCount       = 2UL;          // Let's take one thread only
		char const*   model             = "Face.Front"; // Search faces
		float         imageScale        = 1.0f;         // Scale the images
		float         minFaceSize       = 0.0f;         // Find small faces too
		long          minFaceScore      = 9L;           // That's the default value
		float         idMemoryLength    = 0.0f;
		char const*   idMemoryType      = "Spatial";
		bool          trackFaces        = false;
		char const*   phantomTrap       = "Off";
		bool          searchEyes        = false;
		bool          searchNose        = false;
		bool          searchMouth       = false;
		bool          analyzeEyes       = false;
		bool          analyzeMouth      = false;
		bool          analyzeGender     = false;
		bool          analyzeAge        = false;
		bool          analyzeHappy      = false;
		bool          analyzeSad        = false;
		bool          analyzeSurprised  = false;
		bool          analyzeAngry      = false;
	
		//capture = cvCaptureFromCAM( -1 );
		//cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH, FRAME_WIDTH);
		//cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT);		<- Use by Agent
		//Alternative:
		//capture.set(CV_CAP_PROP_FRAME_WIDTH, FRAME_WIDTH);
		//capture.set(CV_CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT);

		engine = Shore::CreateFaceEngine( timeBase,
										  updateTimeBase,
										  threadCount,
										  model,
										  imageScale,
										  minFaceSize,
										  minFaceScore,
										  idMemoryLength,
										  idMemoryType,
										  trackFaces,
										  phantomTrap,
										  searchEyes,
										  searchNose,
										  searchMouth,
										  analyzeEyes,
										  analyzeMouth,
										  analyzeGender,
										  analyzeAge,
										  analyzeHappy,
										  analyzeSad,
										  analyzeSurprised,
										  analyzeAngry);

		if ( engine == 0 )
		{
			std::cerr << "Error: Engine setup failed - exit!\n";
			std::exit(1);
		}
	
	}

	//extern "C" __declspec(dllexport)

	/*********TERMINATE FACE DETECTION PROCESS*************/
	void terminateFaceDetectionProcess( int intDebug ) {
		//cvReleaseCapture( &capture );			<- Use By Agent
		if (intDebug == 1)
			cvDestroyWindow( "Display window" );
		Shore::DeleteEngine( engine );
	}

	/************RECOGNIZE HAPPINESS*************/
	//extern "C" __declspec(dllexport)

	/*int recognizeHappiness( Shore::Object const* object ) {

		return *(object->GetRating(0)); //0 Rating index is for "Happy" face
	}*/
	//extern "C" __declspec(dllexport)

	/**************GET FACE INFO***************/
	bool getFaceInfo( char* data, int Debug, int count )
	{
	//	cout<<"Getting Face Info";
		
		unsigned long int loops = 1;
		int intPrevWidth = 0;
		int intPrevHeight = 0;
		int intCurrWidth = 0;
		int intCurrHeight = 0;
		int intSelectedFaceIndex = -1;

		//Declare Shore Content
		Shore::Content const* content = 0;

		//Declare Matrix Frames for Processing
		Mat frame( 240,320, CV_8UC1, data );

		//Declare IPL Image for Processing
		IplImage* im = cvCreateImage( cvSize(320,240), IPL_DEPTH_8U, 1 );
		//Convert Matrix to IPL Image
		im->imageData = ( char * ) frame.data;
	
		if( 1 ) //Alternative: if( capture.isOpened() )		<-Use By Agent
		{
			//Declare Image for Displaying
			Image image( 320,240,im );

			for ( unsigned int i = 0; i < loops; ++i )
			{
				//cout<<"\n Setting Engine \n";
				content = engine->Process(  image.LeftTop(),
											image.Width(),
											image.Height(),
										   1,
										   1,
										   image.Width(), //frame.cols+1 //frame.size().width
										   0,
										   "GRAYSCALE" );
			//	cout<<"\n Getting Content \n";
				if ( content->GetObjectCount() > 0 )
				{
					for( int i = 0 ; i < content->GetObjectCount() ; i++ )
					{
						intCurrWidth = abs(content->GetObject(i)->GetRegion()->GetRight() - content->GetObject(i)->GetRegion()->GetLeft());
						intCurrHeight = abs(content->GetObject(i)->GetRegion()->GetBottom() - content->GetObject(i)->GetRegion()->GetTop());
					
						if(( intPrevWidth*intPrevHeight ) < ( intCurrWidth*intCurrHeight ))
						{
							intSelectedFaceIndex = i;
						}
						intPrevWidth = intCurrWidth;
						intPrevHeight = intCurrHeight;
					}
					
					if( intSelectedFaceIndex != -1 )
					{
						faceInfo.intBottom = content->GetObject(intSelectedFaceIndex)->GetRegion()->GetBottom();
						faceInfo.intTop    = content->GetObject(intSelectedFaceIndex)->GetRegion()->GetTop();
						faceInfo.intLeft   = content->GetObject(intSelectedFaceIndex)->GetRegion()->GetLeft();
						faceInfo.intRight  = content->GetObject(intSelectedFaceIndex)->GetRegion()->GetRight();

						faceInfo.intCenter = (faceInfo.intRight-faceInfo.intLeft)/2;
						faceInfo.intCenter += faceInfo.intLeft;
						faceInfo.intTiltCenter = (faceInfo.intBottom-faceInfo.intTop)/2;
						faceInfo.intTiltCenter += faceInfo.intTop;
						
						faceInfo.intArea = (faceInfo.intRight-faceInfo.intLeft)*(faceInfo.intBottom-faceInfo.intTop);

						cout << "\nInterface dll content:\n";
						cout << "Bottom: " << faceInfo.intBottom;
						cout << "\nTop: "    << faceInfo.intTop;
						cout << "\nLeft: "   << faceInfo.intLeft;
						cout << "\nRight: "  << faceInfo.intRight;
						cout << "\nCenter: "  << faceInfo.intCenter;
						cout << "\nTilt Center: "  << faceInfo.intTiltCenter;
						cout << "\nArea: " << faceInfo.intArea;
						/*********DEBUG****************/
						cout << "\nLast Area: " << faceInfo.lastArea;
						cout<< "\nLast Left: "<<faceInfo.lastLeft;
						cout<< "\nLast Top: "<<faceInfo.lastTop;

						cout<<"\nAREA ERROR:"<< abs(faceInfo.intArea - faceInfo.lastArea);
						cout<<"\nLEFT ERROR: "<<abs(faceInfo.intLeft - faceInfo.lastLeft);
						cout<<"\nTOP ERROR:" <<abs(faceInfo.intTop - faceInfo.lastTop);
						if( ( count > 0 && abs(faceInfo.intArea - faceInfo.lastArea) >= 1000) || ( count > 0 && abs(faceInfo.intLeft - faceInfo.lastLeft) >= 20) || ( count > 0 && abs(faceInfo.intTop - faceInfo.lastTop) >= 20))
						{
							cout<<"\nReturning\n";
							return false;
						}
						
					}
				}
				else
				{
					faceInfo.intBottom    = -1;
					faceInfo.intTop       = -1;
					faceInfo.intLeft      = -1;
					faceInfo.intRight     = -1;
					faceInfo.intArea	  = -1;
					return false;
				}
				faceInfo.lastArea = faceInfo.intArea;
				faceInfo.lastLeft = faceInfo.intLeft;
				faceInfo.lastTop = faceInfo.intTop;

				if( Debug )
					Display_Image(content,image);

				return true;
			}
		}

	/*	else
		{
			cout << "\nCamera connection problem...\n";
		}*/

	//	return true;
	}
	/******************DISPLAY IMAGE****************/
	void Display_Image( Shore::Content const* content, Image image )
	{
		//cout<<"Displaying";
		Mat markedFace;
		std::cout << ContentToText( content ) << "\n\n";
		image.DrawContent( content );
		image.SavePgm( "face.pgm" );
		markedFace = cvLoadImage( "face.pgm", CV_LOAD_IMAGE_UNCHANGED );
		namedWindow( "Display window", CV_WINDOW_AUTOSIZE );
		imshow( "Display window", markedFace );
		cvWaitKey( 500 );
	}
	/*******************GET FACE LEFT**************/
	double getFaceLeft()
	{
		return faceInfo.intLeft;
	}
	/*******************GET FACE CENTER**************/
	double getFaceCenter()
	{
		return faceInfo.intCenter;
	}
	/*******************GET FACE TILT CENTER**************/
	double getFaceTiltCenter()
	{
		return faceInfo.intTiltCenter;
	}
	/**********************GET FACE AREA******************/
	double getFaceArea()
	{
		return faceInfo.intArea;
	}
};

/********************************PID CONTROL******************************************/

#include "PID\PID.h"

/*********FLAGS FOR PID FACE TRACKING***********/
typedef struct Flags{
	bool repeat;
	bool true_face;
	int facecount;
};

Flags flag={ true,0 };

/*********************CLASS DEFINITION****************/
class FaceTrackPID
{
private:

	PID XPID;
	PID YPID;
	
	/*********SEARCH COUNT VARIABLE***********/
	int SearchCount;

	/******************SET XPID*************/
	void SetXPID( double center )
	{	
		XPID.setInput( center );
	}
	/******************SET YPID*************/
	void SetYPID( double center )
	{
	    YPID.setInput( center );
	}
	/******************SET PID*************/
	void SetPID( double Xcenter, double Ycenter )
	{
		//cout<<endl<<"Setting Both X & Y PIDs"<<endl;
		SetXPID( Xcenter );
		SetYPID( Ycenter );
	}
	
	/************COMPUTE NECK ROTATE (XPID)*************/
	double ComputeNeckXPID()
	{
		XPID.Compute();
		double Output = XPID.getOutput();
		return Output;
	}

	/************COMPUTE NECK TILT (YPID)**************/
	double ComputeNeckYPID()
	{
		YPID.Compute();
	    double Output = YPID.getOutput();
		return Output;
	}

	/************COMPUTE EYE PAN (XPID)**************/
	double  ComputeEyeXPID()
	{
	//	XPID.Compute();
		double Output = XPID.getEyeOut();
		return Output;
	}

	/************COMPUTE EYE TILT (YPID)***************/
	double ComputeEyeYPID()
	{
		double Output = YPID.getEyeOut();
		return Output;
	}

	String XTrack()
	{
		//Declare Message String
		std::string Message;

		/*Compute the PID OUTPUTS*/
		double Xout = ComputeNeckXPID();
		//double out=50;
		double XeyeLOut = ComputeEyeXPID();
		//double eyeLOut = 40;
		double XeyeROut = XeyeLOut + 20;

		//Convert Output in double to String
		String string_XOutput;
		std::stringstream XOut;
		XOut << Xout;
		string_XOutput = XOut.str();

		String string_XEyeLOutput;
		std::stringstream XEyeLOut;
		XEyeLOut << XeyeLOut;
		string_XEyeLOutput = XEyeLOut.str();

		String string_XEyeROutput;
		std::stringstream XEyeROut;
		XEyeROut << XeyeROut;
		string_XEyeROutput = XEyeROut.str();

		/*Create Message to be sent over socket*/
		Message = "Global.servo.color=\"green\",";	//Change LED Color to indicate Face Detection
		Message += "Global.servo.neckRotat=";			//Rotate Neck
		Message += string_XOutput ;
		Message += "," ;			
		Message += "Global.servo.leftEyePan=";		//Move leftEye
		Message += string_XEyeLOutput;
		Message += ",Global.servo.rightEyePan=" ;		//Move rightEye
		Message += string_XEyeROutput ;
		
		
		return Message;
	}

	String YTrack()
	{
		//Declare Message String
		std::string Message;

		double Yout = ComputeNeckYPID();
		//double out=50;
		double YeyeLOut = ComputeEyeYPID()+2.55;
		//double eyeLOut = 40;
		double YeyeROut = YeyeLOut;


		//Convert Output in double to String
		String string_YOutput;
		std::stringstream YOut;
		YOut << Yout;
		string_YOutput = YOut.str();

		String string_YEyeLOutput;
		std::stringstream YEyeLOut;
		YEyeLOut << YeyeLOut;
		string_YEyeLOutput = YEyeLOut.str();

		String string_YEyeROutput;
		std::stringstream YEyeROut;
		YEyeROut << YeyeROut;
		string_YEyeROutput = YEyeROut.str();

		//Same with YPID
		Message += "Global.servo.neckTilt=";
		Message += string_YOutput;
		Message += ",";	
		Message += "Global.servo.leftEyeTilt=";		//Move leftEye
		Message += string_YEyeLOutput;
		Message += ",Global.servo.rightEyeTilt=" ;		//Move rightEye
		Message += string_YEyeROutput ;
		Message += ";" ;
	
		return Message;
	}
public:

	/********CONSTRUCTOR**********/
	FaceTrackPID( double InXPID, double OutXPID, int SetXPID, double InYPID, double OutYPID,int SetYPID ) : XPID( InXPID, OutXPID, SetXPID,0.03,0.00,0,6,7 ), YPID( InYPID, OutYPID, SetYPID,0.03,0,0,20,5.5 )	//0.03,0,0,12,5.5 //neck xpid //0.03,0.00,0,3,7	//4.5
	{
		SearchCount = -1;
	};
	
	String Track( double center, char XorY )
	{
		
		if(XorY == 'X')
		{
			SetXPID( center );
			//Declare Message String
			std::string Message;
		
			Message = XTrack();
			Message += ";" ;
			
			return Message; 
		}
		else if (XorY == 'Y')
		{

			SetYPID( center );
			//Declare Message String
			std::string Message;
	
			Message = "Global.servo.color=\"green\",";

			Message += YTrack();

			return Message; 
		}
	}
	/***************TRACKING**********/
	String Track( double Xcenter,double Ycenter )
	{
	//	cout<<"Tracking"<<endl;
		
		//Set PID INPUTS
		SetPID( Xcenter,Ycenter );	

		//Declare Message String
		std::string Message;
		
		Message = XTrack();

		Message += "," ;

		Message += YTrack();

		return Message; 
	}

	/***********SEND CONSTANT COMMANDS WHEN NO FACE IS DETECTED**********/
	String constant_commands()
	{
		double Xout = XPID.getOutput();
		//double out=50;
		double XeyeLOut = XPID.getEyeOut();
		double XeyeROut = XeyeLOut+20;

		std::string Message;

		String string_XOutput;
		std::stringstream XOut;
		XOut << Xout;
		string_XOutput = XOut.str();

		String string_XEyeLOutput;
		std::stringstream XEyeLOut;
		XEyeLOut << XeyeLOut;
		string_XEyeLOutput=XEyeLOut.str();

		String string_XEyeROutput;
		std::stringstream XEyeROut;
		XEyeROut << XeyeROut;
		string_XEyeROutput = XEyeROut.str();

		Message = "Global.servo.color=\"green\",";
		Message += "Global.servo.neckRotat=";
		Message += string_XOutput ;
		Message += "," ;
		Message += "Global.servo.leftEyePan=";
		Message += string_XEyeLOutput;
		Message += ",Global.servo.rightEyePan=" ;
		Message += string_XEyeROutput ;
		Message += ";" ;

		double Yout = YPID.getOutput();
		//double out=50;
		double YeyeLOut = (YPID.getEyeOut())+2.55;
		//double eyeLOut = 40;
		double YeyeROut = YeyeLOut;


		//Convert Output in double to String
		String string_YOutput;
		std::stringstream YOut;
		YOut<<Yout;
		string_YOutput = YOut.str();

		String string_YEyeLOutput;
		std::stringstream YEyeLOut;
		YEyeLOut<<YeyeLOut;
		string_YEyeLOutput = YEyeLOut.str();

		String string_YEyeROutput;
		std::stringstream YEyeROut;
		YEyeROut<<YeyeROut;
		string_YEyeROutput = YEyeROut.str();

		//Same with YPID
		Message += "Global.servo.neckTilt=";
		Message += string_YOutput;
		Message += ",";	
		Message += "Global.servo.leftEyeTilt=";		//Move leftEye
		Message += string_YEyeLOutput;
		Message += ",Global.servo.rightEyeTilt=" ;		//Move rightEye
		Message += string_YEyeROutput ;
		Message += ";" ;

		cout<<endl<<"Constant Command Sent"<<endl;
		return Message;
	}
	/*******************SEARCH FOR A FACE***************/
	String Search() 
	{
		String command;
	/*	if( SearchCount >=0 && SearchCount < 4)
		{
			SearchCount++;
	
		}	
		if( SearchCount == 1 || SearchCount == 3)
		{		*/
			command = "Global.servo.color=\"red\",Global.servo.neckRotat=50 smooth:0.50s; Global.servo.leftEyePan=40, Global.servo.rightEyePan=60 smooth:0.50s, Global.servo.neckTilt=55.56 smooth:0.50s, Global.servo.leftEyeTilt=42.55 smooth:0.50s, Global.servo.rightEyeTilt=42.55 smooth:0.50s;";
			cout<<endl<<command<<endl;
			XPID.setOutput( 50 );
			YPID.setOutput( 55.56 );
	//	}
			
/*		else if( SearchCount == 2)
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
		cout<<"Search Command sent"; 	
		return command;
	}
	void setSearchCount( int count )
	{
		SearchCount = count;
	}
};
#include<Windows.h>

/**************************MAIN LOOP***************************/
int __cdecl main( int argc, char **argv )
{
	/*********PID VARIABLES***********/
	static const int SetpointXPID = 160;
	static const int SetpointYPID = 120;

	double InputXPID = 0;
	double InputYPID = 0;

	double OutputXPID = 50;  
	double OutputYPID = 55.56;  

	/************Object Initializatoin*********/
	Communication Com( 2, argv);
	FaceDetection DetectFace;
	FaceTrackPID FaceTrack( InputXPID, OutputXPID, SetpointXPID, InputYPID, OutputYPID, SetpointYPID );
	
	

	int count = 0;
	double sum = 0;

	//Main Loop
	while(1)
	{
		long now = GetTickCount();

		//Receive Image over Socket
		char* data = Com.receiveSocket();

		//Process Image for Face Detection
		cout<<"\n Flag Count" << flag.facecount;
		flag.true_face = DetectFace.getFaceInfo( data, 0, flag.facecount );
	
		/**************DEBUGGING************/
		//cout<<endl<<"Face Center:"<<DetectFace.getFaceCenter()<<endl;
		long facetime;
		//Check if face was detected

		if(flag.true_face)	//face found
		{	
			facetime=GetTickCount();
			
			//cout<<"found"<<endl;
			flag.facecount++;	//tracking face

			cout<<"\nDetermining PID";
			//Determine Face Center
			double XInputPID = DetectFace.getFaceCenter();
			double YInputPID = DetectFace.getFaceTiltCenter();

			/*******DEBUGGING*******/
			//cout<<endl<<"XINPUT: "<<XInputPID<<endl;

			//Track Face and Determine Command
			std::string Message = FaceTrack.Track( XInputPID, YInputPID);	
			
			/*******DEBUGGING*******/
			cout<<Message<<endl;
			
			//Convert Message String to char* to send over socket
			char* command = new char[Message.size() + 1];
			std::strcpy(command,Message.c_str());

			//Send Command over Socket
			Com.sendSocket(command);
		}	
		else	//face not found
		{	
			flag.facecount = 0;	//face lost
			long facelosttime = GetTickCount()-(facetime);
			
			//if face lost for more than 2 seconds
			//search

			//neutral for debugging: change in Search()
			if(facelosttime >= 2000 && count > 0)
			{
				
				if(flag.repeat != false)
					FaceTrack.setSearchCount(0);
				flag.repeat = false;
				std::string Message=FaceTrack.Search();
				char* command = new char[Message.size() + 1];
				std::strcpy(command,Message.c_str());
				Com.sendSocket(command);
			}
			else
			{
				std::string Message = FaceTrack.constant_commands();
				char* command = new char[Message.size()+1];
				std::strcpy(command,Message.c_str());
				Com.sendSocket(command);
			}
			Sleep(100);
		}	

		count++;
		//Look for Escape Key Hit
		if(GetAsyncKeyState(VK_ESCAPE))
		{	  
			break;
		}
		sum+=GetTickCount() - now;
		//cout<<"Time: "<<GetTickCount() - now;
		Sleep(300);
	}
	cout<<"\nAverage Time: "<<sum/count;
	//Terminate Sockets and Image Processing
	Com.terminateSocket();
	DetectFace.terminateFaceDetectionProcess(1);
	
	return 0;
}