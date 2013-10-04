package edu.wpi.disco.rt.behavior;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.Constraint.Type;
import edu.wpi.disco.rt.realizer.petri.*;
import java.util.*;

public class SyncSayBuilder implements BehaviorBuilder {

   private final List<PrimitiveBehavior> behaviors;
   private final String speech;
   private BehaviorMetadataBuilder metadata;
   private boolean needsFocusResource;
   
   @Override
   public void setNeedsFocusResource (boolean focus) {
      this.needsFocusResource = focus;
   }

   /**
    * NB: speech string is <em>not</em> analyzed for resource-related markup!
    */
   public SyncSayBuilder (String speech, PrimitiveBehavior... behaviors) {
      this.speech = speech;
      this.behaviors = Lists.newArrayList(behaviors);
   }

   @Override
   public Behavior build () {
      ArrayList<PrimitiveBehavior> allPrimitives = new ArrayList<PrimitiveBehavior>();
      ArrayList<Constraint> constraints = new ArrayList<Constraint>();
      allPrimitives.addAll(behaviors);
      String[] split = speech.split("\\$");
      SpeechBehavior prevSpeech = null;
      for (int i = 0; i < split.length; i++) {
         SpeechMarkupBehavior sb = new SpeechMarkupBehavior(split[i]);
         allPrimitives.addAll(sb.getPrimitives(false));
         SpeechBehavior speech = sb.getSpeech();
         if ( prevSpeech != null ) {
            constraints.add(new Constraint(new SyncRef(SyncPoint.End, prevSpeech),
                        new SyncRef(SyncPoint.Start, speech), Type.After, 0));
         }
         if ( i > 0 && i <= behaviors.size() ) {
            constraints.add(new Constraint(new SyncRef(SyncPoint.Start, speech),
                  new SyncRef(SyncPoint.Start, behaviors.get(i - 1)),
                  Type.Sync, 0));
         }
         prevSpeech = speech;
      }
      if ( needsFocusResource ) allPrimitives.add(new FocusRequestBehavior());
      return new Behavior(new CompoundBehaviorWithConstraints(allPrimitives,
            constraints));
   }

   @Override
   public BehaviorMetadata getMetadata () {
      return metadata.build();
   }

   public void setMetaData (BehaviorMetadataBuilder metadata) {
      this.metadata = metadata;
   }
}
