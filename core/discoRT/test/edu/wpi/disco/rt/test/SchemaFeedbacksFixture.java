package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.SchemaBase;
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
      pico.as(Characteristics.CACHE).addComponent(new DiscoRT());
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
            PrimitiveBehaviorManager.class);
      pico.as(Characteristics.CACHE).addComponent(Realizer.class);
      pico.as(Characteristics.CACHE).addComponent(FakeSchema.class);
      pico.as(Characteristics.CACHE).addComponent(
            HigherPriorityDummySchema.class);
      pico.as(Characteristics.CACHE).addComponent(
            CandidateBehaviorsContainer.class);
      pico.addComponent(FuzzyArbitrationStrategy.class);
      pico.as(Characteristics.CACHE).addComponent(Arbitrator.class);
      pico.addComponent(ResourceMonitor.class);
      pico.addComponent(new Resources());
      pico.addComponent(pico);
      PrimitiveRealizerFactory realizerFactory = new PrimitiveRealizerFactory(
            pico);
      pico.addComponent(realizerFactory);
      realizerFactory.register(FakeSpeechRealizer.class);
      return pico;
   }

   public static class FakeSchema extends SchemaBase {

      private boolean done = false;

      public FakeSchema (BehaviorProposalReceiver behaviorReceiver,
            BehaviorHistory behaviorHistory) {
         super(behaviorReceiver, behaviorHistory);
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

      @Override
      public boolean isDone () {
         return done || lastProposalIsDone();
      }
   }

   public static class HigherPriorityDummySchema extends SchemaBase {

      public HigherPriorityDummySchema (
            BehaviorProposalReceiver behaviorReceiver,
            BehaviorHistory behaviorHistory) {
         super(behaviorReceiver, behaviorHistory);
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
   
   public static class FakeSpeechRealizer extends
      SingleRunPrimitiveRealizer<SpeechBehavior> {

      public FakeSpeechRealizer (SpeechBehavior params) { super(params); }

      @Override
      protected void singleRun () {
         System.out.println("Saying: " + getParams().getText());
         fireDoneMessage();
      }
   }
}
