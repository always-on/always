package edu.wpi.always.cm;

import org.picocontainer.MutablePicoContainer;
import edu.wpi.disco.rt.Registry;

public class DummyCollaborationManager implements ICollaborationManager {

   @Override
   public void start (boolean allPlugins) {
   }

   @Override
   public void addRegistry (Registry registry) {
   }

   @Override
   public MutablePicoContainer getContainer () {
      return null;
   }
}
