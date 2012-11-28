package edu.wpi.always.weather.wunderground;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.*;

import edu.wpi.always.weather.*;

public class WundergroundRadar implements Radar{

	private String radarURL;
	private WundergroundHelper helper;

	WundergroundRadar(String zip) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		helper = new WundergroundHelper("radar", zip);

		radarURL = getImageUrl();
	}

	private String getImageUrl() throws XPathExpressionException {
		String pathString = "/response/radar/image_url/text()";
		return helper.getData(pathString);
	}
	
	@Override
	public String getImageURL(){
		return radarURL;
	}

}
