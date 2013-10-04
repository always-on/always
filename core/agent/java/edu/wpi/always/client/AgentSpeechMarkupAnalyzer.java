package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.AgentResources;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.SpeechMarkupAnalyzer;
import java.util.*;

public class AgentSpeechMarkupAnalyzer implements SpeechMarkupAnalyzer {

  @Override
  public Set<Resource> analyze (String text) {
     // quick and dirty for RAG client (e.g., not handling quotation)
     Set<Resource> resources = Collections.emptySet();
     if ( text.contains("<GAZE") || text.contains("<HEADNOD")  ) {
        if ( resources.isEmpty() ) resources = new HashSet<Resource>();
        resources.add(AgentResources.GAZE);
     }
     if ( text.contains("<FACE") || text.contains("<EYEBROWS") ) {
        if ( resources.isEmpty() ) resources = new HashSet<Resource>();
        resources.add(AgentResources.FACE_EXPRESSION);
     }
     if ( text.contains("<GESTURE") ) {
        if ( resources.isEmpty() ) resources = new HashSet<Resource>();
        resources.add(AgentResources.HAND);
     }
     return resources;
  }
  
}
