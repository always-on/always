package edu.wpi.always.cm;

public interface Plugin {
	void doAction(String actionName);
	void initInteraction ();
	BehaviorBuilder updateInteraction (boolean lastProposalIsDone);
	void endInteraction ();
}
