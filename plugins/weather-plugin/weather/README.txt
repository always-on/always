A plugin that talks to the user about the current weather.

The main class edu.wpi.always.weather.wunderground.WundergroundParser downloads fresh data to a
time-stamped JSON file in always/user/weatherData folder.

TODO: WundergroundJSON needs to be changed so that it uses the ontology.

Use always/agent/bin/always-disco to run test cases in test folder.

Note resource edu.wpi.always.weather.resources.Weather.owl is a hand-written file that demonstrates
how to add a plugin-specific property to the user model (see WeatherPlugin.java).
It is not really used in the interaction, but is included as coding example.