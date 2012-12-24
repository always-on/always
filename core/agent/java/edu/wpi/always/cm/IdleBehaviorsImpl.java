package edu.wpi.always.cm;

import edu.wpi.always.client.AgentFaceExpression;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;
import java.awt.Point;
import java.util.ArrayList;

public class IdleBehaviorsImpl implements IdleBehaviors {

   @Override
   public PrimitiveBehavior get (Resource resource) {
      if ( resource instanceof PhysicalResources ) {
         switch ((PhysicalResources) resource) {
         case MENU:
            return new MenuBehavior(new ArrayList<String>());
         case FACE_EXPRESSION:
            return new FaceExpressionBehavior(AgentFaceExpression.Warm);
         case GAZE:
            return new GazeBehavior(new Point(0, 0));// Center face
         case IDLE:
            return new IdleBehavior(true);
         default:
            return null;
         }
      } else return null;
   }
}
