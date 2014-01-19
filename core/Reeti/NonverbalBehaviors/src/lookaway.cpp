#include"lookaway.h"

//Declaring our class to URBI
UStart(LookAway);

//constructeur declaring init to URBI
LookAway::LookAway(const string &n):UObject(n)
{
	UBindFunction(LookAway, init) ;
}

//Init function
int LookAway::init()
{
	//Declaring lookAwayThink, lookAtBoard and lookback to URBI
	UBindFunction(LookAway, lookAwayThink);
	UBindFunction(LookAway, lookAtBoard);
	UBindFunction(LookAway, lookAwayAtRight);
	UBindFunction(LookAway, lookBack);

	return 0;
}

// lookAwayThink function binded to URBI
int LookAway::lookAwayThink()
{
	send("Global.servo.rightEyePan=80 smooth:0.5s,");
	send("Global.servo.rightEyeTilt=70 smooth:0.5s,");
	send("Global.servo.leftEyePan=60 smooth:0.5s,");
	send("Global.servo.leftEyeTilt=70 smooth:0.5s,");
	send("Global.servo.neckRotat=57.5 smooth:1s,");
	send("Global.servo.neckTilt=67.5 smooth:1s;");

	return 1;
}

// lookAtBoard function binded to URBI
int LookAway::lookAtBoard()
{
	send("Global.servo.neckRotat=0 smooth:1s,");
	send("Global.servo.neckTilt=12.5 smooth:1s,");
	send("Global.servo.rightEyePan=60 smooth:0.5s,");
	send("Global.servo.rightEyeTilt=42 smooth:0.5s,");
	send("Global.servo.leftEyePan=40 smooth:0.5s,");
	send("Global.servo.leftEyeTilt=42 smooth:0.5s;");

	return 1;
}

// lookAwayAtRight function binded to URBI
int LookAway::lookAwayAtRight()
{
	send("Global.servo.neckRotat=0 smooth:1s,");
	send("Global.servo.neckTilt=80 smooth:1s,");
	send("Global.servo.rightEyePan=60 smooth:0.5s,");
	send("Global.servo.rightEyeTilt=42 smooth:0.5s,");
	send("Global.servo.leftEyePan=40 smooth:0.5s,");
	send("Global.servo.leftEyeTilt=42 smooth:0.5s;");

	return 1;
}

// lookBack function binded to URBI
int LookAway::lookBack()
{
	send("Global.servo.neckRotat=50 smooth:1s,");
	send("Global.servo.neckTilt=55.56 smooth:1s,");
	send("Global.servo.rightEyePan=60 smooth:0.5s,");
	send("Global.servo.rightEyeTilt=42 smooth:0.5s,");
	send("Global.servo.leftEyePan=40 smooth:0.5s,");
	send("Global.servo.leftEyeTilt=42 smooth:0.5s;");

	return 1;
}
