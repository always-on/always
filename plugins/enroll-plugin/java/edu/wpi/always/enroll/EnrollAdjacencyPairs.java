package edu.wpi.always.enroll;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.enroll.schema.EnrollStateContext;
import edu.wpi.always.enroll.schema.InitialEnroll;
import edu.wpi.always.user.places.ZipCode;
import edu.wpi.always.user.people.Person.*;
import edu.wpi.always.enroll.ErrorCheckState.*;

public abstract class EnrollAdjacencyPairs{

	protected static String PersonName, PersonAge, PhoneNumber, SkypeNumber, Spouse;
	protected static Relationship relationship;
	protected static Gender gender;
	protected static String address;
	protected static String zipcode;

	public static class PersonNameAdjacencyPair extends 
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonNameAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's name?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PersonName = text;
			return new PersonAgeAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
		}
	}

	public static class PersonAgeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonAgeAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's age?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PersonAge = text;
			return new PersonGenderAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
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
					return new PersonAddressAdjacencyPair(getContext());
				}
			});
			choice("Female", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					gender = Gender.Female;
					return new PersonAddressAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new PersonGenderAdjacencyPair(getContext());
				}
			});
		}
	}

	public static class PersonAddressAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonAddressAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's address?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			address = text;
			return new PersonZipCodeAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
		}
	}

	public static class PersonZipCodeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PersonZipCodeAdjacencyPair(final EnrollStateContext context) {
			super("What is the address's zipcode?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				zipcode = text; 
				result = new PersonRelationshipAdjacencyPair(getContext());
			}
			else{
				result = new ZipCodeInvalidAdjacencyPair(getContext());
			}
			return result;
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
		}
	}

	public static class ZipCodeInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ZipCodeInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The zipcode entered is invalid. Please enter an valid zipcode.", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				zipcode = text;
				result = new PersonRelationshipAdjacencyPair(getContext());
			}
			else{
				result = new ZipCodeInvalidAdjacencyPair(getContext());
			}
			return result;
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
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
		}
	}

	public static class EnterSpouseAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EnterSpouseAdjacencyPair(final EnrollStateContext context){
			super("What is his or her spouse's name?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			Spouse = text;
			return new PersonContactAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
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
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Please repeat this question.", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					return new PersonContactAdjacencyPair(getContext());
				}
			});
		}
	}

	public static class PhoneNumberAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public PhoneNumberAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her phone number?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PhoneNumber = text;
			return new SkypeNumberAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
		}
	}

	public static class SkypeNumberAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public SkypeNumberAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her skype Account?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			SkypeNumber = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new InitialEnroll(getContext());
		}
	}
}
