package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.provider.*;
import java.util.*;

public class WundergroundJSON {
	CurrentWeather currentWeather;
	Radar radar;
	Alert alert;
	Almanac almanac;
	Forecast[] forecast;

	Map<String, CurrentWeather> interestCities;
	Map<String, CurrentWeather> friendsCities;
	
	static final int forecastSize = 2;
	private static final Map<String, String> CITY_ZIP_MAP = createCityMap();
	private static final Map<String, String> FRIENDS_ZIP_MAP = createFriendMap();
 
	public WundergroundJSON(String s) throws Exception {
		fillData(s);
	}
	
	private void fillData(String zip) throws Exception {
		currentWeather = new WundergroundCurrentWeather(zip);
		radar = new WundergroundRadar(zip);
		almanac = new WundergroundAlmanac(zip);
		alert = new WundergroundAlert(zip);
		
		//forecast
		forecast = new Forecast[forecastSize];
		for(int i=0; i<forecastSize; i++)
			forecast[i] = new WundergroundForecast(zip, i);
		
		//interest cities
		interestCities = new HashMap<String, CurrentWeather>();
		for (Map.Entry<String, String> entry : CITY_ZIP_MAP.entrySet()){
			interestCities.put(entry.getKey(), new WundergroundCurrentWeather(entry.getValue()));
		}
		
		//friends
		friendsCities = new HashMap<String, CurrentWeather>();
		for (Map.Entry<String, String> entry : FRIENDS_ZIP_MAP.entrySet()){
			friendsCities.put(entry.getKey(), new WundergroundCurrentWeather(entry.getValue()));
		}
	}

	/* TODO READ FROM ONTOLOGY */
	private static Map<String, String> createCityMap() {
	    Map<String, String> result = new HashMap<String, String>();
	    result.put("Seattle", "98106");
	    result.put("Miami", "33125");
	    result.put("San Francisco", "94105");
	    return Collections.unmodifiableMap(result);
	}
	/* TODO COMBINE WITH PREVIOUS FUNCTION*/
	private static Map<String, String> createFriendMap() {
	    Map<String, String> result = new HashMap<String, String>();
	    result.put("Mary", "85301");
	    result.put("Lucy", "99726");
	    return Collections.unmodifiableMap(result);
	}
}

