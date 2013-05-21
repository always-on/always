package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.*;

/**
 * Note that the realizer for this behavior is system-specific.
 */
public class SpeechBehavior extends PrimitiveBehavior {

   private final String text;

   public SpeechBehavior (String text) {
      this.text = text;
   }

   @Override
   public Resource getResource () {
      return Resources.SPEECH;
   }

   @Override
   public boolean equals (Object o) {
      if ( o == this )
         return true;
      if ( !(o instanceof SpeechBehavior) )
         return false;
      SpeechBehavior theOther = (SpeechBehavior) o;
      return this.getText().equals(theOther.getText());
   }

   @Override
   public int hashCode () {
      return getText().hashCode();
   }

   public String getText () {
      return text;
   }

   @Override
   public String toString () {
      return "SPEECH(\"" + text + "\")";
   }
}
