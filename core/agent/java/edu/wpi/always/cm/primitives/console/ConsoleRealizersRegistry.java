package edu.wpi.always.cm.primitives.console;

import edu.wpi.always.PicoRegistry;
import org.picocontainer.MutablePicoContainer;

public class ConsoleRealizersRegistry implements PicoRegistry {

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(ConsoleGazeRealizer.class);
      container.addComponent(ConsoleFaceTrackerRealizer.class);
      container.addComponent(ConsoleSpeechRealizer.class);
      container.addComponent(ConsoleMenuRealizer.class);
   }
}
