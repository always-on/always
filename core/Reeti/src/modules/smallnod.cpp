#include"include/smallnod.h"

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
	
	return 0;
}

// play function binded to URBI
int SmallNod ::play()
{
	send("Global.servo.neckTilt=20 smooth:0.5s;");
	send("Global.servo.neckTilt=55.56 smooth:0.5s;");

	return 1;
}

