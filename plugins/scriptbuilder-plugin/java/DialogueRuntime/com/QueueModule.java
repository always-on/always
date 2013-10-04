package DialogueRuntime.com;

import java.util.*;

public class QueueModule extends ThreadModule {
    public QueueModule(String name) { super(name); }

    //Still to be implemented: what to do with each event...
    protected void processEvent(String event) throws Exception {}
  
    @Override
	protected void process() { //Called repeatedly during run.
	//System.out.println("---QM.process 1");
        String event=null;
        if (!eventQueue.isEmpty()) {
	    try {
		event=(String)eventQueue.firstElement();
		processEvent(event);
	    }catch(Exception e) {
		if(DEBUG) System.out.println(getName()+":QueueModule.process:"+e);
	    };
	    synchronized(eventQueue) {
		if(!eventQueue.isEmpty() && event==(String)eventQueue.firstElement())
		    eventQueue.removeElementAt(0); //don't remove until processed.
	    }
        };
	//System.out.println("---QM.process 2");
        if(eventQueue.isEmpty()) 
	    synchronized(eventQueue) {
		eventQueue.notify();   //Wake up shutdown thread, if any.
		try {
                    eventQueue.wait();
                    //Go to sleep until something new arrives.
                }catch(Exception we) {};
            };
	//System.out.println("---QM.process 3");
    }
    /*mine 
	//String event=popEvent();
        if (event!=null) {
	    try {
	    //System.out.println("QueueModule.process, event="+event);
		processEvent(event);
	    }catch(Exception e) {
		//addLogEntry(10,getName()+".ProcessingEvent: "+e);
		if(DEBUG) System.out.println(getName()+":QueueModule.process:"+e);
	    };
        };
	synchronized(eventQueue) {
	    if(eventQueue.isEmpty()) 
		this.notify();   //Wake up shutdown thread, if any.
	    try {
		this.wait();
		//Go to sleep until something new arrives.
	    }catch(Exception we) {};
	};
    }
    */
}
