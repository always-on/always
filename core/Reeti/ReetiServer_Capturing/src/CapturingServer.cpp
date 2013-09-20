/*
 * reeti.cpp
 *
 *  Created on: Jun 25, 2013
 *      Author: Ayesha Fathima
 */




#include <uclient.hh>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>


#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/video/background_segm.hpp>
#include <cv.h>
#include <highgui.h>

using namespace std;
using namespace urbi;
using namespace cv;

/********************ERROR MESSAGE HANDLER**********************/
void error(const char *msg)
{
    perror(msg);

}

/********************CAPTURE FRAMES MODULE**********************/

/*********CLASS DEFINITION**********/
class CaptureFrames
{
private:
	cv::VideoCapture m_cap;
	CvCapture* capture;

public:
	/***********CONSTRUCTOR************/
	CaptureFrames(int arg): capture(cvCreateCameraCapture(arg))
	{

		/*if ( !capture ) {
		     fprintf( stderr, "ERROR: capture is NULL \n" );
		}

		cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_WIDTH, 320 );
		cvSetCaptureProperty( capture, CV_CAP_PROP_FRAME_HEIGHT, 240 );*/
		OpenCamera(arg);
	}
	/***********OPEN CAMERA***************/
	void OpenCamera(int arg)
	{
		m_cap.open(arg);
		if(!m_cap.isOpened())
		{
			cout << "Can't open camera" << endl;

		}
		m_cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
		m_cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);

	}
	/***********CAPTURE IMAGES**********/
	Mat CaptureImage(int intDebug)
	{
		cout<<"Capturing";
		Mat tmp_frame,gray_frame;
		m_cap>>tmp_frame;
		if(!tmp_frame.data)
			cout<<"can not read data from the video source"<<endl;
		cvtColor(tmp_frame,gray_frame,CV_RGB2GRAY);
		if(intDebug)
			DisplayImage(gray_frame,1);
		return gray_frame;

	}
	/************DISPLAY IMAGES***********/
	unsigned char * DisplayImage(Mat gray_frame, int Debug)
	{
		unsigned char *input = (gray_frame.data);
					Mat output(240, 320, CV_8UC1, input);
		if(Debug)
		{
			cout<<endl<<"Displaying"<<endl;
			namedWindow( "Display Image", CV_WINDOW_AUTOSIZE );
			imshow( "Display Image", output );
			waitKey(500);
			cvDestroyWindow("Display Image");
		}
		return input;
	}
};
/*******************COMMUNICATION MODULE**************/

/*********CLASS DEFINITION**********/
class Communication
{
private:
	int sockfd, newsockfd, portno;
	socklen_t clilen;
	char buffer[500];
	struct sockaddr_in serv_addr, cli_addr;
	int n;

public:
	/*********CONSTRUCTOR*********/
	Communication(int argc, char** argv) {
		initSocket(argc, argv);
	}

	/*********INITIALIZE SOCKET******/
	void initSocket(int argc, char** argv) {
		if (argc < 2) {
			fprintf(stderr, "ERROR, no port provided\n");
		}
		sockfd = socket(AF_INET, SOCK_STREAM, 0);
		if (sockfd < 0)
			error("ERROR opening socket");

		bzero((char*) (&serv_addr), sizeof(serv_addr));
		portno = atoi(argv[1]);
		serv_addr.sin_family = AF_INET;
		serv_addr.sin_addr.s_addr = INADDR_ANY;
		serv_addr.sin_port = htons(portno);
		if (bind(sockfd, (struct sockaddr*) (&serv_addr), sizeof(serv_addr))
				< 0)
			error("ERROR on binding");

		listen(sockfd, 5);
		clilen = sizeof(cli_addr);
		newsockfd = accept(sockfd, (struct sockaddr*) (&cli_addr),&clilen);
		if (newsockfd < 0)
			error("ERROR on accept");
		bzero(buffer,500);
	}
	/**************SEND VIA SOCKET*********/
	void sendSocket(Mat gray_frame,unsigned char *input)
	{
		cout<<"\nwriting\n";
		n = write(newsockfd,input,(gray_frame.rows)*(gray_frame.cols)*(gray_frame.elemSize())+1);
		if (n < 0)
			error("ERROR writing to socket");
	}
	/*************RECEIVE VIA SOCKET**********/
	char* receiveSocket()
	{
		memset (buffer,0, (sizeof(char)*500));
		cout<<"\nreceiving\n";
		n = read(newsockfd,buffer,499);
		if (n < 0) error("ERROR reading from socket");
		printf("Here is the message: %s\n",buffer);
		return buffer;
	}
	/**************CLOSE SOCKET************/
	void closeSocket()
	{
		close(newsockfd);
		close(sockfd);
	}
};

/*******************REETI COMMUNICATION MODULE******************/

/**********CLASS DEFINITION*******/
class ReetiCommunication
{
private:
	UClient *client;
public:
	/********CONSTRUCTOR*******/
	ReetiCommunication(String IP_addr, int Port)
	{
		client = new UClient(IP_addr,Port);//"130.215.28.4",54001);
	}
	/*********SEND COMMANDS TO REETI**********/
	void Send(String commands)
	{
		client->send("%s", commands.c_str());
	}
};
/*int main(int argc, char** argv)
{
	CaptureFrames FrameCapture(2);
	Mat gray_frame = FrameCapture.CaptureImage(0);
	Communication Com(argc,argv);
	cout<<"Captured";
	Com.sendSocket(gray_frame,FrameCapture.DisplayImage(gray_frame,0));
	Com.closeSocket();
	return 0;
}*/
int main(int argc, char** argv)
{
	Communication Com(argc,argv);
	CaptureFrames FrameCapture(2);

	int count=0;
	double sum=0;

	/*********MAIN LOOP******/
	while(count<500)
	{

		time_t now = time(0);
		//Receive Message
		char *data;
		data = Com.receiveSocket();
		String message(data);
		if(message == "Capture")
		{
			//Capture Frame
			Mat gray_frame = FrameCapture.CaptureImage(0);
			//Send Frame over Socket
			Com.sendSocket(gray_frame,FrameCapture.DisplayImage(gray_frame,0));
			cout<<"Time to capture and send"<<time(0)-now;
			sum+=time(0)-now;
		}
		count++;
	}
	cout<<"Average Time: "<<sum/50;
	Com.closeSocket();
	return 0;
}
void sendingCommands(char* message)
{
	ReetiCommunication Reeti("130.215.28.4",54001);
	//Convert Command from char* to String
	String commands(message);
	//Sending Commands
	Reeti.Send(commands);

	/*if( std::string::npos != commands.find("red"))
	{
		cout<<"\n Sleeping";
		usleep(5500000);
	}
	else
	{
		usleep(450000);
	}*/
}



