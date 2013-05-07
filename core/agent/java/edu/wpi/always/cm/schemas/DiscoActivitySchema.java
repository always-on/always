package edu.wpi.always.cm.schemas;

import com.google.common.collect.ImmutableList;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.io.IOException;
import java.util.List;

public class DiscoActivitySchema extends SchemaBase implements AdjacencyPair {

   DiscoActivityHelper discoHelper = new DiscoActivityHelper(getClass().getSimpleName());
   private final DiscoUtteranceFormatter formatter;
   protected MenuTurnStateMachine stateMachine;
   protected APCache currentAP;

   public DiscoActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor) {
      super(behaviorReceiver, behaviorHistory);
      formatter = new DiscoUtteranceFormatter(discoHelper.getDisco());
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(0.9);
      setNeedsFocusResource(true);
   }

   public void setTaskId (String taskId) {
      discoHelper.setTaskId(taskId);
      stateMachine.setAdjacencyPair(this);
   }

   public Object eval (String script, String where) {
      Object result = discoHelper.getDisco().eval(script, where);
      return result;
   }

   @Override
   public void run () {
      if ( discoHelper.getPlan() != null ) {
         propose(stateMachine);
      } else {
         proposeNothing();
      }
   }

   public List<Item> generateUserTasks () {
      Interaction interaction = discoHelper.getDisco().getInteraction();
      return interaction.getExternal().generate(interaction);
   }

   @Override
   public double timeRemaining () {
      return 5;
   }

   @Override
   public void enter () {
   }

   @Override
   public AdjacencyPair nextState (String text) {
      List<Item> items = generateUserTasks();
      List<String> choices = formatter.format(items);
      int i = choices.indexOf(text);
      if ( i >= 0 ) discoHelper.userItemDone(items.get(i), text);
      updateCurrentAP();
      return this;
   }

   protected void updateCurrentAP () {
      discoHelper.getDisco().getInteraction().getSystem()
            .respond(discoHelper.getDisco().getInteraction(), false, true);
      currentAP = new APCache(discoHelper.getlastUtterance(),
            getChoicesFromDisco());
   }

   protected List<String> getChoicesFromDisco () {
      List<Item> items = generateUserTasks();
      return formatter.format(items);
   }

   @Override
   public String getMessage () {
      if ( currentAP == null )
         updateCurrentAP();
      return currentAP.getMessage();
   }

   @Override
   public List<String> getChoices () {
      if ( currentAP == null )
         updateCurrentAP();
      return currentAP.getMenus();
   }

   @Override
   public boolean isTwoColumnMenu () {
      return false;
   }

   @Override
   public boolean prematureEnd () {
      return false;
   }

   protected void loadModel (String resourcePath) throws IOException {
      discoHelper.getDisco().load(resourcePath);
   }

   public static class APCache {

      private final String message;
      private final List<String> menus;

      public APCache (String message, List<String> menus) {
         this.message = message;
         this.menus = ImmutableList.copyOf(menus);
      }

      public String getMessage () {
         return message;
      }

      public List<String> getMenus () {
         return menus;
      }
   }
}
