package alwaysAvailableCore;


import DialogueRuntime.*;

//Default ECA session initialization behavior

public class AAECADialogueSessionInitializer implements DialogueSessionInitializer {
    public boolean handleInitializationEvent(String event,DialogueSession session) throws Exception {
    	System.out.println("Initialization routine got event: " + event);
    	if(event==null) {
	    return false; //do nothing on startup
	} else if(event.startsWith("<USER_LOGIN ")) {
		System.out.println("Got login message from client ");
			//String id = Utilities.extractArgument(event,"ID");
			//System.out.println("userid entered " + id);
			//session.getRuntime().setProperty("THIS_SESSION_ID",""+id);
		
			
   			int session_id = session.getRuntime().getStore().addSession(ServerConstants.Media.ECA);
   			
   			System.out.println("SESSIONID = "+session_id);
   			session.getRuntime().setSessionID(session_id);
   			session.getRuntime().getStore().setSessionID(session_id);
   			session.getRuntime().initialize(true);
   			session.getClient().write("<SESSION OK=\"true\"/>"); //Clears login screen	
   			return true; //ID and status are valid and hence return true
	} else if (event.startsWith("<USER_INPUT MENU=\"LOGO\"")) {
		session.getClient().write("<SESSION OK=\"true\"/>"); //Clears login screen	
		session.getRuntime().getStore().addSession(ServerConstants.Media.ECA);
		return true; 
	} else {
		return false;
    }
    }

} //