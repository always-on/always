package edu.wpi.always.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.wpi.disco.rt.menu.*;

public abstract class KeyboardAdjacencyPair<C> extends AdjacencyPairBase<C> {

	 private String prompt;
	 private final Keyboard keyboard;
	 private boolean isNumeric = false;
	 private IsOverflowListener listener = new IsOverflowListener();

	 public KeyboardAdjacencyPair (String prompt, Keyboard keyboard) {
		 this(prompt, null, keyboard);
		 ((KeyboardMessageHandler) keyboard).addPropertyChangeListener(listener);
	 }

	 public KeyboardAdjacencyPair (String prompt, C context, Keyboard keyboard) {
		 super(prompt + " Let me know when you're done", context);
		 this.prompt = prompt;
		 this.keyboard = keyboard;
		 ((KeyboardMessageHandler) keyboard).addPropertyChangeListener(listener);
		 choice("I'm Done", new DialogStateTransition() {

			 @Override
			 public AdjacencyPair run () {
				 return success(KeyboardAdjacencyPair.this.keyboard.getInputSoFar());
			 }
		 });
		 choice("Never Mind", new DialogStateTransition() {
			
		 	 @Override
		 	 public AdjacencyPair run () {
				 return cancel();
			 }
		 });
	 }
	 
	 public KeyboardAdjacencyPair (String prompt, String showText, C context, Keyboard keyboard) {
		 super(prompt + " Let me know when you're done", context);
		 this.prompt = showText;
		 this.keyboard = keyboard;
		 ((KeyboardMessageHandler) keyboard).addPropertyChangeListener(listener);
		 choice("I'm Done", new DialogStateTransition() {

			 @Override
			 public AdjacencyPair run () {
				 return success(KeyboardAdjacencyPair.this.keyboard.getInputSoFar());
			 }
		 });
		 choice("Never Mind", new DialogStateTransition() {

			 @Override
			 public AdjacencyPair run () {
				 return cancel();
			 }
		 });
	 }

	 public KeyboardAdjacencyPair (String prompt, String showText, C context, Keyboard keyboard, boolean isNumeric) {
		 super(prompt + " Let me know when you're done", context);
		 this.prompt = showText;
		 this.keyboard = keyboard;
		 this.isNumeric = isNumeric;
		 ((KeyboardMessageHandler) keyboard).addPropertyChangeListener(listener);
		 choice("I'm Done", new DialogStateTransition() {

			 @Override
			 public AdjacencyPair run () {
				 return success(KeyboardAdjacencyPair.this.keyboard.getInputSoFar());
			 }
		 });
		 choice("Never Mind", new DialogStateTransition() {

			 @Override
			 public AdjacencyPair run () {
				 return cancel();
			 }
		 });
	 }

	 @Override
	 public void enter () {
		 keyboard.showKeyboard(prompt, isNumeric);
	 }

	 public abstract AdjacencyPair success (String text);

	 public abstract AdjacencyPair cancel ();
	 
	 public class IsOverflowListener implements PropertyChangeListener {
	    @Override
	    public void propertyChange(PropertyChangeEvent event) {
	   	 if (event.getPropertyName().equals("isOverflow")) {
	   		 changeToOverflowState(getContext(), keyboard);
	   	 }
	   }

		private AdjacencyPair changeToOverflowState(C context, Keyboard keyboard) {
			return new KeyboardOverflowAdjacencyPair<C>(context, keyboard);		
		}
	}
}
