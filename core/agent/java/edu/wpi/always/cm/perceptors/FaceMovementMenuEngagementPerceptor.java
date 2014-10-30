package edu.wpi.always.cm.perceptors;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.perceptor.PerceptorBase;
import edu.wpi.disco.rt.util.Utils;

public class FaceMovementMenuEngagementPerceptor 
             extends PerceptorBase<EngagementPerception>
             implements EngagementPerceptor {

   private final FacePerceptor facePerceptor;
   private final MovementPerceptor movementPerceptor;
   private final MenuPerceptor menuPerceptor;
   
   private MovementTransition lastMovementChange;
   private FaceTransition lastFaceChange;
   private TouchTransition lastTouchChange;
   private long lastStateChangeTime = System.currentTimeMillis();

   public FaceMovementMenuEngagementPerceptor (FacePerceptor facePerceptor,
         MovementPerceptor movementPerceptor, MenuPerceptor menuPerceptor) {
      this.facePerceptor = facePerceptor;
      this.movementPerceptor = movementPerceptor;
      this.menuPerceptor = menuPerceptor;
      latest = new EngagementPerception(EngagementState.IDLE);
   }

   private final Object stateLock = new Object();
   
   @Override
   public void run () {
      synchronized (stateLock) {
         EngagementState currentState = null;
         if ( latest != null ) currentState = latest.getState();
         if ( currentState == null ) currentState = EngagementState.IDLE;
         FacePerception facePerception = facePerceptor.getLatest();
         boolean isFace = false, isNear = false;
         if ( facePerception != null ) {
            isFace = facePerception.isFace();
            isNear = facePerception.isNear();
         }
         if ( lastFaceChange == null
               || lastFaceChange.isFace != isFace
               || lastFaceChange.isNear != isNear )
            lastFaceChange = new FaceTransition(isFace, isNear);
         MovementPerception movementPerception = movementPerceptor.getLatest();
         boolean isMoving = movementPerception != null && movementPerception.isMoving();
         if ( lastMovementChange == null 
               || lastMovementChange.isMoving != isMoving )	
            lastMovementChange = new MovementTransition(isMoving);
         MenuPerception menuPerception = menuPerceptor.getLatest();
         boolean hadTouch = false;
         long timeStamp = 0L;
         if ( menuPerception != null ) 
            timeStamp = menuPerception.getTimeStamp();
         if ( lastTouchChange == null 
               || lastTouchChange.timeStamp < timeStamp ) {
            lastTouchChange = new TouchTransition(timeStamp);
            if ( menuPerception != null ) hadTouch = true;
         }
         EngagementState nextState = currentState.nextState(lastMovementChange,
               lastFaceChange, lastTouchChange, hadTouch, 
               System.currentTimeMillis() - lastStateChangeTime);
         if ( nextState != currentState ) setState(nextState);
      }
   }

   private void setState (EngagementState newState) {
      synchronized (stateLock) {
         if ( latest != null ) {
            EngagementState oldState = latest.getState();
            if ( Always.TRACE ) Utils.lnprint(System.out, "ENGAGEMENT: "
                  + oldState + " -> " + newState);
            Logger.THIS.logEngagement(oldState, newState);
         }
         // reset timing for new state
         if ( lastMovementChange != null ) {
            lastMovementChange = new MovementTransition(lastMovementChange.isMoving);
            if ( Always.TRACE ) System.out.print(" (Movement)");
         }
         if ( lastFaceChange != null ) {
            lastFaceChange = new FaceTransition(lastFaceChange.isFace, lastFaceChange.isNear);
            if ( Always.TRACE ) System.out.print(" (Face)");
         }
         if ( lastTouchChange != null ) {
            lastTouchChange = new TouchTransition(lastTouchChange.timeStamp);
            System.out.print(" (Touch)");
         }
         lastStateChangeTime = System.currentTimeMillis();
         latest = new EngagementPerception(newState);
      }
   }
     
   private static abstract class Transition {
      
      private final long changeTime;
      
      protected Transition () { changeTime = System.currentTimeMillis(); }
      
      public long timeSinceChange () { return System.currentTimeMillis() - changeTime; }
      
   }
   static class TouchTransition extends Transition {
      
      final long timeStamp;
   
      public TouchTransition (long timeStamp) {
         this.timeStamp = timeStamp;
      }
   }

   static class MovementTransition extends Transition {

      final boolean isMoving;

      public MovementTransition (boolean isMoving) {
         this.isMoving = isMoving;
      }
   }

   static class FaceTransition extends Transition {

      final boolean isFace, isNear;

      public FaceTransition (boolean isFace, boolean isNear) {
         this.isFace = isFace;
         this.isNear = isNear;
      }
   }
}
