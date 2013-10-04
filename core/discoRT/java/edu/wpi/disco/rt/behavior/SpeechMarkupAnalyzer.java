package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.*;
import java.util.Set;

public interface SpeechMarkupAnalyzer {
   
   /**
    * Analyze the text markup for resources used (in addition to 
    * {@link Resources#SPEECH}). 
    */
   Set<Resource> analyze (String text);

}
