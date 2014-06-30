package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.schema.SchemaBase;

public class SkypeInterruptSchema extends SchemaBase {
  
   public SkypeInterruptSchema () {
      super(null, null); 
   }

   // used in _SkypeInterruption script
   public static String CALLER = "someone you know";
   
   // Note: you can set other static variables here
   // and similarly refer to them in SkypeSchema

   // public method for testing
   // e.g., in command line of Session window, type:
   //   eval edu.wpi.always.cm.schemas.SkypeInterruptSchema.interrupt()
   //
   public static boolean interrupt () {
      // see definition of _SkypeInterruption in edu.wpi.always.resources.Activities.d4g.xml
      return SessionSchema.interrupt("_SkypeInterruption");
   }
   
   // run method called once/minute (set in StartupSchemas)
   
   @Override
   public void run () {
      // incoming call detection code goes where false test is below
      // NB: I have gone to some trouble to make sure that plugin projects depend on the core,
      // but not vice-versa.  Please include whatever code you need to detect incoming calls here
      // (and put rest in SkypeSchema).
      // If you need supporting libraries, add jar files to agent/lib folder
      // and native libraries to agent/lib/win32-x86-64 (and use Native.loadLibrary()).
      if ( false ) { 
         CALLER = "John"; // set with actual name (reverse lookup in user model?)
         interrupt();
      }
   }
   
}
  