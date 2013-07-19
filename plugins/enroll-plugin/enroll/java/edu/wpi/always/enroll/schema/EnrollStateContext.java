package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.enroll.EnrollUI;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class EnrollStateContext {

   private final Keyboard keyboard;
   private final EnrollUI enrollUI;
   private final UIMessageDispatcher dispatcher;
   private final UserModel model;
   private final PlaceManager placeManager;
   private final PeopleManager peopleManager;


   public EnrollStateContext(Keyboard keyboard, EnrollUI enrollUI,
         UIMessageDispatcher dispatcher, UserModel model, PlaceManager placeManager,
         PeopleManager peopleManager) {
      this.keyboard = keyboard;
      this.enrollUI = enrollUI;
      this.dispatcher = dispatcher;
      this.model = model;
      this.placeManager = placeManager;
      this.peopleManager = peopleManager;
   }


   public Keyboard getKeyboard() {
      return keyboard;
   }

   public EnrollUI getEnrollUI() {
      return enrollUI;
   }

   public void hideKeyboard() {
      keyboard.hideKeyboard();
   }

   public void hideEnrollUI() {
      enrollUI.hideEnrollUI();
   }

   public UIMessageDispatcher getDispatcher() {
      return dispatcher;
   }

   public UserModel getUserModel() {
      return model;
   }


   public PlaceManager getPlaceManager() {
      return placeManager;
   }


   public PeopleManager getPeopleManager() {
      return peopleManager;
   }


}
