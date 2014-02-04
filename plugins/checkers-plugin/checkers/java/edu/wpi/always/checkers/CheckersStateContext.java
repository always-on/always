package edu.wpi.always.checkers;

import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.menu.AdjacencyPair;

public class CheckersStateContext extends AdjacencyPair.Context {

   private final Keyboard keyboard;
   private final CheckersUI checkersUI;
   private final UIMessageDispatcher dispatcher;
   private final PlaceManager placeManager;
   private final PeopleManager peopleManager;

   public CheckersStateContext (Keyboard keyboard, CheckersUI srummyUIUI,
         UIMessageDispatcher dispatcher,
         PlaceManager placeManager, PeopleManager peopleManager) {
      this.keyboard = keyboard;
      this.checkersUI = srummyUIUI;
      this.dispatcher = dispatcher;
      this.placeManager = placeManager;
      this.peopleManager = peopleManager;
   }

   public Keyboard getKeyboard () {
      return keyboard;
   }

   public CheckersUI getCheckersUI () {
      return checkersUI;
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
