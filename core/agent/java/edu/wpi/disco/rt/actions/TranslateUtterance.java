package edu.wpi.disco.rt.actions;


import edu.wpi.disco.*;
import edu.wpi.disco.lang.*;
import edu.wpi.disco.rt.DiscoFunc;

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
