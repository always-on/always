This folder contains a subfolder for each plugin:

  calendar-plugin - plugin for using the calendar (example of
                    plugin with application-specific GUI, but
                    most of logic on server side)

  rummy-plugin - plugin for playing rummy (example of plugin with
                 appliation-specific GUI and most of logic on client side)

  story-plugin - plugin for audio story recording (example of plugin
                 with application-specific GUI, but most of logic
                 on server side)

  weather-plugin - plugin for discussing weather (example of purely 
  		   dialogue plugin with no application-specific GUI)

Each plugin folder contains one Eclipse Java project and (optionally)
a Visual Studio C# solution and one or more projects.  Please see
README.txt files in each plugin folder for more details.

The Visual Studio C# solution Plugins.sln (with startup project
Plugins.Startup) starts the client with all of the client plugin
components. 

For more information contact rich@wpi.edu.
