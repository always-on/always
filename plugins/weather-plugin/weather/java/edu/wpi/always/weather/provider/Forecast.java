package edu.wpi.always.weather.provider;

import org.joda.time.LocalDate;

public interface Forecast {

   String getSummary ();

   LocalDate getDate ();

   int getDaysApartFromToday ();
}
