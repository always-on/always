package edu.wpi.always.user;

import java.lang.annotation.*;

import org.picocontainer.annotations.*;

import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.*;

public interface UserModel {
	@Retention(RetentionPolicy.RUNTIME) 
	@Target({ElementType.FIELD, ElementType.PARAMETER}) 
	@Bind 
	public @interface UserName {}
	
	
	public String getUserName();
	
	public void save();
	
	public void load();
	

	public Calendar getCalendar();

	public PeopleManager getPeopleManager();

	public PlaceManager getPlaceManager();
}
