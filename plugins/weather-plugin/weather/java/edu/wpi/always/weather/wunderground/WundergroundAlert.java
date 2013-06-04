package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.provider.Alert;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class WundergroundAlert implements Alert {

   private String alertMessage;
   private WundergroundHelper helper;

   WundergroundAlert (String zip) throws IOException,
         ParserConfigurationException, SAXException, XPathExpressionException {
      helper = new WundergroundHelper("alerts", zip);
      fillData();
      if ( alertMessage == null )
         throw new IOException("No alert found");
   }

   private void fillData () throws XPathExpressionException {
      String pathString = "/response/alerts/text()";
      String alert = helper.getData(pathString);
      System.err.println(alert);
      if ( alert.isEmpty() )
         alertMessage = "There is no severe alert in your area.";
      else
         alertMessage = "There is an active severe alert in your area.";
   }

   @Override
   public String getMessage () {
      return alertMessage;
   }
}
