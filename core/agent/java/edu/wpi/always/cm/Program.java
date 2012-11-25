package edu.wpi.always.cm;

import org.picocontainer.*;

import edu.wpi.always.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.cm.schemas.registries.*;


public class Program {

	public static void main(String[] args) {
		ProgramBootstrapper program = new ProgramBootstrapper(false);
		program.addRegistry(new PicoRegistry(){
			@Override
			public void register(MutablePicoContainer container) {
				container.as(Characteristics.CACHE).addComponent(IRelationshipManager.class, FakeRelationshipManager.class);
				container.as(Characteristics.CACHE).addComponent(ICollaborationManager.class, edu.wpi.always.cm.Bootstrapper.class);
			}
		});

		program.addCMRegistry(new RagClientRegistry());
		
		program.addCMRegistry(new StandardRegistry());
		program.addCMRegistry(new FunPackRegistry());
		
		program.addCMRegistry(new FakeGuiRegistry());

		program.start();
		
	}

}
