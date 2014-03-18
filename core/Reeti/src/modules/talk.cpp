#include"include/talk.h"
#include<sstream>
#include<iostream>
#include<time.h>
#include<unistd.h>
#include<boost/thread.hpp>

//Declaring our class to URBI
UStart(Talk);

//constructeur declaring init to URBI
Talk::Talk(const string &n):UObject(n)
{
	mouthMovePermission = false;
	mouthOpenDuration   = 0.2;

	UBindFunction(Talk, init) ;
}

//Init function
int Talk::init()
{
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
	int counter = 0;
	long cTime1, cTime2;
	timeval curtime;

	mouthMovePermission = true;
	
	while (mouthMovePermission)
	{
		send( "Global.servo.bottomLip=10 smooth:0.3s;" );

		send( "Global.servo.bottomLip=80 smooth:0.2s;" );

		gettimeofday(&curtime, NULL);
		cTime1 = (curtime.tv_sec*1000) + (curtime.tv_usec/1000);
		cTime2 = cTime1;

		while(cTime2-cTime1 <= 1000) {
			gettimeofday(&curtime, NULL);
			cTime2 = (curtime.tv_sec*1000) + (curtime.tv_usec/1000);
			if(!mouthMovePermission) break;
		}
	}
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
