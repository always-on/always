This folder contains the Eclipse Java project that implements the
"server" part of the agent, which includes the collaboration manager,
the relationship manager and the semantic network.  The functions of
the toplevel packages in edu.wpi.always are:

  client - communicating with the C# client

  cm - collaboration manager (see docs/CollaborationManager.pdf)

  owl - semantic network, including people, places and calendar

  rm - relationship manager [as of 1/3/12, this component is disabled]

  user - code for managing user-specific information

The main class edu.wpi.always.Always starts the complete system with
all the plugins listed at user/Init.xml.  [As of 1/3/12, this main
class is disabled.  Use main classes for each plugin]

For more information contact rich@wpi.edu.
