//============================================================================
// Name        : HELLOWORLD.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

//#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <iostream>
#include <urbi/uclient.hh>

using namespace urbi;
using namespace std;

/********************ERROR MESSAGE HANDLER**********************/
void error(const char *msg)
{
	perror(msg);

}
/*******************COMMUNICATION MODULE**************/

/*********CLASS DEFINITION**********/
class Communication
{
private:
	int sockfd, newsockfd, portno;
	socklen_t clilen;
	char buffer[256];
	struct sockaddr_in serv_addr, cli_addr;
	int n;
public:

	/*********CONSTRUCTOR*********/
	Communication(int argc, char**argv)
	{

		initSocket(argc,argv);
	}

	/*********INITIALIZE SOCKET******/
	void initSocket(int argc, char **argv)
	{
		if (argc < 2) {
			fprintf(stderr,"ERROR, no port provided\n");
		}
		sockfd = socket(AF_INET, SOCK_STREAM, 0);
		if (sockfd < 0)
			error("ERROR opening socket");
		bzero((char *) &serv_addr, sizeof(serv_addr));
		portno = atoi(argv[1]);
		serv_addr.sin_family = AF_INET;
		serv_addr.sin_addr.s_addr = INADDR_ANY;
		serv_addr.sin_port = htons(portno);
		if (bind(sockfd, (struct sockaddr *) &serv_addr,sizeof(serv_addr)) < 0)
			error("ERROR on binding");
		listen(sockfd,5);
		clilen = sizeof(cli_addr);
		newsockfd = accept(sockfd,(struct sockaddr *) &cli_addr,&clilen);
		if (newsockfd < 0)
			error("ERROR on accept");
		bzero(buffer,256);
	}
	/**************SEND VIA SOCKET*********/
	void sendSocket(char * message, int size)
	{
		cout<<"\nwriting\n";
		n = write(newsockfd,message,size);
		if (n < 0)
			error("ERROR writing to socket");
	}
	/*************RECEIVE VIA SOCKET**********/
	char* receiveSocket()
	{
		cout<<"\n receiving\n";
		memset(buffer, 0, sizeof buffer);
		n = read(newsockfd,buffer,255);
		if (n < 0)
		{
			error("ERROR reading from socket");
			return 0;
		}
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
	ReetiCommunication(string IPaddr, int Port)
	{
		client = new UClient(IPaddr,Port);//"130.215.28.4",54001);
	}
	/*********SEND COMMANDS TO REETI**********/
	void Send(string commands)
	{
		client->send("%s", commands.c_str());
	}
};

int main(int argc, char** argv)
{
	Communication Com(argc,argv);
	ReetiCommunication Reeti("130.215.28.4",54001);
	cout<<"connected";
	while(1)
	{

		char *message = Com.receiveSocket();
		//cout<<message;
		string commands(message);
		Reeti.Send(commands);
	}
	return 0;
}


