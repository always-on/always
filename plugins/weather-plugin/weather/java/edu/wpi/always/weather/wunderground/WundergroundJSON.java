package edu.wpi.always.weather.wunderground;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.Person;
import edu.wpi.always.weather.provider.*;
import java.util.*;

public class WundergroundJSON {
	CurrentWeather currentWeather;
	Radar radar;
	Alert alert;
	Almanac almanac;
	Forecast[] forecast;
	static transient UserModel model; // transient so not included in JSON

	Map<String, CurrentWeather> interestCities;
	Map<String, CurrentWeather> friendsCities;
	
	static final int forecastSize = 2;
	private static final Map<String, String> CITY_ZIP_MAP = createCityMap();
	private static final Map<String, String> FRIENDS_ZIP_MAP = createFriendMap();
 
	public WundergroundJSON(String s, UserModel model) throws Exception {
	   this.model = model;
	   System.out.println("Getting weather for "+model.getUserName());
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


	private static Map<String, String> createCityMap() {
	    Map<String, String> result = new HashMap<String, String>();
	    result.put("Seattle", "98106");
	    result.put("Miami", "33125");
	    result.put("San Francisco", "94105");
	    result.put("Boston", "2101");
	    result.put("New York", "10001");
	    result.put("New Orleans", "70112");
	    return Collections.unmodifiableMap(result);
	}
	private static Map<String, String> createFriendMap() {
	    Map<String, String> result = new HashMap<String, String>();
	    if(model != null) {
	       Person[] people = model.getPeopleManager().getPeople();
	       if(people != null) {
	          for(Person person : people) {
	             result.put(person.getName(), person.getLocation().getZip());
	          }
	       }
	    }
	    return Collections.unmodifiableMap(result);
	}
}

