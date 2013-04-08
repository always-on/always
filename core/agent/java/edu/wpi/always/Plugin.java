package edu.wpi.always;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.OntologyUserModel;
import edu.wpi.disco.rt.Registry;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;
import java.io.InputStream;
import java.util.*;

/**
 * Base class for plugins.
 */
public abstract class Plugin {
   
   protected final UserModel userModel;
   protected final String name;
   
   /**
    * @param name used as prefix for plugin-specific user properties
    * @param userModel shared user model
    */
   protected Plugin (String name, UserModel userModel) {
      this.name = name;
      this.userModel = userModel;
      InputStream stream = getClass().getResourceAsStream(name+".owl");
      if ( stream != null ) {
         System.out.println("Loading "+name+".owl");
         ((OntologyUserModel) userModel).addAxiomsFromInputStream(stream);
      }
   }
   
   /**
    * Get user property associated with this plugin.  Property is stored
    * in user model.  Note property must be declared in [plugin name].owl resource file
    * in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin name)
    */
   public String getProperty (String property) {
      checkProperty(property);
      return userModel.getProperty(property);
   }
   
   /**
    * Set user property associated with this plugin.  Property is stored
    * in user model.  Note property must be declared in [plugin name].owl resource file
    * in toplevel package of plugin class.
    *
    * @param property name of property (must be a constant and start with plugin name)
    * @param value property value
    */
   public void setProperty (String property, String value) { 
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   private void checkProperty (String property) {
      if ( !property.startsWith(name) ) 
         throw new IllegalArgumentException(
               "Property "+property+" must start with plugin name "+name); 
   }
   
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
