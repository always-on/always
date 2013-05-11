package edu.wpi.always.cm;

import org.picocontainer.MutablePicoContainer;
import edu.wpi.disco.rt.Registry;

public interface ICollaborationManager {

   void start (boolean allPlugins);

   void addRegistry (Registry registry);
   
   MutablePicoContainer getContainer ();
}
