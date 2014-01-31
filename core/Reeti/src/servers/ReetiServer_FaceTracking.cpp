//============================================================================
// Name        : FaceTracking
// Author      : MSH
// Description : Agent's Face Tracking behavior.
//============================================================================

#include "include/ReetiServer_FaceTracking.hpp"

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

	if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
		error("ERROR: could not bind the socket!");

	listen(sockfd, 5);
	clilen = sizeof(cli_addr);
	newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);

	if (newsockfd < 0)
		error("ERROR: could not accept the incoming connection!");

	bzero(buffer, 500);
}

void Communication::sendSocket(char * message, int size)
{
	cout<<"\nWriting to socket...\n";
	n = write(newsockfd, message, size);

	if (n < 0)
		error("ERROR: could not write to the socket!");
}

char* Communication::receiveSocket()
{
	cout<<"\n Receiving from socket...\n";
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

int main(int argc, char** argv)
{
	Communication Com(argc, argv);
	ReetiCommunication Reeti(argv[1], 54001);
	cout << "Connected...";

	while(1) 
	{
		char *message = Com.receiveSocket();
		string commands(message);
		Reeti.sendCommand(commands);
	}

	Com.closeSocket();

	cout << "Disconnected...";

	return 0;
}


