package edu.wpi.always.cm.perceptors.physical.speech;

import java.util.regex.*;

import org.apache.activemq.*;
import org.joda.time.*;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.SpeechPerception.SpeechState;

public class AizulaSpeechPerceptor extends JMSMessagePerceptor<SpeechPerception> implements SpeechPerceptor{

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

	
	public AizulaSpeechPerceptor(){
		super(url, "DEFAULT_SCOPE");
	}
	private static final Pattern MESSAGE_PATTERN = Pattern.compile("blackboard\\sxaizula\\+(\\d+)\\+\\+(\\d+)");
	@Override
	public SpeechPerception handleMessage(String content) {
		Matcher matcher = MESSAGE_PATTERN.matcher(content);
		if(matcher.find()){
			int code = Integer.parseInt(matcher.group(1));
			switch(code){
			case 10:
				return new SpeechPerceptionImpl(DateTime.now(), SpeechState.Silent);
			case 11:
				return new SpeechPerceptionImpl(DateTime.now(), SpeechState.Normal);
			}
		}
		return getLatest();
	}
}
