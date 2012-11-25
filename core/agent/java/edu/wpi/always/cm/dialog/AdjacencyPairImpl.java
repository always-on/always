package edu.wpi.always.cm.dialog;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.utils.exceptions.*;

public class AdjacencyPairImpl<C> implements AdjacencyPair {

	private String message;
	private Map<String, DialogStateTransition> choices = new LinkedHashMap<String, DialogStateTransition>();
	private C context;
	private final boolean twoColumn;

	public AdjacencyPairImpl(String message, C context) {
		this(message, context, false);
	}
	public AdjacencyPairImpl(String message, C context, boolean twoColumn) {
		this.message = message;
		this.context = context;
		this.twoColumn = twoColumn;
	}
	
	public C getContext() {
		return context;
	}

	protected void choice(String choice, DialogStateTransition transition) {
		if(choice == null)
			throw new RArgumentNullException("choice");
		
		choices.put(choice, transition);
	}

	public String getMessage() {
		return message;
	}

	public List<String> getChoices() {
		return Lists.newArrayList(choices.keySet());
	}

	@Override
	public AdjacencyPair nextState(String text) {
		if (choices.containsKey(text))
			return choices.get(text).run();
		
		return null;
	}

	@Override
	public double timeRemaining() {
		return 0;
	}

	@Override
	public void enter() {
		//
	}

	@Override
	public boolean isTwoColumnMenu() {
		return twoColumn;
	}
	@Override
	public boolean prematureEnd() {
		return false;
	}

}
