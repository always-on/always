package pluginCore;

import edu.wpi.disco.rt.menu.*;


class AADialogStateTransition implements DialogStateTransition {
	int menuChoice = 0;
	RAGStateContext context;

	public AADialogStateTransition(int menuChoice, RAGStateContext context) {
		this.menuChoice = menuChoice;
		this.context = context;
	}

	@Override
	public AdjacencyPair run() {
		context.menuChoice = menuChoice;
		return new ScriptbuilderCoreScript(context);
	}
}