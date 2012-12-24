package edu.wpi.always.test.cm;

import static org.junit.Assert.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.primitives.console.ConsoleSpeechRealizer;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import org.junit.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;

public class SchemaFeedbacksFixture {

   private MutablePicoContainer pico;
   private FakeSchema schemaUnderTest;
   private HigherPriorityDummySchema otherSchema;
   private Arbitrator arbitrator;

   @Before
   public void setUp () {
      pico = configureContainer();
      schemaUnderTest = pico.getComponent(FakeSchema.class);
      otherSchema = pico.getComponent(HigherPriorityDummySchema.class);
      arbitrator = pico.getComponent(Arbitrator.class);
   }

   @Test
   public void testIsLastProposalDone () {
      for (int i = 0; i < 2; i++) {
         singleRun();
         sleep(40);
         assertFalse(schemaUnderTest.isDone());
      }
      otherSchema.disable();
      singleRun();
      for (int i = 0; i < 10; i++) {
         sleep(20);
         if ( schemaUnderTest.isDone() )
            return; // assert success
      }
      fail("expected isDone() on schemaUnderTest to be true by now");
   }

   private void singleRun () {
      otherSchema.run();
      schemaUnderTest.run();
      arbitrator.run();
   }

   private void sleep (int millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   private MutablePicoContainer configureContainer () {
      MutablePicoContainer pico = new DefaultPicoContainer(new OptInCaching());
      pico.as(Characteristics.CACHE).addComponent(
            PrimitiveBehaviorControlImpl.class);
      pico.as(Characteristics.CACHE).addComponent(RealizerImpl.class);
      pico.as(Characteristics.CACHE).addComponent(FakeSchema.class);
      pico.as(Characteristics.CACHE).addComponent(
            HigherPriorityDummySchema.class);
      pico.as(Characteristics.CACHE).addComponent(
            CandidateBehaviorsContainerImpl.class);
      pico.addComponent(FuzzyArbitrationStrategy.class);
      pico.as(Characteristics.CACHE).addComponent(Arbitrator.class);
      pico.addComponent(ResourceMonitorImpl.class);
      pico.addComponent(new IdleBehaviors() {

         @Override
         public PrimitiveBehavior get (Resource resource) {
            return null;
         }
      });
      pico.addComponent(pico);
      PrimitiveRealizerFactoryImpl realizerFactory = new PrimitiveRealizerFactoryImpl(
            pico);
      pico.addComponent(realizerFactory);
      realizerFactory.register(ConsoleSpeechRealizer.class);
      return pico;
   }

   public static class FakeSchema extends SchemaImplBase {

      private boolean done = false;

      public FakeSchema (BehaviorProposalReceiver behaviorReceiver,
            BehaviorHistory resourceMonitor) {
         super(behaviorReceiver, resourceMonitor);
      }

      @Override
      public void run () {
         if ( lastProposalIsDone() ) {
            done = true;
            proposeNothing();
         } else {
            propose(new SpeechBehavior("Hi"), NORMAL_PRIORITY);
         }
      }

      public boolean isDone () {
         return done || lastProposalIsDone();
      }
   }

   public static class HigherPriorityDummySchema extends SchemaImplBase {

      public HigherPriorityDummySchema (
            BehaviorProposalReceiver behaviorReceiver,
            BehaviorHistory resourceMonitor) {
         super(behaviorReceiver, resourceMonitor);
      }

      private boolean enabled = true;

      public void disable () {
         enabled = false;
      }

      @Override
      public void run () {
         if ( enabled )
            propose(new SpeechBehavior("This is not good"), NORMAL_PRIORITY + 1);
         else
            proposeNothing();
      }
   }
}
