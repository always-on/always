package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;
import org.joda.time.DateTime;
import org.junit.*;
import java.awt.Point;
import java.util.ArrayList;

// TODO Ignore primitives that use resources other than those in behavior

public class ResourceMonitorImplFixture {

   private PrimitiveBehaviorControlStub realizer;
   private ResourceMonitor resMon;

   @Before
   public void setUp () {
      realizer = new PrimitiveBehaviorControlStub();
      resMon = new ResourceMonitor(realizer);
   }

   @Test
   public void testBehaviorWithOnePrimitive () {
      assertNotNull(realizer.observer);
      long someEarlierTime = System.currentTimeMillis()-10000;
      realizer.observer
            .primitiveDone(realizer, new SpeechBehavior("something"));
      ArrayList<PrimitiveBehavior> primitives = new ArrayList<PrimitiveBehavior>();
      primitives.add(new SpeechBehavior("something"));
      assertTrue(resMon.allDone(primitives, someEarlierTime));
      long someLaterTime = System.currentTimeMillis()+10000;
      assertFalse(resMon.allDone(primitives, someLaterTime));
   }

   @Test
   public void testWithOnePrimitive_OnePrimitiveDoneWithDifferentParameterShouldNotCount () {
      long someEarlierTime = System.currentTimeMillis()-1000;
      realizer.observer
            .primitiveDone(realizer, new SpeechBehavior("something"));
      ArrayList<PrimitiveBehavior> primitives = new ArrayList<PrimitiveBehavior>();
      primitives.add(new SpeechBehavior("something else"));
      assertFalse(resMon.allDone(primitives, someEarlierTime));
   }

   @Test
   public void testWithTwoPrimitives () {
      long someEarlierTime = System.currentTimeMillis()-1000;
      ArrayList<PrimitiveBehavior> primitives = new ArrayList<PrimitiveBehavior>();
      primitives.add(new SpeechBehavior("Good"));
      primitives.add(MenuBehavior.EMPTY);
      assertFalse(resMon.allDone(primitives, someEarlierTime));
      realizer.observer.primitiveDone(realizer, new SpeechBehavior("Good"));
      assertFalse(resMon.allDone(primitives, someEarlierTime));
      realizer.observer.primitiveDone(realizer, MenuBehavior.EMPTY);
      assertTrue(resMon.allDone(primitives, someEarlierTime));
   }

   @Test
   public void primtivesShouldRunWithNoOtherPrimitivesUsingSameResourcesInBetween () {
      long someEarlierTime = System.currentTimeMillis()-1000;
      ArrayList<PrimitiveBehavior> primitives = new ArrayList<PrimitiveBehavior>();
      primitives.add(new SpeechBehavior("Good"));
      primitives.add(MenuBehavior.EMPTY);
      realizer.observer.primitiveDone(realizer, new SpeechBehavior("Good"));
      realizer.observer.primitiveDone(realizer, new SpeechBehavior(
            "something else"));
      realizer.observer.primitiveDone(realizer, MenuBehavior.EMPTY);
      assertFalse(resMon.allDone(primitives, someEarlierTime));
   }

   @Test
   public void whenPrimitivesThatAreDoneTheSame_ShouldBeOk () {
      long someEarlierTime = System.currentTimeMillis()-1000;
      ArrayList<PrimitiveBehavior> primitives = new ArrayList<PrimitiveBehavior>();
      primitives.add(new SpeechBehavior("Good"));
      primitives.add(MenuBehavior.EMPTY);
      realizer.observer.primitiveDone(realizer, MenuBehavior.EMPTY);
      realizer.observer.primitiveDone(realizer, new SpeechBehavior("Good"));
      realizer.observer.primitiveDone(realizer, new SpeechBehavior("Good"));
      assertTrue(resMon.allDone(primitives, someEarlierTime));
   }
}
