package edu.wpi.always.weather;

public interface CurrentWeather {
	String getZip();

	String getWeatherCondition();
	String getTemperature();
	String getHumidity();
	String getLocationName();
}
