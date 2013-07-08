package edu.wpi.disco.rt.behavior;

public interface BehaviorBuilder {

   Behavior build ();

   BehaviorMetadata getMetadata ();
   
   void setNeedsFocusResource (boolean focus);
}
