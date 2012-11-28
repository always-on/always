package edu.wpi.always.rm;

import edu.wpi.always.*;

public class Program {

	public static void main(String[] args) {
		IRelationshipManager rm = configureRelationshipManager();
		configureCollaborationManager(rm);
	}

	private static void configureCollaborationManager(IRelationshipManager rm) {
		//new edu.wpi.always.cm.Bootstrapper(rm).start();
		new CollaborationManager( (RelationshipManager) rm).start();
	}
	


	private static IRelationshipManager configureRelationshipManager() {
		return new RelationshipManager();
	}

}
