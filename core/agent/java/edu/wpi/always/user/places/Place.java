package edu.wpi.always.user.places;

import org.joda.time.*;

public interface Place {
	public String getZip();

	public String getCityName();
	
	public DateTimeZone getTimeZone();
}
