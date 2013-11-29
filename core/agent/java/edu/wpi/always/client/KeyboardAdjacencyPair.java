package edu.wpi.always.client;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.AdjacencyPairBase;
import edu.wpi.disco.rt.menu.DialogStateTransition;

public abstract class KeyboardAdjacencyPair<C extends AdjacencyPair.Context> extends AdjacencyPairBase<C> {

   private String prompt;
   private final Keyboard keyboard;
   private boolean isNumeric = false;

   public KeyboardAdjacencyPair (String prompt, Keyboard keyboard) {
      this(prompt, null, keyboard);
   }

   public KeyboardAdjacencyPair (String prompt, C context, Keyboard keyboard) {
      this (prompt, prompt, context, keyboard, false);
   }

   public KeyboardAdjacencyPair (String prompt, String showText, C context, Keyboard keyboard) {
      this (prompt, showText, context, keyboard, false);
   }

   public KeyboardAdjacencyPair (String prompt, String showText, C context, Keyboard keyboard, boolean isNumeric) {
      super(prompt + ". Let me know when you're done", context);
      this.prompt = showText;
      this.keyboard = keyboard;
      this.isNumeric = isNumeric;
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

}
