package edu.wpi.always.weather.wunderground;

import com.google.gson.*;
import java.io.FileWriter;
import java.text.*;
import java.util.Date;

public class WundergroundParser {
	//default
	private static String zip = "01609";
	
	// also see bin/get
	public static void main(String[] args) {
		checkArgs(args);
		
		try {
			WundergroundJSON weather = new WundergroundJSON(zip);
			// make easier to read for debugging
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(weather);
			JsonUtilities.writeToFile(json);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/*
	 * if argument 1 is a valid zip code, use that
	 * otherwise, use the default
	 */
	private static void checkArgs(String[] args){
		if(args.length>0){
			if(validateZip(args[0]))
				zip = args[0];
		}
	}
	
	/*
	 * validate zip code
	 */
	private static boolean validateZip(String zip){
		return zip.matches("\\d{5}((-)?\\d{4})?");
	}
}

class JsonUtilities{
   
   // write to user folder
	static final String filePath = "../../../user/weatherData/";
	
	static void writeToFile(String s) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(filePath + getFileName());
			fstream.write(s);
			// Close the output stream
			fstream.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/*
	 * Get today's date + .json, use that as the file name
	 */
	private static String getFileName(){
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
		Date date = new Date();
		return dateFormat.format(date)+".json";		
	}
}