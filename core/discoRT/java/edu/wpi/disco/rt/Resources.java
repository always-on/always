package edu.wpi.disco.rt;

import java.util.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;

/**
 * This is part of implementation to, in effect, support an extensible enumeration for resources.
 */
public class Resources implements Resource { 

   /**
    * Resource for Disco focus.
    * 
    * @see FocusRequestBehavior
    */
   public static final Resource FOCUS = new Resource() {
      @Override
      public String toString() { return "FOCUS"; }
   };      

    /**
    * Resource for speaking.
    * 
    * @see SpeechBehavior
    */
   public static final Resource SPEECH = new Resource() {
      @Override
      public String toString() { return "SPEECH"; }
   };   
   
    /**
    * Resource for menu (optional).
    * 
    * @see MenuBehavior
    */
   public static final Resource MENU = new Resource() {
      @Override
      public String toString() { return "MENU"; }
   };   

    /**
    * Resource for extension menu (optional).
    * 
    * @see MenuBehavior
    */
   public static final Resource MENU_EXTENSION = new Resource() {
      @Override
      public String toString() { return "MENU_EXTENSION"; }
   };   
   
   public static Resource[] values = new Resource[] { FOCUS, SPEECH, MENU, MENU_EXTENSION };

   public static Resource[] values () { return values; }

   /**
    * Return the idle behavior for given resource, or null.
    */
   public PrimitiveBehavior getIdleBehavior (Resource resource) {
      return ( resource == MENU ) ? // no idle behavior for MENU_EXTENSION 
         new MenuBehavior(Collections.<String>emptyList()) // sic not MenuBehavior.EMPTY 
         : null;
   }
   
}
