package edu.wpi.always.test.cm;

import static org.junit.Assert.*;
import com.google.common.base.Function;
import com.google.common.collect.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import org.joda.time.DateTime;
import org.junit.*;
import java.awt.Point;
import java.util.*;
import java.util.concurrent.*;

public class CandidateBehaviorsContainerTest {

   private static final int ANY_PRIORITY = 1;
   CandidateBehaviorsContainerImpl container;
   BehaviorHistory resourceMonitor;

   @Before
   public void setUp () {
      container = new CandidateBehaviorsContainerImpl();
      resourceMonitor = new BehaviorHistory() {

         @Override
         public boolean isDone (CompoundBehavior behavior, DateTime since) {
            // TODO Auto-generated method stub
            return false;
         }
      };
   }

   @Test
   public void testAll_AddRecoverSingleCandidate () {
      DummySchema schema = newDummySchema();
      Behavior behavior = Behavior
            .newInstance(new GazeBehavior(new Point(1, 1)));
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(
            ANY_PRIORITY).build();
      container.add(schema, behavior, m);
      List<CandidateBehavior> candidates = container.all();
      assertContainsOneCandidate(candidates, schema, behavior);
   }

   private DummySchema newDummySchema () {
      return new DummySchema(container, resourceMonitor);
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
      Behavior behavior1 = Behavior.newInstance(new GazeBehavior(
            new Point(1, 1)));
      Behavior behavior2 = Behavior.newInstance(new GazeBehavior(
            new Point(2, 2)));
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(
            ANY_PRIORITY).build();
      container.add(schema, behavior1, m);
      container.add(schema, behavior2, m);
      List<CandidateBehavior> candidates = container.all();
      assertContainsOneCandidate(candidates, schema, behavior2);
   }

   @Test
   public void testAll_ProposingBehaviorsFromDifferentThreads () {
      List<Point> gazePoints = Lists.newArrayList(new Point(1, 1), new Point(2,
            3), new Point(10, 20));
      Random rnd = new Random();
      ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
      List<ScheduledFuture<?>> schemaTasks = Lists.newArrayListWithCapacity(3);
      for (int i = 0; i < gazePoints.size(); i++) {
         FakeGazeSchema schema = new FakeGazeSchema(container, resourceMonitor,
               gazePoints.get(i));
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
         Iterable<Point> pointsFromBehaviors = Iterables.transform(candidates,
               new Function<CandidateBehavior, Point>() {

                  @Override
                  public Point apply (CandidateBehavior cb) {
                     GazeBehavior gazeBehavior = (GazeBehavior) ((SimpleCompoundBehavior) cb
                           .getBehavior().getInner()).getPrimitives().get(0);
                     return gazeBehavior.getPoint();
                  }
               });
         for (Point expected : gazePoints) {
            assertTrue(Iterables.contains(pointsFromBehaviors, expected));
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
