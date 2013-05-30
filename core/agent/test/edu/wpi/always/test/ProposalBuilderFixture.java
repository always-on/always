package edu.wpi.always.test;

import static org.junit.Assert.assertEquals;
import com.google.common.collect.Lists;
import edu.wpi.always.client.ClientPlugin;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import org.junit.Test;
import java.util.ArrayList;

public class ProposalBuilderFixture {

   @Test
   public void testRequired () {
      DummyPlugin plugin = new DummyPlugin();
      Behavior b = new ProposalBuilder(plugin)
            .say("Hello").pluginAction("Meld", AgentResources.HAND).gazeAtUser()
            .build();
      ArrayList<PrimitiveBehavior> l = Lists.newArrayList();
      l.add(new SpeechBehavior("Hello"));
      l.add(new PluginSpecificBehavior(plugin, "Meld", AgentResources.HAND));
      l.add(new FaceTrackBehavior());
      CompoundBehavior expected = new SimpleCompoundBehavior(l);
      assertEquals(expected, b.getInner());
   }

   @Test
   public void whenAddinRequireds_IfABehaviorForTheResourceAlreadyExists_JustOverrideThatWithTheNewOne () {
      Behavior b = new ProposalBuilder(new DummyPlugin()).say("Hello").gazeAtUser()
            .gazeAtUser().say("Good morning").build();
      ArrayList<PrimitiveBehavior> l = Lists.newArrayList();
      l.add(new SpeechBehavior("Good morning"));
      l.add(new FaceTrackBehavior());
      CompoundBehavior expected = new SimpleCompoundBehavior(l);
      assertEquals(expected, b.getInner());
   }

   public static class DummyPlugin implements ClientPlugin {

      @Override
      public void doAction (String actionName) {
      }

      @Override
      public void initInteraction () {
      }

      @Override
      public BehaviorBuilder updateInteraction (boolean lastProposalIsDone, double focusMillis) {
         return null;
      }

      @Override
      public void endInteraction () {
      }
   }
}
