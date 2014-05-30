package edu.wpi.always.cm.perceptors;

import edu.wpi.always.Always;
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
      latest = new EngagementPerception(EngagementState.Idle);
   }

   private final Object stateLock = new Object();

   @Override
   public void run () {
      synchronized (stateLock) {
         EngagementState currentState = null;
         if ( latest != null ) currentState = latest.getState();
         if ( currentState == null ) currentState = EngagementState.Idle;
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
         if ( lastMovementChange == null || lastMovementChange.isMoving != isMoving )	
            lastMovementChange = new MovementTransition(isMoving);
         MenuPerception menuPerception = menuPerceptor.getLatest();
         boolean hadTouch = false;
         if ( menuPerception != null && lastTouchChange != null ) {
            if ( !menuPerception.getSelected().equals(lastTouchChange.selected) 
                  || menuPerception.getTimeStamp() > lastTouchChange.timeStamp ) {
               lastTouchChange = new TouchTransition(menuPerception);
               hadTouch = true;
            }
         } else
            lastTouchChange = new TouchTransition();
         EngagementState nextState = currentState.nextState(lastMovementChange,
               lastFaceChange, lastTouchChange, hadTouch,
               System.currentTimeMillis() - lastStateChangeTime);
         if ( nextState != currentState ) setState(nextState);
      }
   }

   public void setEngaged (boolean engaged) {
      synchronized (stateLock) {
         if ( engaged )
            setState(EngagementState.Engaged);
         else
            setState(EngagementState.Idle);
      }
   }

   protected void setState (EngagementState newState) {
      synchronized (stateLock) {
         if ( Always.TRACE ) Utils.lnprint(System.out, "ENGAGEMENT: "
            + (latest != null ? latest.getState() : "") + " -> " + newState);
         // reset timing for new state
         if ( lastMovementChange != null )
            lastMovementChange = new MovementTransition(
                  lastMovementChange.isMoving);
         if ( lastFaceChange != null )
            lastFaceChange = new FaceTransition(lastFaceChange.isFace,
                  lastFaceChange.isNear);
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
      
      final String selected;
      final long timeStamp;  // to distinguish same menu put up twice (for recovery)

      public TouchTransition () {
         selected = null;
         timeStamp = 0L;
      }
      
      public TouchTransition (MenuPerception menu) {
         this.selected = menu.getSelected();
         this.timeStamp = menu.getTimeStamp();
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
