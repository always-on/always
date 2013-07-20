package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.user.UserUtils;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.places.*;
import edu.wpi.always.user.places.ZipCodes.StateEntry;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.disco.rt.menu.*;
import org.joda.time.MonthDay;
import java.util.List;

public class UserModelAdjacencyPair extends KeyboardAdjacencyPair<EnrollStateContext>{

   private static String UserName, UserAge, PhoneNumber, SkypeNumber, Spouse;
   private static int Month;
   private static int Day;
   private static MonthDay userBirthday;
   private static Gender gender;
   private static Place zipcode;
   private static String UserState;

   public UserModelAdjacencyPair(final EnrollStateContext context) {
      super("What is the name you want me to use to talk with you", 
            "Enter your name:", context, context.getKeyboard());
   }

   @Override
   public AdjacencyPair success(String text) {
      UserName = text;
      getContext().getUserModel().setUserName(text);
      return new UserAgeAdjacencyPair(getContext());
   }

   @Override
   public AdjacencyPair cancel() {
      UserName = "this person";
      return new UserAgeAdjacencyPair(getContext());
   }

   public static class UserAgeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserAgeAdjacencyPair(final EnrollStateContext context) {
         super("How old are you", "Enter your age:", 
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            UserAge = text;
            getContext().getPeopleManager().getUser().setAge(UserAge);
            return new TellUserBirthdayAdjacencyPair(getContext());
         }
         else{
            getContext().hideKeyboard();
            return new UserAgeInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellUserBirthdayAdjacencyPair(getContext());
      }

      //		@Override
      //		public void overflow() {
      //
      //		}	
   }

   public static class UserAgeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserAgeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The age you enter is invalid please enter again", 
               "Enter valid age:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isInteger(text)){
            getContext().hideKeyboard();
            UserAge = text;
            getContext().getPeopleManager().getUser().setAge(UserAge);
            return new TellUserBirthdayAdjacencyPair(getContext());
         }
         else{
            getContext().hideKeyboard();
            return new UserAgeInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new TellUserBirthdayAdjacencyPair(getContext());
      }	
   }

   public static class TellUserBirthdayAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public TellUserBirthdayAdjacencyPair(final EnrollStateContext context) {
         super("Do you want to tell me your birthday", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserBirthdayMonthAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
         choice("Please repeat this question.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new TellUserBirthdayAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserBirthdayMonthAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserBirthdayMonthAdjacencyPair(final EnrollStateContext context) {
         super("What is your birthday month", context, true);
         for(int i = 0; i < 12; i++) {
            final int MonthNum = i;
            choice(Person.Month[i], new DialogStateTransition() {
               @Override
               public AdjacencyPair run() {
                  Month = MonthNum + 1;
                  return new UserBirthdayDayAdjacencyPair(getContext());
               }
            });
         }
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserBirthdayDayAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserBirthdayDayAdjacencyPair(final EnrollStateContext context) {
         super("What is the day of your Birthday", 
               "Enter your Birthday:", 
               context, context.getKeyboard(), true);}

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            userBirthday = new MonthDay(Month, Day);
            getContext().getUserModel().getPeopleManager().
            getPerson(UserName).setBirthday(userBirthday);
            return new UserGenderAdjacencyPair(getContext());
         }
         else{
            return new UserBirthdayDayInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserGenderAdjacencyPair(getContext());
      }	
   }

   public static class UserBirthdayDayInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The day you enter is invalid please enter again", 
               "Enter valid birthday:", context, context.getKeyboard(), true);}

      @Override
      public AdjacencyPair success(String text) {
         int day = Integer.parseInt(text);
         if(UserUtils.isValidDayOfMonth(Month, day)){
            getContext().hideKeyboard();
            Day = day;
            userBirthday = new MonthDay(Month, Day);
            getContext().getUserModel().getPeopleManager().
            getPerson(UserName).setBirthday(userBirthday);
            return new UserGenderAdjacencyPair(getContext());
         }
         else{
            return new UserBirthdayDayInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserGenderAdjacencyPair(getContext());
      }	
   }

   public static class UserGenderAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserGenderAdjacencyPair(final EnrollStateContext context) {
         super("What is your gender?", context);
         choice("Male", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               gender = Gender.Male;
               getContext().getPeopleManager().getUser().setGender(gender);
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Female", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               gender = Gender.Female;
               getContext().getPeopleManager().getUser().setGender(gender);
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Never Mind", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("Please repeat this question.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new UserGenderAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class KnowUserZipCodeAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public KnowUserZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("Do you know your ZipCode?", context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserZipCodeAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run() {
               return new UserStateAdjacencyPair(getContext());
            }
         });
         choice("Please repeat this question.", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new KnowUserZipCodeAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class UserZipCodeAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserZipCodeAdjacencyPair(final EnrollStateContext context) {
         super("What is the your zipcode?", "Enter your zipcode:",
               context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            zipcode = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().getPeopleManager().getUser().setLocation(zipcode);
            getContext().hideKeyboard();
            return new UserSpouseAdjacencyPair(getContext());
         }
         else{
            return new UserZipCodeInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserZipCodeInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserZipCodeInvalidAdjacencyPair(final EnrollStateContext context) {
         super("The zipcode entered is invalid. Please enter an valid zipcode.", 
               "Enter your zipcode again:", context, context.getKeyboard(), true);
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         ZipCodeEntry zip = zipcodes.getPlaceData(text);
         if(zip != null){
            zipcode = getContext().getPlaceManager().getPlace(zip.getZip());
            getContext().getPeopleManager().getUser().setLocation(zipcode);
            getContext().hideKeyboard();
            return new UserSpouseAdjacencyPair(getContext());
         }
         else{
            return new UserZipCodeInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserStateAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserStateAdjacencyPair(final EnrollStateContext context) {
         super("Which state do you live", "Enter your state:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            UserState = state.getStateAbbrev().get(0);
            return new UserCityAdjacencyPair(getContext());
         }
         else{
            return new UserStateInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class UserStateInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserStateInvalidAdjacencyPair(final EnrollStateContext context) {
         super("Sorry, but you must enter a valid state name here", 
               "Please enter valid state name:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(text);
         if(state != null){
            UserState = state.getStateAbbrev().get(0);
            return new UserCityAdjacencyPair(getContext());
         }
         else{
            return new UserStateInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserStateInvalidAdjacencyPair(getContext());
      }
   }

   public static class UserCityAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserCityAdjacencyPair(final EnrollStateContext context) {
         super("Which city do you live in", 
               "Enter your city:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(UserState)){
               getContext().hideKeyboard();
               zipcode = getContext().getPlaceManager().getPlace(city.getZip());
               getContext().getPeopleManager().getUser().setLocation(zipcode);
               return new UserSpouseAdjacencyPair(getContext());
            }
         }
         return new UserCityInvalidAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(UserState);
         zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(zipcode);
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserCityInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserCityInvalidAdjacencyPair(final EnrollStateContext context) {
         super("City name is not valid please enter it again", 
               "Invalid city name. Plase re-enter your city:",
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         List<ZipCodeEntry> cities = zipcodes.getCityData(text);
         for(ZipCodeEntry city : cities){
            if(city.getState().equals(UserState)){
               getContext().hideKeyboard();
               zipcode = getContext().getPlaceManager().getPlace(city.getZip());
               getContext().getPeopleManager().getUser().setLocation(zipcode);
               return new UserSpouseAdjacencyPair(getContext());
            }
         }
         getContext().hideKeyboard();
         StateEntry state = zipcodes.getState(UserState);
         zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(zipcode);
         return new UserSpouseAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
         StateEntry state = zipcodes.getState(UserState);
         zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
         getContext().getPeopleManager().getUser().setLocation(zipcode);
         return new UserSpouseAdjacencyPair(getContext());
      }
   }

   public static class UserSpouseAdjacencyPair extends
   AdjacencyPairBase<EnrollStateContext> {

      public UserSpouseAdjacencyPair(final EnrollStateContext context){
         super("Do you married?", context);
         choice("Yes", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new EnterUserSpouseAdjacencyPair(getContext());
            }
         });
         choice("No", new DialogStateTransition() {
            @Override
            public AdjacencyPair run() {
               return new UserPhoneNumberAdjacencyPair(getContext());
            }
         });
         choice("Please repeat this question.", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new UserSpouseAdjacencyPair(getContext());
            }
         });
         choice("Never Mind", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new UserPhoneNumberAdjacencyPair(getContext());
            }
         });
      }
   }

   public static class EnterUserSpouseAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public EnterUserSpouseAdjacencyPair(final EnrollStateContext context){
         super("What is your spouse's name?", "Enter your spouse name:", 
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         Spouse = text;
         getContext().getPeopleManager().getUser().setSpouse(Spouse);
         return new UserPhoneNumberAdjacencyPair(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserPhoneNumberAdjacencyPair(getContext());
      }
   }


   public static class UserPhoneNumberAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserPhoneNumberAdjacencyPair(final EnrollStateContext context) {
         super("What is your phone number?", "Enter your phone number: (XXX-XXX-XXXX)", 
               context, context.getKeyboard(),true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isPhoneNumberValid(text)){
            PhoneNumber = text;
            getContext().getPeopleManager().getUser().setPhoneNumber(PhoneNumber);
            return new UserSkypeNumberAdjacencyPair(getContext());
         }
         else{
            return new UserPhoneNumberInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserSkypeNumberAdjacencyPair(getContext());
      }
   }

   public static class UserPhoneNumberInvalidAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserPhoneNumberInvalidAdjacencyPair(final EnrollStateContext context) {
         super("That's not a valid phone number, please enter again", 
               "Enter valid phone number: (XXX-XXX-XXXX)", 
               context, context.getKeyboard(),true);
      }

      @Override
      public AdjacencyPair success(String text) {
         if(UserUtils.isPhoneNumberValid(text)){
            PhoneNumber = text;
            getContext().getPeopleManager().getUser().setPhoneNumber(PhoneNumber);
            return new UserSkypeNumberAdjacencyPair(getContext());
         }
         else{
            return new UserPhoneNumberInvalidAdjacencyPair(getContext());
         }
      }

      @Override
      public AdjacencyPair cancel() {
         return new UserSkypeNumberAdjacencyPair(getContext());
      }
   }

   public static class UserSkypeNumberAdjacencyPair extends
   KeyboardAdjacencyPair<EnrollStateContext> {

      public UserSkypeNumberAdjacencyPair(final EnrollStateContext context) {
         super("What is your skype Account?", "Enter your skype name:", 
               context, context.getKeyboard());
      }

      @Override
      public AdjacencyPair success(String text) {
         getContext().hideKeyboard();
         SkypeNumber = text;
         getContext().getPeopleManager().getUser().setSkypeNumber(SkypeNumber);
         return new InitialEnroll(getContext());
      }

      @Override
      public AdjacencyPair cancel() {
         getContext().hideKeyboard();
         return new InitialEnroll(getContext());
      }
   }
}

