package edu.wpi.always.cm.disco.actions;


import edu.wpi.always.DiscoFunc;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.*;

public class TranslateUtterance implements DiscoFunc<String> {

	private final Utterance utterance;
	private final String formatted;

	public TranslateUtterance (Utterance utterance) {
		this(utterance, null);
	}

	public TranslateUtterance(Utterance utterance, String formatted) {
		this.utterance = utterance;
		this.formatted = formatted;
	}
	
	@Override
	public String execute (Disco disco) {
		if(formatted == null)
			return disco.translate(utterance);
		
		return disco.translate(formatted, utterance);
	}

}
