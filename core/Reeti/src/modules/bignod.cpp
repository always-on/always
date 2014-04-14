#include"include/bignod.h"

//Declaring our class to URBI
UStart(BigNod);

//constructeur declaring init to URBI
BigNod::BigNod(const string &n):UObject(n)
{
	UBindFunction(BigNod, init) ;
}

//Init function
int BigNod::init()
{
	//Declaring play to URBI
	UBindFunction(BigNod, play) ;

	return 0;
}

// play function binded to URBI
int BigNod::play()
{
	send("Global.servo.neckTilt=0 smooth:0.25s,");
	send("Global.servo.leftEar=0 smooth:0.15s,");
	send("Global.servo.rightEar=0 smooth:0.15s,");
	send("Global.servo.leftEyeLid=55 smooth:0.25s,");
	send("Global.servo.rightEyeLid=55 smooth:0.25s;");

	send("Global.servo.neckTilt=55.56 smooth:0.25s;");
			
	send("Global.servo.leftEar=50 smooth:0.15s,");
	send("Global.servo.rightEar=50 smooth:0.15s,");
	send("Global.servo.leftEyeLid=90 smooth:0.25s,");
	send("Global.servo.rightEyeLid=90 smooth:0.25s;");

	return 1;
}
