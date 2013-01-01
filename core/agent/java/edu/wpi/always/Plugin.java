package edu.wpi.always;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;
import java.util.*;

/**
 * Base class for plugins.
 */
public abstract class Plugin {
   
   private final List<Activity> activities = new ArrayList<Activity>();
   private final Map<String,List<Registry>> registries = new HashMap<String,List<Registry>>();
    
    /**
    * Returns the activities that this plugin currently makes available.  This method
    * is called by {@link edu.wpi.always.rm.IRelationshipManager}.
    * 
    * @param baseline The closeness at the start of this session (see {@link edu.wpi.always.rm.Closeness})
    */
   public List<Activity> getActivities (int baseline) { return activities; }
     
   /**
    * Returns registries containing the schemas and other components that
    * implement the given activity. This method is called by
    * {@link edu.wpi.always.cm.ICollaborationManager}.
    */
   public List<Registry> getRegistries (Activity activity) { 
      if ( activity.getPlugin() != getClass() ) 
         throw new IllegalArgumentException("Not this plugin: "+activity);
      return registries.get(activity.getName());
   }

   /**
    * Add activity with specified metadata parameters and components.  Note any schema 
    * components will be automatically started.
    * <p>
    * See {@link Activity} for metadata parameters.
    * 
    * @see #addActivity(String,int,int,int,int,Registry...)
    */
   protected void addActivity (String name,
         int required, int duration, int instrumental, int relational, 
         Class<? extends Object>... components) {
      final List<Class<? extends Schema>> schemas = new ArrayList<Class<? extends Schema>>();
      final List<Class<? extends Object>> other = new ArrayList<Class<? extends Object>>();
      for (Class<? extends Object> c : components)
          if ( Schema.class.isAssignableFrom(c) )
             schemas.add((Class<? extends Schema>) c);
          else other.add(c); 
      addActivity(name, required, duration, instrumental, relational,
            new Registry[] {
         new SchemaRegistry() {

            @Override
            public void register (SchemaManager manager) {
               for (Class<? extends Schema> s : schemas)
                  manager.registerSchema(s, true); // start it automatically
            }
         },
         new ComponentRegistry() {

            @Override
            public void register (MutablePicoContainer container) {
               for (Class<? extends Object> c : other)
                  container.as(Characteristics.CACHE).addComponent(c);
            }
         }});
   }
   
   /**
    * Add activity with specified metadata parameters and registries.
    * <p>
    * See {@link Activity} for metadata parameters.
    */
   protected void addActivity (String name,
         int required, int duration, int instrumental, int relational, 
         Registry... registries) {
      activities.add(new Activity(getClass(), name, required, duration, instrumental, relational));
      this.registries.put(name, Arrays.asList(registries));
   }
}