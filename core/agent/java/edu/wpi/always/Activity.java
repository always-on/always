package edu.wpi.always;

/**
 * Activities are the toplevel elements in an interaction session.
 */
public class Activity {
  
   /**
    * Construct an activity.
    * 
    * @param plugin The plugin that implements this activity
    * @param name The name of this activity (relative to plugin)
    * <p>
    * The following are the four metadata parameters of an activity as described in
    * "Activity Planning for Long-Term Relationships", W. Coon, C. Rich and C. Sidner
    * (to be submitted).  They are all non-negative integers.
    * 
    * FIXME review closeness values in RM
    * 
    * @param required The minimum required closeness (see {@link edu.wpi.always.rm.Closeness})
    * @param duration The expected duration (in minutes)
    * @param instrumental The instrumental utility resulting from this activity
    * @param relational The relational utility (increase in closeness) resulting
    *                    from this activity
    */
   public Activity (Class<? extends Plugin> plugin, String name,
         int required, int duration, int instrumental, int relational) {
      this.plugin = plugin;
      this.name = name;
      this.required = required;
      this.duration = duration;
      this.instrumental = instrumental;
      this.relational = relational;
   }
    
   private final Class<? extends Plugin> plugin;
   private final String name;
   private final int required, duration, instrumental, relational;
   
   public Class<? extends Plugin> getPlugin () { return plugin;  }
   public String getName () { return name; }
   public int getRequired () { return required; }
   public int getDuration () { return duration; }
   public int getInstrumental () { return instrumental; }
   public int getRelational () { return relational; }

   @Override
   public String toString () { return plugin+":"+name; }
 }
