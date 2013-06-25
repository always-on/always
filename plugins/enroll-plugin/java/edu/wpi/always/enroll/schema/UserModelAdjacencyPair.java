package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.client.KeyboardAdjacencyPair;
import edu.wpi.always.enroll.EnrollAdjacencyPairImpl;
import edu.wpi.always.enroll.EnrollAdjacencyPairs.PersonNameAdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPairBase;
import edu.wpi.disco.rt.menu.DialogStateTransition;

public class UserModelAdjacencyPair extends KeyboardAdjacencyPair<EnrollStateContext>{

	public UserModelAdjacencyPair(String prompt, EnrollStateContext context,
			Keyboard keyboard) {
		super(prompt, context, keyboard);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AdjacencyPair success(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdjacencyPair cancel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	private static String UserName;

	public UserModelAdjacencyPair(final EnrollStateContext context) {
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
		return new TellUserBirthdayAdjacencyPair(getContext());
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
	}
	
	*/
}

