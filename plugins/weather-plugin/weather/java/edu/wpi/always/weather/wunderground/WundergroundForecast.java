package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.provider.Forecast;
import org.joda.time.LocalDate;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class WundergroundForecast implements Forecast {

   private String summary;
   private WundergroundHelper helper;
   private LocalDate date;
   private int daysApart;

   WundergroundForecast (String zip, int howManyDaysLater) throws IOException,
         ParserConfigurationException, SAXException, XPathExpressionException {
      helper = new WundergroundHelper("forecast", zip);
      date = new LocalDate().plusDays(howManyDaysLater);
      daysApart = howManyDaysLater;
      summary = forecastDescription(howManyDaysLater);
   }

   // 0 will return rest of today
   // 1 will return tomorrow
   private String forecastDescription (int howManyDaysLater)
         throws XPathExpressionException {
      String pathString = "/response/forecast/txt_forecast/forecastdays/forecastday["
         + (howManyDaysLater + 1) + "]/" + "fcttext/text()";
      return helper.getData(pathString);
   }

   @Override
   public String getSummary () {
      return summary;
   }

   @Override
   public LocalDate getDate () {
      return date;
   }

   @Override
   public int getDaysApartFromToday () {
      return daysApart;
   }
}
