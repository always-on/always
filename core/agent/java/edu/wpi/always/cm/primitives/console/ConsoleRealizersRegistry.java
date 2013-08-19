package edu.wpi.always.cm.primitives.console;

import org.picocontainer.MutablePicoContainer;

import edu.wpi.disco.rt.util.ComponentRegistry;

public class ConsoleRealizersRegistry implements ComponentRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(ConsoleGazeRealizer.class);
      container.addComponent(ConsoleFaceTrackerRealizer.class);
      container.addComponent(ConsoleSpeechRealizer.class);
      container.addComponent(ConsoleMenuRealizer.class);
   }
}
