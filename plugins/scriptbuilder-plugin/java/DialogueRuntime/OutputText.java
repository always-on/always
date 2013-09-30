package DialogueRuntime;

import java.util.Arrays;

/* Captures input and output to BEAT for generating transcripts. */

public class OutputText {
	private final String source;
	private final String output;
	
	public OutputText(String source, String output) {
		this.source = source;
		this.output = output;
	}

	public OutputText(String source) {
		this.source = source;
		this.output = source;
	}

	public String getSource() {
		return source;
	}
	
	public String getOutput() { 
		return output;
	}
	
	
}
