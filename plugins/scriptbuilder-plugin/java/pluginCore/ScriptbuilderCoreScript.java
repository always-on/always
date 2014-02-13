package pluginCore;

import DialogueRuntime.OutputText;
import com.google.common.collect.Lists;
import edu.wpi.cetask.Utils;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.util.NullArgumentException;
import java.util.*;

public class ScriptbuilderCoreScript extends AdjacencyPairBase<RAGStateContext> {

	private final boolean outputOnly;

	final RAGStateContext context;
	
	// Master function that creates the DSM
	public ScriptbuilderCoreScript(RAGStateContext context) {
		//TODO: ADD NVB SUPPORT ONCE WE GET IT WORKING
		super(context.getNextMessage().getProperty("text"), context);
		this.context = context;
//		super(" ", context);

		//OLD STUFF
		/*
		Message m = context.getNextMessage();
		if (m != null) {
			System.out.println("MESSAGE:" + m.getProperty("text"));
			getContext().getDispatcher().send(m);
		}*/
		if (context.messageQue.size() > 0) {
			outputOnly = true;
		} else {
			if (context.outputOnly) {
				outputOnly = true;
			} else {
			   outputOnly = false;
				try {
					OutputText[] choices = context.getMenuPrompts();
					if (choices == null) {
						System.out.println("What? How are choices empty here?");
					}
					for (int i = 0; i < choices.length; i++) {
						choice(choices[i].getOutput(),
								new AADialogStateTransition(i, context));
					}
				} catch (Exception e) {
				   Utils.rethrow(e);
				}
			}
		}
	}

	@Override
	public AdjacencyPair nextState(String text) {
		if (outputOnly) {
			System.out.println("doing an output only state");
			return new AADialogStateTransition(0, getContext()).run();
		} else return super.nextState(text);
	}
}
