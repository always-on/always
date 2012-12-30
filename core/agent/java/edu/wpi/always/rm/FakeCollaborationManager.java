package edu.wpi.always.rm;

import edu.wpi.always.cm.*;
import edu.wpi.always.rm.RelationshipManager.relationshipStage;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import org.w3c.dom.Document;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeCollaborationManager extends Thread implements ICollaborationManager {

   private final RelationshipManager rm;
   private ConcurrentLinkedQueue<PlanningMessage> planBuffer;

   public FakeCollaborationManager (RelationshipManager rm) {
      this.rm = rm;
   }

   @Override
   public void run () {
      rm.currentStage = relationshipStage.ACQUAINTANCES;
      rm.baseCloseness = 3; // TODO: enh
      rm.stockedSocial = 99;
      rm.currentCloseness = rm.baseCloseness;
      rm.closenessTime = new Date();
      Document plan = rm.getSession().getDocument();
      Interaction interaction = new Interaction(new Agent("agent"), new User(
            "user"), null);
      // TODO: Add preloads again! This should work with the rearranged disco.
      // ArrayList<Document> preloads = relationshipModule.plan().preload; ?
      Disco disco = interaction.getDisco();
      interaction.start(true);
      disco.load(plan, null);
      try {
         interaction.join();
      } catch (InterruptedException e) {
      }
      int closeness = 0;
      int time = 3;
      rm.afterInteraction(new DiscoSynchronizedWrapper(disco),
            closeness, time);
   }

   @SuppressWarnings("unused")
   private void execute (PlanningMessage plan) {
      System.out.println("Talking about weather"); // refer to WeatherPlugin
      rm.report(null);
   }

   public void planTasks (PlanningMessage message) {
      planBuffer.add(message);
      // System.out.println(message);
   }

   @Override
   public void addRegistry (Registry registry) {
      // TODO Auto-generated method stub
      
   }

}
