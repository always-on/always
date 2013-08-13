package edu.wpi.always.weather.provider;

public interface WeatherProvider {

   CurrentWeather getCurrentWeather (String zip);

   Almanac getAlmanac (String zip);

   Alert getAlert (String zip);

   Radar getRadar (String zip);

   Forecast getForecast (String zip, int howManyDaysLater);
}
