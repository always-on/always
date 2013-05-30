package edu.wpi.always.cm.primitives;

import edu.wpi.always.client.AgentFaceExpression;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import java.awt.Point;

public class AgentResources extends Resources {

   /**
    * Resource for agent's gaze.
    * 
    * @see GazeBehavior
    */
   public static final Resource GAZE = new Resource() {
      @Override
      public String toString() { return "GAZE"; }
   };      

   /**
    * Resource for agent's facial expression.
    * 
    * @see FaceExpressionBehavior
    */
   public static final Resource FACE_EXPRESSION = new Resource() {
      @Override
      public String toString() { return "FACE_EXPRESSION"; }
   };      
   
   /**
    * Resource for agent's display.
    * 
    * @see FaceExpressionBehavior
    */
   public static final Resource DISPLAY = new Resource() {
      @Override
      public String toString() { return "DISPLAY"; }
   };   
   
   /**
    * Plugin-specific resource for agent's hand.
    */
   public static final Resource HAND = new Resource() {
      @Override
      public String toString() { return "HAND"; }
   };     
   
   /**
    * Resource for agent's idling. ??????
    * 
    * @see ??IdleBehavior??
    */
   public static final Resource IDLE = new Resource() {
      @Override
      public String toString() { return "IDLE"; }
   };      
      
   static { Resources.values = 
      new Resource[] { 
      Resources.FOCUS, Resources.SPEECH, Resources.MENU, Resources.MENU_EXTENSION, 
      GAZE, FACE_EXPRESSION, HAND, DISPLAY, IDLE }; 
   }
  
   @Override
   public PrimitiveBehavior getIdleBehavior (Resource resource) {
      if ( resource == DISPLAY ) System.out.println("***TODO*** getIdleBehavior(DISPLAY)");
      PrimitiveBehavior idle = super.getIdleBehavior(resource);
      return idle != null ? idle :
         resource == FACE_EXPRESSION ?  new FaceExpressionBehavior(AgentFaceExpression.Warm) :
            resource == GAZE ?  new GazeBehavior(new Point(0, 0)) :
               resource == IDLE ? new IdleBehavior(true) : 
                  resource == DISPLAY ? null : // *****
                  null;
   }
}
