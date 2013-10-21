package edu.wpi.always.checkers;

import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class CheckersStateContext{

   private final Keyboard keyboard;
   private final CheckersUI CheckersUI;
   private final UIMessageDispatcher dispatcher;
   private final PlaceManager placeManager;
   private final PeopleManager peopleManager;

   public CheckersStateContext (Keyboard keyboard, CheckersUI CheckersUI,
         UIMessageDispatcher dispatcher,
         PlaceManager placeManager, PeopleManager peopleManager) {
      this.keyboard = keyboard;
      this.CheckersUI = CheckersUI;
      this.dispatcher = dispatcher;
      this.placeManager = placeManager;
      this.peopleManager = peopleManager;
   }

   public Keyboard getKeyboard () {
      return keyboard;
   }

   public CheckersUI geCheckersTUI () {
      return CheckersUI;
   }

   public UIMessageDispatcher getDispatcher () {
      return dispatcher;
   }

   public PlaceManager getPlaceManager () {
      return placeManager;
   }

   public PeopleManager getPeopleManager () {
      return peopleManager;
   }

}
