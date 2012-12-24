package edu.wpi.always.cm.engagement;

import edu.wpi.always.cm.engagement.GeneralEngagementPerception.EngagementState;
import edu.wpi.always.cm.perceptors.*;

public class GeneralEngagementPerceptorImpl implements
      GeneralEngagementPerceptor {

   private volatile GeneralEngagementPerception latest;
   private EmotiveFacePerceptor facePerceptor;
   private MotionPerceptor motionPerceptor;
   private MenuPerceptor menuPerceptor;
   private MotionTransition lastMotionChange = null;
   private FaceDistanceTransition lastFaceDistanceChange = null;
   private TouchTransition lastTouchChange = null;
   private long lastStateChangeTime = System.currentTimeMillis();

   public GeneralEngagementPerceptorImpl (EmotiveFacePerceptor facePerceptor,
         MotionPerceptor motionPerceptor, MenuPerceptor menuPerceptor) {
      this.facePerceptor = facePerceptor;
      this.motionPerceptor = motionPerceptor;
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
         EmotiveFacePerception facePerception = facePerceptor.getLatest();
         if ( facePerception != null ) {
            if ( lastFaceDistanceChange == null
               || lastFaceDistanceChange.isNear() != facePerception.isNear()
               || lastFaceDistanceChange.hasFace() != facePerception.hasFace() )
               lastFaceDistanceChange = new FaceDistanceTransition(
                     facePerception.hasFace(), facePerception.isNear());
         } else
            lastFaceDistanceChange = new FaceDistanceTransition(false, false);
         MotionPerception motionPerception = motionPerceptor.getLatest();
         if ( motionPerception != null ) {
            if ( lastMotionChange == null
               || lastMotionChange.hasMotion() != motionPerception
                     .hasMovement() )
               lastMotionChange = new MotionTransition(
                     motionPerception.hasMovement());
         } else
            lastMotionChange = new MotionTransition(false);
         boolean hadTouch = false;
         MenuPerception menuPerception = menuPerceptor.getLatest();
         if ( menuPerception != null && lastTouchChange != null ) {
            if ( !menuPerception.selectedMenu().equals(
                  lastTouchChange.selectedMenu()) ) {
               lastTouchChange = new TouchTransition(
                     menuPerception.selectedMenu());
               hadTouch = true;
            }
         } else
            lastTouchChange = new TouchTransition(null);
         EngagementState nextState = currentState.nextState(lastMotionChange,
               lastFaceDistanceChange, lastTouchChange, hadTouch,
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
         if ( lastMotionChange != null )
            lastMotionChange = new MotionTransition(
                  lastMotionChange.hasMotion());
         if ( lastFaceDistanceChange != null )
            lastFaceDistanceChange = new FaceDistanceTransition(
                  lastFaceDistanceChange.hasFace(),
                  lastFaceDistanceChange.isNear());
         lastStateChangeTime = System.currentTimeMillis();
         latest = new GeneralEngagementPerceptionImpl(newState);
      }
   }

   @Override
   public GeneralEngagementPerception getLatest () {
      return latest;
   }

   class TouchTransition {

      private long changeTime;
      private String selectedMenu;

      public TouchTransition (String selectedMenu) {
         this.selectedMenu = selectedMenu;
         this.changeTime = System.currentTimeMillis();
      }

      public long timeSinceChange () {
         return System.currentTimeMillis() - changeTime;
      }

      public String selectedMenu () {
         return selectedMenu;
      }

      public boolean hadTouch () {
         return selectedMenu != null;
      }
   }

   class MotionTransition {

      private long changeTime;
      private boolean hasMotion;

      public MotionTransition (boolean hasMotion) {
         this.changeTime = System.currentTimeMillis();
         this.hasMotion = hasMotion;
      }

      public long timeSinceChange () {
         return System.currentTimeMillis() - changeTime;
      }

      public boolean hasMotion () {
         return hasMotion;
      }
   }

   class FaceDistanceTransition {

      private long changeTime;
      private boolean hasFace;
      private boolean isNear;

      public FaceDistanceTransition (boolean hasFace, boolean isNear) {
         this.changeTime = System.currentTimeMillis();
         this.hasFace = hasFace;
         this.isNear = isNear;
      }

      public long timeSinceChange () {
         return System.currentTimeMillis() - changeTime;
      }

      public boolean hasFace () {
         return hasFace;
      }

      public boolean isNear () {
         return isNear;
      }
   }
}
