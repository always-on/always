package DialogueRuntime.com;

import java.util.*;

//To create a module:
//  1. Create a new instance.
//  2. Subscribe (typical usage)
//  3. startup() --will throw an exception if initialization problem.
//  ...
//  N. reset() --to effect reset (flush queues, etc.)
//  ...
//  X. shutdown() --cleanup & halts thread.

//Note: Events are not Strings, assumed to be single-line XML messages.

public abstract class CMModule {
    protected boolean DEBUG = false;
    public void setDebug(boolean DEBUG) { this.DEBUG=true; }
    
    protected String name;
    public CMModule(String name) { this.name=name; }
    public String getName() { return name; }

    private Hashtable knownModules=new Hashtable();
    public void register(String name,CMModule subscriber) {
        knownModules.put(name,subscriber);
    }
    protected void sendEvent(String name,String event) {
        if(DEBUG) System.out.println(getClass().getName()+" sending "+event+" to "+name);
        CMModule module=(CMModule)knownModules.get(name);
        if(module==null) {
	    if(DEBUG)
		System.out.println("CMModule.sendEvent:Trying to send to unregistered module "+name);
        } else
            module.addEvent(event);
    }
    protected CMModule getModule(String name) {
        return (CMModule)knownModules.get(name);
    }
    
    public void startup() throws Exception {
	try {
    	    initialize();
    	}catch(Exception e) {
	    throw new Exception("CMModule.startup:"+e);
    	};
    }
    
    public synchronized void shutdown() { }
    	
    //---Implementation
    protected void initialize() throws Exception {}

    /*
    public void addLogEntry(int severity, String message) {
	System.out.println("LOG("+severity+"): "+message);
	//&&& TBD
        //Log.addEntry(severity,getClass().getName(),message);
    }
    */

	//---Event Queue handling
    protected Vector eventQueue=new Vector();
    
    public void addEvent(String event) {
        if(DEBUG) System.out.println(getClass().getName()+" received event "+event);
	//synchronized(eventQueue) {
	    eventQueue.addElement(event);
	    //}
    }

    /*
    public String popEvent() {
	synchronized(eventQueue) {
	    if (!eventQueue.isEmpty()) {
		String event=(String)eventQueue.firstElement();
		eventQueue.removeElementAt(0); 
		return event;
	    } else
		return null;
	}
	} */

	//Should not restart thread...just cause queues to be flushed, etc.
    public synchronized void reset() {
	//synchronized(eventQueue) {
	eventQueue.removeAllElements();
	//	}
        killAllTimers();
    }

    public void flushEvents() {
	synchronized(eventQueue) {
	    eventQueue.removeAllElements();
	}
    }
    
    public synchronized  void killAllTimers() {
	//synchronized(timers) {
	for(int i=0;i<timers.size();i++) {
	    WaitThread timer=(WaitThread)timers.elementAt(i);
	    timer.stop();
	};
	timers.removeAllElements();
	//}
    }

    protected Vector timers=new Vector();
    protected int nextTimerInstanceID=0;

    //Timer event:  <TIMER ID="id" INSTANCE="instance"/>

    //Wait for waitTime ms, then inject a TimerEvent into *this* modules eventQueue.
    public synchronized int setTimerEvent(long waitTime,int ID) {
	//synchronized(timers) {
	    WaitThread thread=new WaitThread(waitTime,ID,nextTimerInstanceID);
	    timers.addElement(thread);
	    thread.start();    
	    return nextTimerInstanceID++;
	    //}
    }
    
    public synchronized void cancelTimerEvent(int instanceID) {
	//synchronized(timers) {
	for(int i=0;i<timers.size();i++) {
	    WaitThread timer=(WaitThread)timers.elementAt(i);
	    if(timer.instanceID==instanceID) {
		timer.cancel();
		timers.removeElementAt(i);
	    };
	};
	//}
    }
    
    class WaitThread extends Thread {
        private long waitTime;
        private int ID;
        private int instanceID;
	private boolean active=true;
        public WaitThread(long waitTime,int ID,int instanceID) {
            this.waitTime=waitTime;
            this.ID=ID;
            this.instanceID=instanceID;
        }
        public void run() {
            try {
                sleep(waitTime);
            }catch(Exception e){};
            synchronized(CMModule.this) {
		if(active)
		    addEvent("<TIMER ID=\""+ID+"\" INSTANCE=\""+instanceID+"\"/>");
		//synchronized(timers) {
		timers.removeElement(this);
		//}
            };
        }
	public void cancel() { active=false; }
    }

    //Utilities for XML message parsing..assumes all trimmed and well-formed.
    //strips double-quotes
    public static String parseAttribute(String event,String attribute) {
	int start=event.indexOf(" "+attribute+"=");
	if(start<0) return null;
	start+=(1+attribute.length()+1+1);
	int end=event.indexOf('\"',start);
	return event.substring(start,end);
    }
}
