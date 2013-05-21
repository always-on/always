package edu.wpi.always.cm.perceptors;

import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.disco.rt.menu.*;

// TODO Respond to touches elsewhere on screen than menu

public class FaceMovementMenuEngagementPerceptor implements EngagementPerceptor {

   private volatile EngagementPerception latest;
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
   }

   private final Object stateLock = new Object();

   @Override
   public void run () {
      synchronized (stateLock) {
         EngagementState currentState = null;
         if ( latest != null )
            currentState = latest.getState();
         if ( currentState == null )
            currentState = EngagementState.Idle;
         FacePerception facePerception = facePerceptor.getLatest();
         if ( facePerception != null ) {
            if ( lastFaceChange == null
                  || lastFaceChange.isFace != facePerception.isFace()
                  || lastFaceChange.isNear != facePerception.isNear() )
               lastFaceChange = new FaceTransition(facePerception.isFace(),
                     facePerception.isNear());
         } else
            lastFaceChange = new FaceTransition(false, false);
         MovementPerception movementPerception = movementPerceptor.getLatest();
         if ( movementPerception != null ) {
            if ( lastMovementChange == null
               || lastMovementChange.isMoving != movementPerception.isMoving() )
               lastMovementChange = new MovementTransition(
                     movementPerception.isMoving());
         } else
            lastMovementChange = new MovementTransition(false);
         boolean hadTouch = false;
         MenuPerception menuPerception = menuPerceptor.getLatest();
         if ( menuPerception != null && lastTouchChange != null ) {
            if ( !menuPerception.getSelected().equals(
                  lastTouchChange.selected) ) {
               lastTouchChange = new TouchTransition(menuPerception.getSelected());
               hadTouch = true;
            }
         } else
            lastTouchChange = new TouchTransition(null);
         EngagementState nextState = currentState.nextState(lastMovementChange,
               lastFaceChange, lastTouchChange, hadTouch,
               System.currentTimeMillis() - lastStateChangeTime);
         if ( nextState != currentState )
            setState(nextState);
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
         System.out.println("Engagement Status: "
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

   @Override
   public EngagementPerception getLatest () {
      return latest;
   }
     
   private static abstract class Transition {
      
      private final long changeTime;
      
      protected Transition () { changeTime = System.currentTimeMillis(); }
      
      public long timeSinceChange () { return System.currentTimeMillis() - changeTime; }
      
   }
   static class TouchTransition extends Transition {
      
      final String selected;

      public TouchTransition (String selected) {
         this.selected = selected;
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
