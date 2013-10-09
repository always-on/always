package DialogueRuntime;

public enum LogEventType {
    //To do - make consistent with prior enums
    STATE_CHANGE(0), 
    USER_INPUT(1), 
    INTERNAL_ERROR(2),
    TERMINATION(3);

    private int DBCode;
    LogEventType(int DBCode) { this.DBCode=DBCode; }
    public int getDBCode() { return DBCode; }
}
