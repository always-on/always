package edu.wpi.always.calendar;


import org.picocontainer.*;

import edu.wpi.always.calendar.schema.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.dummy.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.*;
import edu.wpi.always.user.owl.*;

public class CalendarProgram {

	public static void main(String[] args) {
		final ProgramBootstrapper program = new ProgramBootstrapper(false);
		
		program.addRegistry(new PicoRegistry(){
			@Override
			public void register(MutablePicoContainer container) {
				container.as(Characteristics.CACHE).addComponent(IRelationshipManager.class, FakeRelationshipManager.class);
				container.as(Characteristics.CACHE).addComponent(ICollaborationManager.class, edu.wpi.always.cm.Bootstrapper.class);
			}
		});
		program.addRegistry(new OntologyUserRegistry("Test User"));

		program.addCMRegistry(new DummyPerceptorsRegistry());
		
		program.addCMRegistry(new RagClientRegistry());
		program.addCMRegistry(new CalendarViewerPluginRegistry());
		
		program.start();
	}
}
