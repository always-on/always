package edu.wpi.always.weather;

import org.picocontainer.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.*;
import edu.wpi.always.weather.wunderground.*;

public class WeatherPluginRegistry implements SchemaRegistry, PicoRegistry{
	@Override
	public void register(SchemaManager manager) {
		manager.registerSchema(WeatherSchema.class, true);
	}

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(WeatherProvider.class, WundergroundWeatherProvider.class);
	}

}
