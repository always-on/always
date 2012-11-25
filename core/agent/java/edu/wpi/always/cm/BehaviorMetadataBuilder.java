package edu.wpi.always.cm;

public class BehaviorMetadataBuilder {
	
	private double _specificity = 0.5; 
	private double _dueIn = 30, _timeRemaining = 30;
	private boolean _newActivity;
	private boolean _isContainer;
	private boolean _goodInterruptMoment = true;
	private double _timeActive;
	
	public BehaviorMetadataBuilder() {
		
	}
	
	public BehaviorMetadataBuilder(BehaviorMetadata defaultValues) {
		_specificity = defaultValues.getSpecificity();
		_dueIn = defaultValues.getDueIn();
		_timeRemaining = defaultValues.getTimeRemaining();
		_newActivity = defaultValues.getNewActivity();
		_isContainer = defaultValues.getIsContainer();
		_goodInterruptMoment = defaultValues.getGoodInterruptMoment();
		_timeActive = defaultValues.getTimeActive();
	}

	public BehaviorMetadataBuilder specificity(double val) {
		_specificity = val;
		return this;
	}
	
	public BehaviorMetadataBuilder dueIn(double minutes) {
		_dueIn = minutes;
		return this;
	}
	
	public BehaviorMetadataBuilder timeRemaining(double minutes) {
		_timeRemaining = minutes;
		return this;
	}
	
	public BehaviorMetadata build() {
		return new BehaviorMetadata(_specificity, _dueIn, _timeRemaining, _newActivity, _isContainer, _goodInterruptMoment, _timeActive);
	}

	public BehaviorMetadataBuilder newActivity(boolean isNew) {
		_newActivity = isNew;
		return this;
	}

	public void isContainer(boolean n) {
		_isContainer = n;
	}

	public void goodInterruntMoment(boolean n) {
		_goodInterruptMoment = n;
	}

	public void timeActive(double mins) {
		_timeActive = mins;
	}
}
