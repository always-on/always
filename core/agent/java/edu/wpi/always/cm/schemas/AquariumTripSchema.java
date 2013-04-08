package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.primitives.SpeechBehavior;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import org.joda.time.*;
import org.joda.time.format.*;
import javax.swing.JTextField;

// FIXME This is for testing only--should be moved.

public class AquariumTripSchema extends SchemaBase {

   private final JTextField fakeTimeTextBox;

   public AquariumTripSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, JTextField fakeTimeTextBox) {
      super(behaviorReceiver, resourceMonitor);
      this.fakeTimeTextBox = fakeTimeTextBox;
      setNeedsFocusResouce();
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
