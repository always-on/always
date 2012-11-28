package edu.wpi.always.weather;

import org.joda.time.*;

public interface Forecast {

	String getSummary();

	LocalDate getDate();

	int getDaysApartFromToday();

}
