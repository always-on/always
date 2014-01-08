#ifndef TALK_H
#define TALK_H

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
class Talk:public UObject{
	public:
	//default constructor, always with an argument : const string&
	Talk(const string &n);

	// ‘‘Real’’ constructor URBI taking an int and a string
	int init();

	//A declared function to URBI
	int play(int n, double t);

	private:
	// client to send orders to URBI
	UClient * client;
};
#endif

