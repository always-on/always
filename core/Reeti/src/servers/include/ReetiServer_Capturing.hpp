#ifndef REETISERVER_H
#define REETISERVER_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <urbi/uclient.hh>

#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/video/background_segm.hpp>
#include <cv.h>
#include <highgui.h>

using namespace urbi;
using namespace std;
using namespace cv;

class ReetiServer {

};

class Communication {
	private:

		int sockfd, newsockfd, portno, n;
		socklen_t clilen;
		char buffer[500];
		struct sockaddr_in serv_addr, cli_addr;

		void initSocket(int argc, char **argv);		
		void error(const char *msg);
	public:
		Communication(int argc, char**argv);
		char* receiveSocket();
		void closeSocket();
		void sendSocket(Mat gray_frame, unsigned char *input);
};

class ReetiCommunication {
	private:

		UClient *client;
	public:

		ReetiCommunication(string IPaddr, int Port);
		void sendCommand(string commands);
};

class CaptureFrames { //: public CvCapture {
	private:
		cv::VideoCapture m_cap;
		CvCapture* capture;

	public:
		CaptureFrames(int arg); //: capture(CvCapture* capture);

		void OpenCamera(int arg);
		
		Mat CaptureImage(int intDebug);
	
		unsigned char * DisplayImage(Mat gray_frame, int Debug);
};

#endif
