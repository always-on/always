package edu.wpi.always.cm;

import edu.wpi.always.IRelationshipManager;
import edu.wpi.always.cm.schemas.DiscoBasedSchema;
import edu.wpi.always.cm.utils.RNotImplementedException;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.actions.*;

public class DiscoBasedActivityManager {

   private static final String TODAY_PLAN_NAME = "_Today";
   private final IRelationshipManager relationshipManager;
   private final DiscoSynchronizedWrapper disco;
   private Plan todaysPlan;
   private final SchemaManager schemaManager;

   public DiscoBasedActivityManager (IRelationshipManager relationshipManager,
         SchemaManager schemaManager) {
      this.relationshipManager = relationshipManager;
      this.schemaManager = schemaManager;
      disco = new DiscoSynchronizedWrapper(new DiscoBootstrapper().bootstrap(
            false, new Agent("agent"), new DiscoUser("user")));
   }

   public void initFromRelationshipManager () {
      DiscoDocumentSet currentPlanDocuments = relationshipManager
            .getLatestPlan();
      disco.execute(new LoadModelFromDocument(currentPlanDocuments));
      todaysPlan = getDisco().execute(new AddNewTask(TODAY_PLAN_NAME));
   }

   public DiscoSynchronizedWrapper getDisco () {
      return disco;
   }

   public Plan getPlan () {
      return todaysPlan;
   }

   public void runSchemaBasedOnTaskOnTopOfStack () {
      disco.execute(new DiscoAction() {

         @SuppressWarnings("unchecked")
         @Override
         public void execute (Disco disco) {
            Plan plan = disco.getStack().peek().getPlan();
            String schemaClassName = DiscoUtils.getProperty(plan, "ref_schema");
            String pluginClassName = DiscoUtils.getProperty(plan, "ref_plugin");
            String taskId = DiscoUtils.getProperty(plan, "ref_task");
            if ( schemaClassName != null ) {
               try {
                  Class<?> c = Class.forName(schemaClassName);
                  schemaManager.start((Class<? extends Schema>) c);
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();
               }
            } else if ( pluginClassName != null ) {
               throw new RNotImplementedException();
            } else if ( taskId != null ) {
               schemaManager.start(DiscoBasedSchema.class).setTaskId(taskId);
            }
         }
      });
   }

   public boolean isRunning (final TaskClass taskClass) {
      return disco.execute(new DiscoFunc<Boolean>() {

         @Override
         public Boolean execute (Disco disco) {
            for (Segment seg : disco.getStack()) {
               if ( seg.getPlan() != null ) {
                  if ( seg.getPlan().getType().equals(taskClass) )
                     return true;
                  for (Plan p : seg.getPlan().getLive()) {
                     if ( p.getType().equals(taskClass) )
                        return true;
                  }
               }
            }
            return false;
         }
      });
   }
}
