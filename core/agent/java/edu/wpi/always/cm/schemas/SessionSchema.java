package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.always.rm.IRelationshipManager;
import edu.wpi.cetask.*;
import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.DiscoDocument;
import org.picocontainer.MutablePicoContainer;
import java.util.*;

// FIXME replacement for DiscoActivitySchema? ActivityStarterSchema?

public class SessionSchema extends DiscoActivitySchema {
   
   // TODO Sort out Disco vs. SyncronizedDiscoWrapper
   // TODO New schema started for every session
   
   private final MutablePicoContainer container; // for plugins
   
   public SessionSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, IRelationshipManager rm, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      container = always.getContainer();
      Disco disco = discoHelper.getDisco();
      disco.load("Activities.xml");
      DiscoDocument session = rm.getSession();
      if ( session != null )
         disco.load("Relationship Manager", 
               session.getDocument(), session.getProperties(), session.getTranslate());
      stateMachine.setAdjacencyPair(this);
      // lower specificity below activities
      stateMachine.setSpecificityMetadata(0.5);
   }

   private final Map<Task,Schema> started = new HashMap<Task,Schema>();
   
   @Override
   public void run () {
      Plan focus = discoHelper.getDisco().getFocus();
      if ( focus != null ) {
         Task goal = focus.getGoal();
         Schema schema = started.get(goal);
         if ( schema != null ) {
            if ( schema.isDone() ) {
               focus.setComplete(true);
               started.remove(goal);
            }
         } else if ( focus.isLive() && edu.wpi.cetask.Utils.isTrue(goal.getShould())
               && !focus.isStarted() ) {
            TaskClass task = goal.getType();
            Plugin.getPlugin(task, container).startActivity(Plugin.getActivity(task));
            focus.setStarted(true);
         }
      }
      // keep proposing state machine
      propose(stateMachine);
   }
}
