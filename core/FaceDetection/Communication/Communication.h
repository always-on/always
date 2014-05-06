#ifndef COMMUNICATION_H
#define COMMUNICATION_H
/***********************************************CLIENT_SOCKET COMMUNICATION MODULE**********************************************/

#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>
#include <iostream>


// Need to link with Ws2_32.lib, Mswsock.lib, and Advapi32.lib
#pragma comment (lib, "Ws2_32.lib")
#pragma comment (lib, "Mswsock.lib")
#pragma comment (lib, "AdvApi32.lib")

/*******************DEFAULT****************/
#define DEFAULT_BUFLEN 76801
#define DEFAULT_PORT "27017"

/*********************CLASS DEFINITION****************/
class Communication
{
private:
	WSADATA wsaData;

	SOCKET ConnectSocket;	//Declare Socket

	struct addrinfo *result,
					*ptr,
					hints;

	//Receive Buffer
	char recvbuf[DEFAULT_BUFLEN];	
	//Error Detection Flag
	long iResult;	
	//Receive Buffer Length
	int recvbuflen;	

public:
	
	/************INITIALIZER************/
	int initSocket(int argc, char *argv[])
	{
		/******Initialize Data Members*****/
		 ConnectSocket = INVALID_SOCKET;
		 addrinfo *result = NULL,
					*ptr = NULL,
					hints;
		//Set Buffer Length Appropriately
		 recvbuflen = DEFAULT_BUFLEN;	

		// Validate the parameters
		if (argc != 2) {
			printf("usage: %s server-name\n", argv[0]);
			return 1;
		}
		 // Initialize Winsock
		iResult = WSAStartup(MAKEWORD(2,2), &wsaData);
		if (iResult != 0) {
			printf("WSAStartup failed with error: %d\n", iResult);
			return 1;
		}

		ZeroMemory( &hints, sizeof(hints) );
		hints.ai_family = AF_UNSPEC;
		hints.ai_socktype = SOCK_STREAM;
		hints.ai_protocol = IPPROTO_TCP;

		 // Resolve the server address and port
		iResult = getaddrinfo(argv[0], DEFAULT_PORT, &hints, &result);
		if ( iResult != 0 ) {
			printf("getaddrinfo failed with error: %d\n", iResult);
			WSACleanup();
			return 1;
		}
		  // Attempt to connect to an address until one succeeds
		for(ptr=result; ptr != NULL ;ptr=ptr->ai_next) {

			// Create a SOCKET for connecting to server
			ConnectSocket = socket(ptr->ai_family, ptr->ai_socktype, 
				ptr->ai_protocol);
			if (ConnectSocket == INVALID_SOCKET) {
				printf("socket failed with error: %ld\n", WSAGetLastError());
				WSACleanup();
				return 1;
			}

			// Connect to server.
			iResult = connect( ConnectSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
			if (iResult == SOCKET_ERROR) {
				closesocket(ConnectSocket);
				ConnectSocket = INVALID_SOCKET;
				continue;
			}
			break;
		}
		freeaddrinfo(result);

		if (ConnectSocket == INVALID_SOCKET) {
			printf("Unable to connect to server!\n");
			WSACleanup();
			return 1;
		}
	}
	/***************TERMINATE SOCKET***************/
	void terminateSocket()
	{
		// cleanup
		closesocket(ConnectSocket);
		WSACleanup();
	}
	/*****************SEND VIA SOCKET*************/
	int __cdecl sendSocket(char *sendbuf)
	{
		//printf("Sending\n");
		iResult = send( ConnectSocket, sendbuf, (int)strlen(sendbuf), 0 );
		if (iResult == SOCKET_ERROR) {
			printf("Send failed with error: %d\n, will send exit/restart message to Java...", WSAGetLastError());
			closesocket(ConnectSocket);
			WSACleanup();
			return 1;
		}

	//	printf("Bytes Sent: %ld\n", iResult);
	}
	/***************CLOSE SOCKET**************/
	int closeSocket()
	{
		// shutdown the connection since no more data will be sent
		iResult = shutdown(ConnectSocket, SD_SEND);
		if (iResult == SOCKET_ERROR) {
			printf("shutdown failed with error: %d\n", WSAGetLastError());
			closesocket(ConnectSocket);
			WSACleanup();
			return 1;
		}
	}
	/*************RECEIVE VIA SOCKET***********/
	char* receiveSocket()
	{
		do{
			//printf("\nreceiving\n");
			iResult = recv(ConnectSocket, recvbuf, recvbuflen, MSG_WAITALL);
			int nError=WSAGetLastError();
			if ( iResult > 0 )
			{	
				break;//printf("Bytes received: %d\n", iResult);
			}
			else if ( iResult == 0 )
			{
				printf("Connection closed\n");
				exit(0);
			}
			else
			{
				printf("recv failed with error: %d\n", WSAGetLastError());
				exit(0);
			}
		}while(1);
		return recvbuf;
	}
};

#endif