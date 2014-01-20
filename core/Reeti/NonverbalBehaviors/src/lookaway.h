#ifndef LOOKAWAY_H
#define LOOKAWAY_H

#include<string.h>
#include<iostream>
#include<stdio.h>

// headers inclusions
#include<urbi/uobject.hh>
#include<urbi/uclient.hh>

using namespace std;

//namespace URBI
using namespace urbi;

//our class inherits from UObject
class LookAway:public UObject{
	public:
		//default constructor, always with an argument : const string&
		LookAway(const string &n);

		// ‘‘Real’’ constructor URBI taking an int and a string
		int init();

		//Declared function to URBI
		int lookAwayThink();
		int lookAtBoard();
		int lookAwayAtRight();
		int lookBack();

	private:
		// client to send orders to URBI
		UClient * client;
};
#endif

