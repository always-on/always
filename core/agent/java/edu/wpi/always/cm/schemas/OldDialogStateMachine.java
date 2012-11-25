package edu.wpi.always.cm.schemas;

import java.util.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;

public class OldDialogStateMachine implements BehaviorBuilder {
	private final BehaviorHistory behaviorHistory;
	private TimeStampedValue<Behavior> lastProposal = new TimeStampedValue<Behavior>(
			Behavior.NULL);
	private State state;
	private final DialogContentProvider contentProvider;
	private final MenuPerceptor menuPerceptor;
	private double specificity;
	private boolean newActivity;

	public OldDialogStateMachine(BehaviorHistory behaviorHistory,
			DialogContentProvider contentProvider, MenuPerceptor menuPerceptor) {
		this.behaviorHistory = behaviorHistory;
		this.contentProvider = contentProvider;
		this.menuPerceptor = menuPerceptor;
		state = State.Say;
	}

	@Override
	public Behavior build() {
		if (state == State.Say) {
			String text = contentProvider.whatToSay();

			if (text != null) {
				Behavior b = Behavior.newInstance(new SpeechBehavior(text));

				if (saveProposalAndCheckIfAlreadyDone(b)) {
					contentProvider.doneSaying(text);
					return toggleState();
				}

				return b;
			} else {
				if (haveSomeChoicesForTheUser()) {
					return toggleState();
				}
			}
		} else if (state == State.Hear) {
			List<String> userChoices = contentProvider.userChoices();

			if (userChoices != null && !userChoices.isEmpty()) {
				Behavior b = Behavior
						.newInstance(new MenuBehavior(userChoices));

				if (saveProposalAndCheckIfAlreadyDone(b)) {
					MenuPerception p = menuPerceptor.getLatest();
					if (p != null &&
							p.getTimeStamp().isAfter(lastProposal.getTimeStamp()) &&
							userChoices.contains(p.selectedMenu())) {
						contentProvider.userSaid(p.selectedMenu());
						return toggleState();
					}
				}

				return b;
			} else {
				if (haveSomethingToSay()) {
					return toggleState();
				}
			}

		}

		return Behavior.NULL;
	}

	public void setSpecificityMetadata(double s) {
		this.specificity = s;

	}

	private boolean haveSomeChoicesForTheUser() {
		List<String> userChoices = contentProvider.userChoices();

		return userChoices != null && !userChoices.isEmpty();
	}

	private boolean haveSomethingToSay() {
		String s = contentProvider.whatToSay();

		return s != null && s.length() > 0;
	}

	private Behavior toggleState() {
		state = (state == State.Hear) ? State.Say : State.Hear;
		return build();
	}

	private boolean saveProposalAndCheckIfAlreadyDone(Behavior b) {
		if (lastProposal.getValue().equals(b)) {
			if (behaviorHistory.isDone(b.getInner(),
					lastProposal.getTimeStamp())) {
				return true;
			}
		} else {
			setLastProposal(b);
		}

		return false;
	}

	private void setLastProposal(Behavior b) {
		lastProposal = new TimeStampedValue<Behavior>(b);
	}

	@Override
	public BehaviorMetadata getMetadata() {
		BehaviorMetadataBuilder builder = new BehaviorMetadataBuilder()
				.specificity(specificity).timeRemaining(
						contentProvider.timeRemaining());

		builder.newActivity(newActivity);

		return builder.build();
	}

	private enum State {
		Say, Hear
	}

	public void setNewActivity(boolean n) {
		this.newActivity = n;
	}
}
