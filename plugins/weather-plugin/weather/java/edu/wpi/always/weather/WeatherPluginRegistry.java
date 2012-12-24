package edu.wpi.always.weather;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.schemas.registries.SchemaRegistry;
import edu.wpi.always.weather.wunderground.WundergroundWeatherProvider;
import edu.wpi.disco.rt.*;
import org.picocontainer.MutablePicoContainer;

public class WeatherPluginRegistry implements SchemaRegistry, PicoRegistry {

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
