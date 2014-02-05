package edu.wpi.always;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.joda.time.LocalTime;
import org.picocontainer.Characteristics;
import org.picocontainer.MutablePicoContainer;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.owl.OntologyUserModel;
import edu.wpi.cetask.TaskClass;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.schema.SchemaConfig;
import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.schema.SchemaRegistry;
import edu.wpi.disco.rt.util.ComponentRegistry;

/**
 * Base class for plugins.
 */
public abstract class Plugin {
   
   protected final String name;
   protected final UserModel userModel;
   protected final CollaborationManager cm;
   protected final MutablePicoContainer container;
   private final Interaction interaction;
   
   /**
    * @param name used as prefix for plugin-specific user properties
    */
   protected Plugin (String name, UserModel userModel, CollaborationManager cm) {
      this.name = name;
      this.userModel = userModel;
      this.cm = cm;
      container = cm.getContainer();      
      interaction = container.getComponent(Interaction.class);
      InputStream stream = getClass().getResourceAsStream("resources/"+name+".owl");
      if ( stream == null )
         stream = getClass().getResourceAsStream(name+".owl");
      if ( stream != null ) {
         System.out.println("Loading "+name+".owl");
         ((OntologyUserModel) userModel).addAxioms(stream, true);
      }
   }
   
   /**
    * For use in Disco test files.
    */
   public static void test (String pluginModelFile) {
      UserModel model = Always.THIS.getUserModel();
      File file = new File(pluginModelFile);
      ((OntologyUserModel) model).addAxioms(file);
   }
   
   /**
    * For use in main method of plugins for testing.
    */
   public static Always main (String[] args, Class<? extends Plugin> plugin, String activity) {
      Always always = Always.make(args, plugin, activity);
      UserModel model = always.getUserModel();
      if ( model.getUserName() == null ) {
         model.setUserName("TestPluginUser");
         System.out.println("User name: "+model.getUserName());
      }
      SessionSchema.HOUR = LocalTime.now().getHourOfDay();
      always.start();
      return always;
   }
   
   /**
    * Show the related UI plugin, if any
    */
   public void show () {}
   
   /**
    * Hide the related UI plugin, if any
    */
   public void hide () { 
      ClientPluginUtils.hidePlugin(container.getComponent(UIMessageDispatcher.class));
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
    * Set string-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    * @param value property value
    * 
    * @see #setProperty(String,boolean)
    */
   public void setProperty (String property, String value) { 
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   /**
    * Set integer-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    * @param value property value
    * 
    * @see #setProperty(String,int)
    */
   public void setProperty (String property, int value) {
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   /**
    * Get integer-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class. Property defaults to
    * 0 if it has no value.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    */
   public int getIntProperty (String property) {
      checkProperty(property);
      return userModel.getIntProperty(property);
   }
   
     
   /**
    * Set long-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    * @param value property value
    * 
    * @see #setProperty(String,String)
    */
   public void setProperty (String property, long value) {
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   /**
    * Get boolean-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class. Property defaults to
    * 0 if it has no value.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    */
   public long getLongProperty (String property) {
      checkProperty(property);
      return userModel.getLongProperty(property);
   }
   
    /**
    * Set double-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    * @param value property value
    * 
    * @see #setProperty(String,int)
    */
   public void setProperty (String property, double value) {
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   /**
    * Get double-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class. Property defaults to
    * 0 if it has no value.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    */
   public double getDoubleProperty (String property) {
      checkProperty(property);
      return userModel.getDoubleProperty(property);
   }
   
   /**
    * Set boolean-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    * @param value property value
    * 
    * @see #setProperty(String,String)
    */
   public void setProperty (String property, boolean value) {
      checkProperty(property);
      userModel.setProperty(property, value);
   }
   
   /**
    * Get boolean-valued user property associated with this plugin. Property is
    * stored in user model. Note property must be declared in [plugin name].owl
    * resource file in toplevel package of plugin class. Property defaults to
    * false if it has no value.
    * 
    * @param property name of property (must be a constant and start with plugin
    *           name)
    */
   public boolean isProperty (String property) {
      checkProperty(property);
      return userModel.isProperty(property);
   }
   
   private void checkProperty (String property) {
      if ( !property.startsWith(name) ) 
         throw new IllegalArgumentException(
               "Property "+property+" must start with plugin name "+name); 
   }
   
   private final List<Activity> activities = new ArrayList<Activity>();
   private final Map<String,List<Registry>> registries = new HashMap<String,List<Registry>>();
   private final Map<String,List<Class<? extends Schema>>> schemas = 
         new HashMap<String,List<Class<? extends Schema>>>(); // not run at startup
    
    /**
    * Returns the activities that this plugin currently makes available.  This method
    * is called by {@link edu.wpi.always.RelationshipManager}.
    * 
    * @param baseline The closeness at the start of this session (ignored)
    */
   public List<Activity> getActivities (int baseline) { return activities; }
     
   /**
    * Returns registries containing the schemas and other components that
    * implement the given activity. 
    */
   public List<Registry> getRegistries (Activity activity) { 
      if ( activity.getPlugin() != getClass() ) 
         throw new IllegalArgumentException("Not this plugin: "+activity);
      return registries.get(activity.getName());
   }

   /**
    * Start schema(s) that implement given activity.
    */
   public ActivitySchema startActivity (String name) {
      ActivitySchema activity = null;
      for (Class<? extends Schema> schema : schemas.get(name)) {
        Schema instance = cm.getContainer().getComponent(SchemaManager.class).start(schema);
        if ( instance instanceof ActivitySchema ) {
           activity = (ActivitySchema) instance;
           activity.setPlugin(this);
        }
      }
      if ( activity == null ) throw new IllegalStateException("No activity schema for: "+name);
      return activity;
   }
      
   /**
    * Add activity with specified metadata parameters, activity schema and
    * optional other components. Components are either classes (including schema
    * classes) or instances of {@link SchemaConfig} (for schemas that should be
    * automatically started or have non-default interval).
    * <p>
    * See {@link Activity} for metadata parameters.
    */
   protected void addActivity (String name,
         int required, int duration, int instrumental, int relational, 
         Class<? extends ActivitySchema> activity, Object... components) {
      final List<SchemaConfig> configs = new ArrayList<SchemaConfig>();
      final List<Class<? extends Schema>> schemas = new ArrayList<Class<? extends Schema>>();
      final List<Class<?>> other = new ArrayList<Class<? extends Object>>();
      configs.add(newSchemaConfig(activity));
      schemas.add(activity);
      // store information for arbitrator focus assignment
      String task = name;
      Properties properties = interaction.getDisco().getModel("urn:always.wpi.edu:Activities")
                                 .getProperties(); 
      for ( String key : properties.stringPropertyNames() ) 
         if ( key.endsWith("@activity") && name.equals(properties.get(key)) )
            task = key.substring(0, key.length()-9);
      // TODO handle ambiguous task id's properly here
      TaskClass type = interaction.getDisco().getTaskClass(task);
      if ( type == null ) throw new RuntimeException("Unknown task class for activity: "+name);
      cm.setSchema(type, activity);
      for (Object c : components)
          if ( c instanceof SchemaConfig ) {
             SchemaConfig config = (SchemaConfig) c;
             configs.add(config);
             if ( !config.getRunOnStartup() ) schemas.add(config.getType());
          } else if ( c instanceof Class ) {
             Class<?> cls = (Class<?>) c;
             if ( Schema.class.isAssignableFrom(cls) ) {
                Class<? extends Schema> schema = (Class<? extends Schema>) cls;
                configs.add(newSchemaConfig(schema));
                schemas.add(schema);
             } else other.add(cls);
          } else throw new IllegalArgumentException("Should be class: "+c);
      this.schemas.put(name, schemas);
      activities.add(new Activity(getClass(), name, required, duration, instrumental,
                                              relational));
      registries.put(name, Arrays.asList(new Registry[] {
         new SchemaRegistry() {

            @Override
            public void register (SchemaManager manager) {
               for (SchemaConfig config : configs)
                  manager.registerSchema(config);
            }
         },
         new ComponentRegistry() {

            @Override
            public void register (MutablePicoContainer container) {
               for (Class<? extends Object> c : other)
                  container.as(Characteristics.CACHE).addComponent(c);
            }
         }}));
   }
   
   private SchemaConfig newSchemaConfig (Class<? extends Schema> schema) {
      return new SchemaConfig(schema, DiscoRT.SCHEMA_INTERVAL, false);
   }

   public static Plugin getPlugin (TaskClass task, MutablePicoContainer container) {
      Class<? extends Plugin> plugin = getPlugin(task);
      Plugin instance = (Plugin) container.getComponent(plugin);  
      if ( instance != null ) return instance;
      container.as(Characteristics.CACHE).addComponent(plugin);
      return (Plugin) container.getComponent(plugin);  
   }

   private final static List<Class<? extends Plugin>> plugins = new ArrayList<Class<? extends Plugin>>();
   
   public static List<Class<? extends Plugin>> getPlugins () { return plugins; }
   
   public static Class<? extends Plugin> getPlugin (TaskClass task) {
      String plugin = task.getEngine().getProperty(getActivity(task)+"@plugin");
      try { 
         Class<? extends Plugin> cls = (Class<? extends Plugin>) Class.forName(plugin);
         plugins.add(cls);
         return cls;
      } catch (ClassNotFoundException e) {
         throw new RuntimeException("Plugin not found for task "+task, e);
      }
   }
   
   public static String getActivity (TaskClass task) {
      String activity = task.getProperty("@activity");
      return activity == null ? task.getPropertyId() : activity;
   }
   
   public static boolean isPlugin (TaskClass task) {
      return task.getEngine().getProperty(getActivity(task)+"@plugin") != null;
   }

}
