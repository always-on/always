package alwaysAvailableCore;


import edu.wpi.cetask.Utils;
import java.util.List;
import DialogueRuntime.*;
import DialogueRuntime.DialogueSession.EventType;
import DialogueRuntime.DialogueSession.State;


// A class for any specialization needed for the   runtime
public class AAECADialogueSession extends DialogueSession {

	public AAECADialogueSession(Client c, SessionRuntime r,
			String scriptIn, String projname) throws Exception {
		super(c, r, scriptIn, projname);
		declaredStartState = DSTART;
		//timeout1 = Integer.MAX_VALUE;
		timeout1 = 15000;
		timeout2=15000;
//		this.DSM.setStateNameDisplay(true);
	}

	/* Sends first set of commands to client. */
	@Override
	public void start() throws Exception {
		super.start(); // run initializer, start event flow
	}

	@Override
	protected void handleException(String where, Exception e) {
		// int result=
		super.handleException(where, e);
		try {
			clientPerform(
					"<SPEECH>Sorry, I have to run.</SPEECH><DISPLAY CMD=\"HIDE\"/>",
					this);
			synchronized (this) {
				try {
					this.wait(4000);
				} catch (Exception ex) {
					handleException("ECADialogueSession.handleException", ex);
				}
			}
			shutdown(DialogueListener.TerminationReason.INTERNAL_ERROR);
		} catch (Exception ex) {
			Utils.rethrow(e);
		}
	}

	/* ---------------- STATE MACHINE ----------------- */

	public State DSTART = new State() {

		@Override
		public String getName() {
			return "DSTART";
		}

		@Override
		public void enter() throws Exception {
			// &&& flushEvents(); //flush unwanted handshakes.
			String stateName = DSM.getStateName();
			String scriptName = DSM.getScriptName();
			String logInfo = scriptName+"."+stateName;
			dialogueListener.stateEntryEvent(null, "", stateName);
			if (dialogueListener != null)
				dialogueListener.stateEntryEvent(null, DSM.getScriptName(),
						stateName);
			debug("Changing dialogue state to " + stateName);
			runtime.log(LogEventType.STATE_CHANGE, logInfo);
			if (DSM.getStateType() == DialogueState.ACTION_ONLY) {
				if (doAction(0)) { // true if dialogue continues
					changeState(DSTART);
				} else {
					changeState(FAREWELL); // HALT dialogue (normal)
				}
			} else {
				clientPerform(DSM.getOutput().getOutput(), DSM.getSession());
				changeState(DWaitForOutput);
			}
		}
	};
	
    @Override
	public void changeState(State newState) {
    	super.changeState(newState);
    }
	protected State DWaitForOutput = new State() {
		@Override
		public String getName() {
			return "DWaitForOutput";
		}

		@Override
		public void enter() throws Exception {
			setTimerEvent(timeout1, 0); // wait timeout1 ms for output
		}

		@Override
		public void processEvent() throws Exception {
			// clientInputEvent(DSM.getRepeatOutput().getOutput());
			if (eventType == EventType.ET_TIMER) { // timeout
				String sname = DSM.getStateName();
				if (!((sname.toLowerCase()).endsWith(NOTIMEOUT_SUFFIX
						.toLowerCase()) || DSM.getFlag("NOTIMEOUT").equals(
						"TRUE"))) {
					client.flush();
				} else {
					return;
				}
				if (DSM.getStateType() == DialogueState.OUTPUT_ONLY) {
					if (doAction(0)) // true if dialogue continues
						changeState(DSTART);
					else
						changeState(FAREWELL); // HALT dialogue (normal)
				} else {
					clientPerform(DSM.getRepeatOutput().getOutput(), DSM
							.getSession());
					changeState(DRetryOutput);
				}
				;
			} else if (eventType == EventType.ET_PERFORM_COMPLETE) {
				if (DSM.getStateType() == DialogueState.OUTPUT_ONLY) {
					if (doAction(0)) // true if dialogue continues
						changeState(DSTART);
					else
						changeState(FAREWELL); // HALT dialogue (normal)
				} else {
					clientPerform(getInputSpec(), DSM.getSession());
					changeState(DWaitForInput);
				}
				;
			}
			;
		}
	};

	protected State DWaitForInput = new State() {
		@Override
		public String getName() {
			return "DWaitForInput";
		}

		@Override
		public void enter() throws Exception {
			// if special state name suffix ("_NOTIMEOUT") then wait forever,
			// else wait timeout1
			// leave here for backward compatibility
			String sname = DSM.getStateName();
			if (!((sname.toLowerCase())
					.endsWith(NOTIMEOUT_SUFFIX.toLowerCase()) || DSM.getFlag(
					"NOTIMEOUT").equals("TRUE")))
				setTimerEvent(timeout1, 0); // wait 15 seconds for user
		}

		@Override
		public void processEvent() throws Exception {
			if (eventType == EventType.ET_TIMER) { // timeout
				/*
				 * someday - need to update sb for this extension:
				 * if(DSM.hasTimeoutAction()) { DSM.doTimeoutAction();
				 * changeState(DSTART); } else {
				 */
				changeState(DTimeout1); 
			} else if (eventType == EventType.ET_LOGIN) {
				changeState(DSTART);
			} else if (eventType == EventType.ET_USER_INPUT) {
				// clientOutputEvent(DSM.getRepeatOutput().getOutput());
				boolean continueDialogue = doAction(userChoice); // true if
																	// dialogue
																	// continues
				if (!continueDialogue) {
					changeState(FAREWELL); // HALT dialogue (normal)
				} else if (DSM.isRepeat()) {
					clientPerform(DSM.getRepeatOutput().getOutput(), DSM
							.getSession());
					changeState(DWaitForOutput);
				} else {
					changeState(DSTART);
				}
			}
			;
		}
	};


	protected State DRetryOutput = new State() {
		@Override
		public String getName() {
			return "DRetryOutput";
		}

		@Override
		public void enter() throws Exception {
			setTimerEvent(OUTPUT_TIMEOUT, 0);
		}

		@Override
		public void processEvent() throws Exception {
			if (eventType == EventType.ET_TIMER) { // timeout
				clientPerform(
						"<SPEECH>Sorry but I have to run. Goodbye.</SPEECH>",
						DSM.getSession());
				throw new Exception("DRetryOutput timeout");
			} else if (eventType == EventType.ET_PERFORM_COMPLETE) {
				clientPerform(getInputSpec(), DSM.getSession());
				changeState(DWaitForInput);
			}
			;
		};
	};

	protected State DTimeout1 = new State() { // Was waiting for user input and
												// 30 secs went by...
		@Override
		public String getName() {
			return "DTimeout1";
		}

		@Override
		public void enter() throws Exception {
			// Repeat our last prompt... and wait for completion..
			clientPerform(DSM.getRepeatOutput().getOutput(), DSM.getSession());
			setTimerEvent(timeout2, 0);
		}

		@Override
		public void processEvent() throws Exception {
			if (eventType == EventType.ET_TIMER
					|| eventType == EventType.ET_PERFORM_COMPLETE) {
				client.flush();
				clientPerform(getInputSpec(), DSM.getSession());
				changeState(DTimeout2);
			} else if (eventType == EventType.ET_USER_INPUT) {
				changeState(DTimeout5);
			} 
		}
	};

	protected State DTimeout2 = new State() { // User was just re-prompted for
												// input
		@Override
		public String getName() {
			return "DTimeout2";
		}

		@Override
		public void enter() throws Exception {
			setTimerEvent(timeout2, 0); // wait for user - again
		}

		@Override
		public void processEvent() throws Exception { // Ignore PERF_COMP from
														// MENU.
			if (eventType == EventType.ET_TIMER) {
				clientPerform("<SPEECH>Are you there?</SPEECH>", DSM
						.getSession());
				changeState(DTimeout3);
			} else if (eventType == EventType.ET_USER_INPUT) {
				if (doAction(userChoice)) // true if dialogue continues
					changeState(DSTART);
				else
					changeState(FAREWELL); // HALT dialogue (normal)
			}
		}
	};
	
	protected State DTimeout3 = new State() { // User was just re-prompted for
		// input
		@Override
		public String getName() {
			return "DTimeout3";
		}

		@Override
		public void enter() throws Exception {
			setTimerEvent(timeout1, 0); // wait for user - again
		}

		@Override
		public void processEvent() throws Exception { 
			if (eventType == EventType.ET_TIMER) {
				clientPerform("<SPEECH> </SPEECH>", DSM
						.getSession());
				changeState(SUSPEND);
			} else if (eventType == EventType.ET_USER_INPUT) {
				if (doAction(userChoice)) // true if dialogue continues
					changeState(DSTART);
				else
					changeState(FAREWELL); // HALT dialogue (normal)
			}
	}
};	
	
	
	protected State DTimeout5=new State() {
	    @Override
		public String getName() { return "DTimeout5"; }
	    @Override
		public void enter() throws Exception {
		setTimerEvent(5000,0);  //basically, just waiting to eat the PERF_COMP; have remembered user input..
	    }
	    @Override
		public void processEvent() throws Exception {
		if(eventType==EventType.ET_PERFORM_COMPLETE || eventType==EventType.ET_TIMER) {
		    if(doAction(userChoice)) //true if dialogue continues
			changeState(DSTART); 
		    else
			changeState(FAREWELL); //HALT dialogue (normal)
		}
	    }
	};


	private State SUSPEND = new State() {
		@Override
		public String getName() {
			return "SUSPEND";
		}

		@Override
		@SuppressWarnings("static-access")
		public void enter() throws InterruptedException {
			// send idle event to the client
			try {
				//DSM.GO("WRAPUP_MINISESSION_TIMEOUT", DSM.scripts.get("Start"));
				DSM.PUSH("Timeout", "WRAPUP_MINISESSION_TIMEOUT", DSM.scripts.get("Timeout"));
			} catch (Exception e) {
				Utils.rethrow(e);
			}
		}

		@Override
		public void processEvent() {
			changeState(DSTART);
		}
	};






	// Normal dialogue termination.
	protected State FAREWELL=new State() {
	    @Override
		public String getName() { return "FAREWELL"; }
	    @Override
		public void enter() throws Exception {
			//Perhaps should give listener terminationEvent() ability to control character exit?
			clientPerform("<DISPLAY CMD=\"HIDE\"/>", DSM.getSession());
			
			System.out.println("we're in AAECADialogueSession farewell");
	
		//	client.close();
		//	dialogueListener.terminationEvent(DialogueListener.TerminationReason.NORMAL);
			shutdown(DialogueListener.TerminationReason.NORMAL); 
		}
	};
}
