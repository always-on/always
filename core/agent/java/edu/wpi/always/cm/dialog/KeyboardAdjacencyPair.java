package edu.wpi.always.cm.dialog;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.cm.dialog.*;

public abstract class KeyboardAdjacencyPair<C> extends AdjacencyPairBase<C> {

   private String prompt;
   private final Keyboard keyboard;

   public KeyboardAdjacencyPair (String prompt, Keyboard keyboard) {
      this(prompt, null, keyboard);
   }

   public KeyboardAdjacencyPair (String prompt, C context, Keyboard keyboard) {
      super("Let me know when your done", context);
      this.prompt = prompt;
      this.keyboard = keyboard;
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
      keyboard.showKeyboard(prompt);
   }

   public abstract AdjacencyPair success (String text);

   public abstract AdjacencyPair cancel ();
}
