package alwaysAvailableCore;

import edu.wpi.always.Plugin;
import edu.wpi.always.user.UserModel;
import DialogueRuntime.*;

import java.net.*;

public class AAECAServer {
    static Socket AAclientSocket;
    static AAECADialogueSession session;
    
    public AAECAServer(Socket s,String topScript, int user_id,UserModel userModel) {
	try {
		    ECAClient client=new ECAClient(s);
		    //client.setDEBUG(true);
		    
		    //FIXME: CHANGE TO A MYSQL DB
		   // AAFileStore propstore=new AAFileStore("c:\\AlwaysAvailable\\",  "PROPS\\current.dat", "PROPS\\DEFAULT.DAT");
//		    OntologyDBStore store=new OntologyDBStore();
//		    AADBStore store=new AADBStore("c:\\AlwaysAvailable\\Config\\AAConnection.properties");
		    AADBStore store=new AADBStore(userModel);
		    
		    AAPropertiesInitializer init = new AAPropertiesInitializer();
		    
		    AASessionRuntime runtime=new AASessionRuntime(store,init);	
		    runtime.getStore().setUserID(user_id);
		    
		    
		    //FIXME: CHANGE PROJECT NAME TO ALWAYS ON 
		    session=new AAECADialogueSession(client, runtime, topScript, "AlwaysAvailable");
		    
		   
		    session.setDialogueSessionInitializer(new AAECADialogueSessionInitializer());
		   
		    //session.setDEBUG(true);
		    
		    session.start();
		    System.out.println("after start()");
		}catch(Exception e) {
		    System.err.println("ex: "+e);
		    e.printStackTrace();
		}
    }
    
    public AAECADialogueSession getSession(){
    	return session;
    }
/*
    public static void main(String[] args) {
    	//FIXME: CHANGE TOP SCRIPT TO ALWAYS ON SCRIPT
    	String topScript = "top"; ;
    	int user_id=-1;
    	if (args.length > 0) {
    		user_id = Integer.parseInt(args[0]);
    		System.out.println("userid = "+user_id);
    		if(args.length > 1){
    			topScript = new String(args[1]); 
    		}
	    }
    	try {
		    System.out.println("Waiting for AlwaysAvailable client...");
		    ServerSocket server = new ServerSocket(6969);
		    AAclientSocket = server.accept();
		    System.out.println("Got a AlwaysAvailable client!");
		    new AAECAServer(AAclientSocket, topScript, user_id);
		    
		}catch(Exception e) {
		    System.err.println("ex: "+e);
		}
    }*/
}