package edu.wpi.always.ttt;

import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class TTTStateContext{

   private final Keyboard keyboard;
   private final TTTUI tttUI;
   private final UIMessageDispatcher dispatcher;
   private final PlaceManager placeManager;
   private final PeopleManager peopleManager;

   public TTTStateContext (Keyboard keyboard, TTTUI tttUI,
         UIMessageDispatcher dispatcher,
         PlaceManager placeManager, PeopleManager peopleManager) {
      this.keyboard = keyboard;
      this.tttUI = tttUI;
      this.dispatcher = dispatcher;
      this.placeManager = placeManager;
      this.peopleManager = peopleManager;
   }

   public Keyboard getKeyboard () {
      return keyboard;
   }

   public TTTUI getTTTUI () {
      return tttUI;
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
