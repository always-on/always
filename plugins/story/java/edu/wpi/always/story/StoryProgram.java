package edu.wpi.always.story;


import org.picocontainer.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.perceptors.physical.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.*;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.*;

public class StoryProgram {

	public static void main(String[] args) {
		final ProgramBootstrapper program = new ProgramBootstrapper(false);
			  
		program.addRegistry(new PicoRegistry(){
			@Override
			public void register(MutablePicoContainer container) {
				container.as(Characteristics.CACHE).addComponent(IRelationshipManager.class, FakeRelationshipManager.class);
				container.as(Characteristics.CACHE).addComponent(ICollaborationManager.class, edu.wpi.always.cm.Bootstrapper.class);
				container.as(Characteristics.CACHE).addComponent(edu.wpi.always.story.StoryManager.class);
//				container.as(Characteristics.CACHE).addComponent(edu.wpi.always.common.user.people.PeopleManager.class);
			}
		});
		program.addRegistry(new OntologyUserRegistry("Test User"));
		
		program.addCMRegistry(new DummyPerceptorsRegistry());
		program.addCMRegistry(new PhysicalPerceptorsRegistry());
		
		program.addCMRegistry(new RagClientRegistry());
		program.addCMRegistry(new StoryPluginRegistry());
		
		program.start();
		
		System.out.println("Loading user model...");
		program.getContainer().getComponent(UserModel.class).load();
		
	}
}
