package edu.wpi.always.test.cm;

import static org.junit.Assert.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.*;
import org.junit.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrimitiveBehaviorControlImplFixture {

   private static final int SOMETHING = 1;
   private PrimitiveRealizerFactory factory;
   private PrimitiveBehaviorManager realizer;

   @Before
   public void setUp () {
      factory = new FakeRealizerFactory();
      IdleBehaviors np = new IdleBehaviors() {

         @Override
         public PrimitiveBehavior get (Resource resource) {
            return null;
         }
      };
      realizer = new PrimitiveBehaviorManager(factory, np);
   }

   @Test
   public void onePrimitive () {
      DummyPrimitive gazePrimitive = new DummyPrimitive(AgentResources.GAZE,
            SOMETHING);
      realizer.realize(gazePrimitive);
      assertNotNull(realizer.currentRealizerFor(AgentResources.GAZE));
      assertEquals(gazePrimitive, realizer.currentRealizerFor(AgentResources.GAZE)
            .getParams());
      DummyPrimitiveRealizer primitiveRealizer = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      sleep(50);
      assertTrue(primitiveRealizer.runCallsCount > 0);
      assertNull(realizer.currentRealizerFor(AgentResources.SPEECH));
   }

   @Test
   public void primitiveRealizersAreScheduleOnSeparateThreads () {
      realizer.realize(new DummyPrimitive(AgentResources.GAZE, SOMETHING));
      realizer.realize(new DummyPrimitive(AgentResources.SPEECH, SOMETHING));
      DummyPrimitiveRealizer gazeRealizer = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      DummyPrimitiveRealizer speechRealizer = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.SPEECH);
      // wait for their run() function to be called 3 times
      int n = 0;
      while (gazeRealizer.runCallsCount < 3 && speechRealizer.runCallsCount < 3) {
         sleep(100);
         assertFalse("realizer should not have been called on current thread",
               gazeRealizer.runThreadId == Thread.currentThread().getId());
         assertFalse("realizer should not have been called on current thread",
               speechRealizer.runThreadId == Thread.currentThread().getId());
         n++;
         if ( n > 10 )
            fail("test timed out without run() functions being called three times");
      }
   }

   @Test
   public void whenANewBehaviorForAResourceIsGiven_ShouldShutDownTheOneAlreadyRunningOnThatResource () {
      final int paramForFirstBehavior = 1;
      final int paramForSecondBehavior = 2;
      realizer
            .realize(new DummyPrimitive(AgentResources.GAZE, paramForFirstBehavior));
      DummyPrimitiveRealizer r1 = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      int n = 0;
      while (r1.runCallsCount == 0) {
         sleep(20);
         n++;
         if ( n > 5 ) {
            fail("ASSUMPTION error that the behavior I just added would be called! There is something wrong somewhere else");
            return;
         }
      }
      realizer
            .realize(new DummyPrimitive(AgentResources.GAZE, paramForSecondBehavior));
      DummyPrimitiveRealizer r2 = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      assertNotSame(r1, r2);
      assertFalse(r1.getParams().equals(r2.getParams()));
      // now making sure that r2 is running and r1 is stopped
      int r1CountSaved = r1.runCallsCount;
      n = 0;
      while (r2.runCallsCount <= 3) {
         sleep(PrimitiveBehaviorManager.REALIZERS_INTERVAL);
         n++;
         if ( n > 10 )
            fail("timeout in waiting for r2 to be called 4 times");
         assertTrue("looks like r1 is still running",
               r1.runCallsCount == r1CountSaved);
      }
   }

   @Test
   public void whenOnePrimitiveIsDone_ShouldFireDoneEvent () {
      FakeRealizerEventObserver observer = addObserverToRealizer();
      final int DOES_NOT_MATTER = 1;
      DummyPrimitive b1 = new DummyPrimitive(AgentResources.GAZE, DOES_NOT_MATTER);
      realizer.realize(b1);
      sleep(10);
      assertFalse(observer.doneReceivedFor(b1));
      ((DummyPrimitiveRealizer) realizer.currentRealizerFor(AgentResources.GAZE))
            .done();
      assertTrue(observer.doneReceivedFor(b1));
   }

   @Test
   public void testStopResource () {
      DummyPrimitive behavior = new DummyPrimitive(AgentResources.GAZE, SOMETHING);
      realizer.realize(behavior);
      DummyPrimitiveRealizer r = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      sleep(10);
      realizer.stop(AgentResources.GAZE);
      assertNotRunning(r);
   }

   @Test
   public void whenNullPrimitiveBehaviorIsSetForBehavior_ItShouldStopCurrentRealizerOnThatResource () {
      DummyPrimitive behavior = new DummyPrimitive(AgentResources.GAZE, SOMETHING);
      realizer.realize(behavior);
      DummyPrimitiveRealizer r = (DummyPrimitiveRealizer) realizer
            .currentRealizerFor(AgentResources.GAZE);
      realizer.realize(PrimitiveBehavior.nullBehavior(AgentResources.GAZE));
      assertNotRunning(r);
   }

   @Test
   public void whenARealizerForTheSameBehaviorIsInEffect_ShouldNotStopIt () {
      DummyPrimitive b1 = new DummyPrimitive(AgentResources.GAZE, SOMETHING);
      DummyPrimitive b2 = new DummyPrimitive(AgentResources.GAZE, SOMETHING);
      realizer.realize(b1);
      PrimitiveRealizer<?> firstRealizer = realizer
            .currentRealizerFor(AgentResources.GAZE);
      realizer.realize(b2);
      PrimitiveRealizer<?> secondRealizer = realizer
            .currentRealizerFor(AgentResources.GAZE);
      assertSame(firstRealizer, secondRealizer);
   }

   private FakeRealizerEventObserver addObserverToRealizer () {
      FakeRealizerEventObserver observer = new FakeRealizerEventObserver();
      realizer.addObserver(observer);
      return observer;
   }

   /**
    * very simplistic, I know ;)
    * 
    * @param r
    */
   private void assertNotRunning (DummyPrimitiveRealizer r) {
      int savedCount = r.runCallsCount;
      sleep(PrimitiveBehaviorManager.REALIZERS_INTERVAL * 2);
      assertEquals(savedCount, r.runCallsCount);
   }

   private void sleep (int milliSeconds) {
      try {
         Thread.sleep(milliSeconds);
      } catch (InterruptedException ex) {
      }
   }

   static class FakeRealizerEventObserver implements
         PrimitiveBehaviorControlObserver {

      public List<PrimitiveBehavior> done = Collections
            .synchronizedList(new ArrayList<PrimitiveBehavior>());

      @Override
      public void primitiveDone (PrimitiveBehaviorControl sender,
            PrimitiveBehavior pb) {
         done.add(pb);
      }

      public boolean doneReceivedFor (PrimitiveBehavior pb) {
         return done.contains(pb);
      }

      @Override
      public void primitiveStopped (PrimitiveBehaviorControl sender,
            PrimitiveBehavior pb) {
      }
   }

   static class FakeRealizerFactory implements PrimitiveRealizerFactory {

      @Override
      public PrimitiveRealizer<?> create (PrimitiveBehavior primitiveBehavior) {
         if ( DummyPrimitive.class.isAssignableFrom(primitiveBehavior
               .getClass()) ) {
            return new DummyPrimitiveRealizer(
                  (DummyPrimitive) primitiveBehavior);
         }
         return null;
      }
   }

   static class DummyPrimitiveRealizer implements
         PrimitiveRealizer<DummyPrimitive> {

      private final DummyPrimitive params;
      public int runCallsCount = 0;
      public long runThreadId = -1;
      List<PrimitiveRealizerObserver> observers = new CopyOnWriteArrayList<PrimitiveRealizerObserver>();

      public DummyPrimitiveRealizer (DummyPrimitive params) {
         this.params = params;
      }

      @Override
      public DummyPrimitive getParams () {
         return params;
      }

      @Override
      public void run () {
         runCallsCount++;
         runThreadId = Thread.currentThread().getId();
      }

      @Override
      public void addObserver (PrimitiveRealizerObserver observer) {
         observers.add(observer);
      }

      @Override
      public void removeObserver (PrimitiveRealizerObserver observer) {
         observers.remove(observer);
      }

      public void done () {
         for (PrimitiveRealizerObserver o : observers)
            o.prmitiveRealizerDone(this);
      }

      @Override
      public void shutdown () {
      }
   }
}
