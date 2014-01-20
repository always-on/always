#ifndef SMALLNOD_H
#define SMALLNOD_H

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
class SmallNod:public UObject{

	public:
		//default constructor, always with an argument : const string&
		SmallNod(const string &n);

		// ‘‘Real’’ constructor URBI taking an int and a string
		int init();

		//A declared function to URBI
		int play();

	private:
		// client to send orders to URBI
		UClient * client;

		// UVar declared to URBI
		UVar color;

		// Callback function on « something » changes
		int ChangeLEDcolor(UVar& var);
};
#endif

