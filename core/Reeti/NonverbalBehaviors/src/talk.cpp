#include"talk.h"
#include<sstream>
#include<iostream>

bool mouthStateFlag = false;

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

	//Declaring stop to URBI
	//UBindFunction(Talk, stop);

	return 0;
}

// play function binded to URBI
int Talk::play(double time)
{
	stringstream strBottomoLipOpen, strBottomoLipClose;

	strBottomoLipOpen << "Global.servo.bottomLip=10 smooth:" << time << "s;";
	strBottomoLipClose << "Global.servo.bottomLip=80 smooth:" << time << "s;";

	if(mouthStateFlag == false)
	{
		//send( strBottomoLipOpen.str() );
		send( "Global.servo.color=\"red\";" );
		mouthStateFlag = true;
	}
	else
	{
		//send( strBottomoLipClose.str() );
		send( "Global.servo.color=\"green\";" );
		mouthStateFlag = false;
	}

	strBottomoLipOpen.clear();
	strBottomoLipClose.clear();

	return 1;
}

/*int Talk::stop()
{
	send( "Global.servo.color=\"green\";" );

	return 1;
}*/
