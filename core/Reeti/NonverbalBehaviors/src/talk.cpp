#include"talk.h"
#include<sstream>
#include<iostream>
#include<unistd.h>
#include<boost/thread.hpp>

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
	mouthMovePermission = false;
	mouthOpenDuration   = 0.2;

	//Declaring play to URBI
	UBindFunction(Talk, play);

	UBindFunction(Talk, changeMouthPermission);

	UBindFunction(Talk, setMouthOpenDuration);

	return 0;
}

void Talk::setMouthOpenDuration(double duration)
{
	mouthOpenDuration = duration;
}

void Talk::changeMouthPermissionHelper(bool permission)
{
	mouthMovePermission = permission;
}

void Talk::playHelper()
{
	stringstream strBottomoLipOpen, strBottomoLipClose;

	strBottomoLipOpen << "Global.servo.bottomLip=10 smooth:" << mouthOpenDuration << "s;";
	strBottomoLipClose << "Global.servo.bottomLip=80 smooth: 0.1s;";

	while (mouthMovePermission)
	{
		send( strBottomoLipOpen.str() );

		send( strBottomoLipClose.str() );
		
		usleep( ( 0.1 + mouthOpenDuration ) * 1000000 );
	}

	strBottomoLipOpen.clear();
	strBottomoLipClose.clear();
}

//Play function binded to URBI
void Talk::changeMouthPermission(bool permission)
{
	boost::thread mouthPermissionThread(&Talk::changeMouthPermissionHelper, this, permission);
}

//Play function binded to URBI
void Talk::play()
{
	boost::thread playThread(&Talk::playHelper, this);
}
