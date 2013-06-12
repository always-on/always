package edu.wpi.always.enroll;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.enroll.schema.EnrollStateContext;
import edu.wpi.always.enroll.schema.InitialEnroll;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.ZipCode;

public abstract class ErrorCheckState extends EnrollAdjacencyPairs{

	public static class CheckCorrectionAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {
		
		private Person person;

		public CheckCorrectionAdjacencyPair(final EnrollStateContext context, final Person person){
			super("Okay, here's what I have.  Are there any mistakes?", context, true);
			this.person = person;
			choice("Edit Name", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditNameAdjacencyPair(getContext());
				}
			});
			choice("Edit Address", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditAddressAdjacencyPair(getContext());
				}
			});
			choice("Edit Zipcode", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditZipCodeAdjacencyPair(getContext());
				}
			});
			choice("Edit Relation", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditRelationAdjacencyPair(getContext());
				}
			});
			choice("Edit Age", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditAgeAdjacencyPair(getContext());
				}
			});
			choice("Edit Gender", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditGenderAdjacencyPair(getContext());
				}
			});
			choice("Edit Spouse", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditSpouseAdjacencyPair(getContext());
				}
			});
			choice("Edit Skype", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditSkypeAdjacencyPair(getContext());
				}
			});
			choice("Edit Phone", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new EditPhoneAdjacencyPair(getContext());
				}
			});
			choice("All Correct", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new NextPersonAdjacencyPair(getContext());
				}
			});
		}
		@Override
		public void enter() {
			getContext().getEnrollUI().showCurrentEntry(person);
		}
	}

	public static class EditNameAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditNameAdjacencyPair(final EnrollStateContext context) {
			super("Please enter the Person's name.", "Enter name:", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setName(text);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class EditGenderAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public EditGenderAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's gender?", context);
			choice("Male", new DialogStateTransition() {

				@Override
				public AdjacencyPair run() {
					person.setGender(Gender.Male);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Female", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					person.setGender(Gender.Female);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run () {
					person.setGender(null);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
		}
	}

	public static class EditAgeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditAgeAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's age?", "Enter " + PersonName +"'s age:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setAge(text);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setAge(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}	
	}

	public static class EditAddressAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditAddressAdjacencyPair(final EnrollStateContext context) {
			super("What is the person's address?", "Enter " + PersonName + "'s address:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setAddress(text);
			return new EditZipCodeAdjacencyPair(getContext());
		}

		@Override
		public AdjacencyPair cancel() {
			person.setAddress(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class EditZipCodeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditZipCodeAdjacencyPair(final EnrollStateContext context) {
			super("What is the address's zipcode?", "Enter " + PersonName +"s zipcode:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				person.setZipCode(text);
				result = new CheckCorrectionAdjacencyPair(getContext(), person);
			}
			else{
				result = new ZipCodeAgainInvalidAdjacencyPair(getContext());
			}
			return result;
		}

		@Override
		public AdjacencyPair cancel() {
			person.setZipCode(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class ZipCodeAgainInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ZipCodeAgainInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The zipcode entered is invalid. Please enter an valid zipcode.", 
					"Enter " + PersonName + "'s zipcode again:", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			final AdjacencyPair result;
			getContext().hideKeyboard();
			if(ZipCode.isZipCodeValid(text)){
				person.setZipCode(text);
				result = new CheckCorrectionAdjacencyPair(getContext(), person);
			}
			else{
				result = new ZipCodeAgainInvalidAdjacencyPair(getContext());
			}
			return result;
		}  	     

		@Override
		public AdjacencyPair cancel() {
			person.setZipCode(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class EditRelationAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public EditRelationAdjacencyPair(final EnrollStateContext context) {
			super("What is your relationship with this person?", context, true);
			choice("Friend", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Friend);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Sister", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Sister);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Brother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Brother);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Mother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Mother);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Father", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Father);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Daughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Daughter);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Son", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Son);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Granddaughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Granddaughter);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Grandson", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Grandson);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Aunt", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Aunt);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Uncle", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Uncle);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Cousin", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Cousin);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition () {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(null);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
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
					person.setSpouse(null);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setSpouse(null);
					return new CheckCorrectionAdjacencyPair(getContext(), person);
				}
			});
		}
	}

	public static class ReenterSpouseAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public ReenterSpouseAdjacencyPair(final EnrollStateContext context){
			super("What is his or her spouse's name?", "Enter " + PersonName + "'s spouse name:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setSpouse(text);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setSpouse(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class EditPhoneAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditPhoneAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her phone number?", "Enter " + PersonName + "'s phone number:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setPhoneNumber(text);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setPhoneNumber(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}
	}

	public static class EditSkypeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public EditSkypeAdjacencyPair(final EnrollStateContext context) {
			super("What is his or her skype number?", "Enter " + PersonName + "s skype account:", 
					context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setSkypeNumber(text);
			return new CheckCorrectionAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setSkypeNumber(null);
			getContext().hideKeyboard();
			return new CheckCorrectionAdjacencyPair(getContext(), person);
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

