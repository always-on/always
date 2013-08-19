package edu.wpi.always.test;

import javax.swing.JTextField;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import edu.wpi.disco.rt.behavior.BehaviorMetadataBuilder;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
import edu.wpi.disco.rt.behavior.SpeechBehavior;
import edu.wpi.disco.rt.schema.SchemaBase;

// FIXME This is for testing only--should be moved.

public class AquariumTripSchema extends SchemaBase {

   private final JTextField fakeTimeTextBox;

   public AquariumTripSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, JTextField fakeTimeTextBox) {
      super(behaviorReceiver, resourceMonitor);
      this.fakeTimeTextBox = fakeTimeTextBox;
   }

   @Override
   public void run () {
      DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2)
            .toFormatter();
      DateTime dt;
      try {
         dt = DateTime.parse(fakeTimeTextBox.getText(), formatter);
      } catch (IllegalArgumentException ex) {
         proposeNothing();
         return;
      }
      DateTime now = DateTime.parse(DateTime.now().toString(formatter),
            formatter);
      int min = Minutes.minutesBetween(now, dt).getMinutes();
      if ( min < 20 ) {
         BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.9)
               .dueIn(min).timeRemaining(30).build();
         propose(new SpeechBehavior(
               "I think you should go to the lobby for the aquarium trip"), m);
      } else
         proposeNothing();
   }
}
