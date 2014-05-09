//============================================================================
// Name        : Capturing
// Author      : MSH
// Description : Agent's Capturing function.
//============================================================================

#include "include/ReetiServer_Capturing.hpp"

Communication::Communication(int argc, char**argv)
{
	initSocket(argc, argv);
}

void Communication::error(const char *msg)
{
	perror(msg);
}

void Communication::initSocket(int argc, char **argv)
{
	if (argc < 3)
	{
		fprintf(stderr,"ERROR: port number and IP address should be provided!\n");
		urbi::exit(EXIT_FAILURE);
	}

	sockfd = socket(AF_INET, SOCK_STREAM, 0);

	if (sockfd < 0)
		error("ERROR: could not open a socket!");

	bzero((char *) &serv_addr, sizeof(serv_addr));
	portno = atoi(argv[2]);
	serv_addr.sin_family      = AF_INET;
	serv_addr.sin_addr.s_addr = INADDR_ANY;
	serv_addr.sin_port        = htons(portno);

	// NB: This condition checks whether the port is in use because of a crash or any other
	// reason and makes it reusable for a restarted capturing server.
	int yes = 1;
	if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(yes)) == -1)
		error("ERROR: could not prepare the same port to be reused!");

	if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
		error("ERROR: could not bind the socket!");

	listen(sockfd, 5);
	clilen = sizeof(cli_addr);
	newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);

	if (newsockfd < 0)
		error("ERROR: could not accept the incoming connection!");

	bzero(buffer, 500);
}

void Communication::sendSocket(Mat gray_frame,unsigned char *input)
{
	cout<<"\nWriting to the socket...\n";
	n = write(newsockfd, input, (gray_frame.rows)*(gray_frame.cols)*(gray_frame.elemSize())+1);

	if (n < 0)
		error("ERROR: could not write to the socket!");
}

char* Communication::receiveSocket()
{
	cout<<"\nReceiving from socket...\n";
	memset(buffer, 0, sizeof buffer);
	n = read(newsockfd, buffer, 499);

	if (n < 0)
	{
		error("ERROR: could not read from socket!");
		return 0;
	}

	cout << "Received message: " << buffer << endl;

	return buffer;
}

void Communication::closeSocket()
{
	close(newsockfd);
	close(sockfd);
}

ReetiCommunication::ReetiCommunication(string IPaddr, int Port)
{
	client = new UClient(IPaddr, Port);
}

void ReetiCommunication::sendCommand(string commands)
{
	client->send("%s", commands.c_str());
}

CaptureFrames::CaptureFrames(int arg)
{
	capture = cvCreateCameraCapture(arg);
	OpenCamera(arg);
}

void CaptureFrames::OpenCamera(int arg)
{
	m_cap.open(arg);

	if(!m_cap.isOpened())
		cout << "Error: could not open the camera!" << endl;

	m_cap.set(CV_CAP_PROP_FRAME_WIDTH, 320);
	m_cap.set(CV_CAP_PROP_FRAME_HEIGHT, 240);
}
		
Mat CaptureFrames::CaptureImage(int intDebug)
{
	cout << "Capturing frame..." << endl;
	
	Mat tmp_frame, gray_frame;
	m_cap >> tmp_frame;
		
	if(!tmp_frame.data)
	{
		cout << "Error: could not read data from the video source!" << endl;
		urbi::exit(EXIT_FAILURE);
	}
	
	cvtColor(tmp_frame, gray_frame, CV_RGB2GRAY);
			
	if(intDebug)
		DisplayImage(gray_frame, 1);
			
	return gray_frame;
}
	
unsigned char * CaptureFrames::DisplayImage(Mat gray_frame, int Debug)
{
	unsigned char *input = (gray_frame.data);
	Mat output(240, 320, CV_8UC1, input);
		
	if(Debug)
	{
		cout<<endl << "Displaying..." << endl;
		namedWindow("Display Image", CV_WINDOW_AUTOSIZE);
		imshow("Display Image", output);
		waitKey(500);
		cvDestroyWindow("Display Image");
	}
		
	return input;
}

int main(int argc, char** argv)
{
	Communication Com(argc, argv);
	CaptureFrames FrameCapture(2);

	int count=0;
	double sum=0;
	char *data;

	while(1)
	{
		data = Com.receiveSocket();
		String message(data);
		
		if(message == "Capture")
		{
			Mat gray_frame = FrameCapture.CaptureImage(0);
			Com.sendSocket(gray_frame, FrameCapture.DisplayImage(gray_frame, 0));
		}
	}

	Com.closeSocket();

	return 0;
}


