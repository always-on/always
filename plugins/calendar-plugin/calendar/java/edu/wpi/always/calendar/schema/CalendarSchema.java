package edu.wpi.always.calendar.schema;

import edu.wpi.always.calendar.CalendarUI;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class CalendarSchema extends ActivityStateMachineSchema {

   public CalendarSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, CalendarUI calendarUI,
         Calendar calendar, UIMessageDispatcher dispatcher,
         PlaceManager placeManager, PeopleManager peopleManager) {
      super(new WhatDo(new CalendarStateContext(
                keyboard, calendarUI, calendar, dispatcher, placeManager, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
   }

}
