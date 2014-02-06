#include"include/happy.h"

//Declaring our class to URBI
UStart(Happy);

//constructeur declaring init to URBI
Happy::Happy(const string &n):UObject(n)
{
	UBindFunction(Happy, init) ;
}

//Init function
int Happy::init()
{

	//Declaring play to URBI
	UBindFunction(Happy, play) ;
	//Declare something to URBI
	UBindVar(Happy, color) ;
	//Création d’une callback sur la variable something
	UNotifyChange(color, &Happy::ChangeLEDcolor);

	return 0;
}

// play function binded to URBI
int Happy ::play()
{
	send("Global.servo.neckRotat=50 smooth:1s,");
	send("Global.servo.neckTilt=50 smooth:1s,");
	send("Global.servo.neckPan=50 smooth:1s,");
	send("Global.servo.leftLC=100 smooth:1s,");
	send("Global.servo.rightLC=100 smooth:1s,");
	send("Global.servo.leftEyeLid=90 smooth:0.5s,");
	send("Global.servo.rightEyeLid=90 smooth:0.5s;");
	send("Global.servo.topLip=80 smooth:1s,");
	send("Global.servo.bottomLip=50 smooth:1s,");
	send("Global.servo.leftEar=100 smooth:1s,");
	send("Global.servo.rightEar=100 smooth:1s,");

	return 1;
}

//Function calledas soon as something variable changes
int Happy::ChangeLEDcolor(UVar& _color)
{
	//Get the UVar in a classical type
	send("Global.servo.color=Global.GreetTime.color;");
}

