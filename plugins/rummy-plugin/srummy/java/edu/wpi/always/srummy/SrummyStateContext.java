package edu.wpi.always.srummy;

import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class SrummyStateContext{

   private final Keyboard keyboard;
   private final SrummyUI srummyUIUI;
   private final UIMessageDispatcher dispatcher;
   private final PlaceManager placeManager;
   private final PeopleManager peopleManager;

   public SrummyStateContext (Keyboard keyboard, SrummyUI srummyUIUI,
         UIMessageDispatcher dispatcher,
         PlaceManager placeManager, PeopleManager peopleManager) {
      this.keyboard = keyboard;
      this.srummyUIUI = srummyUIUI;
      this.dispatcher = dispatcher;
      this.placeManager = placeManager;
      this.peopleManager = peopleManager;
   }

   public Keyboard getKeyboard () {
      return keyboard;
   }

   public SrummyUI getSrummyUI () {
      return srummyUIUI;
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
