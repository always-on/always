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
		    AADBStore store=new AADBStore(userModel);
		    
		    AAPropertiesInitializer init = new AAPropertiesInitializer();
		    
		    AASessionRuntime runtime=new AASessionRuntime(store,init);	
		    runtime.getStore().setUserID(user_id);
		    
		    
		    //FIXME: CHANGE PROJECT NAME TO ALWAYS ON 
		    session=new AAECADialogueSession(client, runtime, topScript, "AlwaysAvailable");
		    
		   
		    session.setDialogueSessionInitializer(new AAECADialogueSessionInitializer());
		   
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
}