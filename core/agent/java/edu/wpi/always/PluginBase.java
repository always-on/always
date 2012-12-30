package edu.wpi.always;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.*;
import org.picocontainer.*;
import java.util.*;

/**
 * Base class for plugins.
 */
public abstract class PluginBase implements Plugin {
   
   private final List<Activity> activities;
   private final List<Registry> registries = new ArrayList<Registry>(2);
   
   /**
    * Construct simple plugin in which all metadata parameters for activity are zero.
    * 
    * @param name of activity
    * @param schema for activity
    * @param components for additional optional components
    * 
    * @see #PluginBase(String,Class,int,int,int,int,Class...)
    */
   protected PluginBase (String name, Class<? extends Schema> schema,
                         Class<? extends Object>... components) {
      this  (name, schema, 0, 0 , 0 , 0, components);
   }
 
   /**
    * Construct simple plugin with one fixed activity implemented by one schema.
    *  
    * @param name of activity
    * @param schema for activity
    * @param components for additional optional components 
    * <p>
    * See {@link Plugin} for metadata parameters.
    * @see #PluginBase(String,Class,int,int,int,int,ComponentRegistry)
    */
   protected PluginBase (String name, final Class<? extends Schema> schema, 
         int required, int duration, int instrumental, int relational,
         final Class<? extends Object>... components) {
      this(name, schema, required, duration, instrumental, relational,
            components.length == 0 ? null :
               new ComponentRegistry() {

         @Override
         public void register (MutablePicoContainer container) {
            for (Object c : components)
               container.as(Characteristics.CACHE).addComponent(c);
         }});
   }
   
   /**
    * 
    * Construct simple plugin with one fixed activity implemented by one schema.
    *
    * @param name of activity
    * @param schema for activity
    * @param components for additional optional components
    * <p>
    * See {@link Plugin} for metadata parameters
    */
   protected PluginBase (String name, final Class<? extends Schema> schema, 
         int required, int duration, int instrumental, int relational,
         ComponentRegistry components) {
       activities = Collections.singletonList(
               new Activity(this, name, required, duration, instrumental, relational));
       registries.add(new SchemaRegistry() {

            @Override
            public void register (SchemaManager manager) {
               manager.registerSchema(schema, true); // start it automatically
            }
         });
         if ( components != null ) registries.add(components);
   }
  
   @Override
   public List<Activity> getActivities (int baseline) { return activities; }

   @Override
   public List<Registry> getRegistries (Activity activity) { return registries; }

}
