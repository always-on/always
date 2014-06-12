package edu.wpi.always.cm.schemas;

import edu.wpi.always.*;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

/**
 * Note: This schema will automatically stop (cancel) when
 * there is nothing left for the user or agent to say.
 * It can also be ended abruptly by calling '$schema.cancel()'
 * in the 'eval' attribute of a D4g element.
 */
public class DiscoActivitySchema extends DiscoAdjacencyPairSchema {

   private final DiscoRT.ConsoleWindow console;
   
   public DiscoActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always, DiscoRT.Interaction interaction,
         Logger.Activity loggerName) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always,
            interaction, loggerName);
      if ( behaviorReceiver != null ) { // for always_disco testing
         // note activities append to session log
         // since reusing interaction, reuse thread and console also
         console = interaction.getConsoleWindow();
         console.setVisible(true);
         // share main log for all Disco plugins
         interaction.getConsole().setAppend(always.getCM().getInteraction().getConsole().getLogStream());
      } else console = null;
   }
   
   protected void start (String id) {
      Plan plan = interaction.addTop(id);
      plan.getGoal().setShould(true);
      interaction.push(plan);
   }
   
   @Override
   public void stop() {
      super.stop();
      dispose();
   }
   
   @Override
   public void dispose () { 
      console.setVisible(false);
      history(); // before dispose
      super.dispose();
   }
}
