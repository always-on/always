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



using namespace std;
using namespace urbi;


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
	ReetiCommunication(std::string IP_addr, int Port)
	{
		client = new UClient(IP_addr,Port);//"130.215.28.4",54001);
	}
	/*********SEND COMMANDS TO REETI**********/
	void Send(std::string  commands)
	{
		client->send("%s", commands.c_str());
	}
};

int main(int argc, char** argv)
{
	Communication Com(argc,argv);
	ReetiCommunication Reeti("130.215.28.4",54001);

	int count=0;
	double sum=0;

	/*********MAIN LOOP*******/
	while(count<500)
	{

		time_t now = time(0);
		//Receive Message
		char *data = Com.receiveSocket();
		//Convert Command from char* to String
		std::string message(data);
		//Sending Commands
		Reeti.Send(message);

		cout<<"Time to capture and send"<<time(0)-now;
		sum+=time(0)-now;

		count++;
	}
	cout<<"Average Time: "<<sum/50;
	Com.closeSocket();
	return 0;
}
void sendingCommands(char* message)
{


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



