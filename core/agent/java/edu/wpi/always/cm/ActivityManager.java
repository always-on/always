package edu.wpi.always.cm;

import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.rm.IRelationshipManager;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.action.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.*;
import edu.wpi.disco.rt.util.Utils;

public class ActivityManager {

   private Plan session;
   private static final String sessionName = "_Today";
   private final IRelationshipManager rm;
   private final DiscoSynchronizedWrapper disco;
   private final SchemaManager schemaManager;

   public ActivityManager (IRelationshipManager relationshipManager,
         SchemaManager schemaManager, DiscoSynchronizedWrapper disco) {
      this.rm = relationshipManager;
      this.schemaManager = schemaManager;
      this.disco = disco;
   }

   public void initSession () {
      // FIXME make a new disco for each session
      // need to cleanup old disco (what about other Disco data lying around???)
      disco.execute(new LoadModelDocument(rm.getSession()));
      // session = getDisco().execute(new AddNewTask(sessionName));
   }

   public DiscoSynchronizedWrapper getDisco () {
      return disco;
   }

   public Plan getPlan () {
      return session;
   }

   public void runSchemaBasedOnTaskOnTopOfStack () {
      disco.execute(new DiscoAction() {

         @SuppressWarnings("unchecked")
         @Override
         public void execute (Disco disco) {
            Plan plan = disco.getFocus(true);
            String schemaClassName = Utils.getProperty(plan, "ref_schema");
            String pluginClassName = Utils.getProperty(plan, "ref_plugin");
            String taskId = Utils.getProperty(plan, "ref_task");
            if ( schemaClassName != null ) {
               try {
                  Class<?> c = Class.forName(schemaClassName);
                  schemaManager.start((Class<? extends Schema>) c);
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();
               }
            } else if ( pluginClassName != null ) {
               throw new NotImplementedException();
            } else if ( taskId != null ) {
               schemaManager.start(DiscoActivitySchema.class).setTaskId(taskId);
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
