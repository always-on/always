package DialogueRuntime;

/* Represents interface with client. 
   Subclasses will have methods for obtaining pedometer steps, loading
   characters or voices, etc., for specialized apps. */

public abstract class Client {
    protected SessionRuntime runtime;
    public void registerRuntime(SessionRuntime r) { runtime=r; }

    protected boolean DEBUG=false;
    public void setDEBUG(boolean d) { DEBUG=d; }
    public void debug(String msg) {
    	if(DEBUG) runtime.debug(msg);
    }

    /* ----- INPUT METHODS ----- */
    
    /* Associates a listener with XML message arrival events. */
    protected ClientListener clientListener=null;
    public void setListener(ClientListener c) { clientListener=c; }
    
    /* ----- OUTPUT METHODS ----- */
    
    /* Lowest level output. */
    public void write(String xml) throws Exception {
    	debug("Client.write(\""+xml+"\")");
    }
    
    /* Normal termination of client connection. */
    public void close() {
    	debug("Client.close()");
    }
    
    /* Abnormal session termination. ECA: cause client app to exit, 
       displaying the stated reason in a dialogue box. */
    public void exit(String reason) {
    	System.out.println("we're exiting in client.java");
    	debug("Client.exit(\""+reason+"\")");
    }
    
    /* Cause client computer to shutdown. */
    public void shutdown() throws Exception {
		write("<SESSION SHUTDOWN=\"DIE\" />");
    }
    /* The following are really just meaningful for ECAClient. */
    public void perform(String xml,DialogueSession session) throws Exception {}
    public void flush() throws Exception {}

    /* ------- BEGINS STREAM OF INPUT EVENTS ------- */
    public void start() {
    	debug("Client.start()");
    }

    /* ------- UTILITY METHODS -------- */

    /* Given an XML tag, return value parg of <XXX ... arg="value" ...> */
    public static String extractArg(String tag, String arg) {
		int index = tag.indexOf(arg + "=");
		if (index < 0)
		    return null;
		int index2 = tag.indexOf('\"', index + arg.length() + 2);
		if (index2 < 0)
		    return null;
		return tag.substring(index + arg.length() + 2, index2);
    }

    // <USER_INPUT TEXT="text input"/> or <USER_INPUT MENU="23"/>
    public static String parseUserInput(String input) {
		if (input.startsWith("<USER_INPUT MENU")) {
		    return input.substring(18, input.indexOf('\"', 18));
		} else if (input.startsWith("<USER_INPUT TEXT")) {
		    return input.substring(18, input.indexOf('\"', 18));
		} else
		    return null;
	    }
    }
