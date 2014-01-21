#include"sad.h"

//Declaring our class to URBI
UStart(Sad);

//constructeur declaring init to URBI
Sad::Sad(const string &n):UObject(n)
{
	UBindFunction(Sad, init) ;
}

//Init function
int Sad::init()
{

	//Declaring play to URBI
	UBindFunction(Sad, play) ;
	
	//Declare something to URBI
	UBindVar(Sad, color) ;
	
	//Création d’une callback sur la variable something
	UNotifyChange(color, &Sad::ChangeLEDcolor);

	return 0;
}

// play function binded to URBI
int Sad ::play()
{
	send("Global.servo.neckRotat=50 smooth:1s,");
	send("Global.servo.neckTilt=0 smooth:1s,");
	send("Global.servo.neckPan=50 smooth:1s,");
	send("Global.servo.leftLC=0 smooth:1s,");
	send("Global.servo.rightLC=0 smooth:1s,");
	send("Global.servo.leftEyeTilt=30 smooth:1s,");
	send("Global.servo.rightEyeTilt=30 smooth:1s,");
	send("Global.servo.leftEyeLid=90 smooth:0.5s,");
	send("Global.servo.rightEyeLid=90 smooth:0.5s,");
	send("Global.servo.topLip=0 smooth:1s,");
	send("Global.servo.bottomLip=1000 smooth:1s,");
	send("Global.servo.leftEar=0 smooth:1s,");
	send("Global.servo.rightEar=0 smooth:1s;");

	return 1;
}

//Function calledas soon as something variable changes
int Sad::ChangeLEDcolor(UVar& _color)
{
	//Get the UVar in a classical type
	send("Global.servo.color=Global.GreetTime.color;");
}

