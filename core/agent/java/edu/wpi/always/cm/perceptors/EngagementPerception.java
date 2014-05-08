package edu.wpi.always.cm.perceptors;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.cm.perceptors.FaceMovementMenuEngagementPerceptor.FaceTransition;
import edu.wpi.always.cm.perceptors.FaceMovementMenuEngagementPerceptor.MovementTransition;
import edu.wpi.always.cm.perceptors.FaceMovementMenuEngagementPerceptor.TouchTransition;
import edu.wpi.disco.rt.perceptor.Perception;

public class EngagementPerception extends Perception {

   public static long IDLE_FACE_TIME = 1000; // idle->attention after seeing (far) face for this long
   public static long ATTENTION_NO_FACE_TIMEOUT = 10000;  // attention->idle if no face for this long
   
   public static long ATTENTION_FACE_TIME = 20000; // attention->initiation after seeing (far) face for this long
   
   public static long INITIATION_NOT_NEAR_TIMEOUT = 20000; // initiation->idle if no near face for this long
   
   // Note these two timeouts should be larger than @link{MenuTurnStateMachine#TIMEOUT_DELAY} to let the agent
   // repeat once first if it is waiting for menu response.  However, this timeout is *not* restarted
   // when agent repeats
   public static long ENGAGED_NOT_NEAR_TIMEOUT = 60000; // engaged->recovering if no near face for this long   
   public static long ENGAGED_NO_TOUCH_TIMEOUT = 60000; //   and no touch for this long (and not moving)

   // TODO: Increase these a *lot*
   public static long RECOVERING_NOT_NEAR_TIMEOUT = 60000; // engaged->idle if no near face for this long 
   public static long RECOVERING_NO_TOUCH_TIMEOUT = 60000; //   and no touch for this long (and not moving) 
   
   private final EngagementState state;

   public EngagementPerception (EngagementState state) {
      this.state = state;
   }

   public EngagementState getState () {
      return state;
   }

   public boolean isEngaged () {
      return state == EngagementState.Engaged;
   }

   public enum EngagementState {
        
      Idle {

         @Override
         EngagementState nextState (MovementTransition lastMovementChange,
               FaceTransition lastFaceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch ) return Engaged;
            if ( Always.isLogin() ) return Idle; // user must touch Hello
            if ( lastFaceChange.isNear ) return Initiation;
            if ( lastMovementChange.isMoving ) return Attention;
            if ( lastFaceChange.isFace && lastFaceChange.timeSinceChange() > IDLE_FACE_TIME )
               return Attention;
            return Idle;
         }
      },
      Attention {

         @Override
         EngagementState nextState (MovementTransition lastMovementChange,
               FaceTransition lastFaceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch ) return Engaged;
            if ( lastFaceChange.isNear ) return Initiation;
            if ( !lastFaceChange.isFace && lastFaceChange.timeSinceChange() > ATTENTION_NO_FACE_TIMEOUT
                 && !lastMovementChange.isMoving )
               return Idle;
            if ( lastFaceChange.isFace && lastFaceChange.timeSinceChange() > ATTENTION_FACE_TIME )
               return Initiation;
            return Attention;
         }
      },
      Initiation {

         @Override
         EngagementState nextState (MovementTransition lastMovementChange,
               FaceTransition lastFaceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch ) return Engaged;
            if ( !lastFaceChange.isNear && lastFaceChange.timeSinceChange() > INITIATION_NOT_NEAR_TIMEOUT )
               return Idle;
            return Initiation;
         }
      },
      Engaged {

         @Override
         EngagementState nextState (MovementTransition lastMovementChange,
               FaceTransition lastFaceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch ) return Engaged;
            if ( (!lastFaceChange.isNear && lastFaceChange.timeSinceChange() > ENGAGED_NOT_NEAR_TIMEOUT)
                 && lastTouch.timeSinceChange() > ENGAGED_NO_TOUCH_TIMEOUT 
                 && timeInState > ENGAGED_NO_TOUCH_TIMEOUT 
                 && !lastMovementChange.isMoving )
               return Recovering;
            return Engaged;
         }
      },
      Recovering {

         @Override
         EngagementState nextState (MovementTransition lastMovementChange,
               FaceTransition lastFaceChange,
               TouchTransition lastTouch, boolean hadTouch, long timeInState) {
            if ( hadTouch ) return Engaged;
            if ( timeInState > RECOVERING_NO_TOUCH_TIMEOUT 
                 && !lastFaceChange.isNear && lastFaceChange.timeSinceChange() > RECOVERING_NOT_NEAR_TIMEOUT 
                 && !lastMovementChange.isMoving )
               return Idle;
            return Recovering;
         }
      };
      
        /**
       * Called by the engagement perceptor to figure out what state it should
       * transition to
       * 
       * @param lastMovementChange represents the last change in state of the
       *           distance to the user(time is reset on state change)
       * @param lastFaceChange represents the last change in state of
       *           the distance to the user (time is reset on state change)
       * @param lastTouch represents the last touch (time is reset on state
       *           change, will be marked as not having a touch)
       * @param hadTouch true if there was a touch since the most recent update
       *           of the perceptor
       * @return the next state (or the current state to stay in the same state)
       */
      abstract EngagementState nextState (MovementTransition lastMovementChange,
            FaceTransition lastFaceChange,
            TouchTransition lastTouch, boolean hadTouch, long timeInState);
   }
}
