package edu.wpi.always.weather.wunderground;

import edu.wpi.always.weather.provider.Radar;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

public class WundergroundRadar implements Radar {

   private String radarURL;
   private WundergroundHelper helper;

   WundergroundRadar (String zip) throws IOException,
         ParserConfigurationException, SAXException, XPathExpressionException {
      helper = new WundergroundHelper("radar", zip);
      radarURL = getImageUrl();
   }

   private String getImageUrl () throws XPathExpressionException {
      String pathString = "/response/radar/image_url/text()";
      return helper.getData(pathString);
   }

   @Override
   public String getImageURL () {
      return radarURL;
   }
}
