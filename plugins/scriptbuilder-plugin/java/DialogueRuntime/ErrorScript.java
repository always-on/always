package DialogueRuntime;

public class ErrorScript extends DialogueScript {
	private final String scriptName;
	
	public ErrorScript(String scriptName) {
		super("$$$Error$$$", new String[0]);
		this.scriptName = scriptName;
		add(new ErrorState(this));
	}
	
	private class ErrorState extends DialogueState {
		public ErrorState(DialogueScript script) {
			super("Error", script);
		}
		
		public int getStateType() {
			return OUTPUT_ONLY;
		}

		public void doAction(int n, DialogueStateMachine DSM) throws Exception {
			POP(DSM);
		}
		
		public OutputText getOutput(DialogueStateMachine DSM) {
			String error = "<SPEECH>Error in " + scriptName + "</SPEECH>";
			return new OutputText(error, error);
		}
	}
}
 