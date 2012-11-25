package edu.wpi.always.calendar.schema;

import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.cm.ui.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.*;

public class CalendarStateContext {
	
	private final Keyboard keyboard;
	private final CalendarUI calendarUI;
	private final Calendar calendar;
	private final UIMessageDispatcher dispatcher;
	private final PlaceManager placeManager;
	private final PeopleManager peopleManager;

	public CalendarStateContext(Keyboard keyboard, CalendarUI calendarUI, Calendar calendar, UIMessageDispatcher dispatcher, PlaceManager placeManager, PeopleManager peopleManager) {
		this.keyboard = keyboard;
		this.calendarUI = calendarUI;
		this.calendar = calendar;
		this.dispatcher = dispatcher;
		this.placeManager = placeManager;
		this.peopleManager = peopleManager;
	}
	
	public Keyboard getKeyboard() {
		return keyboard;
	}
	
	public CalendarUI getCalendarUI(){
		return calendarUI;
	}
	
	public Calendar getCalendar(){
		return calendar;
	}

	public UIMessageDispatcher getDispatcher() {
		return dispatcher;
	}

	public PlaceManager getPlaceManager() {
		return placeManager;
	}

	public PeopleManager getPeopleManager() {
		return peopleManager;
	}
}
