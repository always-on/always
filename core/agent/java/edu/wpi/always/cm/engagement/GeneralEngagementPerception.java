package edu.wpi.always.cm.engagement;

import edu.wpi.always.cm.Perception;
import edu.wpi.always.cm.engagement.GeneralEngagementPerceptorImpl.FaceDistanceTransition;
import edu.wpi.always.cm.engagement.GeneralEngagementPerceptorImpl.MotionTransition;
import edu.wpi.always.cm.engagement.GeneralEngagementPerceptorImpl.TouchTransition;

public interface GeneralEngagementPerception extends Perception {

   public static final long IDLE_FACE_TIME = 1000;
   public static final long ATTENTION_NO_FACE_TIMEOUT = 10000;
   public static final long ATTENTION_FACE_TIME = 2000;
   public static final long INITIATION_NOT_NEAR_TIMEOUT = 20000;
   public static final long ENGAGED_NO_TOUCH_TIMEOUT = 20000;
   public static final long ENGAGED_NOT_NEAR_TIMEOUT = 15000;
   public static final long RECOVERING_NO_TOUCH_TIMEOUT = 60000;
   public static final long RECOVERING_NOT_NEAR_TIMEOUT = 10000;

   public enum EngagementState {
      Idle {

         @Override
         EngagementState nextState (MotionTransition lastMotionChange,
               FaceDistanceTransition lastFaceDistanceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( lastFaceDistanceChange.hasFace()
               && lastFaceDistanceChange.isNear() )
               return Initiation;
            if ( lastMotionChange.hasMotion() )
               return Attention;
            if ( lastFaceDistanceChange.hasFace()
               && lastFaceDistanceChange.timeSinceChange() > IDLE_FACE_TIME )
               return Attention;
            return Idle;
         }
      },
      Attention {

         @Override
         EngagementState nextState (MotionTransition lastMotionChange,
               FaceDistanceTransition lastFaceDistanceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( lastFaceDistanceChange.isNear() )
               return Initiation;
            if ( !lastFaceDistanceChange.hasFace()
               && lastFaceDistanceChange.timeSinceChange() > ATTENTION_NO_FACE_TIMEOUT
               && !lastMotionChange.hasMotion() )
               return Idle;
            if ( lastFaceDistanceChange.hasFace()
               && lastFaceDistanceChange.timeSinceChange() > ATTENTION_FACE_TIME )
               return Initiation;
            return Attention;
         }
      },
      Initiation {

         @Override
         EngagementState nextState (MotionTransition lastMotionChange,
               FaceDistanceTransition lastFaceDistanceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch )
               return Engaged;
            if ( lastFaceDistanceChange.isNear() )
               return Engaged;
            if ( !lastFaceDistanceChange.isNear()
               && lastFaceDistanceChange.timeSinceChange() > INITIATION_NOT_NEAR_TIMEOUT )
               return Idle;
            return Initiation;
         }
      },
      Engaged {

         @Override
         EngagementState nextState (MotionTransition lastMotionChange,
               FaceDistanceTransition lastFaceDistanceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( (!lastFaceDistanceChange.isNear() && lastFaceDistanceChange
                  .timeSinceChange() > ENGAGED_NOT_NEAR_TIMEOUT)
               || (lastTouch.timeSinceChange() > ENGAGED_NO_TOUCH_TIMEOUT && timeInState > ENGAGED_NO_TOUCH_TIMEOUT) ) {
               if ( lastMotionChange.hasMotion() )
                  return Recovering;
               else
                  return Idle;
            }
            return Engaged;
         }
      },
      Recovering {

         @Override
         EngagementState nextState (MotionTransition lastMotionChange,
               FaceDistanceTransition lastFaceDistanceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch )
               return Engaged;
            if ( timeInState > RECOVERING_NO_TOUCH_TIMEOUT )
               return Idle;
            if ( !lastFaceDistanceChange.isNear()
               && lastFaceDistanceChange.timeSinceChange() > RECOVERING_NOT_NEAR_TIMEOUT )
               return Idle;
            return Recovering;
         }
      };

      /**
       * Called by the engagement perceptor to figure out what state it should
       * transition to
       * 
       * @param lastMotionChange represents the last change in state of the
       *           distance to the user(time is reset on state change)
       * @param lastFaceDistanceChange represents the last change in state of
       *           the distance to the user (time is reset on state change)
       * @param lastTouch represents the last touch (time is reset on state
       *           change, will be marked as not having a touch)
       * @param hadTouch true if there was a touch since the most recent update
       *           of the perceptor
       * @return the next state (or the current state to stay in the same state)
       */
      abstract EngagementState nextState (MotionTransition lastMotionChange,
            FaceDistanceTransition lastFaceDistanceChange,
            TouchTransition lastTouch, boolean hadTouch, long timeInState);
   }

   EngagementState getState ();

   boolean engaged ();
}
