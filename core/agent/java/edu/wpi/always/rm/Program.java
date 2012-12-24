package edu.wpi.always.rm;

import edu.wpi.always.IRelationshipManager;

public class Program {

	public static void main(String[] args) {
		IRelationshipManager rm = configureRelationshipManager();
		configureCollaborationManager(rm);
	}

	private static void configureCollaborationManager(IRelationshipManager rm) {
		//new edu.wpi.always.test.client.Bootstrapper(rm).start();
		new CollaborationManager( (RelationshipManager) rm).start();
	}
	


	private static IRelationshipManager configureRelationshipManager() {
		return new RelationshipManager();
	}

}
