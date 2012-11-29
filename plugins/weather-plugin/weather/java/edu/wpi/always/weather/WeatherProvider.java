package edu.wpi.always.weather;

public interface WeatherProvider {
	CurrentWeather getCurrentWeather(String zip);

	Almanac getAlmanac(String zip);

	Alert getAlert(String zip);

	Radar getRadar(String zip);

	Forecast getForcast(String zip, int howManyDaysLater);
}
