package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import com.google.common.base.Function;
import com.google.common.collect.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.test.*;
import org.joda.time.DateTime;
import org.junit.*;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.*;

public class CandidateBehaviorsContainerTest {

   private static final int ANY_PRIORITY = 1;
   CandidateBehaviorsContainer container;
   BehaviorHistory behaviorHistory;

   @Before
   public void setUp () {
      container = new CandidateBehaviorsContainer();
      behaviorHistory = new BehaviorHistory() {

         @Override
         public boolean isDone (CompoundBehavior behavior, long since) {
            // TODO Auto-generated method stub
            return false;
         }
      };
   }

   @Test
   public void testAll_AddRecoverSingleCandidate () {
      DummySchema schema = newDummySchema();
      Behavior behavior = Behavior.newInstance(MenuBehavior.EMPTY);
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(
            ANY_PRIORITY).build();
      container.add(schema, behavior, m);
      List<CandidateBehavior> candidates = container.all();
      assertContainsOneCandidate(candidates, schema, behavior);
   }

   private DummySchema newDummySchema () {
      return new DummySchema(container, behaviorHistory);
   }

   private void assertContainsOneCandidate (List<CandidateBehavior> candidates,
         DummySchema schema, Behavior behavior) {
      assertEquals(1, candidates.size());
      assertSame(schema, candidates.get(0).getProposer());
      assertEquals(behavior, candidates.get(0).getBehavior());
   }

   @Test
   public void testAll_AddTwiceFromTheSameSchema_ShouldIndluceOnlyLastOne () {
      DummySchema schema = newDummySchema();
      Behavior behavior1 = Behavior.newInstance(new SpeechBehavior("Hi"));
      Behavior behavior2 = Behavior.newInstance(new SpeechBehavior("Ho"));     
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(
            ANY_PRIORITY).build();
      container.add(schema, behavior1, m);
      container.add(schema, behavior2, m);
      List<CandidateBehavior> candidates = container.all();
      assertContainsOneCandidate(candidates, schema, behavior2);
   }

   @Test
   public void testAll_ProposingBehaviorsFromDifferentThreads () {
      List<String> texts = Lists.newArrayList("One", "Two", "Three");
      Random rnd = new Random();
      ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
      List<ScheduledFuture<?>> schemaTasks = Lists.newArrayListWithCapacity(3);
      for (int i = 0; i < texts.size(); i++) {
         FakeSpeechSchema schema = new FakeSpeechSchema(container, behaviorHistory,
               texts.get(i));
         int period = rnd.nextInt(201) + 100;
         ScheduledFuture<?> future = executor.scheduleAtFixedRate(schema, 0,
               period, TimeUnit.MILLISECONDS);
         schemaTasks.add(future);
      }
      sleep(600);
      for (int i = 0; i < 7; i++) {
         // making sure schemas are still running
         for (int j = 0; j < schemaTasks.size(); j++)
            assertFalse(schemaTasks.get(j).isDone());
         List<CandidateBehavior> candidates = container.all();
         assertEquals(3, candidates.size());
         Iterable<String> textsFromBehaviors = Iterables.transform(candidates,
               new Function<CandidateBehavior, String>() {

                  @Override
                  public String apply (CandidateBehavior cb) {
                     SpeechBehavior speechBehavior = (SpeechBehavior) ((SimpleCompoundBehavior) cb
                           .getBehavior().getInner()).getPrimitives().get(0);
                     return speechBehavior.getText();
                  }
               });
         for (String expected : texts) {
            assertTrue(Iterables.contains(textsFromBehaviors, expected));
         }
         sleep(200);
      }
      executor.shutdownNow();
   }

   private void sleep (int millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      }
   }
}
