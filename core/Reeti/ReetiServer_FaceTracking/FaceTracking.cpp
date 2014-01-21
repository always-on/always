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

void error(const char *msg)
{
    perror(msg);
}

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
			newsockfd = accept(sockfd, (struct sockaddr*) (&cli_addr),&clilen);
		
			if (newsockfd < 0)
				error("ERROR: could not accept the incoming connection!");
		
			bzero(buffer,500);
		}

		char* receiveSocket()
		{
			memset (buffer,0, (sizeof(char)*500));
			cout << "\nReceiving from socket...\n";
			n = read(newsockfd,buffer,499);
		
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
		ReetiCommunication(std::string IP_addr, int Port)
		{
			client = new UClient(IP_addr,Port);
		}

		void Send(std::string  commands)
		{
			client->send("%s", commands.c_str());
		}
};

int main(int argc, char** argv)
{
	Communication Com(argc,argv);
	ReetiCommunication Reeti("130.215.28.4",54001);

	int count  = 0;
	double sum = 0;

	while(count<500)
	{
		char *data = Com.receiveSocket();
		std::string message(data);
		Reeti.Send(message);
	}

	Com.closeSocket();

	return 0;
}
