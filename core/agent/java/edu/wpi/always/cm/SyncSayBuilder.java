package edu.wpi.always.cm;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.behavior.Constraint.Type;
import edu.wpi.disco.rt.realizer.*;
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
         SpeechBehavior sb = new SpeechBehavior(split[i]);
         allPrimitives.add(sb);
         if ( prevSpeech != null ) {
            constraints
                  .add(new Constraint(new SyncRef(SyncPoint.End, prevSpeech),
                        new SyncRef(SyncPoint.Start, sb), Type.After, 0));
         }
         if ( i > 0 && i <= behaviors.size() ) {
            constraints.add(new Constraint(new SyncRef(SyncPoint.Start, sb),
                  new SyncRef(SyncPoint.Start, behaviors.get(i - 1)),
                  Type.Sync, 0));
         }
         prevSpeech = sb;
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
