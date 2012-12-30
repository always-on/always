package edu.wpi.always.weather;

import edu.wpi.always.cm.*;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.*;
import org.picocontainer.MutablePicoContainer;

public class WeatherPluginRegistry implements SchemaRegistry, ComponentRegistry {

   @Override
   public void register (SchemaManager manager) {
      manager.registerSchema(WeatherSchema.class, true);
   }

   @Override
   public void register (MutablePicoContainer container) {
      container.addComponent(WeatherProvider.class,
            WundergroundWeatherProvider.class);
   }
}
