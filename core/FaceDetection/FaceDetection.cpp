/**
 * @file videoCap.cpp
 * @author M. Shayganfar ( based in the classic facedetect.cpp in samples/c )
 * @Show how to connect to a camera and change some camera properties. Also, detect human face.
 */

#include "Communication/Communication.h"

#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/features2d/features2d.hpp"

#include "shore/Shore.h"
#include "shore/CreateFaceEngine.h"
#include "shore/ContentToText.h"
#include "shore/Image.h"

#include <iostream>
#include <stdio.h>
#include <windows.h>

#define FRAME_WIDTH 320
#define FRAME_HEIGHT 240
#define TIMER_DELAY 2000

#define IMAGE_CONTENT_ERROR -1
#define FRAME_CAPTURE_ERROR -2
#define CAMERA_CONNECTION_ERROR -3
#define SENDING_CAPTURE_COMMAND_ERROR -4

using namespace std;
using namespace cv;

Communication Com;
CvCapture* capture = NULL; //Alternative: VideoCapture capture(0);
Shore::Engine* engine;
Shore::Engine* ReetiEngine;

typedef struct FACE {
	int intLeft;
	int intRight;
	int intTop;
	int intBottom;
	int intHappiness;
	int intArea;
	int intCenter;
	int intTiltCenter;
}FaceInfo;

extern "C" __declspec(dllexport)

int initAgentShoreEngine( int intDebug ) {
	
	float         timeBase          = 0;            // Use single image mode
	bool          updateTimeBase    = false;        // Not used in video mode
	unsigned long threadCount       = 2UL;          // Let's take one thread only
	char const*   model             = "Face.Front"; // Search frontal faces
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
	
	capture = cvCaptureFromCAM( -1 );
	cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH, FRAME_WIDTH);
	cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT);
	
	if ( !capture )
	{
		std::cerr << "Error: cvCapture structure initialization failed - exit!\n";
		return -2;
		//std::exit(1);
	}

	Mat frame;
	frame = cvRetrieveFrame(capture);
	if(frame.data == NULL)
	{
		cout << "Could not get the camera, returning" << endl;
		return -2;
	}

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
		return -1;
		//std::exit(1);
	}
	
	return 0;
	
	//Alternative:
	//capture.set(CV_CAP_PROP_FRAME_WIDTH, FRAME_WIDTH);
	//capture.set(CV_CAP_PROP_FRAME_HEIGHT, FRAME_HEIGHT);

}

extern "C" __declspec(dllexport)

int initReetiShoreEngine( char ** IP_ADDRESS, int intDebug ) {

	float         timeBase          = 0;            // Use single image mode
	bool          updateTimeBase    = false;        // Not used in video mode
	unsigned long threadCount       = 2UL;          // Let's take one thread only
	char const*   model             = "Face.Front"; // Search frontal faces
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
	
	ReetiEngine = Shore::CreateFaceEngine( timeBase,
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

	if ( ReetiEngine == 0 )
	{
		std::cerr << "Error: Engine setup failed - exit!\n";
		return -1;
		//std::exit(1);
	}

	Com.initSocket(2, IP_ADDRESS);
	return 0;
}

extern "C" __declspec(dllexport)

void terminateAgentShoreEngine( int intDebug ) {
	try{
		if(capture == NULL){
			cout << "Capture is null, returning " << endl;
			return;
		}
		Mat frame;
		frame = cvRetrieveFrame(capture);
		if(frame.data == NULL){
			cout << "frame is null, returning " << endl;
			return;
		}
		cvReleaseCapture( &capture );
		if (intDebug == 1)
				cvDestroyWindow( "Display window" );
		Shore::DeleteEngine( engine );
		cout << "RELEASED IT" << endl;
	} catch(Exception e){
		cout << "ERROR!" << endl;
	}
}

extern "C" __declspec(dllexport)

void terminateReetiShoreEngine( int intDebug ) {
	if (intDebug == 1)
			cvDestroyWindow( "Display window" );
	Shore::DeleteEngine( ReetiEngine );
}

FaceInfo invalidateFaceInfo(int errorCode)
{
	FaceInfo faceInfo;
	
	faceInfo.intBottom     = errorCode;
	faceInfo.intTop        = errorCode;
	faceInfo.intLeft       = errorCode;
	faceInfo.intRight      = errorCode;
	faceInfo.intHappiness  = errorCode;
	faceInfo.intCenter     = errorCode;
	faceInfo.intTiltCenter = errorCode;
	faceInfo.intArea 	   = errorCode;
	
	return faceInfo;
}

extern "C" __declspec(dllexport)

FaceInfo getAgentFaceInfo( int intDebug )
{
	int intPrevWidth = 0;
	int intPrevHeight = 0;
	int intCurrWidth = 0;
	int intCurrHeight = 0;
	int intSelectedFaceIndex = -1;

	FaceInfo faceInfo;

	Mat frame, gray_frame;
	Shore::Content const* content = 0;

	//Capture Image using Asus Camera
	if( capture ) //Alternative: if( capture.isOpened() )
	{
		//frame = cvQueryFrame( capture ); //Alternative: capture >> frame;

		frame = cvRetrieveFrame(capture);

		if(frame.data == NULL)
		{
			std::cerr <<"Error: Frame capture problem - exit!\n";
			return invalidateFaceInfo(FRAME_CAPTURE_ERROR);
			//std::exit(1);
		}
		//cout << frame.data << endl;

		//Convert to Grayscale
		cvtColor( frame , gray_frame , CV_RGB2GRAY );

		//Declare IPL Image for Processing
		
		IplImage* im = cvCreateImage( cvSize(FRAME_WIDTH,FRAME_HEIGHT), IPL_DEPTH_8U, 1 );

		//Convert Matrix to IPL Image
		im->imageData = ( char * ) gray_frame.data;


		//Declare Image for Displaying and Processing
		Image image( FRAME_WIDTH,FRAME_HEIGHT,im );

		content = engine->Process( image.LeftTop(), //+ frame.step*0, //frame.data, //ptr<unsigned char const>(0)
									image.Width(),//frame.cols
									image.Height(), //frame.rows
									1,
									1,
									image.Width(), //frame.cols+1 //frame.size().width
									0,
									"GRAYSCALE" );
		cout << "AFTER PROCES" << endl;		
		if ( content->GetObjectCount() > 0 )
		{
			cout << "got a face" << endl;
			for( int i = 0 ; i < content->GetObjectCount() ; i++ )
			{
				intCurrWidth = abs(content->GetObject(i)->GetRegion()->GetRight() - content->GetObject(i)->GetRegion()->GetLeft());
				intCurrHeight = abs(content->GetObject(i)->GetRegion()->GetBottom() - content->GetObject(i)->GetRegion()->GetTop());
					
				if((intPrevWidth*intPrevHeight) < (intCurrWidth*intCurrHeight))
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

				if( intDebug )
				{
					
					Mat markedFace;
					std::cout << ContentToText( content ) << "\n\n";
					image.DrawContent( content );
					image.SavePgm( "face.pgm" );
					markedFace = cvLoadImage( "face.pgm", CV_LOAD_IMAGE_UNCHANGED );
					namedWindow( "Display window", CV_WINDOW_AUTOSIZE );
					imshow( "Display window", markedFace );
					cvWaitKey( 500 );
				}
			}
		}
		else
		{
			faceInfo = invalidateFaceInfo(IMAGE_CONTENT_ERROR);
		}
		cvReleaseImage(&im);
	}
	else
	{
		cout << "\nCamera connection problem...\n";
		faceInfo = invalidateFaceInfo(CAMERA_CONNECTION_ERROR);
	}
	return faceInfo;
}

extern "C" __declspec(dllexport)

FaceInfo getReetiFaceInfo( int intDebug )
{	
	FaceInfo faceInfo;

	//Request Image
	int sendingResult = Com.sendSocket("Capture");
	
	if(sendingResult != 1)
	{
		//Receive Image over Socket
		char* data = Com.receiveSocket();

		int intPrevWidth = 0;
		int intPrevHeight = 0;
		int intCurrWidth = 0;
		int intCurrHeight = 0;
		int intSelectedFaceIndex = -1;

		//Declare Shore Content
		Shore::Content const* content = 0;

		//Declare Matrix Frames for Processing
		Mat gray_frame( FRAME_HEIGHT,FRAME_WIDTH, CV_8UC1, data );

		//Declare IPL Image for Processing
	
		IplImage* im = cvCreateImage( cvSize(FRAME_WIDTH,FRAME_HEIGHT), IPL_DEPTH_8U, 1 );

		//Convert Matrix to IPL Image
		im->imageData = ( char * ) gray_frame.data;

		//Declare Image for Displaying
		Image image( FRAME_WIDTH,FRAME_HEIGHT,im );

		//Setting Engine
		content = ReetiEngine->Process(  image.LeftTop(),
									image.Width(),
									image.Height(),
									1,
									1,
									image.Width(), //frame.cols+1 //frame.size().width
									0,
									"GRAYSCALE" );
		//Getting Content 
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

				if( intDebug )
				{
					Mat markedFace;
					std::cout << ContentToText( content ) << "\n\n";
					image.DrawContent( content );
					image.SavePgm( "face.pgm" );
					markedFace = cvLoadImage( "face.pgm", CV_LOAD_IMAGE_UNCHANGED );
					namedWindow( "Display window", CV_WINDOW_AUTOSIZE );
					imshow( "Display window", markedFace );
					cvWaitKey( 500 );
				}
			}
		}
		else
		{
			faceInfo = invalidateFaceInfo(IMAGE_CONTENT_ERROR);
		}
		cvReleaseImage(&im);
	}

	//if sending had raised an error,
	else
	{
		/*use happiness value holder, as a signal
		to Java to exit itself and cause a restart*/
		//faceInfo.intHappiness = -2;
		
		return invalidateFaceInfo(SENDING_CAPTURE_COMMAND_ERROR);
	}

	return faceInfo;
}

int main( int argc, const char* argv[] )
{
	initAgentShoreEngine(0);
	for(int i = 0; i < 10; i++){
	getAgentFaceInfo(0);
	}
	terminateAgentShoreEngine(0);
	int i;
	cin >> i;
}