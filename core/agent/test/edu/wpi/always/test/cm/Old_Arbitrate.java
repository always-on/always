package edu.wpi.always.test.cm;

import edu.wpi.always.cm.*;

public class Old_Arbitrate {

	BehaviorMetadataBuilder focus = new BehaviorMetadataBuilder();
	BehaviorMetadataBuilder other = new BehaviorMetadataBuilder();

	public void setFocus_DueIn(double mins) {
		focus.dueIn(mins);
	}

	public void setFocus_TimeRemaining(double mins) {
		focus.timeRemaining(mins);
	}

	public void setFocus_Spec(double spec) {
		focus.specificity(spec);
	}

	public void setFocus_IsNew(boolean n) {
		focus.newActivity(n);
	}

	public void setFocus_IsContainer(boolean n) {
		focus.isContainer(n);
	}

	public void setFocus_GoodInterruptMoment(boolean n) {
		focus.goodInterruntMoment(n);
	}

	public void setFocus_TimeActive(double mins) {
		focus.timeActive(mins);
	}
	
	public void setOther_DueIn(double mins) {
		other.dueIn(mins);
	}

	public void setOther_TimeRemaining(double mins) {
		other.timeRemaining(mins);
	}

	public void setOther_Spec(double spec) {
		other.specificity(spec);
	}

	public void setOther_IsNew(boolean n) {
		other.newActivity(n);
	}

	public void setOther_IsContainer(boolean n) {
		other.isContainer(n);
	}

	public void setOther_GoodInterruptMoment(boolean n) {
		other.goodInterruntMoment(n);
	}
	
	public void setOther_TimeActive(double mins) {
		other.timeActive(mins);
	}
	
	public boolean shouldSwitch() {
		BehaviorMetadata f = focus.build();
		BehaviorMetadata o = other.build();
		FuzzyArbitration a = new FuzzyArbitration(FuzzyArbitrationStrategy.loadFuzzyRules(), f);
		double d = a.shouldSwitch(o);
		System.out.println(f + " vs. " + o + " --> " + d);
		return d > FuzzyArbitrationStrategy.SWITCH_THRESHOLD;
	}
}
