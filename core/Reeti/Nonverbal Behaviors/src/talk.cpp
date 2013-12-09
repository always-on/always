#include"talk.h"
#include<sstream>
#include<iostream>

//Declaring our class to URBI
UStart(Talk);

//constructeur declaring init to URBI
Talk::Talk(const string &n):UObject(n)
{
	UBindFunction(Talk, init) ;
}

//Init function
int Talk::init()
{
	//Declaring play to URBI
	UBindFunction(Talk, play);

	return 0;
}

// play function binded to URBI
int Talk::play(int iterations, double time)
{
	stringstream strBottomoLipOpen, strBottomoLipClose;

	strBottomoLipOpen << "Global.servo.bottomLip=10 smooth:" << time << "s;";
	strBottomoLipClose << "Global.servo.bottomLip=80 smooth:" << time << "s;";

	int cnt = 0;

	while(cnt < iterations)
	{
		send( strBottomoLipOpen.str() );
		send( strBottomoLipClose.str() );
		cnt++;
	}

	strBottomoLipOpen.clear();
	strBottomoLipClose.clear();

	return 1;
}

