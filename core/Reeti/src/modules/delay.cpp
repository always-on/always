#include"include/delay.h"

//Declaring our class to URBI
UStart(Delay);

//constructeur declaring init to URBI
Delay::Delay(const string &n):UObject(n)
{
	UBindFunction(Delay, init) ;
}

//Init function
int Delay::init()
{
	//Declaring play to URBI
	UBindFunction(Delay, play) ;

	return 0;
}

// play function binded to URBI
int Delay ::play(int intDuration)
{
	sleep(intDuration);

	return 1;
}

