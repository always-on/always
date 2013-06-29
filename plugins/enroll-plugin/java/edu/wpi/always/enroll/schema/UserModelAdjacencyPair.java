package edu.wpi.always.enroll.schema;

import org.joda.time.MonthDay;
import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.enroll.EnrollAdjacencyPairs.PersonNameAdjacencyPair;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.people.ValidPhoneAgeBirthday;
import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPairBase;
import edu.wpi.disco.rt.menu.DialogStateTransition;

public class UserModelAdjacencyPair extends KeyboardAdjacencyPair<EnrollStateContext>{
	
	private static String UserName;
	private static int Month;
	private static int Day;
	private static MonthDay userBirthday;

	public UserModelAdjacencyPair(final EnrollStateContext context) {
		super("What is the name you want me to use to talk with you", 
				"Enter your first name:", context, context.getKeyboard());
	}

	@Override
	public AdjacencyPair success(String text) {
		getContext().hideKeyboard();
		UserName = text;
		getContext().getUserModel().setUserName(text);
		return new TellUserBirthdayAdjacencyPair(getContext());
	}

	@Override
	public AdjacencyPair cancel() {
		getContext().hideKeyboard();
		UserName = "this person";
		return new TellUserBirthdayAdjacencyPair(getContext());
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
	}
	
	public static class UserBirthdayMonthAdjacencyPair extends
	AdjacencyPairBase<EnrollStateContext> {

		public UserBirthdayMonthAdjacencyPair(final EnrollStateContext context) {
			super("What is " + UserName + "'s birthday month", context, true);
			Month = 0;
			for(int i = 0; i < 12; i++) {
				choice(Person.Month[i], new DialogStateTransition() {
					@Override
					public AdjacencyPair run() {
						Month = Month + 1;
						return new UserBirthdayDayAdjacencyPair(getContext());
					}
				});
			}
			choice("Never Mind", new DialogStateTransition() {
				@Override
				public AdjacencyPair run() {
					return new InitialEnroll(getContext());
				}
			});
		}
	}

	public static class UserBirthdayDayAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public UserBirthdayDayAdjacencyPair(final EnrollStateContext context) {
			super("What is the day of " + UserName + "'s Birthday", 
					"Enter "+ UserName + "'s Birthday:", 
					context, context.getKeyboard(), true);}

		@Override
		public AdjacencyPair success(String text) {
			int day = Integer.parseInt(text);
			if(ValidPhoneAgeBirthday.isValidDayOfMonth(Month, day)){
				getContext().hideKeyboard();
				Day = day;
				userBirthday = new MonthDay(Month, Day);
				getContext().getUserModel().getPeopleManager().
				getPerson(UserName).setBirthday(userBirthday);
				return new InitialEnroll(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new UserBirthdayDayInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new InitialEnroll(getContext());
		}	
	}

	public static class UserBirthdayDayInvalidAdjacencyPair extends
	KeyboardAdjacencyPair<EnrollStateContext> {

		public UserBirthdayDayInvalidAdjacencyPair(final EnrollStateContext context) {
			super("The day you enter is invalid please enter again", 
					"Enter valid "+ UserName+ "'s birthday:", context, context.getKeyboard(), true);}

		@Override
		public AdjacencyPair success(String text) {
			int day = Integer.parseInt(text);
			if(ValidPhoneAgeBirthday.isValidDayOfMonth(Month, day)){
				getContext().hideKeyboard();
				Day = day;
				userBirthday = new MonthDay(Month, Day);
				getContext().getUserModel().getPeopleManager().
				getPerson(UserName).setBirthday(userBirthday);
				return new InitialEnroll(getContext());
			}
			else{
				getContext().hideKeyboard();
				return new UserBirthdayDayInvalidAdjacencyPair(getContext());
			}
		}

		@Override
		public AdjacencyPair cancel() {
			getContext().hideKeyboard();
			return new InitialEnroll(getContext());
		}	
	}
	
}

