package edu.wpi.always.enroll;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.enroll.schema.EnrollStateContext;
import edu.wpi.always.enroll.schema.InitialEnroll;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.ZipCode;

public abstract class ErrorCheckState extends EnrollAdjacencyPairs{

	public static class CheckCorrectionAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public CheckCorrectionAdjacencyPair(final EnrollStateContext context){
			super("Okay, here's what I have.  Are there any mistakes?", context, true);
			choice("The name is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditNameAdjacencyPair(getContext());
				}
			});
			choice("The address is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditAddressAdjacencyPair(getContext());
				}
			});
			choice("The zipcode is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditZipCodeAdjacencyPair(getContext());
				}
			});
			choice("The relation is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditRelationAdjacencyPair(getContext());
				}
			});
			choice("The age is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditAgeAdjacencyPair(getContext());
				}
			});
			choice("The gender is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditGenderAdjacencyPair(getContext());
				}
			});
			choice("The spouse's name is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditSpouseAdjacencyPair(getContext());
				}
			});
			choice("The Skype contact is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditSkypeAdjacencyPair(getContext());
				}
			});
			choice("The phone number is wrong.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditPhoneAdjacencyPair(getContext());
				}
			});
			choice("No, all are correct.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new NextPersonAdjacencyPair(getContext());
				}
			});
		}
		@Override
		public void enter() {
			person = getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
					PersonAge, PhoneNumber, SkypeNumber, address, zipcode, Spouse);
			getContext().getEnrollUI().showCurrentEntry(person);
		}
	}

	public static class EditNameAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditNameAdjacencyPair(final EnrollStateContext context) {
			super("Please enter the Person's name.", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PersonName = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class EditGenderAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public EditGenderAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's gender?", context);
			choice("Male", new DialogStateTransition() {

				@Override
				public AdjacencyPair run() {
					gender = Gender.Male;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Female", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					gender = Gender.Female;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
		}
	}

	public static class EditAgeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditAgeAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's age?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PersonAge = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}	
	}

	public static class EditAddressAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditAddressAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's address?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			address = text;
			return new EditZipCodeAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class EditZipCodeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditZipCodeAdjacencyPair(final EnrollStateContext context) {
			super("What is the address's zipcode?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				zipcode = text;
				result = new CheckCorrectionAdjacencyPair(getContext());
			}
			else{
				result = new ZipCodeAgainInvalidAdjacencyPair(getContext());
			}
			return result;
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class ZipCodeAgainInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ZipCodeAgainInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The zipcode entered is invalid. Please enter an valid zipcode.", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				zipcode = text;
				result = new CheckCorrectionAdjacencyPair(getContext());
			}
			else{
				result = new ZipCodeAgainInvalidAdjacencyPair(getContext());
			}
			return result;
		}  	     

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class EditRelationAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public EditRelationAdjacencyPair(final EnrollStateContext context) {
			super("What is your relationship with this person?", context, true);
			choice("Friend", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Friend;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Sister", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Sister;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Brother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Brother;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Mother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Mother;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Father", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Father;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Daughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Daughter;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Son", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Son;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Granddaughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Granddaughter;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Grandson", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Grandson;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Aunt", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Aunt;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Uncle", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Uncle;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
			choice("Cousin", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					relationship = Relationship.Cousin;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
		}
	}

	public static class EditSpouseAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public EditSpouseAdjacencyPair(final EnrollStateContext context){
			super("Is the person married?", context);
			choice("Yes", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ReenterSpouseAdjacencyPair(getContext());
				}
			});
			choice("No", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					Spouse = null;
					return new CheckCorrectionAdjacencyPair(getContext());
				}
			});
		}
	}

	public static class ReenterSpouseAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ReenterSpouseAdjacencyPair(final EnrollStateContext context){
			super("What is his or her spouse's name?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			Spouse = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class EditPhoneAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditPhoneAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her phone number?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			PhoneNumber = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class EditSkypeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditSkypeAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her skype number?", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			SkypeNumber = text;
			return new CheckCorrectionAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			return new CheckCorrectionAdjacencyPair(getContext());
		}
	}

	public static class NextPersonAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public NextPersonAdjacencyPair(final EnrollStateContext context) {
			super("Do you want to tell me about someone else?", context);
			choice("No.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new DoneCurrentPersonAdjacencyPair(getContext());
				}
			});
			choice("Yes", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new PersonNameAdjacencyPair(getContext());
				}
			});	
		}

		@Override
		public void enter() {
			getContext().getPeopleManager().addPerson(PersonName, relationship, gender, 
					PersonAge, PhoneNumber, SkypeNumber, address, zipcode, Spouse);
		}
	}

	public static class DoneCurrentPersonAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public DoneCurrentPersonAdjacencyPair(final EnrollStateContext context) {
			super("Now I know about " + PersonName, context);
			choice("Go Back.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new InitialEnroll(getContext());
				}
			});
		}
	}
}

