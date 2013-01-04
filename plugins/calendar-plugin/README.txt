This folder contains the implementation of the plugin for using the
calendar, which consists of one Eclipse Java project (calendar) and
one Visual Studio C# project (Calendar.UI).

This is an example of a plugin with an application specific GUI, but
most of the logic on the *server* side.  The dialogue is implemented
using the state machine implementation in edu.wpi.always.cm.dialog.

To run the server side of this plugin by itself, use the main class of
edu.wpi.always.calendar.CalendarPlugin.

To run the client side of this plugin, use the Calendar solution with
startup project Calendar.Startup.

[As of 1/3/12, this plugin runs but is under development]

For more information contact rich@wpi.edu.
