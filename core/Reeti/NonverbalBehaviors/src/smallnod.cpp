#include"smallnod.h"

//Declaring our class to URBI
UStart(SmallNod);

//constructeur declaring init to URBI
SmallNod::SmallNod(const string &n):UObject(n)
{
	UBindFunction(SmallNod, init) ;
}

//Init function
int SmallNod::init()
{

	//Declaring play to URBI
	UBindFunction(SmallNod, play) ;

	//Declare something to URBI
	//UBindVar(HeadNod, color) ;

	//Création d’une callback sur la variable something
	//UNotifyChange(color, &HeadNod::ChangeLEDcolor);
	return 0;
}

// play function binded to URBI
int SmallNod ::play()
{
	send("Global.servo.neckTilt=20 smooth:0.25s,");
	send("Global.servo.neckTilt=55.56 smooth:0.25s;");

	return 1;
}

//Function calledas soon as something variable changes
/*int HeadNod::ChangeLEDcolor(UVar& _color)
{
	//Get the UVar in a classical type
	send("Global.servo.color=Global.head.color;");
}*/

