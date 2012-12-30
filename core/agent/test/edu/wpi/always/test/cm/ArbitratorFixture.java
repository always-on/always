package edu.wpi.always.test.cm;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.Schema;
import org.junit.*;
import java.awt.Point;
import java.util.*;

public class ArbitratorFixture {

   private FakeContainer container;
   private RealizerStub realizer;
   private Arbitrator arbitrator;

   @Before
   public void setUp () {
      container = new FakeContainer();
      realizer = new RealizerStub();
      arbitrator = new Arbitrator(new FuzzyArbitrationStrategy(), realizer,
            container);
   }

   @Test
   public void simpleTestWithOneSchema () {
      SpeechBehavior b1 = new SpeechBehavior("something");
      GazeBehavior b2 = new GazeBehavior(new Point(1, 1));
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(1).build();
      container.changeTo(new CandidateBehavior(Behavior.newInstance(b1, b2),
            someSchema(), m));
      arbitrator.run();
      assertEquals(1, realizer.realizedBehaviors.size());
      SimpleCompoundBehavior expected = new SimpleCompoundBehavior(
            Lists.newArrayList(b1, b2));
      assertTrue(realizer.realizedBehaviors.contains(expected));
   }

   @Test
   public void whenTwoSchemasGiveConflictingProposals_ShouldPickTheHighestPriority () {
      SpeechBehavior b1 = new SpeechBehavior("something");
      SpeechBehavior b2 = new SpeechBehavior("something else");
      BehaviorMetadata m1 = new BehaviorMetadataBuilder().specificity(0.5)
            .build();
      BehaviorMetadata m2 = new BehaviorMetadataBuilder().specificity(1)
            .build();
      container.changeTo(new CandidateBehavior(Behavior.newInstance(b1),
            someSchema(), m1), new CandidateBehavior(Behavior.newInstance(b2),
            someSchema(), m2));
      arbitrator.run();
      assertEquals(1, realizer.realizedBehaviors.size());
      assertTrue(realizer.realizedBehaviors
            .contains(new SimpleCompoundBehavior(Lists
                  .<PrimitiveBehavior> newArrayList(b2))));
   }

   @Test
   public void whenTwoSchemasGiveNonConflictingProposals_ShouldListenToBoth () {
      SpeechBehavior speech = new SpeechBehavior("something");
      MenuBehavior menu = new MenuBehavior(new ArrayList<String>());
      GazeBehavior gaze = new GazeBehavior(new Point(10, 10));
      BehaviorMetadata m3 = new BehaviorMetadataBuilder().specificity(1)
            .build();
      BehaviorMetadata m2 = new BehaviorMetadataBuilder().specificity(0.7)
            .build();
      BehaviorMetadata m1 = new BehaviorMetadataBuilder().specificity(0.5)
            .build();
      container
            .changeTo(new CandidateBehavior(Behavior.newInstance(speech, menu),
                  someSchema(), m3),
                  new CandidateBehavior(Behavior.newInstance(gaze),
                        someSchema(), m2), new CandidateBehavior(Behavior.NULL,
                        someSchema(), m1) // caught
            // a bug
            // with
            // this
            );
      arbitrator.run();
      assertBehaviorsRealized(realizer.realizedBehaviors, gaze, speech, menu);
   }

   private void assertBehaviorsRealized (
         List<CompoundBehavior> realizedBehaviors,
         PrimitiveBehavior... behaviors) {
      int n = 0;
      List<PrimitiveBehavior> stillLooking = com.google.common.collect.Lists
            .newArrayList(behaviors);
      for (CompoundBehavior b : realizedBehaviors) {
         if ( !b.getClass().equals(SimpleCompoundBehavior.class) )
            fail("Expected only SimpleCompoundBehaviors");
         SimpleCompoundBehavior s = (SimpleCompoundBehavior) b;
         n += s.getPrimitives().size();
         for (PrimitiveBehavior p : s.getPrimitives()) {
            stillLooking.remove(p);
         }
      }
      assertEquals("More primitives realized than expected", behaviors.length,
            n);
      assertTrue(stillLooking.size() + " behaviors not realized: "
         + stillLooking, stillLooking.size() == 0);
   }

   private Schema someSchema () {
      return new Schema() {

         @Override
         public void run () {
         }
      };
   }

   public static class FakeContainer implements CandidateBehaviorsContainer {

      java.util.List<CandidateBehavior> list = newArrayList();

      public void changeTo (CandidateBehavior... behaviors) {
         ArrayList<CandidateBehavior> l = Lists.newArrayList();
         for (CandidateBehavior b : behaviors)
            l.add(b);
         list = l;
      }

      @Override
      public java.util.List<CandidateBehavior> all () {
         return list;
      }
   }
}
