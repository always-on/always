package DialogueRuntime;

/* When the DialogueSession starts, this is sent a null event.
	Thereafter it is sent every event from the client until 
	initialization is complete. Returns 'true' when initialization
	is complete, 'false' otherwise. 
	NOTE: if a DialogueInitializer is not specified, then
	initialization immediately terminates. */

public interface DialogueSessionInitializer {
    public boolean handleInitializationEvent(String event,DialogueSession session) throws Exception;
}

