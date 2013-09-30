package DialogueRuntime;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

/* For ECA (e.g., FitTrack classic) clients. */
public class ECAClient extends Client implements Runnable {
    protected OutputStream outs;
    protected BufferedReader bufr;    

    public ECAClient(InputStream is,OutputStream os) {
		outs=os;
		InputStreamReader reader = new InputStreamReader(is);
		bufr = new BufferedReader(reader);	
    }

    public ECAClient(Socket socket) throws Exception {
    	if(socket == null)
    		return;
		outs = socket.getOutputStream();
		InputStream ins = socket.getInputStream();
		InputStreamReader reader = new InputStreamReader(ins);
		bufr = new BufferedReader(reader);
    }

    protected Thread thread;
	    public void start() {
		super.start();
		thread=new Thread(this);
		thread.start();
    }

    public void run() {
		try {
            while(clientListener!=null) {
                String line=bufr.readLine();
                debug("ECAClient.receive(\""+line+"\")");
                clientListener.clientInputEvent(line);
            }
		}catch(Exception e) {
			try{
				this.runtime.store.addLog(LogEventType.INTERNAL_ERROR, e.getMessage() + "at" + this.getClass() + ".run");
			}catch(Exception ex){
				ex.printStackTrace();
			}
		    return;
		}
    }

    /* ----- OUTPUT METHODS ----- */
    
    /* Lowest level output. */
    public void write(String xml) throws Exception {
		super.write(xml);
		outs.write((xml.replace('\n', ' ') + "\n").getBytes());
		outs.flush();
    }

    /* No formatting at all on output. */
    public void writeRaw(String string) throws Exception {
	super.write(string);
	outs.write(string.getBytes());
	outs.flush();
    }



    /* Normal termination of client connection. 
     * Set Kill=true if the client should execute a system shutdown on the host computer, or 
     * Kill=false to just close the client app */
    public void close() {
        clientExit(); 
        super.close();
		try {
		    clientListener=null;
		    //twb: let client hold the socket til exit: outs.close();
		    //twb: ditto: bufr.close();
		    thread.interrupt();	
		}catch(Exception e) {};
    }
    
    /* Abnormal session termination. ECA: cause client app to exit, 
       displaying the stated reason in a dialogue box. */
    public void exit(String reason) {
		super.exit(reason);
		try {
		    write("<SESSION ERROR=\"" + reason + "\"/>");
		}catch(Exception e) {
		    ; //death-throes anyway - just ignore
		}
    }
    
    public void perform(String xml,DialogueSession session) throws Exception {
		write("<PERFORM>" + xml + "</PERFORM>");
		//session.clientOutputEvent(xml);
    }
    
    /* Cause client to halt any ongoing speech & animation, and flush 
       its input queues. */
    public void flush() throws Exception {
		write("<FLUSH/>"); // flush any stuck PERFORM
    }
    
     
    public void clientExit() {
    	try {
    		write("<SESSION EXIT=\"NORMAL\" />");
    	}catch(Exception e) {
    	    ; //error shutting down machine - what to do ?
    	}
    }
}
