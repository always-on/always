package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;

public class SpeechBehavior extends PrimitiveBehavior {

   private final String text;

   public SpeechBehavior (String text) {
      this.text = text;
   }

   @Override
   public Resource getResource () {
      return AgentResources.SPEECH;
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
