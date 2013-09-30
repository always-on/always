package DialogueRuntime;

/* The events of interest from a dialogue session. */
public class DialogueListener {
    public static enum EventResponse { ABORT,  RESTART_TOP, RESTART_DESIGNATED, REPEAT_TIMEOUT2, PICK_FIRST };
    public static enum StateNavigation { GO, PUSH, POP };
    public static enum TerminationReason { NORMAL, USER_EXIT, TIMEOUT, INTERNAL_ERROR, STALE_SESSION, ADMIN_REQUEST, ADMIN_KILLED};

    /* Could argue casting these into ClientEvent subclass objects. */
    public void clientEvent(String xml) {}
    
    /* nv is GO, PUSH or POP. */
    public void stateEntryEvent(StateNavigation nv,String netname,String statename) {}
       
       /* Dialogue session termination. */
    public void terminationEvent(TerminationReason reason) {}
    
    /* Can return REPEAT_TIMEOUT2 (default) or PICK_FIRST (pick first
       User option) or ABORT. */
    public EventResponse timeout1(String netname,String statename) { return EventResponse.REPEAT_TIMEOUT2; }
    
    /* Can return ABORT (default) or REPEAT_TIMEOUT2 or 
       PICK_FIRST (pick first User option). */
    public EventResponse timeout2(String netname,String statename) { return EventResponse.ABORT; }
    
    /* Can return ABORT, RESTART_TOP, RESTART_DESIGNATED */
    public EventResponse exception(Exception e) { return EventResponse.ABORT; }
}
