package edu.wpi.always.cm.dialog;

import java.util.*;
import java.util.regex.*;

import edu.wpi.always.cm.utils.exceptions.*;

public class RepeatMenuTimeoutHandler implements MenuTimeoutHandler {

	@Override
	public AdjacencyPair handle(AdjacencyPair original) {
		if(original == null)
			throw new RArgumentNullException("original");
		
		if(original instanceof RepeatAdjacencyPairWrapper)
			return original;
		
		return new RepeatAdjacencyPairWrapper(original);
	}

	private static class RepeatAdjacencyPairWrapper implements AdjacencyPair {

		private final AdjacencyPair inner;

		public RepeatAdjacencyPairWrapper(AdjacencyPair inner) {
			this.inner = inner;
		}
		
		@Override
		public void enter() {
		}

		@Override
		public boolean prematureEnd() {
			return inner.prematureEnd();
		}

		@Override
		public AdjacencyPair nextState(String text) {
			return inner.nextState(text);
		}

		@Override
		public String getMessage() {
			String original = inner.getMessage();
			Pattern p = Pattern.compile("[a-zA-Z0-9]");
			
			if(original == null || !p.matcher(original).find())
				return original;
			
			return "I said, " + original;
		}

		@Override
		public List<String> getChoices() {
			return inner.getChoices();
		}

		@Override
		public double timeRemaining() {
			return inner.timeRemaining();
		}

		@Override
		public boolean isTwoColumnMenu() {
			return inner.isTwoColumnMenu();
		}
		
	}
	
}
