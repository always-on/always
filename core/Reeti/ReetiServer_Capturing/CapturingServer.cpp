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

void error(const char *msg)
{
    perror(msg);
}

class CaptureFrames
{
	private:
		cv::VideoCapture m_cap;
		CvCapture* capture;

	public:
		CaptureFrames(int arg): capture(cvCreateCameraCapture(arg))
		{
			OpenCamera(arg);
		}

		void OpenCamera(int arg)
		{
			m_cap.open(arg);

			if(!m_cap.isOpened())
				cout << "Error: could not open the camera!" << endl;

			m_cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
			m_cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);
		}
		
		Mat CaptureImage(int intDebug)
		{
			cout<<"Capturing frame...";
			Mat tmp_frame,gray_frame;
			m_cap>>tmp_frame;
		
			if(!tmp_frame.data)
				cout<<"Error: could not read data from the video source!"<<endl;
			
			cvtColor(tmp_frame,gray_frame,CV_RGB2GRAY);
			
			if(intDebug)
				DisplayImage(gray_frame,1);
			
			return gray_frame;
		}
	
		unsigned char * DisplayImage(Mat gray_frame, int Debug)
		{
			unsigned char *input = (gray_frame.data);
			Mat output(240, 320, CV_8UC1, input);
		
			if(Debug)
			{
				cout<<endl<<"Displaying..."<<endl;
				namedWindow( "Display Image", CV_WINDOW_AUTOSIZE );
				imshow( "Display Image", output );
				waitKey(500);
				cvDestroyWindow("Display Image");
			}
		
			return input;
		}
};

class Communication
{
	private:
		int n;
		int sockfd, newsockfd, portno;
		socklen_t clilen;
		char buffer[500];
		struct sockaddr_in serv_addr, cli_addr;

	public:
		Communication(int argc, char** argv) {
			initSocket(argc, argv);
		}

		void initSocket(int argc, char** argv) {
			
			if (argc < 2)
				fprintf(stderr, "ERROR: no port number is provided!\n");

			sockfd = socket(AF_INET, SOCK_STREAM, 0);
		
			if (sockfd < 0)
				error("ERROR: could not open a socket!");

			bzero((char*) (&serv_addr), sizeof(serv_addr));
			portno = atoi(argv[1]);
			serv_addr.sin_family = AF_INET;
			serv_addr.sin_addr.s_addr = INADDR_ANY;
			serv_addr.sin_port = htons(portno);
		
			if (bind(sockfd, (struct sockaddr*) (&serv_addr), sizeof(serv_addr)) < 0)
				error("ERROR: could not bind the socket!");

			listen(sockfd, 5);
			clilen = sizeof(cli_addr);
			newsockfd = accept(sockfd, (struct sockaddr*) (&cli_addr), &clilen);
			
			if (newsockfd < 0)
				error("ERROR: could not accept the incoming connection!");
			
			bzero(buffer,500);
		}
	
		void sendSocket(Mat gray_frame,unsigned char *input)
		{
			cout<<"\nWriting to the socket...\n";
			n = write(newsockfd,input,(gray_frame.rows)*(gray_frame.cols)*(gray_frame.elemSize())+1);
		
			if (n < 0)
				error("ERROR: could not write to the socket!");
		}

		char* receiveSocket()
		{
			memset (buffer, 0, (sizeof(char)*500));
			cout << "\nReceiving from socket...\n";
			n = read(newsockfd, buffer, 499);
			
			if (n < 0) 
				error("ERROR: could not read from socket!");
			
			cout << "Received message: " << buffer << endl;
		
			return buffer;
		}

		void closeSocket()
		{
			close(newsockfd);
			close(sockfd);
		}
};

class ReetiCommunication
{
	private:
		UClient *client;
	public:
		ReetiCommunication(String IP_addr, int Port)
		{
			client = new UClient(IP_addr,Port);
		}
	
		void Send(String commands)
		{
			client->send("%s", commands.c_str());
		}
};

int main(int argc, char** argv)
{
	Communication Com(argc,argv);
	CaptureFrames FrameCapture(2);

	int count=0;
	double sum=0;

	while(1)
	{
		char *data;
		data = Com.receiveSocket();
		String message(data);
		
		if(message == "Capture")
		{
			Mat gray_frame = FrameCapture.CaptureImage(0);
			Com.sendSocket(gray_frame,FrameCapture.DisplayImage(gray_frame,0));
		}
	}

	Com.closeSocket();

	return 0;
}

void sendingCommands(char* message)
{
	ReetiCommunication Reeti("130.215.28.4", 54001);
	String commands(message);
	Reeti.Send(commands);
}