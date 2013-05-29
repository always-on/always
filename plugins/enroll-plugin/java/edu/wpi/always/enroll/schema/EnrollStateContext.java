package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class EnrollStateContext {
	
	private final Keyboard keyboard;
	private final UIMessageDispatcher dispatcher;
	private final PlaceManager placeManager;
	private final PeopleManager peopleManager;
	
	
	public EnrollStateContext(Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager) {
		super();
		this.keyboard = keyboard;
		this.dispatcher = dispatcher;
		this.placeManager = placeManager;
		this.peopleManager = peopleManager;
	}


	public Keyboard getKeyboard() {
		return keyboard;
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
