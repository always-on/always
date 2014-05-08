This folder contains the implementation of the plugin for audio story
recording, which consists of one Eclipse Java project (story) and one
Visual Studio C# project (Story.UI).

This is an example of plugin with an application-specific GUI, but
most of the logic on the *server* side.  The dialogue is implemented
using the state machine implementation in edu.wpi.always.cm.dialog.

To run the server side of this plugin by itself, use the main class of
edu.wpi.always.story.StoryPlugin.

To run the client side of this plugin, use the Story solution with
startup project Story.Startup.

[As of 1/3/12, this plugin is under development and does not
completely run.]

For more information contact rich@wpi.edu.

