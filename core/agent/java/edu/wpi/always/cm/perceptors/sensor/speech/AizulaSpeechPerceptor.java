package edu.wpi.always.cm.perceptors.sensor.speech;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.activemq.ActiveMQConnection;
import org.joda.time.DateTime;

import edu.wpi.always.cm.perceptors.SpeechPerception;
import edu.wpi.always.cm.perceptors.SpeechPerception.SpeechState;
import edu.wpi.always.cm.perceptors.SpeechPerceptor;

public class AizulaSpeechPerceptor extends
      JMSMessagePerceptor<SpeechPerception> implements SpeechPerceptor {

   private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

   public AizulaSpeechPerceptor () {
      super(url, "DEFAULT_SCOPE");
   }

   private static final Pattern MESSAGE_PATTERN = Pattern
         .compile("blackboard\\sxaizula\\+(\\d+)\\+\\+(\\d+)");

   @Override
   public SpeechPerception handleMessage (String content) {
      Matcher matcher = MESSAGE_PATTERN.matcher(content);
      if ( matcher.find() ) {
         int code = Integer.parseInt(matcher.group(1));
         switch (code) {
         case 10:
            return new SpeechPerception(SpeechState.Silent);
         case 11:
            return new SpeechPerception(SpeechState.Normal);
         }
      }
      return getLatest();
   }
}
