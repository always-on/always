package edu.wpi.always.weather.wunderground;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.*;

import edu.wpi.always.weather.*;

public class WundergroundAlmanac implements Almanac{
	private RecordTemp recordLow;
	private RecordTemp recordHigh;

	private WundergroundHelper helper;

	WundergroundAlmanac(String zip) throws IOException, ParserConfigurationException, SAXException, NumberFormatException, XPathExpressionException {
		helper = new WundergroundHelper("almanac", zip);

		recordLow = fillRecordTemp(HighLow.temp_low);
		recordHigh = fillRecordTemp(HighLow.temp_high);
	}

	private RecordTemp fillRecordTemp(HighLow hl) throws NumberFormatException,XPathExpressionException{
		return new RecordTemp(Integer.parseInt(getRecordYear(hl.toString())),
				Integer.parseInt(getTemp(hl.toString(), "normal")),
				Integer.parseInt(getTemp(hl.toString(), "record")));
	}

	private String getTemp(String lowOrHigh, String normalOrRecord) throws XPathExpressionException {
		String pathString = "/response/almanac/" + lowOrHigh + "/" + normalOrRecord + "/F/text()";
		return helper.getData(pathString);
	}

	private String getRecordYear(String lowOrHigh) throws XPathExpressionException {
		String pathString = "/response/almanac/" + lowOrHigh + "/recordyear/text()";
		return helper.getData(pathString);
	}
	
	@Override
   public RecordTemp getRecordLow(){
		return recordLow;
	}
	
	@Override
   public RecordTemp getRecordHigh(){
		return recordHigh;
	}
	
	enum HighLow{
		temp_high, temp_low
	}

}


