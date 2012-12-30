package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.*;

public enum AgentResources implements Resource {
   GAZE, SPEECH, MENU, HAND, FACE_EXPRESSION, IDLE;
   
   static { Resources.values = 
      new Resource[] { Resources.FOCUS, GAZE, SPEECH, MENU, HAND, FACE_EXPRESSION, IDLE }; }
  
}
