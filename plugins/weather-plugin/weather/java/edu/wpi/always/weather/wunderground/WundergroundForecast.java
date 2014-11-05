package edu.wpi.always.weather.wunderground;

import edu.wpi.always.user.UserUtils;
import edu.wpi.always.weather.provider.Forecast;
import org.joda.time.LocalDate;
import org.joda.time.format.*;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class WundergroundForecast implements Forecast {

   private transient final WundergroundHelper helper;
   private transient final LocalDate localDate;

   private final String summary;
   private final Date date;

   private static class Date {
      @SuppressWarnings("unused")
      private final String date;
      private final int daysApartFromToday;
      private Date (String date, int daysApartFromToday) {
         this.date = date;
         this.daysApartFromToday = daysApartFromToday;
      }
   }
   
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
         .forPattern("MM/dd/yyyy");

   WundergroundForecast (String zip, int howManyDaysLater) throws IOException,
         ParserConfigurationException, SAXException, XPathExpressionException {
      helper = new WundergroundHelper("forecast", zip);
      localDate = new LocalDate().plusDays(howManyDaysLater);
      date = new Date(DATE_FORMAT.print(localDate), howManyDaysLater);
      summary = forecastDescription(howManyDaysLater);
   }

   // 0 will return rest of today
   // 1 will return tomorrow
   // not supported for higher numbers!
   private String forecastDescription (int howManyDaysLater)
         throws XPathExpressionException {
      // a day and a night forecast period are generated for each day, 
      // so if currently daytime, then tomorrow's forecast is period[3] otherwise
      // period[2]. Unfortunately, I don't know exactly when wunderground switches.
      int period = (howManyDaysLater == 0 ? 1 :
         (howManyDaysLater + ((UserUtils.isEvening() || UserUtils.isNight()) ? 3 : 2)));
      String path = "/response/forecast/txt_forecast/forecastdays/forecastday["
            + period + "]/";
      return "The forecast for " + helper.getData(path+"title/text()") + " is "
       + helper.getData(path+"fcttext/text()");
   }

   @Override
   public String getSummary () {
      return summary;
   }

   @Override
   public LocalDate getDate () {
      return localDate;
   }

   @Override
   public int getDaysApartFromToday () {
      return date.daysApartFromToday;
   }

}
