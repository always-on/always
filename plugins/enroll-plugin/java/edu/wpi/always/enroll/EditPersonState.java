package edu.wpi.always.enroll;

import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.enroll.schema.EnrollStateContext;
import edu.wpi.always.enroll.schema.InitialEnroll;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.people.ValidPhoneOrAge;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.ZipCodes;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;
import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPairBase;
import edu.wpi.disco.rt.menu.DialogStateTransition;

public class EditPersonState extends EnrollAdjacencyPairs{
	
	public static class EditPersonAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {
		
		private Person person;

		public EditPersonAdjacencyPair(final EnrollStateContext context, final Person person){
			super("Here is the previous information about the person?", context, true);
			this.person = person;
			choice("Edit name", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeNameAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Address", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeAddressAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Zipcode", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeZipCodeAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Relation", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeRelationAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Age", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeAgeAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Gender", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeGenderAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Spouse.", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeSpouseAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Skype", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeSkypeAdjacencyPair(getContext(), person);
				}
			});
			choice("Edit Phone", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangePhoneAdjacencyPair(getContext(), person);
				}
			});
			choice("Done Edit", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					getContext().hideEnrollUI();
					return new InitialEnroll(getContext());
				}
			});
		}
		@Override
		public void enter() {
			getContext().getEnrollUI().showCurrentEntry(person);
		}
	}

	public static class ChangeNameAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeNameAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("Please enter the Person's name.", "Enter name:", context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setName(text);
			return new EditPersonAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangeGenderAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {
		
		public ChangeGenderAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is the person's gender?", context);
			choice("Male", new DialogStateTransition() {

				@Override
				public AdjacencyPair run() {
					person.setGender(Gender.Male);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Female", new DialogStateTransition() {

				@Override
				public AdjacencyPair run () {
					person.setGender(Gender.Female);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				
				@Override
				public AdjacencyPair run() {
					person.setGender(null);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
		}
	}

	public static class ChangeAgeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeAgeAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is the person's age?", "Enter " + person.getName() + "'s age (Integer Only)", 
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isInteger(text)){
				getContext().hideKeyboard();
				person.setAge(text);
				return new EditPersonAdjacencyPair(getContext(), person);
			}
			else{
				getContext().hideKeyboard();
				return new ChangeAgeAdjacencyPair(getContext(), person);
			}
		}

		@Override
		public AdjacencyPair cancel() {
			person.setAge(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}	
	}

	public static class ChangeAddressAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeAddressAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is the person's address?", "Enter " + person.getName() + "'s address", 
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setAddress(text);
			return new ChangeZipCodeAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setAddress(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangeZipCodeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeZipCodeAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is the address's zipcode?", "Enter " + person.getName() + "'s zipcode:",
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			ZipCodeEntry zip = zipcodes.getPlaceData(text);
			if(zip != null){
				person.setLocation(getContext().getPlaceManager().getPlace(zip.getZip()));
				getContext().hideKeyboard();
				return new EditPersonAdjacencyPair(getContext(), person);
			}
			else{
				getContext().hideKeyboard();
				return new ChangeZipCodeAgainInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			person.setLocation(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangeZipCodeAgainInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		

		public ChangeZipCodeAgainInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The zipcode entered is invalid. Please enter an valid zipcode.", 
					"Enter " + person.getName() + "'s zipcode again:", context, context.getKeyboard());
		}

		@Override
		public AdjacencyPair success(String text) {
			ZipCodes zipcodes = getContext().getPlaceManager().getZipCodes();
			ZipCodeEntry zip = zipcodes.getPlaceData(text);
			if(zip != null){
				person.setLocation(getContext().getPlaceManager().getPlace(zip.getZip()));
				getContext().hideKeyboard();
				return new EditPersonAdjacencyPair(getContext(), person);
			}
			else{
				getContext().hideKeyboard();
				return new ChangeZipCodeAgainInvalidAdjacencyPair(getContext());
			}
		}  	     

		@Override
		public AdjacencyPair cancel() {
			person.setLocation(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangeRelationAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {
		
		public ChangeRelationAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is your relationship with this person?", context, true);
			choice("Friend", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Friend);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Sister", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Sister);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Brother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Brother);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Mother", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Mother);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Father", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Father);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Daughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Daughter);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Son", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Son);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Granddaughter", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Granddaughter);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Grandson", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Grandson);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Aunt", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Aunt);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Uncle", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Uncle);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Cousin", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(Relationship.Cousin);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setRelationship(null);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
		}
	}

	public static class ChangeSpouseAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {
		
		public ChangeSpouseAdjacencyPair(final EnrollStateContext context, final Person person){
			super("Is the person married?", context);
			choice("Yes", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new ChangeReenterSpouseAdjacencyPair(getContext(), person);
				}
			});
			choice("No", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setSpouse(null);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					person.setSpouse(null);
					return new EditPersonAdjacencyPair(getContext(), person);
				}
			});
		}
	}

	public static class ChangeReenterSpouseAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeReenterSpouseAdjacencyPair(final EnrollStateContext context, final Person person){
			super("What is his or her spouse's name?", "Enter " + person.getName() +"'s spouse name:", 
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setSpouse(text);
			return new EditPersonAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setSpouse(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangePhoneAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangePhoneAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is his or her phone number?", "Enter " + person.getName() + "'s phone number:", 
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			if(ValidPhoneOrAge.isPhoneNumberValid(text)){
				getContext().hideKeyboard();
				person.setPhoneNumber(text);
				return new EditPersonAdjacencyPair(getContext(), person);
			}
			else{
				getContext().hideKeyboard();
				return new ChangePhoneAdjacencyPair(getContext(), person);
			}
		}

		@Override
		public AdjacencyPair cancel() {
			person.setPhoneNumber(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}

	public static class ChangeSkypeAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {
		
		private Person person;

		public ChangeSkypeAdjacencyPair(final EnrollStateContext context, final Person person) {
			super("What is his or her skype number?", "Enter " + person.getName() +"'s skype number:", 
					context, context.getKeyboard());
			this.person = person;
		}

		@Override
		public AdjacencyPair success(String text) {
			getContext().hideKeyboard();
			person.setSkypeNumber(text);
			return new EditPersonAdjacencyPair(getContext(), person);
		}

		@Override
		public AdjacencyPair cancel() {
			person.setSkypeNumber(null);
			getContext().hideKeyboard();
			return new EditPersonAdjacencyPair(getContext(), person);
		}
	}
}
