package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class for adding resources based on markup embedded in text string.
 */
public class SpeechMarkupBehavior implements CompoundBehavior {

   // basically a "wrapper" for this speech behavior
   private final SpeechBehavior speech;
   private final Set<Resource> resources;

   public SpeechMarkupBehavior (String text) {
      this.speech = new SpeechBehavior(text);
      resources = ANALYZER == null ? new CopyOnWriteArraySet<Resource>() : ANALYZER.analyze(text);
      resources.add(Resources.SPEECH);
   }
   
   public static SpeechMarkupAnalyzer ANALYZER;
   
   @Override
   public Set<Resource> getResources () { return resources; }

   @Override
   public CompoundRealizer createRealizer (PrimitiveBehaviorControl primitiveControl) {
      return new Realizer(primitiveControl);
   }

   @Override
   public boolean equals (Object o) {
      return o == this ||
        (o instanceof SpeechMarkupBehavior && 
         speech.equals(((SpeechMarkupBehavior) o).speech));  
   }

   @Override
   public int hashCode () {
      return speech.hashCode();
   }

   @Override
   public String toString () {
      return "SpeechMarkup(\""+speech.getText()+"\")";
   }

   private class Realizer extends CompoundRealizerBase implements
         PrimitiveBehaviorControlObserver {

      private final PrimitiveBehaviorControl primitiveControl;
      private boolean done;

      public Realizer (PrimitiveBehaviorControl primitiveControl) {
         this.primitiveControl = primitiveControl;
      }

      @Override
      public void run () {
         primitiveControl.addObserver(this);
         primitiveControl.realize(speech);
      }

      @Override
      public boolean isDone () { return done; }

      @Override
      public void primitiveDone (PrimitiveBehaviorControl sender,
            PrimitiveBehavior primitive) {
         if ( primitive == speech ) { 
            done = true;
            notifyDone();
         }
      }

      @Override
      public void primitiveStopped (PrimitiveBehaviorControl sender,
            PrimitiveBehavior primitive) {}

      @Override
      public String toString () {
         return SpeechMarkupBehavior.this.toString();
      }
   }
}
