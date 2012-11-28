package edu.wpi.always.weather.wunderground;

import java.io.*;
import java.net.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class WundergroundHelper {
	private static final String API_KEY = "cd3b2dc51dd67e26";

	private final Document doc;
	WundergroundHelper(String feature, String zip) throws IOException, ParserConfigurationException, SAXException {
		String urlString = "http://api.wunderground.com/api/" + API_KEY + "/" + feature + "/q/" + zip + ".xml";

		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();

		doc = parseXML(is);

		is.close();
	}

	protected String getData(String path) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();    
		XPathExpression expr = xPath.compile(path);

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		 String content = nodes.item(0).getNodeValue();
		 if(content.replaceAll("\\s+", "").isEmpty())
			 return "";
		 return content;
	}

	private Document parseXML(InputStream stream) throws ParserConfigurationException, SAXException, IOException  {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;

		objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();

		doc = objDocumentBuilder.parse(stream);

		return doc;
	}
}
