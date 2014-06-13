package edu.wpi.always.calendar.schema;

import java.util.Date;
import edu.wpi.always.Logger;
import edu.wpi.always.calendar.CalendarUI;
import edu.wpi.always.calendar.CalendarClient.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.schemas.ActivityStateMachineKeyboardSchema;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class CalendarSchema extends ActivityStateMachineKeyboardSchema<CalendarStateContext> {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.CALENDAR;
   
   public enum View { DAY, WEEK, MONTH }

   public static void logView (View view, Date date) {
      Logger.logActivity(LOGGER_NAME, view, date);
   }
   
   public CalendarSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Keyboard keyboard, CalendarUI calendarUI,
         Calendar calendar, UIMessageDispatcher dispatcher,
         UserModel model, PlaceManager placeManager, PeopleManager peopleManager) {
      super(new WhatDo(new CalendarStateContext(
                keyboard, calendarUI, calendar, dispatcher, model, placeManager, peopleManager)),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, keyboard,
            LOGGER_NAME);
   }

}
