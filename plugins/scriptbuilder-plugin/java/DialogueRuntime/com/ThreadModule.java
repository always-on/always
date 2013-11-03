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

public abstract class ThreadModule extends CMModule implements Runnable {
    private boolean running = false;
    Thread moduleThread; 

    public ThreadModule(String name) { super(name); }

    @Override
	public void startup() throws Exception {
	super.startup();
	running=true;
	(moduleThread=new Thread(this)).start();    //Launches thread.
    }

    @Override
	public synchronized void shutdown() {
        running=false;    
        moduleThread.interrupt();
    }
    	
    //---Implementation
    @Override
	protected void initialize() throws Exception {}
    synchronized protected void process() throws Exception {}
    @Override
	public void run() {
	if(DEBUG) System.out.println(getName()+": starting thread.");
	while(running) {
	    try {
		process();
	    }catch(InterruptedException e1) {
		return;
	    }catch(Exception e) {
		if(DEBUG) 
		    System.out.println(getName()+": ThreadModule.run.process(): "+e);
		e.printStackTrace();		
	    };
	};
    }
	
	//---Event Queue handling
    @Override
	public void addEvent(String event) {
      synchronized(eventQueue) {
	  super.addEvent(event);
	  if(eventQueue.size()==1)   //Q was emtpy, so wake up consumer.
	      eventQueue.notify();
      };
    }
}
