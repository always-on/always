package edu.wpi.always.cm;

import edu.wpi.always.client.AgentFaceExpression;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.PrimitiveBehavior;
import java.awt.Point;
import java.util.ArrayList;

public class IdleBehaviorsImpl implements IdleBehaviors {

   @Override
   public PrimitiveBehavior get (Resource resource) {
      switch (resource) {
      case Menu:
         return new MenuBehavior(new ArrayList<String>());
      case FaceExpression:
         return new FaceExpressionBehavior(AgentFaceExpression.Warm);
      case Gaze:
         return new GazeBehavior(new Point(0, 0));// Center face
      case Idle:
         return new IdleBehavior(true);
      default:
         return null;
      }
   }
}
