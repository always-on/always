package edu.wpi.disco.rt;

import edu.wpi.disco.*;
import edu.wpi.disco.rt.action.LoadModelFromResource;
import edu.wpi.disco.rt.util.Utils;

public class DiscoBootstrapper {

   public Disco bootstrap (Actor me, boolean startConsole) {
      return bootstrap(startConsole, me, new User("user"));
   }

   public Disco bootstrap (boolean startConsole, Actor me, Actor userActor) {
      Interaction interaction = new Interaction(me, userActor);
      Disco disco = interaction.getDisco();
      if ( startConsole )
         new ConsoleWindow(interaction, 600, 500, 14);
      Utils.setAgendaInteraction(interaction.getExternal().getAgenda(),
            interaction);
      Utils.setAgendaInteraction(interaction.getSystem().getAgenda(),
            interaction);
      loadMainModels(disco);
      return disco;
   }

   private void loadMainModels (Disco disco) {
      loadModel(disco, "/resources/DemoTasks.xml");
      loadModel(disco, "/resources/taskModels/Knock.xml");
   }

   private void loadModel (Disco disco, String resourcePath) {
      new LoadModelFromResource(resourcePath).execute(disco);
   }
}
