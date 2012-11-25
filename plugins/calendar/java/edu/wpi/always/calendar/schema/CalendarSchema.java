package edu.wpi.always.calendar.schema;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.cm.ui.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.places.*;

public class CalendarSchema extends SchemaImplBase {

	private MenuTurnStateMachine stateMachine;

	public CalendarSchema(BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor, Keyboard keyboard, CalendarUI calendarUI, Calendar calendar, UIMessageDispatcher dispatcher, PlaceManager placeManager, PeopleManager peopleManager) {
		super(behaviorReceiver, behaviorHistory);

		stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor, menuPerceptor, new RepeatMenuTimeoutHandler());
		stateMachine.setSpecificityMetadata(.9);
		stateMachine.setAdjacencyPair(new WhatDo(new CalendarStateContext(keyboard, calendarUI, calendar, dispatcher, placeManager, peopleManager)));

		setNeedsFocusResouce();
	}

	@Override
	public void run() {
		propose(stateMachine);
	}

}
