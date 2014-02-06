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

using namespace urbi;
using namespace std;

class ReetiServer {

};

class Communication {
	private:

		int sockfd, newsockfd, portno, n;
		socklen_t clilen;
		char buffer[256];
		struct sockaddr_in serv_addr, cli_addr;

		void initSocket(int argc, char **argv);
		void sendSocket(char * message, int size);		
		void error(const char *msg);
	public:
		Communication(int argc, char**argv);
		char* receiveSocket();
		void closeSocket();
};

class ReetiCommunication {
	private:

		UClient *client;
	public:

		ReetiCommunication(string IPaddr, int Port);
		void sendCommand(string commands);
};

#endif
