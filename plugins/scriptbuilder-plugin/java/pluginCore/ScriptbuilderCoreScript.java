package pluginCore;

import DialogueRuntime.OutputText;

import com.google.common.collect.Lists;

import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.util.NullArgumentException;

import java.util.*;

public class ScriptbuilderCoreScript extends AdjacencyPairBase<RAGStateContext> {
	public final Map<String, DialogStateTransition> choices = new LinkedHashMap<String, DialogStateTransition>();

	private boolean outputOnly = false;

	public RAGStateContext context;
	
	// Master function that creates the DSM
	public ScriptbuilderCoreScript(final RAGStateContext context) {
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
					System.out.println("Exception thrown:" + e);
				}
			}
		}
	}

	@Override
	public AdjacencyPair nextState(String text) {
		if (outputOnly) {
			System.out.println("doing an output only state");
			return (new AADialogStateTransition(0, getContext())).run();
		} else {
			if (choices.containsKey(text))
				return choices.get(text).run();
			return null;
		}
	}

	@Override
	protected void choice(String choice, DialogStateTransition transition) {
		if (choice == null)
			throw new NullArgumentException("choice");
		choices.put(choice, transition);
	}

	@Override
	public List<String> getChoices() {
		return Lists.newArrayList(choices.keySet());
	}
}
