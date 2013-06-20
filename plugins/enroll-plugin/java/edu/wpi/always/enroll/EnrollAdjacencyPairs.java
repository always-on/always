package edu.wpi.always.enroll;

import java.util.List;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.enroll.schema.EnrollStateContext;
import edu.wpi.always.enroll.schema.InitialEnroll;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.user.places.ZipCodes;
import edu.wpi.always.user.places.ZipCodes.StateEntry;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.people.ValidPhoneOrAge;
import edu.wpi.always.user.people.Person.*;
import edu.wpi.always.enroll.ErrorCheckState.*;

public abstract class EnrollAdjacencyPairs{

	protected static String UserName, PersonName, PersonAge, PhoneNumber, SkypeNumber, Spouse;
	protected static Relationship relationship;
	protected static Gender gender;
	protected static Place zipcode;
	protected static Person person;
	protected static String personState;
	
	public static class UserNameAdjacencyPair extends 
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		public UserNameAdjacencyPair(final EnrollStateContext context) {
			super("What is the name you want me to use to talk with you", 
					"Enter your first name:", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			UserName = text;
			return new TellUserBirthdayAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new InitialEnroll(getContext());
		}
	}
	
	public static class TellUserBirthdayAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public TellUserBirthdayAdjacencyPair(final EnrollStateContext context) {
			super("Do you want to tell me your birthday", context);
			choice("Yes", new DialogStateTransition() {

				@Override
				public AdjacencyPair run() {
					return new UserBirthdayAdjacencyPair(getContext());
				}
			});
			choice("No", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new PersonNameAdjacencyPair(getContext());
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					return new PersonNameAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new TellUserBirthdayAdjacencyPair(getContext());
				}
			});
	}

	public static class PersonNameAdjacencyPair extends 
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		public PersonNameAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's name", "Enter name:", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PersonName = text;
			return new PersonAgeAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new InitialEnroll(getContext());
		}
	}

	public static class PersonAgeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonAgeAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's age", "Enter "+ PersonName+ "'s age:", 
					context, context.getKeyboard(), true);
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isInteger(text)){
				getContext().hideKeyboard();
				PersonAge = text;
				return new PersonGenderAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new PersonAgeInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new PersonGenderAdjacencyPair(getContext());
		}	
	}
	
	public static class PersonAgeInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonAgeInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The age you enter is invalid please enter again", 
					"Enter valid "+ PersonName+ "'s age:", context, context.getKeyboard(), true);
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isInteger(text)){
				getContext().hideKeyboard();
				PersonAge = text;
				return new PersonGenderAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new PersonAgeInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new PersonGenderAdjacencyPair(getContext());
		}	
	}

	public static class PersonGenderAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public PersonGenderAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's gender?", context);
			choice("Male", new DialogStateTransition() {

				@Override
				public AdjacencyPair run() {
					gender = Gender.Male;
					return new KnowZipCodeAdjacencyPair(getContext());
				}
			});
			choice("Female", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					gender = Gender.Female;
					return new KnowZipCodeAdjacencyPair(getContext());
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					return new KnowZipCodeAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new PersonGenderAdjacencyPair(getContext());
				}
			});
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class KnowZipCodeAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public KnowZipCodeAdjacencyPair(final EnrollStateContext context) {
			super("Do you know " + PersonName + "'s ZipCode?", context);
			choice("Yes", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					return new PersonZipCodeAdjacencyPair(getContext());
				}
			});
			choice("No", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					return new PersonStateAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new KnowZipCodeAdjacencyPair(getContext());
				}
			});
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class PersonZipCodeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonZipCodeAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's zipcode?", "Enter " +PersonName+ "'s zipcode:",
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			ZipCodeEntry zip = zipcodes.getPlaceData(text);
			if(zip != null){
				zipcode = getContext().getPlaceManager().getPlace(zip.getZip());
				getContext().hideKeyboard();
				return new PersonRelationshipAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new ZipCodeInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new PersonRelationshipAdjacencyPair(getContext());
		}
	}

	public static class ZipCodeInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ZipCodeInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The zipcode entered is invalid. Please enter an valid zipcode.", 
					"Enter" + PersonName +"'s zipcode again:", context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			ZipCodeEntry zip = zipcodes.getPlaceData(text);
			if(zip != null){
				zipcode = getContext().getPlaceManager().getPlace(zip.getZip());
				getContext().hideKeyboard();
				return new PersonRelationshipAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new ZipCodeInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new PersonRelationshipAdjacencyPair(getContext());
		}
	}
	
	public static class PersonStateAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonStateAdjacencyPair(final EnrollStateContext context) {
			super("Which state does the person live", "Enter " +PersonName+ "'s state:",
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			StateEntry state = zipcodes.getState(text);
			if(state != null){
				getContext().hideKeyboard();
				personState = state.getStateAbbrev().get(0);
				return new PersonCityAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new StateInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new StateInvalidAdjacencyPair(getContext());
		}
	}
	
	public static class StateInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public StateInvalidAdjacencyPair(final EnrollStateContext context) {
			super("Sorry, but you must enter a valid state name here", 
					"Please Enter valid state name:",
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			StateEntry state = zipcodes.getState(text);
			if(state != null){
				getContext().hideKeyboard();
				personState = state.getStateAbbrev().get(0);
				return new PersonCityAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new StateInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new StateInvalidAdjacencyPair(getContext());
		}
	}
	
	public static class PersonCityAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonCityAdjacencyPair(final EnrollStateContext context) {
			super("Which city does the person live in", 
					"Enter " + PersonName + "'s city:",
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			List<ZipCodeEntry> cities = zipcodes.getCityData(text);
			for(ZipCodeEntry city : cities){
				if(city.getState().equals(personState)){
					getContext().hideKeyboard();
					zipcode = getContext().getPlaceManager().getPlace(city.getZip());
					return new PersonRelationshipAdjacencyPair(getContext());
				}
			}
			getContext().hideKeyboard();
			return new CityInvalidAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			StateEntry state = zipcodes.getState(personState);
			zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
			return new PersonRelationshipAdjacencyPair(getContext());
		}
	}
	
	public static class CityInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public CityInvalidAdjacencyPair(final EnrollStateContext context) {
			super("City name is not valid please enter it again", 
					"Invalid city name. Plase re-enter " + PersonName + "'s city:",
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			List<ZipCodeEntry> cities = zipcodes.getCityData(text);
			for(ZipCodeEntry city : cities){
				if(city.getState().equals(personState)){
					getContext().hideKeyboard();
					zipcode = getContext().getPlaceManager().getPlace(city.getZip());
					return new PersonRelationshipAdjacencyPair(getContext());
				}
			}
			getContext().hideKeyboard();
			StateEntry state = zipcodes.getState(personState);
			zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
			return new CityInvalidAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			StateEntry state = zipcodes.getState(personState);
			zipcode = getContext().getPlaceManager().getPlace(state.getCapitalZip());
			return new PersonRelationshipAdjacencyPair(getContext());
		}
	}
	
	
	public static class PersonRelationshipAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public PersonRelationshipAdjacencyPair(final EnrollStateContext context) {
			super("What is your relationship with this person?", context, true);
			choice("Friend", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Friend;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Sister", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Sister;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Brother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Brother;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Mother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Mother;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Father", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Father;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Daughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Daughter;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Son", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Son;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Granddaughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Granddaughter;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Grandson", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Grandson;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Aunt", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Aunt;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Uncle", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Uncle;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Cousin", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Cousin;
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class PersonSpouseAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public PersonSpouseAdjacencyPair(final EnrollStateContext context){
			super("Is the person married?", context);
			choice("Yes", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EnterSpouseAdjacencyPair(getContext());
				}
			});
			choice("No", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new PersonContactAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run () {
					return new PersonSpouseAdjacencyPair(getContext());
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run () {
					return new PersonContactAdjacencyPair(getContext());
				}
			});
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class EnterSpouseAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EnterSpouseAdjacencyPair(final EnrollStateContext context){
			super("What is his or her spouse's name?", "Enter " +PersonName +"'s spouse name:", 
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			Spouse = text;
			return new PersonContactAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new PersonContactAdjacencyPair(getContext());
		}
	}

	public static class PersonContactAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public PersonContactAdjacencyPair(final EnrollStateContext context) {
			super("Do you want to plan to talk to this person by video calls?", context);
			choice("Yes", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new PhoneNumberAdjacencyPair(getContext());
				}
			});
			choice("No", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person = getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
							PersonAge, PhoneNumber, SkypeNumber, zipcode, Spouse);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run () {
					return new PersonContactAdjacencyPair(getContext());
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run () {
					person = getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
							PersonAge, PhoneNumber, SkypeNumber, zipcode, Spouse);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class PhoneNumberAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PhoneNumberAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her phone number?", "Enter " + PersonName +"'s phone number: (XXX-XXXXXXX)", 
					context, context.getKeyboard(),true);
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isPhoneNumberValid(text)){
				getContext().hideKeyboard();
				PhoneNumber = text;
				return new SkypeNumberAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new PhoneNumberInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new SkypeNumberAdjacencyPair(getContext());
		}
	}
	
	public static class PhoneNumberInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PhoneNumberInvalidAdjacencyPair(final EnrollStateContext context) {
			super("That's not a valid phone number, please enter again", 
					"Enter valid " + PersonName +"'s phone number: (XXX-XXXXXXX)", 
					context, context.getKeyboard(),true);
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isPhoneNumberValid(text)){
				getContext().hideKeyboard();
				PhoneNumber = text;
				return new SkypeNumberAdjacencyPair(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new PhoneNumberInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new SkypeNumberAdjacencyPair(getContext());
		}
	}
	
	public static class SkypeNumberAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public SkypeNumberAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her skype Account?", "Enter " + PersonName + "'s skype name:", 
					context, context.getKeyboard());
			choice("Skip "+PersonName, new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					getContext().hideKeyboard();
					return new InitialEnroll(getContext());
				}
			});
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			SkypeNumber = text;
			person = getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
					PersonAge, PhoneNumber, SkypeNumber, zipcode, Spouse);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			person = getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
					PersonAge, PhoneNumber, SkypeNumber, zipcode, Spouse);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}
}
