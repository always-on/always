package edu.wpi.always.weather.provider;

public interface CurrentWeather {

   String getZip ();

   String getWeatherCondition ();

   String getTemperature ();

   String getHumidity ();

   String getLocationName ();
}
