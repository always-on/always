package edu.wpi.always.cm.primitives.console;

import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.MutablePicoContainer;

public class ConsoleRealizersRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(ConsoleGazeRealizer.class);
      container.addComponent(ConsoleFaceTrackerRealizer.class);
      container.addComponent(ConsoleSpeechRealizer.class);
      container.addComponent(ConsoleMenuRealizer.class);
   }
}
