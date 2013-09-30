package DialogueRuntime.com;

import java.net.*;
import java.io.*;

//Exchanges XML messages over the network with a single client.
//Sends <READY MODULE=id/> to all known modules when client has connected.
//Sends <NOTREADY MODULE=id/> to all known modules when client has disconnected.

public abstract class NetServerModule extends CMModule implements Runnable {
    private BufferedReader in = null;
    private ServerSocket server;

    public NetServerModule(String name) { super(name); }

    //Just sets up the accept..
    public void initialize(int PORT) throws Exception {
	if(DEBUG) System.out.println(getName()+": initialize "+PORT);
	// start networking
	server=new ServerSocket(PORT);
	new Thread(this).start();
    }

    public void run() {
	while(true) {
	    try {
		if(DEBUG) System.out.println(getName()+": waiting for connect...");
		Socket client=server.accept();
		if(DEBUG) System.out.println(getName()+": got a client.");
		sendStatus("<READY MODULE=\""+getName()+"\"/>");
		InputStream ins=client.getInputStream();
		InputStreamReader reader=new InputStreamReader(ins);
		BufferedReader bufr=new BufferedReader(reader);  
		outs=client.getOutputStream();
		//Now read messages forever in this thread...
		String input;
		while((input=bufr.readLine())!=null) {
		    sendInput(input.trim());
		}
	    }catch(Exception e) {
		if(DEBUG) System.out.println(getName()+":NetServerModule.run:"+e);
	    };
	    //Only get to here on error of somekind - assume disconnect
	    sendStatus("<NOTREADY MODULE=\""+getName()+"\"/>");
	    outs=null;
	}
    }

    //Sent <READY...> and <NOTREADY...>
    public synchronized void sendStatus(String event) {}

    //Sent all inputs from net client...
    public synchronized void sendInput(String event) {}

    protected OutputStream outs=null;

    //override...
    public synchronized void addEvent(String event) {
	if(outs==null) return;
	try {
	    outs.write((event.replace('\n',' ')+"\n").getBytes());
	    outs.flush();
	}catch(Exception e) {
	    if(DEBUG) System.out.println(getName()+":NetServerModule.addEvent:"+e);
	    sendStatus("<NOTREADY MODULE=\""+getName()+"\"/>");
	}	    
    }
}
