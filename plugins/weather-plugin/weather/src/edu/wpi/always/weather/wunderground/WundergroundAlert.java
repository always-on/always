package edu.wpi.always.weather.wunderground;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.*;

import edu.wpi.always.weather.*;

public class WundergroundAlert implements Alert{

	private String alertMessage;
	private WundergroundHelper helper;

	WundergroundAlert(String zip) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		helper = new WundergroundHelper("alerts", zip);

		fillData();
		if(alertMessage==null)
			throw new IOException("No alert found");
	}

	private void fillData() throws XPathExpressionException {
		String pathString = "/response/alerts/text()";
		String alert = helper.getData(pathString);

		System.err.println(alert);
		if(alert.isEmpty())
			alertMessage = null;
		else
			alertMessage = "There is an active severe alert in your area.";
	}

	public String getMessage() {
		return alertMessage;
	}

}
