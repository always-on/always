**NB: This may not correspond exactly with current code organization -CR 5/13/13

-----Files-------
models	- weather model file
src	- java code that can get live data
bin	- a shell script to get fresh daily data
lib	- Google�s GSON library
test	- test dialogue
weatherDate	- json file that have that day�s weather data.  Test1.json file is used by the test dialogue, in order to keep the data static.


-----Resources-------
-Weather API
http://www.wunderground.com/weather/api/
Current API Key:  cd3b2dc51dd67e26
You should register for a new account and get a new API key.  Then, update the API key in AbsWeatherService.java

-Gson
Google�s JSON library
https://sites.google.com/site/gson/gson-user-guide


-----Know Issues-------
1.	Calls per minute for the free API service is 10 calls/minute.  Currently, WeatherJSON.java makes 11 calls/minute, which will use the �raindrops�.  Read more at http://www.wunderground.com/weather/api/.
Need to find a way to aggregate the queries, spread the calls to several minute (sleep function), or reduce the un-necessary information.

2.	Alert.java always returns there�s no alert.  Need to modify the method getAlert() or pathToData(), in order to handle the case when there is no such XML node.


-----To-do List-------
1.	Change package name to edu.wpi.weather
2.	Organize the dialogue, reduce the unnecessary dialogues 




-----Contact Info-------
Zhaochen "JJ" Liu
zhaochen.jj.application@gmail.com
