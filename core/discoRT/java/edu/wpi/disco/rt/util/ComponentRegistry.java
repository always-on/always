package edu.wpi.disco.rt.util;

import edu.wpi.disco.rt.Registry;
import org.picocontainer.MutablePicoContainer;

public interface ComponentRegistry extends Registry {

   void register (MutablePicoContainer container);
}
