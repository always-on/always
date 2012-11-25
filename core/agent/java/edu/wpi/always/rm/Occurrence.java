package edu.wpi.always.rm;

import java.util.Date;

public class Occurrence {
	Date date;
	double social;
	double virtue;
	double duration;
	
	Occurrence(Date dateVal, double socialVal, double virtueVal, double durationVal){
		date = dateVal;
		social = socialVal;
		virtue = virtueVal;		
		duration = durationVal;
	}
	
	Occurrence(Date dateVal, Activity act){
		date = dateVal;
		social = act.social;
		virtue = act.virtue;
		duration = act.duration;
	}
}
