package edu.wpi.always.test.cm;

import static org.junit.Assert.*;
import com.google.common.collect.Lists;
import edu.wpi.always.cm.DialogContentProvider;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.OldDialogStateMachine;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.realizer.*;
import org.joda.time.DateTime;
import org.junit.*;
import java.util.*;

/* TODO
 */
public class DialogStateMachineFixture {

   private FakeResourceMonitor resourceMonitor;
   private FakeDialogProvider contentProvider;
   private OldDialogStateMachine subject;
   private FakeMenuPerceptor menuPerceptor;

   @Before
   public void setUp () {
      resourceMonitor = new FakeResourceMonitor();
      contentProvider = new FakeDialogProvider();
      menuPerceptor = new FakeMenuPerceptor();
      subject = new OldDialogStateMachine(resourceMonitor, contentProvider,
            menuPerceptor);
   }

   @Test
   public void shouldStartBySay () {
      contentProvider.toSay = "Hello";
      contentProvider.userChoices = Lists.newArrayList("Good", "Aaaaa");
      for (int i = 0; i < 10; i++) {
         Behavior b = subject.build();
         assertIsSpeech(b, "Hello");
      }
   }

   @Test
   public void afterSayIsDone_ShouldReportBackThatItIsDone () {
      contentProvider.toSay = "Hello";
      contentProvider.userChoices = Lists.newArrayList("Good", "Aaaaa");
      subject.build();
      resourceMonitor.done = true;
      subject.build();
      assertEquals("Hello", contentProvider.lastDoneSay);
   }

   @Test
   public void afterSay_ShouldShowUserChoices () {
      contentProvider.toSay = "Good morning";
      contentProvider.userChoices = Lists.newArrayList("Good", "Aaaaa");
      subject.build();
      resourceMonitor.done = true;
      Behavior b = subject.build();
      resourceMonitor.done = false;
      assertIsSpeech(resourceMonitor.primitivesLastChecked, "Good morning");
      for (int i = 0; i < 10; i++) {
         assertIsMenu(b, contentProvider.userChoices);
         if ( i == 5 )
            resourceMonitor.done = true; // should return the menu even after
                                         // they're shown once
         b = subject.build();
      }
   }

   @Test
   public void testBackAndForthTwice () {
      contentProvider.toSay = "Hi";
      contentProvider.userChoices = Lists.newArrayList("Hello", "Good morning");
      Behavior b;
      for (int i = 0; i < 5; i++) {
         b = subject.build();
         assertIsSpeech(b, "Hi");
      }
      resourceMonitor.done = true;
      b = subject.build();
      resourceMonitor.done = false;
      for (int i = 0; i < 10; i++) {
         assertIsMenu(b, contentProvider.userChoices);
         if ( i == 5 )
            resourceMonitor.done = true;
         b = subject.build();
      }
      contentProvider.toSay = "Good to see you!";
      try {
         Thread.sleep(5);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      menuPerceptor.perception = new MenuPerceptionImpl("Hello");
      b = subject.build();
      assertEquals("Hello", contentProvider.lastUserSaid);
      resourceMonitor.done = false;
      for (int i = 0; i < 5; i++) {
         assertIsSpeech(b, "Good to see you!");
         b = subject.build();
      }
      contentProvider.userChoices = Lists.newArrayList("Bet you're happy",
            "Okay");
      resourceMonitor.done = true;
      b = subject.build();
      assertIsMenu(b, contentProvider.userChoices);
      resourceMonitor.done = false;
      b = subject.build();
      assertIsMenu(b, contentProvider.userChoices);
      resourceMonitor.done = true;
      b = subject.build();
      assertIsMenu(b, contentProvider.userChoices);
      try {
         Thread.sleep(5);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      menuPerceptor.perception = new MenuPerceptionImpl("Okay");
      subject.build();
      assertEquals("Okay", contentProvider.lastUserSaid);
   }

   @Test
   public void whenTheChoicesForUserIsNullTriesSay_IfThereIsSomethingToSay_ShouldStayInHearState () {
      whenTheresNoChoiceForUserTriesSay_IfThereIsSomethingToSay_ShouldStayInHearState(true);
   }

   @Test
   public void whenTheresNoChoiceForUserTriesSay_IfThereIsSomethingToSay_ShouldStayInHearState () {
      whenTheresNoChoiceForUserTriesSay_IfThereIsSomethingToSay_ShouldStayInHearState(false);
   }

   private void whenTheresNoChoiceForUserTriesSay_IfThereIsSomethingToSay_ShouldStayInHearState (
         boolean trueForNull_falseForEmptyList) {
      contentProvider.userChoices = trueForNull_falseForEmptyList ? null
         : new ArrayList<String>();
      contentProvider.toSay = "Good";
      contentProvider.toSayNext = "Breecher";
      subject.build();
      resourceMonitor.done = true;
      assertIsSpeech(subject.build(), "Breecher");
   }

   @Test
   public void whenTheresNothingToSay_IfThereAreChoicesForTheUser_ShouldSwitchToHearState () {
      contentProvider.userChoices = Lists.newArrayList("Good", "Great");
      assertIsMenu(subject.build(), contentProvider.userChoices);
   }

   private void assertIsMenu (Behavior b, List<String> userChoices) {
      assertNotNull(b);
      CompoundBehavior primitives = b.getInner();
      assertIsMenu(primitives, userChoices);
   }

   private void assertIsMenu (CompoundBehavior behavior,
         List<String> userChoices) {
      assertNotNull(behavior);
      ArrayList<PrimitiveBehavior> l = new ArrayList<PrimitiveBehavior>();
      l.add(new MenuBehavior(userChoices));
      CompoundBehavior expected = new SimpleCompoundBehavior(l);
      assertEquals(expected, behavior);
   }

   private void assertIsSpeech (Behavior b, String text) {
      assertNotNull(b);
      assertIsSpeech(b.getInner(), text);
   }

   private void assertIsSpeech (CompoundBehavior behavior, String text) {
      assertNotNull(behavior);
      ArrayList<PrimitiveBehavior> l = new ArrayList<PrimitiveBehavior>();
      l.add(new SpeechBehavior(text));
      SimpleCompoundBehavior expected = new SimpleCompoundBehavior(l);
      assertEquals(expected, behavior);
   }

   private static class FakeDialogProvider implements DialogContentProvider {

      public String toSay, toSayNext;
      public List<String> userChoices, userChoicesNext;
      public String lastUserSaid;
      public String lastDoneSay;

      @Override
      public String whatToSay () {
         return toSay;
      }

      @Override
      public List<String> userChoices () {
         return userChoices;
      }

      @Override
      public void userSaid (String text) {
         lastUserSaid = text;
         userChoices = userChoicesNext;
         userChoicesNext = null;
      }

      @Override
      public void doneSaying (String text) {
         lastDoneSay = text;
         toSay = toSayNext;
         toSayNext = null;
      }

      @Override
      public double timeRemaining () {
         return 1;
      }
   }

   private static class FakeResourceMonitor implements BehaviorHistory {

      public boolean done = false;
      public CompoundBehavior primitivesLastChecked;

      @Override
      public boolean isDone (CompoundBehavior primtives, DateTime since) {
         this.primitivesLastChecked = primtives;
         return done;
      }
   }

   private static class FakeMenuPerceptor implements MenuPerceptor {

      public MenuPerception perception;

      @Override
      public MenuPerception getLatest () {
         return perception;
      }

      @Override
      public void run () {
      }
   }
}
